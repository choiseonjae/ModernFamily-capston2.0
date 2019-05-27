package com.example.capstonee.Model;

public class User {
    private String Id;
    private String Phone;
    private String Name;
    private String Password;
    private String BirthDate;

    private User(){}

    public User(String id, String phone, String name, String password, String birthDate) {
        Id = id;
        Phone = phone;
        Name = name;
        Password = password;
        BirthDate = birthDate;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() { return Phone; }

    public void setPhone(String phone) { Phone = phone; }

    public String getBirthDate() { return BirthDate; }

    public void setBirthDate(String birthDate) { BirthDate = birthDate; }
}
