package database.model;


import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
class UserTest {
    User testUser = new User("mih1711","12345678");
    User blankUser = new User();
    @org.junit.jupiter.api.Test
    void testGetUsername() {
        assertEquals("mih1711",testUser.getUsername(),"user.getUsername() works as expected!");
        //Pair<Double,String> expense = new Pair<Double,String>(12.0,"Tony");
        //ArrayList<Pair<Double,String>> listOfExpenses = new ArrayList<Pair<Double,String>>();
        //listOfExpenses.add(expense);

    }

    @org.junit.jupiter.api.Test
    void getPassword() {
        assertEquals("12345678",testUser.getPassword(),"user.getPassword() works as expected!");
    }

    @org.junit.jupiter.api.Test
    void setUserName() {
        blankUser.setUserName("blank");
        assertEquals("blank",blankUser.getUsername(),"user.setUsername() works as expected!");
    }

    @org.junit.jupiter.api.Test
    void setPassword() {
        blankUser.setPassword("12345");
        assertEquals("12345",blankUser.getPassword(),"user.setPassword() works as expected!");

    }
}