package de.wathoserver.vaadin;

import java.util.stream.Collectors;

import com.github.appreciated.demo.helper.DemoHelperView;
import com.github.appreciated.demo.helper.view.devices.DeviceSwitchView;
import com.github.appreciated.demo.helper.view.entity.CodeExample;
import com.google.common.collect.ImmutableMap;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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
  private final HorizontalLayout stringResultLayout = new HorizontalLayout();
  private final HorizontalLayout userResultLayout = new HorizontalLayout();
  private final HorizontalLayout enumResultLayout = new HorizontalLayout();

  public MainView() {
    final DemoHelperView view =
        new DemoHelperView().withHorizontalHeader("Multiselect ComboBox Demo", "") //
            .withParagraph("Demo", "Select Strings, Pojos or Enumerations",
                new DeviceSwitchView(new VerticalLayout(createStringExample(), stringResultLayout,
                    createUserExample(), userResultLayout, createEnumExample(), enumResultLayout,
                    new Button("Show selected items", VaadinIcon.BULLETS.create(),
                        e -> showSelectedItems())))) //
            .withVerticalHeader("Usage")
            .withStep("Install", "Add to your pom.xml",
                new CodeExample(
                    "<dependency>\n" + "   <groupId>de.wathoserver.vaadin</groupId>\n"
                        + "   <artifactId>multiselect-combo-box</artifactId>\n"
                        + "   <version>0.0.2</version>\n" + "</dependency>\n\n" + "<repository>\n"
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
                    + "items.add(\"One\");\n" + "    items.add(\"Two\");\n"
                    + "items.add(\"Three\");\n" + "    box.setItems(items);\n\nor\n\n"
                    + "box.setItems(\"One\", \"Two\", \"Three\");", "java", "Java"))
            .withStep("Get selected items",
                "Currently no Listener support. You have to get them yourself.",
                new CodeExample("final Collection<List> selectedItems = box.getSelectedItems();",
                    "java", "Java"))
            .withParagraph("Ressources", "",
                new Div(new Anchor("https://vaadin.com/directory/component/multiselectcombobox",
                    "Vaadin Directory")),
                new Div(
                    new Anchor("https://github.com/watho/vaadin-multiselect-combo-box", "Github")))
            .withParagraph("Time to say thanks", "Used projects",
                new Div(
                    new Div(new Text("This Addon is based on the "),
                        new Anchor(
                            "https://vaadin.com/directory/component/gatanasomultiselect-combo-box",
                            "AddOn mutliselect-combo-box"),
                        new Text(" by Goran Atanasovski")),
                    new Div(new Text("This Page is created with "),
                        new Anchor("https://vaadin.com/directory/component/demohelperview",
                            "AddOn DemoHelperView"),
                        new Text(" by Johannes Goebel"))));
    add(view);
  }

  private void showSelectedItems() {
    final ImmutableMap<? extends FlexComponent<?>, MultiselectComboBox<?>> results = ImmutableMap
        .of(stringResultLayout, stringBox, userResultLayout, userBox, enumResultLayout, enumBox);
    results.entrySet().stream().forEach(entry -> {
      entry.getKey().removeAll();
      entry.getKey().add(new Span(entry.getValue().getSelectedItems().stream().map(Object::toString)
          .collect(Collectors.joining("; "))));
    });
  }

  private Component createStringExample() {
    stringBox = new MultiselectComboBox<>();
    stringBox.setLabel("String example");
    stringBox.setPlaceholder("Click here to select");
    stringBox.setItems("One", "Two", "Three");
    return stringBox;
  }

  private Component createUserExample() {
    userBox = new MultiselectComboBox<>(user -> user.getForename() + " " + user.getSurename());
    userBox.setLabel("User example");
    userBox.setPlaceholder("Click here to select");
    userBox.setItems(new User("John", "Doe"), new User("Jane", "Dee"), new User("Bugs", "Bunny"));
    return userBox;
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
