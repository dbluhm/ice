/*******************************************************************************
 * Copyright (c) 2020- UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Michael Walsh - Initial implementation
 *******************************************************************************/
package org.eclipse.ice.dev.annotations.processors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;

import org.eclipse.ice.dev.annotations.Persisted;

/**
 * Uses metadata extracted from spec classes annotated with @DataElement to
 * generate the interface, implementation, and persistence handler.
 *
 */
public class DataElementWriterGenerator extends AbstractWriterGenerator implements WriterGenerator {

	/**
	 * Map of file name key to the respective file's writer initializer
	 */
	private Map<TemplateProperty, BiFunction<FileObject, Map, List<VelocitySourceWriter>>> writerInitializers =
		new HashMap<>();

	/**
	 * Util instance for extracting specific data from Element objects
	 */
	protected SpecExtractionHelper specExtractionHelper = new SpecExtractionHelper();

	DataElementWriterGenerator(ProcessingEnvironment processingEnv) {
		super(processingEnv);
		writerInitializers.put(
			MetaTemplateProperty.QUALIFIED,
			DataElementInterfaceWriter.getContextInitializer()
		);
		writerInitializers.put(
			MetaTemplateProperty.QUALIFIEDIMPL,
			DataElementImplementationWriter.getContextInitializer()
		);
		writerInitializers.put(
			PersistenceHandlerTemplateProperty.QUALIFIED,
			DataElementPersistenceHandlerWriter.getContextInitializer()
		);
		writerInitializers.put(
			MetaTemplateProperty.TYPESCRIPT,
			DataElementTypeScriptWriter.getContextInitializer()
		);
	}

	/**
	 * DataElement specific method of class generation. Includes interfaces,
	 * implementation, and possibly a persistence handler
	 */
	@Override
	public List<VelocitySourceWriter> generateWriters(
		Element element, AnnotationExtractionResponse response
	) {
		List<VelocitySourceWriter> writers = new ArrayList<>();
		Map<TemplateProperty, Object> classMetadata = response.getClassMetadata();
		boolean hasAnnotation = specExtractionHelper.hasAnnotation(element, Persisted.class);

		writerInitializers.keySet().stream()
			.filter(key -> key != PersistenceHandlerTemplateProperty.QUALIFIED || hasAnnotation)
			.forEach(key -> {
				try {
					FileObject fileObject = null;
					if (key == MetaTemplateProperty.TYPESCRIPT) {
						fileObject = createResourceForName(
							(String) classMetadata.get(MetaTemplateProperty.CLASS)
						);
					} else {
						String name = (String) classMetadata.get(key);
						fileObject = createFileObjectForName(name);
					}
					List<VelocitySourceWriter> newWriters = writerInitializers
						.get(key)
						.apply(
							fileObject,
							classMetadata
						);
					writers.addAll(newWriters);
				} catch (UnsupportedOperationException | IOException e) {
					e.printStackTrace();
				}
			});

		return writers;
	}

}
