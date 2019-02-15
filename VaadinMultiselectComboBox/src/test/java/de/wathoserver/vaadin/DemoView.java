package de.wathoserver.vaadin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
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
    createStringComboBox();
    createUserComboBox();
    createEnumComboBox();
  }

  private void createStringComboBox() {
    final MultiselectComboBox<String> box = new MultiselectComboBox<String>();
    box.setLabel("SimpleTest");
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
  }

  private void createUserComboBox() {
    final MultiselectComboBox<User> box =
        new MultiselectComboBox<User>(user -> user.getForename() + " " + user.getSurename());
    box.setLabel("UserTest");
    box.setPlaceholder("Click here to select user");
    final List<User> items = new ArrayList<>();
    items.add(new User(0, "John", "Doe"));
    items.add(new User(0, "Jane", "Doe"));
    items.add(new User(0, "Bugs", "Bunny"));
    box.setItems(items);
    add(box);
    add(new Button("Show Selected", e -> {
      Notification.show("Selected: "
          + box.getSelectedItems().stream().map(User::toString).collect(Collectors.joining(", ")),
          2000, Position.MIDDLE);
    }));
  }

  private void createEnumComboBox() {
    final MultiselectComboBox<Color> box =
        new MultiselectComboBox<Color>();
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
  }
}
