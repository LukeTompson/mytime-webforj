package com.example.views;

import com.webforj.component.Composite;
import com.webforj.component.Theme;
import com.webforj.component.button.Button;
import com.webforj.component.button.ButtonTheme;
import com.webforj.component.field.TextField;
import com.webforj.component.layout.flexlayout.FlexDirection;
import com.webforj.component.layout.flexlayout.FlexLayout;
import com.webforj.component.toast.Toast;
import com.webforj.router.annotation.Route;

import com.mongodb.client.MongoCollection;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.result.DeleteResult;

/**
 * A simple HelloWorld view.
 */
@Route("/")
public class MyTimeView extends Composite<FlexLayout> {
  
  private FlexLayout self = getBoundComponent();
  private TextField name = new TextField("What is your name?");
  private TextField age = new TextField("What is your age?");
  private TextField business = new TextField("What is your business?");
  private Button btn = new Button("Add");

  private TextField retName = new TextField("Get age for:");
  private Button btn2 = new Button("Get age");
  private TextField upName = new TextField("Name:");
  private TextField upAge = new TextField("Update age to:");
  private Button btn3 = new Button("Update age");
  private TextField delName = new TextField("Delete:");
  private Button btn4 = new Button("Delete");
  private Button btn5 = new Button("getMax");
  MongoCollection collection;
  MongoDatabase mongoDatabase;
  MongoClient mongoClient;

  public MyTimeView(){
    self.setDirection(FlexDirection.COLUMN);
    self.setMaxWidth(300);
    self.setStyle("margin", "1em auto");

    btn.setTheme(ButtonTheme.PRIMARY)
        .addClickListener(e -> handleCreateButtonClick());
    btn2.setTheme(ButtonTheme.PRIMARY)
        .addClickListener(e -> handleRetButtonClick());
    btn3.setTheme(ButtonTheme.PRIMARY)
        .addClickListener(e -> handleUpdateButtonClick());
    btn4.setTheme(ButtonTheme.DANGER)
        .addClickListener(e -> handleDelButtonClick());
    btn5.addClickListener(e -> retrieveMaxAge());

    self.add(name, age, business, btn, retName, btn2, upName, upAge, btn3, delName, btn4, btn5);

    retrieveMaxAge();
}

private void connectToDb() {
  String uri = "mongodb://localhost:27017"; // Adjust to your MongoDB URI
  mongoClient = MongoClients.create(uri);
  mongoDatabase = mongoClient.getDatabase("webforjTest");
  collection = mongoDatabase.getCollection("helloworld");
}

// CREATE
private void handleCreateButtonClick() {
  connectToDb();
  Document document = new Document("_id",name.getValue());
  document.append("name",name.getValue());
  document.append("age", age.getValue());
  document.append("business",business.getValue());
  collection.insertOne(document);
  Toast.show("Name " + name.getValue() + " inserted into database", Theme.GRAY);
}

// RETRIEVE
private void handleRetButtonClick() {
  connectToDb();
  Document search = new Document("name",retName.getValue());
  Document foundDocument = (Document) collection.find(search).first();
  if (foundDocument != null) {
      Object ageValue = foundDocument.get("age");
      Toast.show("Retrieved age: " + ageValue, Theme.GRAY);
  } else {
      Toast.show("No document found with the given name.", Theme.GRAY);
  }
}

// UPDATE
private void handleUpdateButtonClick() {
  connectToDb();
  Document search = new Document("name",upName.getValue());
  if (search != null) {
    Bson updatedvalue = new Document("age", upAge.getValue());
    Bson updateoperation = new Document("$set", updatedvalue);
    collection.updateOne(search, updateoperation);
    Toast.show(upName.getValue() + "'s age is updated to " + upAge.getValue(), Theme.GRAY);
  }
}

// DELETE
private void handleDelButtonClick() {
  connectToDb();
  Document search = new Document("name", delName.getValue());
  DeleteResult result = collection.deleteOne(search);
  if (result.getDeletedCount() > 0) {
      Toast.show(delName.getValue() + " deleted successfully!", Theme.GRAY);
  } else {
      Toast.show("No document found with the given name.", Theme.GRAY);
  }
}

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


