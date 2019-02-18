package de.wathoserver.vaadin;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;

@SuppressWarnings("serial")
@DomEvent("remove-all-items")
public class RemoveAllItemsEvent extends ComponentEvent<MultiselectComboBox<?>> {

  public RemoveAllItemsEvent(MultiselectComboBox<?> source, boolean fromClient) {
    super(source, true); //
  }
}
