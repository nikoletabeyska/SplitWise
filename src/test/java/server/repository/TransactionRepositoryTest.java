package server.repository;

import database.model.Moneyflow;
import database.model.User;
import jakarta.persistence.*;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import org.mockito.Mockito;
import server.services.FriendshipService;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRepositoryTest {

    static private TransactionRepository transactionRepository;
    static private EntityManager entityManager;
    @BeforeAll
    static void setUp() {

        entityManager = Persistence.createEntityManagerFactory("TestPersistenceUnit").createEntityManager();

        transactionRepository = new TransactionRepository();


        //Create
        User mihinka = new User("Mihinka","12345");
        User nikinka = new User("Nikinka","12345");
        User tedinko = new User("Tedinko","12345");
        User daninko = new User("Daninko","12345");
        EntityTransaction transaction=null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            entityManager.persist(mihinka);
            entityManager.persist(nikinka);
            entityManager.persist(tedinko);
            entityManager.persist(daninko);



            //Create mock transactions
            entityManager.persist(new Moneyflow(mihinka,nikinka, 10, "Mock"));
            entityManager.persist(new Moneyflow(mihinka,daninko, 10, "Mock"));
            entityManager.persist(new Moneyflow(mihinka,daninko, 20, "Mock"));
            entityManager.persist(new Moneyflow(mihinka,daninko, 10, "Mock"));

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

    }


    private User PrimitiveUserGet(String username)
    {
        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);


        try {
            return (User)query.getSingleResult();
        } catch (Exception e) {
            // Handle the case when no result is found
            return null;
        }

    }
    @Test
    void getAllTransactions() {

    }

    @Test
    void getOwedMoneyFromIndividuals() {
        User userToGet = PrimitiveUserGet("Mihinka");
       List<Object[]> result = transactionRepository.getOwedMoneyFromIndividuals(userToGet);
       int a =3;
    }

    @Test
    void createTransaction() {
    }

    @Test
    void closeEntityManagerFactory() {
    }
}