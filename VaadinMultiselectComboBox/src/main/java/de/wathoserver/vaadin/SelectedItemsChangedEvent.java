package de.wathoserver.vaadin;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;

import elemental.json.JsonArray;

@SuppressWarnings("serial")
@DomEvent("selected-items-changed")
  public class SelectedItemsChangedEvent extends ComponentEvent<MultiselectComboBox<?>> {

    JsonArray selectedItems;

  public SelectedItemsChangedEvent(MultiselectComboBox<?> source, boolean fromClient,
        @EventData(value = "element.selectedItems") JsonArray selectedItems) {
      super(source, true); //
      this.selectedItems = selectedItems;
    }

    public JsonArray getSelectedItems() {
      return selectedItems;
    }

  }