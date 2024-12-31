package com.example.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import com.example.model.Entry;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.webforj.data.repository.CollectionRepository;
import com.webforj.data.repository.Repository;
import org.bson.Document;

public final class Service {
    private Service() {}

    public static Repository<Entry> getEntries() {
        String uri = "mongodb://localhost:27017"; // Adjust to your MongoDB URI
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("mytimeWebforJ");
        MongoCollection<Document> collection = mongoDatabase.getCollection("entries");
        List<Entry> data = new ArrayList<>();
        for (Document doc : collection.find()) {
            Entry entry = new Entry();
            String description = doc.getString("description");
            String internalNotes = doc.getString("internalNotes");

            if (description.length() >= 56) {
                String[] words = description.split("\\s+"); // Split by whitespace
                StringBuilder firstNineWords = new StringBuilder();
                int wordLimit = Math.min(9, words.length); // Limit to 6 or the number of available words
                for (int i = 0; i < wordLimit; i++) {
                    firstNineWords.append(words[i]);
                    if (i < wordLimit - 1) {
                        firstNineWords.append(" ");
                    }
                }
                firstNineWords.append("...");
                entry.setDescription(firstNineWords.toString());
            } else {
                entry.setDescription(description);
            }
            entry.setCustomer(doc.getString("customer"));
            entry.setDateEntered(convertDate(doc.getDate("dateEntered")));
            entry.setHours(doc.getDouble("hours"));
            if (internalNotes.length() >= 56) {
                String[] words = internalNotes.split("\\s+"); // Split by whitespace
                StringBuilder firstNineWords = new StringBuilder();
                int wordLimit = Math.min(9, words.length); // Limit to 6 or the number of available words
                for (int i = 0; i < wordLimit; i++) {
                    firstNineWords.append(words[i]);
                    if (i < wordLimit - 1) {
                        firstNineWords.append(" ");
                    }
                }
                firstNineWords.append("...");
                entry.setInternalNotes(firstNineWords.toString());
            } else {
                entry.setInternalNotes(internalNotes);
            }
            data.add(entry);
        }
        return new CollectionRepository<>(data);
    }

    private static LocalDateTime convertDate(Date date) {
        return date.toInstant()
                   .atZone(ZoneId.systemDefault())
                   .toLocalDateTime();
    }

    public static LocalDateTime formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        String formatDate = date.format(formatter);
        return LocalDateTime.parse(formatDate);
    }
}
