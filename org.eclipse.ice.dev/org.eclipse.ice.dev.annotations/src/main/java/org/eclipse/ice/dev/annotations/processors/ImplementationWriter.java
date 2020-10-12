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

import java.io.Writer;

import lombok.Builder;
import lombok.NonNull;

/**
 * Writer for DataElement Implementation classes.
 * @author Daniel Bluhm
 */
public class ImplementationWriter extends VelocitySourceWriter {

	/**
	 * Context key for package.
	 */
	private static final String PACKAGE = "package";

	/**
	 * Context key for interface.
	 */
	private static final String INTERFACE = "interface";

	/**
	 * Context key for fields.
	 */
	private static final String FIELDS = "fields";

	/**
	 * Context key for types.
	 */
	private static final String TYPES = "types";

	/**
	 * Context key for class.
	 */
	private static final String CLASS = "class";

	public ImplementationWriter(
		String packageName, String interfaceName, String className,
		@NonNull Fields fields, @NonNull Types types, Writer writer
	) {
		super();
		this.context.put(PACKAGE, packageName);
		this.context.put(INTERFACE, interfaceName);
		this.context.put(CLASS, className);
		this.context.put(FIELDS, fields);
		this.context.put(TYPES, types);
		this.writer = writer;
	}

}
