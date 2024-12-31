package com.example.views;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.Double;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.model.Entry;
import com.example.services.Service;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.result.DeleteResult;
import com.webforj.App;
import com.webforj.component.Composite;
import com.webforj.component.Theme;
import com.webforj.component.button.Button;
import com.webforj.component.button.ButtonTheme;
import com.webforj.component.field.NumberField;
import com.webforj.component.field.TextArea;
import com.webforj.component.field.TextField;
import com.webforj.component.html.elements.Div;
import com.webforj.component.html.elements.H1;
import com.webforj.component.html.elements.H3;
import com.webforj.component.html.elements.Img;
import com.webforj.component.html.elements.Paragraph;
import com.webforj.component.icons.Icon;
import com.webforj.component.icons.TablerIcon;
import com.webforj.component.layout.applayout.AppLayout;
import com.webforj.component.layout.columnslayout.ColumnsLayout;
import com.webforj.component.list.ChoiceBox;
import com.webforj.component.optioninput.CheckBox;
import com.webforj.component.tabbedpane.Tab;
import com.webforj.component.tabbedpane.TabbedPane;
import com.webforj.component.tabbedpane.event.TabSelectEvent;
import com.webforj.component.toast.Toast;
import com.webforj.data.binding.BindingContext;
import com.webforj.data.validation.server.ValidationResult;
import com.webforj.router.annotation.FrameTitle;
import com.webforj.router.annotation.Route;

