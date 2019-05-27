package com.example.capstonee.Model;

public class Login {
    private static String MY_ID;
    private static class LoginHolder{
        static final Login INSTANCE = new Login();
    }
    public static Login getID(){
        return Login.LoginHolder.INSTANCE;
    }
    public static String getUserID(){ return MY_ID; }
    public static void setID(String ID){
        MY_ID = ID;
    }
}
