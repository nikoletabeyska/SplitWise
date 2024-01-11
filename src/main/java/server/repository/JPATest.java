package server.repository;

import database.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

import java.util.List;

public class JPATest {

    private static final String PERSISTENCE_UNIT_NAME = "SplitWisePersistenceUnit";

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            System.out.println("Connected to the database!");

            // Insert a user
            insertUser(entityManager, "john_doe", "password123");

            // Retrieve and display all users
            displayAllUsers(entityManager);

        } finally {
            entityManager.close();
            entityManagerFactory.close();
        }
    }

    private static void insertUser(EntityManager entityManager, String username, String password) {
        try {
            entityManager.getTransaction().begin();

            User user = new User(username, password);

            entityManager.persist(user);

            entityManager.getTransaction().commit();

            System.out.println("User inserted successfully!");
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        }
    }

    private static void displayAllUsers(EntityManager entityManager) {
        try {
            Query query = entityManager.createQuery("SELECT u FROM User u", User.class);
            List<User> userList = query.getResultList();

            System.out.println("All Users:");
            for (User user : userList) {
                System.out.println(
                    ", Username: " + user.getUsername() +
                        ", Password: " + user.getPassword());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

