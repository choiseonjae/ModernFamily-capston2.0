package com.example.capstonee.Model;

public class Login {
    private static String MY_ID;
    private static String MY_NAME;
    private static String MY_PASSWORD;
    private static String MY_PHONE;

    private static class LoginHolder{
        static final Login INSTANCE = new Login();
    }
    public static Login getInstance(){
        return Login.LoginHolder.INSTANCE;
    }
    public static String getUserID(){ return MY_ID; }
    public static String getUserName(){ return MY_NAME; }
    public static String getUserPassword(){ return MY_PASSWORD; }
    public static String getUserPhone(){ return MY_PHONE; }
    public static void setID(String ID){
        MY_ID = ID;
    }
    public static void setName(String NAME) { MY_NAME = NAME;}
    public static void setPassword(String PASSWORD){ MY_PASSWORD = PASSWORD; }
    public static void setPhone(String PHONE){ MY_PHONE = PHONE; }
}
