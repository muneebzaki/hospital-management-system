package com.hospital.model;

import java.util.Date;

public class Patient {
    private int id;
    private String name;
    private Gender gender;
    private Date dateOfBirth;
    private String bloodGroup;
    private String contactNumber;
    private String address;
    private Date registrationDate;
    
    public enum Gender {
        MALE, FEMALE, OTHER
    }
    
    public Patient() {
    }
    
    public Patient(int id, String name, Gender gender, Date dateOfBirth, String bloodGroup, 
                  String contactNumber, String address, Date registrationDate) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.bloodGroup = bloodGroup;
        this.contactNumber = contactNumber;
        this.address = address;
        this.registrationDate = registrationDate;
    }
    
    public Patient(String name, Gender gender, Date dateOfBirth, String bloodGroup, 
                  String contactNumber, String address) {
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.bloodGroup = bloodGroup;
        this.contactNumber = contactNumber;
        this.address = address;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Gender getGender() {
        return gender;
    }
    
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getBloodGroup() {
        return bloodGroup;
    }
    
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Date getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }
} 