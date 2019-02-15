package de.wathoserver.vaadin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

@SuppressWarnings("serial")
@Tag("multiselect-combo-box")
@HtmlImport("bower_components/multiselect-combo-box/multiselect-combo-box.html")
public class MultiselectComboBox<T> extends AbstractField<MultiselectComboBox<T>, T> {

  private static final PropertyDescriptor<String, String> labelProperty =
      PropertyDescriptors.propertyWithDefault("label", "");
  private static final PropertyDescriptor<String, String> placeholderProperty =
      PropertyDescriptors.propertyWithDefault("placeholder", "Click here to select items");
  private static final PropertyDescriptor<String, String> itemLabelPathProperty =
      PropertyDescriptors.propertyWithDefault("itemLabelPath", "");
  private static final PropertyDescriptor<String, String> itemValuePathProperty =
      PropertyDescriptors.propertyWithDefault("itemValuePath", "");

  private Set<T> selectedItems = new HashSet<>();
  private Map<Integer, T> items;

  private ItemLabelGenerator<T> itemLabelGenerator;

  public MultiselectComboBox() {
    this(String::valueOf);
  }

  public MultiselectComboBox(final ItemLabelGenerator<T> itemLabelGenerator) {
    super(null);
    getElement().addSynchronizedProperty("title");

    final ComponentEventListener<SelectedItemsChangedEvent> ls =
        event -> parseSelectedItemArray(event.getSelectedItems());

    // ComponentUtil.addListener(this, SelectedItemsChangedEvent.class, ls);
    addListener(SelectedItemsChangedEvent.class, ls);
    this.itemLabelGenerator = itemLabelGenerator;
    itemLabelPathProperty.set(this, "itemLabelPath");
    itemValuePathProperty.set(this, "itemValuePath");
  }

  public String getPlaceholder() {
    return placeholderProperty.get(this);
  }

  public void setPlaceholder(final String placeholder) {
    placeholderProperty.set(this, placeholder);
  }

  public String getLabel() {
    return labelProperty.get(this);
  }

  public void setLabel(final String label) {
    labelProperty.set(this, label);
  }

  public void setItems(final T... values) {
    this.setItems(Arrays.asList(values));
  }

  public void setItems(List<T> values) {
    final Iterator<T> it = values.iterator();
    final JsonArray jsonItems = Json.createArray();
    items = new HashMap<Integer, T>(values.size());
    int n = 0;

    while (it.hasNext()) {
      final JsonObject object = Json.createObject();
      object.put("itemValuePath", n);
      final T item = it.next();
      object.put("itemLabelPath", itemLabelGenerator.apply(item));
      jsonItems.set(n, object);
      items.put(n, item);
      n++;
    }
    getElement().setPropertyJson("items", jsonItems);
  }

  @Override
  protected void setPresentationValue(T newPresentationValue) {
    // TODO add to selected Items

  }

  private void parseSelectedItemArray(final JsonArray selectedItemsArray) {
    final Set<T> selectedItems = new HashSet<>();
    for (int i = 0; i < selectedItemsArray.length(); i++) {
      final JsonObject jsonItem = selectedItemsArray.getObject(i);
      final Integer key = Double.valueOf(jsonItem.getNumber("itemValuePath")).intValue();
      selectedItems.add(items.getOrDefault(key, getEmptyValue()));
    }
    setSelectedItems(selectedItems);
  }

  // public class ComboBoxSelectionEvent extends MultiSelectionEvent<MultiselectComboBox<T>, T> {
  //
  // public ComboBoxSelectionEvent(MultiselectComboBox<T> listing,
  // HasValue<ComponentValueChangeEvent<MultiselectComboBox<T>, Set<T>>, Set<T>> source,
  // Set<T> oldSelection, boolean userOriginated) {
  // super(listing, source, oldSelection, userOriginated);
  // }
  // }

  public Set<T> getSelectedItems() {
    return selectedItems;
  }

  private void setSelectedItems(final Set<T> selectedItems) {
    this.selectedItems = selectedItems;
  }

  public Optional<T> getFirstSelectedItem() {
    return getSelectedItems().stream().findFirst();
  }

  public void select(T item) {
    // TODO
  }

  public void deselect(T item) {
    // TODO
  }

  public void deselectAll() {
    // TODO
  }

  public void updateSelection(Set<T> addedItems, Set<T> removedItems) {
    // TODO Auto-generated method stub

  }

  public void selectAll() {
    // TODO Auto-generated method stub

  }

  // @SuppressWarnings("unchecked")
  // @Override
  // public Registration addSelectionListener(SelectionListener<MultiselectComboBox<T>, T> listener)
  // {
  // return ComponentUtil.addListener(this, MultiSelectionEvent.class,
  // (ComponentEventListener) (event -> listener.selectionChange((SelectionEvent) event)));
  // }

  // @Override
  // public Registration addValueChangeListener(
  // ValueChangeListener<? super ComponentValueChangeEvent<MultiselectComboBox, String>> listener) {
  //
  // }
}
