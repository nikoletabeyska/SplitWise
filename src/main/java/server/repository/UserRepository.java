package server.repository;

import database.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import server.Server;
import server.services.Logger;

import java.util.List;


public class UserRepository extends RepositoryBase {

    public UserRepository(EntityManager manager) {
        super(manager);
    }

    public void createUser(User user) {
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();

            manager.persist(user);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            Logger.logError("An error occured  ", e);
            e.printStackTrace();
        }
    }

    public User getUserById(Long userId) {
        User user = null;

        try {
            //retrive entity from the database based on its primary key
            user = manager.find(User.class, userId);
        } catch (Exception e) {
            Logger.logError("An error occured  ", e);
            e.printStackTrace();
        }
        return user;
    }

    public User getUserByUsername(String username) {
        User user = null;

        try {
            Query query = manager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);

            List<User> resultList = query.getResultList();
            if (!resultList.isEmpty()) {
                user = resultList.get(0); // Assuming username is unique
            }
        } catch (Exception e) {
            Logger.logError("An error occured  ", e);
            e.printStackTrace();
        }
        return user;
    }

    public List<User> getAllUsers() {
        List<User> userList = null;

        try {
            Query query = manager.createQuery("SELECT u FROM User u", User.class);
            userList = query.getResultList();
        } catch (Exception e) {
            Logger.logError("An error occured  ", e);
            e.printStackTrace();
        }

        return userList;
    }

    public void closeEntityManagerFactory() {
    }
}
