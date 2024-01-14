package server.repository;

import database.model.Moneyflow;
import database.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;


public class TransactionRepository {

    private static final String PERSISTENCE_UNIT_NAME = "SplitWisePersistenceUnit";

    private final EntityManagerFactory entityManagerFactory;

    public TransactionRepository() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    public List<Moneyflow> getAllTransactions(User user)
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            Query query = entityManager.createQuery("SELECT t FROM Moneyflow t WHERE t.firstFriend = :username or t.secondFriend = :username", User.class);
            query.setParameter("username", user.getUsername());

            List<Moneyflow> relatedTransactions = query.getResultList();
            return relatedTransactions;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return null;
    }


    //Get all active transactions to or from  another user
    // that are NOT from a group and sums their amounts
    //Positive amounts means that money is being received, negative means money is owed
    public List<Object[]> getOwedMoneyFromIndividuals(User user)
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            Query query = entityManager.createQuery(
                    "SELECT t.giver, avg(t.amount) FROM Moneyflow t WHERE t.taker = :username", User.class);
            query.setParameter("username", user.getUsername());

            List<Object[]> summedTransactions = query.getResultList();
            for (Object[] o : summedTransactions)
            {
                User from = (User)o[0];
                Double average = (Double) o[1];
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return null;
    }
    public void createTransaction(Moneyflow moneyflowTransaction) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(moneyflowTransaction);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }
    public void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
