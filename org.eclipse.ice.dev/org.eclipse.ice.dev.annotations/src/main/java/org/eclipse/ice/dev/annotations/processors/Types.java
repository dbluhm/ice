/*******************************************************************************
 * Copyright (c) 2020- UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Bluhm - Initial implementation
 *******************************************************************************/

package org.eclipse.ice.dev.annotations.processors;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Class for holding and retrieving type information for a collection of Fields.
 * Stores context needed to prevent name collision.
 * @author Daniel Bluhm
 */
public class Types {

	/**
	 * Type matching regex used in shortening types. Can also be used for getting
	 * the last element of a type hierarchy (through capturing group 1).
	 */
	private static final Pattern TYPE_SHORTENER = Pattern.compile("(?:\\w+\\.)*([\\w\\$]+)\\b");

	/**
	 * Regex for capturing one layer down of type parameters
	 * <br><br>
	 *
	 * Given:
	 * <pre>
	 * {@code java.util.List<org.eclipse.ice.Car>}
	 * </pre>
	 * Capture group 1 will be:
	 * <pre>
	 * {@code org.eclipse.ice.Car}
	 * </pre>
	 *
	 * Given:
	 * <pre>
	 * {@code java.util.List<java.util.Set<org.eclipse.ice.Car>>}
	 * </pre>
	 * Capture group 1 will be:
	 * <pre>
	 * {@code java.util.Set<org.eclipse.ice.Car>}
	 * </pre>
	 * Given:
	 * <pre>
	 * {@code java.util.Map<String, java.util.Set<org.eclipse.ice.Car>>}
	 * </pre>
	 * Capture group 1 will be:
	 * <pre>
	 * {@code String, java.util.Set<org.eclipse.ice.Car>}
	 * </pre>
	 */
	private static final Pattern TYPE_PARAMETERS = Pattern.compile("<([<, \\w\\$\\.>]+)+>>*");

	/**
	 * Regex for type parameters separator.
	 */
	private static final Pattern TYPE_PARAMETER_SEP = Pattern.compile("\\s*,\\s*");

	/**
	 * Lookup table for fully qualified types to their shortened types.
	 *
	 * This is used to record collisions by mapping fully qualified types to
	 * themselves when another type would have conflicted with the shortened
	 * version.
	 *
	 * This map combined with the shortToFull map create a bidirectional mapping.
	 */
	private Map<String, String> fullToShort;

	/**
	 * Lookup table for shortened types to their fully qualified types.
	 *
	 * This is used to determine what types must be imported.
	 *
	 * This map combined with the fullToShort map create a bidirectional mapping.
	 */
	private Map<String, String> shortToFull;

	/**
	 * The set of all types present on the fields accounted for by this instance
	 * of Types.
	 */
	private Set<String> allTypes;

	/**
	 * Instantiate Types.
	 *
	 * Constructs bidirectional mapping between full and short type names.
	 * @param fields for which this Types instance is accountable.
	 */
	public Types(Iterable<Field> fields) {
		this.fullToShort = new HashMap<>();
		this.shortToFull = new HashMap<>();
		this.allTypes = new HashSet<>();

		// Extract types
		for (Field field : fields) {
			Matcher matcher = TYPE_SHORTENER.matcher(field.getType());
			while (matcher.find()) {
				allTypes.add(matcher.group());
			}
		}

		// Determine shortened names, populating the look up tables while
		// detecting collisions.
		// Types that would collide are left in fully qualified form and map to
		// themselves in the bidirectional map.
		Set<String> collisions = new HashSet<>();
		for (String type : allTypes) {
			String shortened = getShortenedType(type);
			if (collisions.contains(shortened)) {
				// Collision detected; first instance of collision already
				// corrected, storing only this instance.
				fullToShort.put(type, type);
				shortToFull.put(type, type);
			} else if (shortToFull.containsKey(shortened)) {
				// Collision detected; correct first instance as well as storing
				// this instance.
				fullToShort.put(type, type);
				shortToFull.put(type, type);
				String previous = shortToFull.remove(shortened);
				shortToFull.put(previous, previous);
				fullToShort.put(previous, previous);
				// Mark this collision as already having its first instance
				// corrected.
				collisions.add(shortened);
			} else {
				// No collision detected, save to bidirectional mapping
				fullToShort.put(type, shortened);
				shortToFull.put(shortened, type);
			}
		}
	}

	/**
	 * Return the shortened type for the given type.
	 * @param type to shorten
	 * @return shortened type
	 */
	public static String getShortenedType(String type) {
		StringBuffer shortenedType = new StringBuffer();
		Matcher matcher = TYPE_SHORTENER.matcher(type);
		while (matcher.find()) {
			matcher.appendReplacement(shortenedType, "$1");
		}
		matcher.appendTail(shortenedType);
		return shortenedType.toString();
	}

	/**
	 * Get set of imports needed for the fields this types instance handles.
	 * @return set of imports
	 */
	public Set<String> getImports() {
		return shortToFull.entrySet().stream()
			// Filter out collisions (fully qualified maps to itself)
			// Also filters out primitives (boolean shortened is still boolean)
			.filter(entry -> !entry.getKey().equals(entry.getValue()))
			.map(entry -> entry.getValue())
			// No need to import java.lang package
			.filter(type -> !type.startsWith("java.lang"))
			.filter(type -> !type.startsWith("$"))
			.collect(Collectors.toSet());
	}

	/**
	 * Resolve the type and its type parameters, shortening all types that
	 * can be shortened.
	 * @param type to look up
	 * @return shortened type if no collisions, full type if it would collide
	 */
	public String resolve(String type) {
		StringBuffer resolved = new StringBuffer();
		Matcher matcher = TYPE_SHORTENER.matcher(type);
		while (matcher.find()) {
			String found = fullToShort.get(matcher.group());
			if (found == null) {
				found = matcher.group();
			}
			matcher.appendReplacement(
				resolved,
				found
			);
		}
		matcher.appendTail(resolved);
		return resolved.toString();
	}

	/**
	 * Get the type parameters of the given type if present.
	 * @param type from which to retrieve type parameters
	 * @return type parameters if present, empty list otherwise.
	 */
	public static List<String> getTypeParameters(String type) {
		Matcher matcher = TYPE_PARAMETERS.matcher(type);
		if (matcher.find()) {
			return Arrays.asList(
				TYPE_PARAMETER_SEP.split(matcher.group(1))
			);
		}
		return Collections.emptyList();
	}

	/**
	 * Return the key type of a Map.
	 * @param type Map type from which key type is retrieved
	 * @return type as string or null if not a map type
	 */
	public static String getMapKeyType(String type) {
		List<String> params = getTypeParameters(type);
		if (params.size() == 2) {
			return params.get(0);
		}
		return null;
	}

	/**
	 * Return the value type of both simple containers and maps. If the passed
	 * type is a Map, the second type parameter is returned. If the passed type
	 * is a List, Set, or other single type container, the first and only type
	 * parameter is returned.
	 * @param type from which contained value type is retrieved
	 * @return first type parameter of containers, second type parameter of
	 *         maps.
	 */
	public static String getContainedValueType(String type) {
		List<String> params = getTypeParameters(type);
		if (params.size() == 2) {
			return params.get(1);
		} else if (params.size() == 1) {
			return params.get(0);
		}
		return null;
	}
}