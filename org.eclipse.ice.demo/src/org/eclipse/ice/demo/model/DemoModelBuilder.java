/*******************************************************************************
 * Copyright (c) 2014- UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Initial API and implementation and/or initial documentation - 
 *   Jay Jay Billings
 *******************************************************************************/
package org.eclipse.ice.demo.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.ice.item.Item;
import org.eclipse.ice.item.ItemType;
import org.eclipse.ice.item.model.AbstractModelBuilder;

public class DemoModelBuilder extends AbstractModelBuilder {

	public DemoModelBuilder() {
		setName("DemoModel");
		setType(ItemType.Model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ice.item.AbstractItemBuilder#getInstance(org.eclipse.core.
	 * resources.IProject)
	 */
	@Override
	public Item getInstance(IProject projectSpace) {
		Item item = new DemoModel(projectSpace);
		item.setItemBuilderName(this.getItemName());
		return item;
	}
}