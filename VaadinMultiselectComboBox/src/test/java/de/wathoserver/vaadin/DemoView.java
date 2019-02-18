package de.wathoserver.vaadin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route("")
public class DemoView extends VerticalLayout {

  public DemoView() {
    setMargin(true);
    setPadding(true);
    setSpacing(true);
    setWidth("400px");
    createStringComboBox("Simple Test");
    createUserComboBox();
    createEnumComboBox();
    final MultiselectComboBox<String> requiredBox = createStringComboBox("Selection required");
    requiredBox.setRequired(true);
  }

  private MultiselectComboBox<String> createStringComboBox(final String label) {
    final MultiselectComboBox<String> box = new MultiselectComboBox<String>();
    box.setLabel(label);
    box.setPlaceholder("Click here to select items");
    final List<String> items = new ArrayList<>();
    items.add("One");
    items.add("Two");
    items.add("Three");
    box.setItems(items);
    add(box);
    add(new Button("Show Selected", e -> {
      Notification.show(
          "Selected: " + box.getSelectedItems().stream().collect(Collectors.joining(", ")), 2000,
          Position.MIDDLE);
    }));
    return box;
  }

  private void createUserComboBox() {
    final MultiselectComboBox<User> box =
        new MultiselectComboBox<User>(user -> user.getForename() + " " + user.getSurename());
    box.setLabel("UserTest");
    box.setPlaceholder("Click here to select user");
    final List<User> items = new ArrayList<>();
    items.add(new User(0, "John", "Doe"));
    items.add(new User(1, "Jane", "Doe"));
    items.add(new User(2, "Bugs", "Bunny"));
    box.setItems(items);
    add(box);
    box.addSelectionListener(event -> Notification.show("selection changed: " + event));
    box.addValueChangeListener(event -> Notification.show("value changed: " + event));
    final Button setSelectedBtn = new Button("setSelectedItems", e -> {
      final HashSet<User> selectedItems = new HashSet<>();
      selectedItems.add(new User(0, "John", "Doe"));
      box.setSelectedItems(selectedItems);
    });
    final Button selectBtn = new Button("select Jane", e -> {
      box.select(new User(1, "Jane", "Doe"));
    });
    final Button deSelectBtn = new Button("deselect Jane", e -> {
      box.deselect(new User(1, "Jane", "Doe"));
    });
    final Button selectAllBtn = new Button("select all", e -> box.selectAll());
    final Button deSelectAllBtn = new Button("deselect all", e -> box.deselectAll());
    add(new HorizontalLayout(new Button("Show Selected", e -> {
      Notification.show("Selected: "
          + box.getSelectedItems().stream().map(User::toString).collect(Collectors.joining(", ")),
          2000, Position.MIDDLE);
      Notification.show(
          "Items: " + box.getItems().stream().map(User::toString).collect(Collectors.joining(", ")),
          2000, Position.MIDDLE);
    }), setSelectedBtn, selectBtn, deSelectBtn, selectAllBtn, deSelectAllBtn));
  }

  private void createEnumComboBox() {
    final MultiselectComboBox<Color> box = new MultiselectComboBox<Color>();
    box.setLabel("ColorTest");
    box.setItems(Color.values());
    add(box);
    add(new Button("Show Selected", e -> {
      Notification.show("Selected: "
          + box.getSelectedItems().stream().map(Color::toString).collect(Collectors.joining(", ")),
          2000, Position.MIDDLE);
    }));
  }

  enum Color {
    RED, BLUE, GREEN;
  }

  public class User {
    private Integer id;
    private String forename;
    private String surename;

    public User(Integer id, String forename, String surename) {
      this.id = id;
      this.forename = forename;
      this.surename = surename;
    }

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public String getForename() {
      return forename;
    }

    public void setForename(String forename) {
      this.forename = forename;
    }

    public String getSurename() {
      return surename;
    }

    public void setSurename(String surename) {
      this.surename = surename;
    }

    @Override
    public String toString() {
      return "forename=" + forename + "; surename=" + surename;
    }

    @Override
    public boolean equals(Object obj) {
      return id.equals(((User) obj).getId());
    }

    @Override
    public int hashCode() {
      return id.hashCode();
    }
  }
}