import com.webforj.component.Composite;
import com.webforj.component.html.elements.Div;
import com.webforj.component.table.Table;
import com.webforj.router.annotation.FrameTitle;
import com.webforj.router.annotation.Route;

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
  private Entry entry = new Entry();
  private BindingContext<Entry> entryBindingContext;
  TextArea description = new TextArea("Description");
  TextArea internalNotes = new TextArea("Internal notes");
  ChoiceBox customerSelect = new ChoiceBox("Customer");
  CheckBox billableCheckBox = new CheckBox("Billable");
  CheckBox discountedCheckBox = new CheckBox("Discounted");
  NumberField hoursField = new NumberField("Hours");
  LocalDateTime currentDate = LocalDateTime.now();
  Table<Entry> entriesTable = new Table<>();
  // Button testButton = new Button("Test button");


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

    createEntryBindingContext();

	}

	private void constructDashboard(){
    entriesTable.setRepository(Service.getEntries());
    entriesTable.setWidth("90%");
    entriesTable.setHeight("40em");
    entriesTable.setAttribute("margin", "auto");
    entriesTable.addColumn("Description",Entry::getDescription);
    entriesTable.addColumn("Customer",Entry::getCustomer);
    // entriesTable.addColumn("Date Entered",Entry::getDateEntered);
    entriesTable.addColumn("Date Entered", (Entry entry) -> formatDate(entry.getDateEntered()));
    entriesTable.addColumn("Hours",Entry::getHours);
    entriesTable.addColumn("Internal notes",Entry::getInternalNotes);
    demo.addToContent(dashboardContent);
    dashboardContent.add(entriesTable);
  }

  // private static LocalDate convertDate(Date date) {
  //     return formatDate(date.toInstant()
  //                 .atZone(ZoneId.systemDefault())
  //                 .toLocalDate());
  // }

  public static String formatDate(LocalDateTime date) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
      return date.format(formatter);
  }

  private void constructNewEntry() {
    Button addEntryBtn = new Button("Add Entry", ButtonTheme.PRIMARY);
    addEntryBtn.addClassName("centerBtns").addClickListener(e -> handleCreateButtonClick());;
    Button deleteDraftBtn = new Button("Delete Draft", ButtonTheme.DANGER);
    deleteDraftBtn.addClassName("centerBtns");
  // testButton.addClickListener(e -> {
  //   entryBindingContext.write(entry);
  //   App.consoleLog("Entry description:" + entry.getDescription() + "/  Hours:" + entry.getHours() + "/  cust:" + entry.getCustomer() + "/  billable:" + entry.getBillable() + "/  discount:" + entry.getDiscounted() + "/  internal:" + entry.getInternalNotes());
  // });

    newEntryForm = new ColumnsLayout(
      List.of(
          new ColumnsLayout.Breakpoint("default", 0, 1),
          new ColumnsLayout.Breakpoint("small", "20em", 1),
          new ColumnsLayout.Breakpoint("medium", "40em", 2),
          new ColumnsLayout.Breakpoint("large", "60em", 4)),
          description,hoursField,customerSelect, billableCheckBox,
          discountedCheckBox,internalNotes);
    
    hoursField.setStep(0.25);
    newEntryForm.setSpan(description, 4);
    newEntryForm.setSpan(hoursField, 1);
    newEntryForm.setSpan(customerSelect, 1);
    newEntryForm.setSpan(billableCheckBox, 1);
    newEntryForm.setSpan(discountedCheckBox, 1);
    newEntryForm.setSpan(internalNotes, 4);
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
    customerSelect.add("Test1");
    customerSelect.add("Test2");
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

  private void createEntryBindingContext() {
    entryBindingContext = new BindingContext<>(Entry.class, true);
    entryBindingContext.bind(description,"description")
    .useSetter(((entry,description) -> {
      entry.setDescription(description.trim());
      // Make sure to add .trim() if no line break is desired
    })).autoValidate(true).add();
    
    entryBindingContext.bind(hoursField,"hours")
    .useSetter(((entry,hoursField) -> {
      entry.setHours(hoursField);
    })).add();

    entryBindingContext.bind(customerSelect,"customer")
    .useSetter(((entry,customerSelect) -> {
      entry.setCustomer(customerSelect.toString());
    })).add();

    entryBindingContext.bind(billableCheckBox,"billable")
    .useSetter(((entry,billableCheckBox) -> {
      entry.setBillable(billableCheckBox);
    })).add();

    entryBindingContext.bind(discountedCheckBox,"discounted")
    .useSetter(((entry,discountedCheckBox) -> {
      entry.setDiscounted(discountedCheckBox);
    })).add();

    entryBindingContext.bind(internalNotes,"internalNotes")
    .useSetter(((entry,internalNotes) -> {
      entry.setInternalNotes(internalNotes.trim());
    })).add();
  }

  private void connectToDb() {
  String uri = "mongodb://localhost:27017"; // Adjust to your MongoDB URI
  mongoClient = MongoClients.create(uri);
  mongoDatabase = mongoClient.getDatabase("mytimeWebforJ");
  collection = mongoDatabase.getCollection("entries");
  }

  // CREATE
  private void handleCreateButtonClick() {
    ValidationResult result = entryBindingContext.write(entry);
    if (result.isValid()) {
      connectToDb();
      Document document = new Document("description",entry.getDescription());
      document.append("hours",entry.getHours());
      document.append("customer",entry.getCustomer());
      document.append("billable",entry.getBillable());
      document.append("discounted",entry.getDiscounted());
      document.append("internalNotes",entry.getInternalNotes());
      document.append("dateEntered",currentDate);
      collection.insertOne(document);
      Toast.show("Entry for " + entry.getCustomer() + " (" + entry.getHours() + " hours) inserted into database",5000, Theme.GRAY);

      description.setText("");
      hoursField.setValue(0.00);
      customerSelect.selectIndex(0);
      billableCheckBox.setChecked(false);
      discountedCheckBox.setChecked(false);
      internalNotes.setText("");
    } else {
      Toast.show("At least one field is invalid.",5000,Theme.DANGER);
    }
  }

  // // RETRIEVE
  // private void handleRetButtonClick() {
  //   connectToDb();
  //   Document search = new Document("name",retName.getValue());
  //   Document foundDocument = (Document) collection.find(search).first();
  //   if (foundDocument != null) {
  //       Object ageValue = foundDocument.get("age");
  //       Toast.show("Retrieved age: " + ageValue, Theme.GRAY);
  //   } else {
  //       Toast.show("No document found with the given name.", Theme.GRAY);
  //   }
  // }

  // // UPDATE
  // private void handleUpdateButtonClick() {
  //   connectToDb();
  //   Document search = new Document("name",upName.getValue());
  //   if (search != null) {
  //     Bson updatedvalue = new Document("age", upAge.getValue());
  //     Bson updateoperation = new Document("$set", updatedvalue);
  //     collection.updateOne(search, updateoperation);
  //     Toast.show(upName.getValue() + "'s age is updated to " + upAge.getValue(), Theme.GRAY);
  //   }
  // }

  // // DELETE
  // private void handleDelButtonClick() {
  //   connectToDb();
  //   Document search = new Document("name", delName.getValue());
  //   DeleteResult result = collection.deleteOne(search);
  //   if (result.getDeletedCount() > 0) {
  //       Toast.show(delName.getValue() + " deleted successfully!", Theme.GRAY);
  //   } else {
  //       Toast.show("No document found with the given name.", Theme.GRAY);
  //   }
  // }

  private void retrieveMaxAge() {
    connectToDb();
    List<Bson> pipeline = Arrays.asList(
        Aggregates.group(null, Accumulators.max("maxAge", "$age"))
    );
    AggregateIterable<Document> result = collection.aggregate(pipeline);
    Document maxAgeDocument = result.first(); // Retrieve the first (and only) document
    
    if (maxAgeDocument != null) {
        String maxAge = maxAgeDocument.get("maxAge").toString();
        Integer maxAgeInt = Integer.parseInt(maxAge);
        maxAgeInt += 1;
        Toast.show("Maximum age is: " + maxAgeInt, Theme.GRAY);
    } else {
        Toast.show("No documents found.", Theme.GRAY);
    }
  }
}
