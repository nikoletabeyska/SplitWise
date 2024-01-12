package database.model;

import jakarta.persistence.*;
public class Main {
    public static void main(String[] args){
        User mihaela = new User();
        mihaela.setUserName("mihaela");
        mihaela.setPassword("12345");
        // persist information to the database here
    }
}
