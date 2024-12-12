package com.example.views;

import java.util.List;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.webforj.annotation.InlineStyleSheet;
import com.webforj.component.Composite;
import com.webforj.component.Theme;
import com.webforj.component.button.Button;
import com.webforj.component.button.ButtonTheme;
import com.webforj.component.element.Element;
import com.webforj.component.html.elements.Div;
import com.webforj.component.html.elements.H1;
import com.webforj.component.html.elements.H3;
import com.webforj.component.html.elements.Img;
import com.webforj.component.html.elements.Paragraph;
import com.webforj.component.field.TextField;
import com.webforj.component.field.PasswordField;
import com.webforj.component.field.TextArea;
import com.webforj.component.icons.Icon;
import com.webforj.component.icons.TablerIcon;
import com.webforj.component.layout.applayout.AppDrawerToggle;
import com.webforj.component.layout.applayout.AppLayout;
import com.webforj.component.layout.columnslayout.ColumnsLayout;
import com.webforj.component.layout.flexlayout.FlexDirection;
import com.webforj.component.layout.flexlayout.FlexLayout;
import com.webforj.component.list.ChoiceBox;
import com.webforj.component.optioninput.CheckBox;
import com.webforj.component.tabbedpane.Tab;
import com.webforj.component.tabbedpane.TabbedPane;
import com.webforj.component.tabbedpane.TabbedPane.Placement;
import com.webforj.component.tabbedpane.event.TabSelectEvent;
import com.webforj.component.toast.Toast;
import com.webforj.router.annotation.FrameTitle;
import com.webforj.router.annotation.Route;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.result.DeleteResult;

// @InlineStyleSheet("context://css/applayout/applayout.css")
@Route
@FrameTitle("AppLayout Multiple Headers")
public class AppLayoutView extends Composite<AppLayout> {

	AppLayout demo = getBoundComponent();
	Paragraph contentLabel = new Paragraph();

	Div header = new Div();
	Div drawer = new Div();
  Div dashboardContent = new Div();
  Div newEntryContent = new Div();
  // FlexLayout newEntryContent = new FlexLayout(FlexDirection.COLUMN);
  Div editEntryContent = new Div();
  Div customersContent = new Div();
  ColumnsLayout newEntryForm;

  MongoCollection collection;
  MongoDatabase mongoDatabase;
  MongoClient mongoClient;

	public AppLayoutView() {

		demo.setDrawerHeaderVisible(true);
		demo.setDrawerFooterVisible(true);
    demo.setDrawerOpened(false);

		// HEADER
		header.addClassName("layout__header").add(
        new Img("ws://nexday icon.png"),
				new H3("MyTime - webforJ Edition"));
		demo.addToHeader(header);

		// Adding the additional toolbar with menu items
		Div secondToolbar = new Div();
		secondToolbar.addClassName("layout__second__header");
		demo.addToHeader(secondToolbar);
		TabbedPane secondMenu = new TabbedPane();
		secondToolbar.add(secondMenu);
		secondMenu.setBodyHidden(true);
		secondMenu.setBorderless(true);

		Icon dashboardIcon = TablerIcon.create("dashboard");
    Icon newEntryIcon = TablerIcon.create("files");
    Icon editEntryIcon = TablerIcon.create("file");
    Icon customersIcon = TablerIcon.create("users");

    secondMenu.addTab(new Tab("Dashboard", dashboardIcon));
    secondMenu.addTab(new Tab("New entry", newEntryIcon));
    secondMenu.addTab(new Tab("Edit entry", editEntryIcon));
		secondMenu.addTab(new Tab("Customers", customersIcon));

    secondMenu.onSelect(this::onTabChange);
    demo.addToContent(new H1(this.contentLabel));
    demo.addClassName("pageTitle");

    constructDashboard();
    constructNewEntry();
    constructEditEntry();
    constructCustomers();

	}

