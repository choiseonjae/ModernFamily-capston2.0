package com.example.capstonee.Model;

public class User {
    private String Phone;
    private String Name;
    private String Password;
    private String BirthDate;
    private Boolean Visited;
    private String FamilyID;

    private User(){}

    public User(String phone, String name, String password, String birthDate, Boolean visited, String familyID) {
        Phone = phone;
        Name = name;
        Password = password;
        BirthDate = birthDate;
        Visited = visited;
        FamilyID = familyID;
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

    public Boolean getVisited() { return Visited; }

    public void setVisited(Boolean visited) { Visited = visited; }

    public String getFamilyID() { return FamilyID; }

    public void setFamilyID(String familyID) { FamilyID = familyID; }
}
