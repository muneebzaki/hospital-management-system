package com.hospital.model;

public class Doctor {
    private int id;
    private int userId;
    private String name;
    private String specialization;
    private String contactNumber;
    private String email;
    
    public Doctor() {
    }
    
    public Doctor(int id, int userId, String name, String specialization, String contactNumber, String email) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.email = email;
    }
    
    public Doctor(int userId, String name, String specialization, String contactNumber, String email) {
        this.userId = userId;
        this.name = name;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.email = email;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }
} 