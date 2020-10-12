/*******************************************************************************
 * Copyright (c) 2020- UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Bluhm - Initial implementation
 *    Michael Walsh
 *******************************************************************************/

package org.eclipse.ice.dev.annotations.processors;

import javax.tools.FileObject;
import javax.tools.JavaFileObject;

import lombok.NonNull;

/**
 * Writer for DataElement Interfaces.
 * 
 * @author Daniel Bluhm
 */
public abstract class InterfaceWriter extends VelocitySourceWriter {

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
	 * Constructor
	 * 
	 * @param packageName
	 * @param interfaceName
	 * @param fields
	 * @param generatedFile
	 */
	public InterfaceWriter(
		String packageName, String interfaceName, @NonNull Fields fields,
		@NonNull Types types, FileObject generatedFile
	) {
		super();
		context.put(PACKAGE, packageName);
		context.put(INTERFACE, interfaceName);
		context.put(FIELDS, fields);
		context.put(TYPES, types);
		this.generatedFile = generatedFile;
	}

	protected InterfaceWriter() {

	}
}
