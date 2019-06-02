package com.example.capstonee.Model;

import java.util.HashMap;

public class Login {
    private static int familyCount = 1;
    private static String MY_ID;
    private static String MY_NAME;
    private static String MY_PASSWORD;
    private static String MY_PHONE;
    private static boolean MY_VISIT;
    private static String MY_BIRTH;
    private static String MY_FAMILYID;
    // 아빠, 엄마, 나, 동생이 있으면 각자 숫자를 매겨줄 필요가 있음
    // 왜냐면 리눅스에서 한글명으로 저장이 안 되는데, 어플에선 입력값을 한글로 받아야 함.
    // 그러니 영어를 사용하지 않으므로 영어로 파일 저장은 무리가 있고
    // 숫자로 대신
    private static HashMap<Integer, String> MY_REL = new HashMap<Integer, String>();

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
    public static boolean getUserVisit(){ return MY_VISIT; }
    public static String getUserBirth(){ return MY_BIRTH; }
    public static String getUserFamilyID(){ return MY_FAMILYID; }
    public static HashMap<Integer, String> getHashMap() { return MY_REL; }
    public static int getFCount(){ return familyCount++; }
    public static void setID(String ID){ MY_ID = ID; }
    public static void setName(String NAME) { MY_NAME = NAME;}
    public static void setPassword(String PASSWORD){ MY_PASSWORD = PASSWORD; }
    public static void setPhone(String PHONE){ MY_PHONE = PHONE; }
    public static void setVisit(Boolean VISIT){ MY_VISIT = VISIT; }
    public static void setBirth(String BIRTH){ MY_BIRTH = BIRTH; }
    public static void setFamilyID(String FAMILYID){ MY_FAMILYID = FAMILYID; }
}
