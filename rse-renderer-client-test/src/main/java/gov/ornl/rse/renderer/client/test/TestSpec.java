package gov.ornl.rse.renderer.client.test;

import java.util.List;

import org.eclipse.ice.dev.annotations.DataElement;
import org.eclipse.ice.dev.annotations.DataField;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@DataElement(name = "Test")
public class TestSpec {
	@JsonDeserialize(contentAs = CarImplementation.class)
	@DataField List<Car> cars;
}
