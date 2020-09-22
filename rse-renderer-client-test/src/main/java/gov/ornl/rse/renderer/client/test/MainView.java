/*******************************************************************************
 * Copyright (c) 2020- UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Initial API and implementation and/or initial documentation - 
 *   Jay Jay Billings
 *******************************************************************************/
package gov.ornl.rse.renderer.client.test;

import java.util.List;
import java.util.function.BiConsumer;

import javax.annotation.PostConstruct;

import org.eclipse.ice.renderer.Renderer;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout {

	private Renderer<VaadinRendererClient<Car>, Car> carRenderer;

	/**
	 * Constructor
	 */
	public MainView() {
	}

	/**
	 * This operation draws the main view. It must be executed after construction
	 * because it depends on dependencies that area injected into the class.
	 */
	@PostConstruct
	public void render() {
		add(new H1("Here's an auto-generated form!"));
		// Nothing to do here - just sample setup
		Car elantra = new CarImplementation();
		try {
			elantra.setName("Car");
			elantra.setDescription("A model for cars");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		elantra.setMake("Hyundai");
		elantra.setModel("Elantra");
		elantra.setYear(2005);
		carRenderer = new Renderer<FormRendererClient<Car>, Car>();
		carRenderer.setViewer(new FormRendererClient<Car>());
		BiConsumer<FormRendererClient<Car>, Car> carDraw = (v, w) -> {
			v.setData(w);
			add(v);
		};
		carRenderer.setDataElement(elantra);
		carRenderer.setDrawMethod(carDraw);

		carRenderer.render();

		Test test = new TestImplementation();
		try {
			test.setName("Test Name");
			test.setCars(List.of(elantra));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String testJson = test.toJson();
		Test newTest = new TestImplementation();
		newTest.fromJson(testJson);
		System.out.println(test);
		System.out.println(newTest);
	}

}