package de.wathoserver.vaadin;

import java.util.HashSet;
import java.util.stream.Collectors;

import com.github.appreciated.demo.helper.DemoHelperView;
import com.github.appreciated.demo.helper.view.devices.DeviceSwitchView;
import com.github.appreciated.demo.helper.view.devices.DeviceType;
import com.github.appreciated.demo.helper.view.devices.PhoneView;
import com.github.appreciated.demo.helper.view.entity.CodeExample;
import com.github.appreciated.demo.helper.view.paragraph.DeviceParagraphView;
import com.google.common.collect.ImmutableList;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

@SuppressWarnings("serial")
@Route
@PWA(name = "Demoapplication for Multiselect ComboBox", shortName = "MultiselectComboBoxDemo")
public class MainView extends VerticalLayout {

  private MultiselectComboBox<String> stringBox;
  private MultiselectComboBox<User> userBox;
  private MultiselectComboBox<Color> enumBox;

  public MainView() {
    final DemoHelperView view = new DemoHelperView()
        .withHorizontalHeader("Multiselect ComboBox Demo", "") //
        .withParagraph("Simple Demo", "Select Strings, Pojos or Enumerations", createFirstDemo()) //
        .withParagraph("Get and Set-Demo", "",
            new Div(new Span("ComboBox implements interface "), new Anchor(
                "https://vaadin.com/api/platform/12.0.6/com/vaadin/flow/data/selection/MultiSelect.html",
                "MultiSelect")),
            createSecondDemo()) //
        .withVerticalHeader("Usage")
        .withStep("Install", "Add to your pom.xml",
            new CodeExample(
                "<dependency>\n" + "   <groupId>de.wathoserver.vaadin</groupId>\n"
                    + "   <artifactId>multiselect-combo-box</artifactId>\n"
                    + "   <version>0.0.3</version>\n" + "</dependency>\n\n" + "<repository>\n"
                    + "   <id>vaadin-addons</id>\n"
                    + "   <url>http://maven.vaadin.com/vaadin-addons</url>\n" + "</repository>",
                "Maven", "Maven"))
        .withStep("Create Component", "Create Component and set properties",
            new CodeExample(
                "final MultiselectComboBox<String> box = new MultiselectComboBox<String>();\n"
                    + "box.setLabel(\"SimpleTest\");\n"
                    + "box.setPlaceholder(\"Click here to select items\");",
                "java", "Java"))
        .withStep("Add Items", "Add as List or with var-args",
            new CodeExample("final List<String> items = new ArrayList<>();\n"
                + "items.add(\"One\");\n" + "    items.add(\"Two\");\n" + "items.add(\"Three\");\n"
                + "    box.setItems(items);\n\nor\n\n"
                + "box.setItems(\"One\", \"Two\", \"Three\");", "java", "Java"))
        .withStep("Get selected items", "Getter or via Event",
            new CodeExample("final Collection<List> selectedItems = box.getSelectedItems();\n"
                + "// or\n" + "box.addSelectionListener(event -> \n"
                + "\tNotification.show(\"selection changed: \" + event.getAllSelectedItems()));",
                "java", "Java"))
        .withParagraph("Ressources", "",
            new Div(new Anchor("https://vaadin.com/directory/component/multiselectcombobox",
                "Vaadin Directory")),
            new Div(new Anchor("https://github.com/watho/vaadin-multiselect-combo-box", "Github")))
        .withParagraph("Time to say thanks", "Used projects", new Div(
            new Div(new Text("This Addon is based on the "),
                new Anchor("https://vaadin.com/directory/component/gatanasomultiselect-combo-box",
                    "AddOn mutliselect-combo-box"),
                new Text(" by Goran Atanasovski")),
            new Div(new Text("This Page is created with "),
                new Anchor("https://vaadin.com/directory/component/demohelperview",
                    "AddOn DemoHelperView"),
                new Text(" by Johannes Goebel"))));
    add(view);
  }

  private DeviceSwitchView createFirstDemo() {
    userBox = createUserExample();
    final DeviceSwitchView firstDemo = new DeviceSwitchView(new VerticalLayout(
        createStringExample(), userBox, createEnumExample(),
        new Button("Show selected items", VaadinIcon.BULLETS.create(), e -> showSelectedItems())));
    // firstDemo.setDeviceType(DeviceType.TABLET_LANDSCAPE);
    return firstDemo;
  }

  private Div createSecondDemo() {
    final MultiselectComboBox<User> userExample = createUserExample();
    userExample.addSelectionListener(event -> Notification.show("selection changed: " + event));
    final Button setSelectedBtn = new Button("setSelectedItems", e -> {
      final HashSet<User> selectedItems = new HashSet<>();
      selectedItems.add(new User("John", "Doe"));
      userExample.setSelectedItems(selectedItems);
    });
    final Button selectBtn = new Button("select Jane", e -> {
      userExample.select(new User("Jane", "Dee"));
    });
    final Button deSelectBtn = new Button("deselect Jane", e -> {
      userExample.deselect(new User("Jane", "Dee"));
    });
    final Button selectAllBtn = new Button("select all", e -> userExample.selectAll());
    final Button deSelectAllBtn = new Button("deselect all", e -> userExample.deselectAll());

    final Button showSelected = new Button("Show Selected", e -> {
      Notification.show("Selected: " + userExample.getSelectedItems().stream().map(User::toString)
          .collect(Collectors.joining(", ")), 2000, Position.MIDDLE);
      Notification.show("Items: "
          + userExample.getItems().stream().map(User::toString).collect(Collectors.joining(", ")),
          2000, Position.MIDDLE);
    });
    final PhoneView content = new PhoneView(
        new VerticalLayout(userExample, new HorizontalLayout(showSelected, setSelectedBtn),
            new HorizontalLayout(selectBtn, deSelectBtn),
            new HorizontalLayout(selectAllBtn, deSelectAllBtn)));
    content.changeTo(DeviceType.TABLET_LANDSCAPE.getClassNames());
    return new DeviceParagraphView(content,
        "Select and deselect items programmatically. Get informed by listener.");
  }

  private void showSelectedItems() {
    final ImmutableList<MultiselectComboBox<?>> boxes =
        ImmutableList.of(stringBox, userBox, enumBox);
    boxes.forEach(box -> Notification.show(box.getSelectedItems().toString()));
  }

  private Component createStringExample() {
    stringBox = new MultiselectComboBox<>();
    stringBox.setLabel("String example");
    stringBox.setPlaceholder("Click here to select");
    stringBox.setItems("One", "Two", "Three");
    return stringBox;
  }

  private MultiselectComboBox<User> createUserExample() {
    final MultiselectComboBox<User> box =
        new MultiselectComboBox<>(user -> user.getForename() + " " + user.getSurename());
    box.setLabel("User example");
    box.setPlaceholder("Click here to select");
    box.setItems(new User("John", "Doe"), new User("Jane", "Dee"), new User("Bugs", "Bunny"));
    return box;
  }

  public enum Color {
    BLUE, RED, GREEN, YELLOW, PURPLE
  };

  private Component createEnumExample() {
    enumBox = new MultiselectComboBox<>();
    enumBox.setLabel("Enumerations example");
    enumBox.setPlaceholder("Click here to select color");
    enumBox.setItems(Color.values());
    return enumBox;
  }

}
