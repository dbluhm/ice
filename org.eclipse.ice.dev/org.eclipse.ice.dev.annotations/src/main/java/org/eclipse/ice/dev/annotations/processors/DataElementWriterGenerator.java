package org.eclipse.ice.dev.annotations.processors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;

import org.eclipse.ice.dev.annotations.Persisted;

public class DataElementWriterGenerator extends AbstractWriterGenerator implements WriterGenerator {

	private Map<TemplateProperty, BiFunction<JavaFileObject, Map, List<VelocitySourceWriter>>> writerInitializers = 
			new HashMap<TemplateProperty, BiFunction<JavaFileObject, Map, List<VelocitySourceWriter>>>() {{
			    put(MetaTemplateProperty.QUALIFIED, DataElementInterfaceWriter.getContextInitializer());
			    put(MetaTemplateProperty.QUALIFIEDIMPL, DataElementImplementationWriter.getContextInitializer());
			    put(PersistenceHandlerTemplateProperty.QUALIFIED, DataElementPersistenceHandlerWriter.getContextInitializer());
			}};
	
	protected SpecExtractionHelper specExtractionHelper = new SpecExtractionHelper();
	
	DataElementWriterGenerator(ProcessingEnvironment processingEnv) {
		super(processingEnv);
	}

	/**
	 * DataElement specific method of class generation. Includes interfaces, implementation, and possibly a persistence handler
	 */
	@Override
	public List<VelocitySourceWriter> generateWriters(Element element, AnnotationExtractionResponse response) {
		List<VelocitySourceWriter> writers = new ArrayList<>();
		Map<TemplateProperty, Object> classMetadata = response.getClassMetadata();	
		boolean hasAnnotation = specExtractionHelper.hasAnnotation(element, Persisted.class);
		
		writerInitializers.keySet()
		.stream()
		.filter(key -> key != PersistenceHandlerTemplateProperty.QUALIFIED || hasAnnotation)
		.forEach(key -> {
			try {
				String name = (String)classMetadata.get(key);
				JavaFileObject fileObject = createFileObjectForName(name);
				List<VelocitySourceWriter> newWriters = writerInitializers.get(key).apply(fileObject, classMetadata);
				
				writers.addAll(newWriters);
			} catch (IOException e) {
				e.printStackTrace();
			}
			});
		
		return writers;
	}

}