	private void constructDashboard(){
    Button btn2 = new Button("Dashboard");
    demo.addToContent(dashboardContent);
    dashboardContent.add(btn2);
  }

  private void constructNewEntry() {
    TextArea description = new TextArea("Description");
    TextArea internalNotes = new TextArea("Internal notes");
    ChoiceBox customerSelect = new ChoiceBox("Customer");
    CheckBox billablecCheckBox = new CheckBox("Billable");
    CheckBox discountedCheckBox = new CheckBox("Discounted");
    TextField hoursField = new TextField("Hours");
    Button addEntryBtn = new Button("Add Entry", ButtonTheme.PRIMARY);
    addEntryBtn.addClassName("centerBtns");
    Button deleteDraftBtn = new Button("Delete Draft", ButtonTheme.DANGER);
    deleteDraftBtn.addClassName("centerBtns");

    newEntryForm = new ColumnsLayout(
      List.of(
          new ColumnsLayout.Breakpoint("default", 0, 1),
          new ColumnsLayout.Breakpoint("small", "20em", 1),
          new ColumnsLayout.Breakpoint("medium", "40em", 2),
          new ColumnsLayout.Breakpoint("large", "60em", 6)),
          description,hoursField,customerSelect, billablecCheckBox,
          discountedCheckBox,internalNotes);
    
    newEntryForm.setSpan(description, 6);
    newEntryForm.setSpan(hoursField, 1);
    newEntryForm.setSpan(customerSelect, 3);
    newEntryForm.setSpan(billablecCheckBox, 1);
    newEntryForm.setSpan(discountedCheckBox, 1);
    newEntryForm.setSpan(internalNotes, 6);
    // newEntryForm.setSpan(addEntryBtn, 2);
    // newEntryForm.setSpan(deleteDraftBtn, 2);
    // // newEntryForm.setColumn(addEntryBtn, 2);
    // // newEntryForm.setColumn(deleteDraftBtn, 3);
    

    newEntryForm.addClassName("description");

    // Button btn3 = new Button("New entry");
    // newEntryContent.addClassName("formContent").add(newEntryForm);
    // demo.addToContent(newEntryContent);
    
    newEntryContent = new Div(addEntryBtn,deleteDraftBtn);
    demo.addToContent(newEntryForm, newEntryContent);
    newEntryForm.setVisible(false);
  }

  private void constructEditEntry() {
    Button btn4 = new Button("Edit entry");
    editEntryContent = new Div(btn4);
    demo.addToContent(editEntryContent);
  }

  private void constructCustomers() {
    Button btn5 = new Button("Customers");
    customersContent = new Div(btn5);
    demo.addToContent(customersContent);
  }
  
  private void onTabChange(TabSelectEvent ev) {
		String value = ev.getTab().getText().replaceAll("<[^>]*>", "").trim();
		contentLabel.setText(value);
    if (value.equals("Dashboard")) {
      dashboardContent.setVisible(true);
      newEntryForm.setVisible(false);
      newEntryContent.setVisible(false);
      editEntryContent.setVisible(false);
      customersContent.setVisible(false);
    }
    if (value.equals("New entry")) {
      dashboardContent.setVisible(false);
      newEntryForm.setVisible(true);
      newEntryContent.setVisible(true);
      newEntryContent.setVisible(true);
      editEntryContent.setVisible(false);
      customersContent.setVisible(false);
    }
    if (value.equals("Edit entry")) {
      dashboardContent.setVisible(false);
      newEntryForm.setVisible(false);
      newEntryContent.setVisible(false);
      editEntryContent.setVisible(true);
      customersContent.setVisible(false);
    }
    if (value.equals("Customers")) {
      dashboardContent.setVisible(false);
      newEntryForm.setVisible(false);
      newEntryContent.setVisible(false);
      editEntryContent.setVisible(false);
      customersContent.setVisible(true);
    }
	}
}
