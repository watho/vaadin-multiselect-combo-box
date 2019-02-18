package de.wathoserver.vaadin;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.AbstractGridMultiSelectionModel;
import com.vaadin.flow.data.selection.MultiSelect;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.data.selection.MultiSelectionListener;
import com.vaadin.flow.shared.Registration;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

@SuppressWarnings("serial")
@Tag("multiselect-combo-box")
@HtmlImport("bower_components/multiselect-combo-box/multiselect-combo-box.html")
public class MultiselectComboBox<T> extends AbstractField<MultiselectComboBox<T>, Set<T>>
    implements MultiSelect<MultiselectComboBox<T>, T> {

  private static final PropertyDescriptor<String, String> labelProperty =
      PropertyDescriptors.propertyWithDefault("label", "");
  private static final PropertyDescriptor<String, String> placeholderProperty =
      PropertyDescriptors.propertyWithDefault("placeholder", "Click here to select items");
  private static final PropertyDescriptor<String, String> itemLabelPathProperty =
      PropertyDescriptors.propertyWithDefault("itemLabelPath", "");
  private static final PropertyDescriptor<String, String> itemValuePathProperty =
      PropertyDescriptors.propertyWithDefault("itemValuePath", "");
  private static final PropertyDescriptor<Boolean, Boolean> focusedProperty =
      PropertyDescriptors.propertyWithDefault("focused", false);
  private static final PropertyDescriptor<Boolean, Boolean> readOnlyProperty =
      PropertyDescriptors.propertyWithDefault("readOnly", false);
  private static final PropertyDescriptor<Boolean, Boolean> requiredProperty =
      PropertyDescriptors.propertyWithDefault("required", false);
  private static final PropertyDescriptor<Boolean, Boolean> invalidProperty =
      PropertyDescriptors.propertyWithDefault("invalid", false);

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
    addListener(SelectedItemsChangedEvent.class, ls);
    // Separate listener for clicking on the cross in the input field, cause it does not generate a
    // SelectedItemChangedEvent
    addListener(RemoveAllItemsEvent.class, e -> deselectAll());
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

  public boolean isFocused() {
    return focusedProperty.get(this);
  }

  public void setFocused(final boolean focused) {
    // FIXME Does not work
    // focusedProperty.set(this, focused);
  }

  public boolean isRequired() {
    return requiredProperty.get(this);
  }

  public void setRequired(final boolean required) {
    requiredProperty.set(this, required);
  }

  public boolean isInvalid() {
    return invalidProperty.get(this);
  }

  public void setInvalid(final boolean invalid) {
    requiredProperty.set(this, invalid);
  }

  @Override
  public boolean isReadOnly() {
    return readOnlyProperty.get(this);
  }

  @Override
  public void setReadOnly(final boolean readOnly) {
    // FIXME Does not work
    // readOnlyProperty.set(this, readOnly);
  }

  public Collection<T> getItems() {
    return Collections.unmodifiableCollection(items.values());
  }

  public void setItems(@SuppressWarnings("unchecked") final T... values) {
    this.setItems(Arrays.asList(values));
  }

  public void setItems(Iterable<T> values) {
    final Iterator<T> it = values.iterator();
    final JsonArray jsonItems = Json.createArray();
    items = new HashMap<Integer, T>();
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

  private void parseSelectedItemArray(final JsonArray selectedItemsArray) {
    final Set<T> selectedItems = new HashSet<>();
    for (int i = 0; i < selectedItemsArray.length(); i++) {
      final JsonObject jsonItem = selectedItemsArray.getObject(i);
      final Integer key = Double.valueOf(jsonItem.getNumber("itemValuePath")).intValue();
      selectedItems.add(items.get(key));
    }
    final Set<T> oldItems = getSelectedItems();
    this.selectedItems = selectedItems;
    this.setModelValue(selectedItems, true);
    this.fireEvent(new MultiSelectionEvent<MultiselectComboBox<T>, T>(this, this, oldItems, true));
  }

  @Override
  public Set<T> getSelectedItems() {
    if (selectedItems == null) {
      selectedItems = new HashSet<>();
    }
    return Collections.unmodifiableSet(selectedItems);
  }

  public void setSelectedItems(final Collection<T> selectedItems) {
    final Set<T> oldItems = getSelectedItems();
    this.selectedItems = new LinkedHashSet<T>(selectedItems);
    final Iterator<T> it = selectedItems.iterator();
    final JsonArray jsonItems = Json.createArray();
    int n = 0;

    while (it.hasNext()) {
      final JsonObject object = Json.createObject();
      object.put("itemValuePath", n);
      final T item = it.next();
      object.put("itemLabelPath", itemLabelGenerator.apply(item));
      jsonItems.set(n, object);
      n++;
    }
    getElement().setPropertyJson("selectedItems", jsonItems);
    this.setModelValue(this.selectedItems, false);
    this.fireEvent(new MultiSelectionEvent<MultiselectComboBox<T>, T>(this, this, oldItems, true));
  }

  @Override
  public void updateSelection(Set<T> addedItems, Set<T> removedItems) {
    final Set<T> oldItems = new LinkedHashSet<>(getSelectedItems());
    for (final T tRemoved : removedItems) {
      oldItems.remove(tRemoved);
    }
    for (final T tAdded : addedItems) {
      oldItems.add(tAdded);
    }
    setSelectedItems(oldItems);
  }

  public void selectAll() {
    setSelectedItems(getItems());
  }

  @Override
  public void deselectAll() {
    setSelectedItems(Collections.emptySet());
  }

  /**
   * Looks hacky, because {@link MultiSelectionEvent} does not inherit from {@link ComponentEvent}.
   * Copied from {@link AbstractGridMultiSelectionModel}
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Registration addValueChangeListener(
      ValueChangeListener<? super ComponentValueChangeEvent<MultiselectComboBox<T>, Set<T>>> listener) {
    final ComponentEventListener componentEventListener = event -> listener
        .valueChanged((ComponentValueChangeEvent<MultiselectComboBox<T>, Set<T>>) event);
    return addListener(MultiSelectionEvent.class, componentEventListener);
  }

  /**
   * Looks hacky, because {@link MultiSelectionEvent} does not inherit from {@link ComponentEvent}.
   * Copied from {@link AbstractGridMultiSelectionModel}
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Registration addSelectionListener(
      MultiSelectionListener<MultiselectComboBox<T>, T> listener) {
    Objects.requireNonNull(listener, "listener cannot be null");
    return ComponentUtil.addListener(this, MultiSelectionEvent.class,
        (ComponentEventListener) (event -> listener.selectionChange((MultiSelectionEvent) event)));
  }

  @Override
  protected void setPresentationValue(Set<T> newPresentationValue) {
    setSelectedItems(newPresentationValue);
  }
}
