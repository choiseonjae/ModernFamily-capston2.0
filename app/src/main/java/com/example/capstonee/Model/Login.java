package com.example.capstonee.Model;

public class Login {
    private static String MY_ID;
    private static String MY_NAME;
    private static String MY_PASSWORD;
    private static String MY_PHONE;
    private static int MY_FAMILYCOUNT, MY_FAMILYCOUNT2, MY_DEFAULT_FAMILY;
    private static String MY_BIRTH;
    private static String MY_FAMILYID, MY_FAMILYID1, MY_FAMILYID2, MY_FAMILYID3;
    private static String MY_PROFILE_URI;
    private static boolean MY_VISIBLE;

    private static class LoginHolder {
        static final Login INSTANCE = new Login();
    }

    public static Login getInstance() {
        return Login.LoginHolder.INSTANCE;
    }

    public static String getUserID() {
        return MY_ID;
    }

    public static String getUserName() {
        return MY_NAME;
    }

    public static String getUserPassword() {
        return MY_PASSWORD;
    }

    public static String getUserPhone() {
        return MY_PHONE;
    }

    public static int getUserFamilyCount() {
        return MY_FAMILYCOUNT;
    }

    public static int getUserFamilyCount2() { return MY_FAMILYCOUNT2; }

    public static int getUserDefaultFamily() {
        return MY_DEFAULT_FAMILY;
    }

    public static String getUserBirth() {
        return MY_BIRTH;
    }

    public static String getUserFamilyID() {
        return MY_FAMILYID;
    }

    public static String getUserFamilyID1() {
        return MY_FAMILYID1;
    }

    public static String getUserFamilyID2() {
        return MY_FAMILYID2;
    }

    public static String getUserFamilyID3() {
        return MY_FAMILYID3;
    }

    public static boolean getUserVisible() {
        return MY_VISIBLE;
    }

    public static void setID(String ID) {
        MY_ID = ID;
    }

    public static void setName(String NAME) {
        MY_NAME = NAME;
    }

    public static void setPassword(String PASSWORD) {
        MY_PASSWORD = PASSWORD;
    }

    public static void setPhone(String PHONE) {
        MY_PHONE = PHONE;
    }

    public static void setBirth(String BIRTH) {
        MY_BIRTH = BIRTH;
    }

    public static void setFamilyCount(int FAMILYCOUNT) {
        MY_FAMILYCOUNT = FAMILYCOUNT;
    }

    public static void setFamilyCount2(int FAMILYCOUNT) { MY_FAMILYCOUNT2 = FAMILYCOUNT; }

    public static void setDefaultFamily(int DEFAULT_FAMILY) {
        MY_DEFAULT_FAMILY = DEFAULT_FAMILY;
        Infomation.getDatabase("User").child(MY_ID).child("Default_family").setValue(DEFAULT_FAMILY);
    }

    public static void setFamilyID(String FAMILYID) {
        MY_FAMILYID = FAMILYID;
        Infomation.getDatabase("User").child(MY_ID).child("familyID").setValue(FAMILYID);
    }

    public static void setFamilyID1(String FAMILYID1) {
        MY_FAMILYID1 = FAMILYID1;
        Infomation.getDatabase("User").child(MY_ID).child("familyID1").setValue(FAMILYID1);
    }

    public static void setFamilyID2(String FAMILYID2) {
        MY_FAMILYID2 = FAMILYID2;
        Infomation.getDatabase("User").child(MY_ID).child("familyID2").setValue(FAMILYID2);
    }

    public static void setFamilyID3(String FAMILYID3) {
        MY_FAMILYID3 = FAMILYID3;
        Infomation.getDatabase("User").child(MY_ID).child("familyID3").setValue(FAMILYID3);
    }

    public static void setVisible(Boolean VISIBLE){
        MY_VISIBLE = VISIBLE;
    }


    public static String getProfileUri() {
        return MY_PROFILE_URI;
    }

    public static void setProfileUri(String myProfileUri) {
        MY_PROFILE_URI = myProfileUri;
    }
}
