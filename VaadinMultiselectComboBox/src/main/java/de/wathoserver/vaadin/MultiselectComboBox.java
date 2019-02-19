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
import com.vaadin.flow.function.ValueProvider;
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
  private static final PropertyDescriptor<String, String> itemIdPathProperty =
      PropertyDescriptors.propertyWithDefault("itemIdPath", "");
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
  // Object:ID
  private Map<T, String> itemsToKeys;
  private Map<String, T> keysToItems;

  private ItemLabelGenerator<T> itemLabelGenerator;
  private ValueProvider<T, String> keyProvider;
  private ValueProvider<T, String> defaultKeyProvider = new ValueProvider<T, String>() {

    @Override
    public String apply(T source) {
      return source.getClass().getName() + '@' + Integer.toHexString(source.hashCode());
    }
  };

  public MultiselectComboBox() {
    this(null, String::valueOf);
  }
  
  public MultiselectComboBox(final ItemLabelGenerator<T> itemLabelGenerator) {
    this(null, itemLabelGenerator);
  }

  public MultiselectComboBox(final ValueProvider<T, String> keyProvider,
      final ItemLabelGenerator<T> itemLabelGenerator) {
    super(null);
    this.itemLabelGenerator = itemLabelGenerator;
    this.keyProvider = keyProvider;
    getElement().addSynchronizedProperty("title");

    final ComponentEventListener<SelectedItemsChangedEvent> ls =
        event -> parseSelectedItemArray(event.getSelectedItems());
    addListener(SelectedItemsChangedEvent.class, ls);
    // Separate listener for clicking on the cross in the input field, cause it does not generate a
    // SelectedItemChangedEvent
    addListener(RemoveAllItemsEvent.class, e -> deselectAll());
    itemLabelPathProperty.set(this, "itemLabelPath");
    itemValuePathProperty.set(this, "itemValuePath");
    itemIdPathProperty.set(this, "id");
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
    return Collections.unmodifiableCollection(itemsToKeys.keySet());
  }

  public void setItems(@SuppressWarnings("unchecked") final T... values) {
    this.setItems(Arrays.asList(values));
  }

  public void setItems(Iterable<T> values) {
    final Iterator<T> it = values.iterator();
    final JsonArray jsonItems = Json.createArray();
    itemsToKeys = new HashMap<T, String>();
    keysToItems = new HashMap<String, T>();
    int n = 0;
    while (it.hasNext()) {
      final T item = it.next();
      final JsonObject object = Json.createObject();
      String id = getKeyProvider().apply(item);
      object.put("id", id);
      object.put("itemValuePath", id);
      object.put("itemLabelPath", itemLabelGenerator.apply(item));
      jsonItems.set(n, object);
      itemsToKeys.put(item, id);
      keysToItems.put(id, item);
      n++;
    }
    getElement().setPropertyJson("items", jsonItems);
  }

  private void parseSelectedItemArray(final JsonArray selectedItemsArray) {
    final Set<T> selectedItems = new HashSet<>();
    for (int i = 0; i < selectedItemsArray.length(); i++) {
      final JsonObject jsonItem = selectedItemsArray.getObject(i);
      final String key = jsonItem.getString("id");
      selectedItems.add(keysToItems.get(key));
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
      final T item = it.next();
      final JsonObject object = Json.createObject();
      String id = itemsToKeys.get(item);
      object.put("itemValuePath", id);
      object.put("id", id);
      object.put("itemLabelPath", itemLabelGenerator.apply(item));
      jsonItems.set(n, object);
      n++;
    }
    getElement().setPropertyJson("selectedItems", jsonItems);
    this.setModelValue(this.selectedItems, false);
    this.fireEvent(new MultiSelectionEvent<MultiselectComboBox<T>, T>(this, this, oldItems, true));
    // validate();
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

  public void validate() {
    getElement().callFunction("validate");
  }

  public ValueProvider<T, String> getKeyProvider() {
    if (keyProvider == null) {
      keyProvider = defaultKeyProvider;
    }
    return keyProvider;
  }

  /**
   * Sets a keyProvider for getting unique keys from Item. Default uses classname+hashcode.
   * 
   * @param keyProvider
   */
  public void setKeyProvider(ValueProvider<T, String> keyProvider) {
    this.keyProvider = keyProvider;
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
