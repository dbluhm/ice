package org.eclipse.ice.data.neutrons;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.router.Route;

/**
 * The main view contains a button and a click listener.
 */
@Route
@PWA(name = "My Application", shortName = "My Application")
public class MainView extends VerticalLayout {

    public MainView() {
    	ReductionConfiguration ex = new ReductionConfigurationImplementation();
        Button button = new Button(ex.getName(),
                event -> Notification.show("Clicked!"));
        add(button);
    }
}