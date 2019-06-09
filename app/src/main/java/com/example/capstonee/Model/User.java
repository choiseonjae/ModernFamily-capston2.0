package com.example.capstonee.Model;

public class User {
    private String Phone;
    private String Name;
    private String Password;
    private String BirthDate;
    private int FamilyCount;
    private int FamilyCount2;
    private String FamilyID, FamilyID1, FamilyID2, FamilyID3;
    private String profileUri;
    private boolean Visible;
    private int Default_family;

    private User() {
    }

    public User(String phone, String name, String password, String birthDate, int familyCount, int familyCount2, String familyID, String familyID1, String familyID2, String familyID3, int default_family, boolean visible) {
        Phone = phone;
        Name = name;
        Password = password;
        BirthDate = birthDate;
        FamilyCount = familyCount;
        FamilyCount2 = familyCount2;
        FamilyID = familyID;
        profileUri = "";
        FamilyID1 = familyID1;
        FamilyID2 = familyID2;
        FamilyID3 = familyID3;
        Default_family = default_family;
        Visible = visible;
    }

    public boolean isVisible() {
        return Visible;
    }

    public void setVisible(boolean visible) {
        Visible = visible;
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

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String birthDate) {
        BirthDate = birthDate;
    }

    public int getFamilyCount() {
        return FamilyCount;
    }

    public void setFamilyCount(int familyCount) {
        FamilyCount = familyCount;
    }

    public int getFamilyCount2() {
        return FamilyCount2;
    }

    public void setFamilyCount2(int familyCount2) {
        FamilyCount2 = familyCount2;
    }

    public String getFamilyID() {
        return FamilyID;
    }

    public void setFamilyID(String familyID) {
        FamilyID = familyID;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
    }

    public String getFamilyID2() {
        return FamilyID2;
    }

    public void setFamilyID2(String familyID2) {
        FamilyID2 = familyID2;
    }

    public String getFamilyID3() {
        return FamilyID3;
    }

    public void setFamilyID3(String familyID3) {
        FamilyID3 = familyID3;
    }

    public int getDefault_family() {
        return Default_family;
    }

    public void setDefault_family(int default_family) {
        Default_family = default_family;
    }

    public String getFamilyID1() {
        return FamilyID1;
    }

    public void setFamilyID1(String familyID1) {
        FamilyID1 = familyID1;
    }
}
