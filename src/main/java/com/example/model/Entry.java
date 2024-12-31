package com.example.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.basis.bbj.web.gwt.server.message.decomposergen.grid_GridSetStringsMessageDecomposer;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class Entry {
    @NotEmpty(message = "Please enter a description of the entry.")
    // For string fields, use NotEmpty
    String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    @NotNull(message = "Please enter the number of hours spent.")
    @Positive(message = "Invalid number.")
    Double hours;
    public Double getHours() {
        return hours;
    }
    public void setHours(Double hours) {
        this.hours = hours;
    }

    @NotNull(message = "Please select a customer.")
    String customer;
    public String getCustomer() {
        return customer;
    }
    public void setCustomer(String customer) {
        this.customer = customer;
    }

    Boolean billable;
    public Boolean getBillable() {
        return billable;
    }
    public void setBillable(Boolean billable) {
        this.billable = billable;
    }

    Boolean discounted;
    public Boolean getDiscounted() {
        return discounted;
    }
    public void setDiscounted(Boolean discounted) {
        this.discounted = discounted;
    }

    @NotEmpty(message = "Please enter some internal notes.")
    String internalNotes;
    public String getInternalNotes() {
        return internalNotes;
    }
    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    LocalDateTime dateEntered;
    public LocalDateTime getDateEntered() {
        return dateEntered;
    }
    public void setDateEntered(LocalDateTime dateEntered) {
        this.dateEntered = dateEntered;
    }
}
