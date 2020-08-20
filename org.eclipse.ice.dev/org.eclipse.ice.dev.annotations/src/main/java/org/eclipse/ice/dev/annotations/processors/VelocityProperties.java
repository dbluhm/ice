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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Properties used to initialize Velocity.
 */
enum VelocityProperties {

	// Set up Velocity using the Singleton approach; ClasspathResourceLoader allows
	// us to load templates from src/main/resources
	RESOURCE_LOADER("resource.loader", "class"), CLASS_RESOURCE_LOADER("class.resource.loader.class",
			"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

	/**
	 * Serial Version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(VelocityProperties.class);

	/**
	 * Get key from enum.
	 * 
	 * @return key
	 */
	private static final String FILENAME = "velocity.properties";

	private static VelocityProperties instance = null;

	/**
	 * Get value from enum.
	 * 
	 * @return value
	 */
	private VelocityProperties() {
		try (InputStream propertyStream = getClass().getClassLoader().getResourceAsStream(FILENAME)) {
			super.load(propertyStream);
		} catch (FileNotFoundException e) {
			logger.error("velocity.properties could not be found");
		} catch (IOException e) {
			logger.error("velocity.properties could not be read");
		}
	}

	/**
	 * Generate and return Properties from enum.
	 * 
	 * @return Properties
	 */
	public static Properties get() {
		Properties p = new Properties();
		for (VelocityProperties vp : VelocityProperties.values()) {
			p.setProperty(vp.key(), vp.value());
		}
		return instance;
	}
}
