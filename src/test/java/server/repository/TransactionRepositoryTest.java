package server.repository;

import database.model.Moneyflow;
import database.model.User;
import jakarta.persistence.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
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

        String persistence_unit = "TestPersistenceUnit";
        entityManager = Persistence.createEntityManagerFactory(persistence_unit).createEntityManager();
        transactionRepository = new TransactionRepository(persistence_unit);


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
            entityManager.persist(new Moneyflow(mihinka,nikinka, 10, "Mock",true));
            entityManager.persist(new Moneyflow(mihinka,daninko, 10, "Mock",true));
            entityManager.persist(new Moneyflow(mihinka,daninko, 20, "Mock",true));
            entityManager.persist(new Moneyflow(mihinka,daninko, 10, "Mock",true));

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
       Map<User,Double> result = transactionRepository.getOwedMoneyFromIndividuals(userToGet);
        Map<User,Double> expected = new HashMap<User,Double>()
        {{
            put(PrimitiveUserGet("Nikinka"),10.0);
            put(PrimitiveUserGet("Daninko"),40.0);

        }};
        assertEquals(result,expected);
    }
    @Test
    void getOwedMoneyToIndividuals() {
        User userToGet = PrimitiveUserGet("Daninko");
        Map<User,Double> result = transactionRepository.getOwedMoneyToIndividuals(userToGet);
        Map<User,Double> expected = new HashMap<User,Double>()
        {{
            put(PrimitiveUserGet("Mihinka"),40.0);

        }};
        assertEquals(expected,result);
    }

    @Test
    void createTransaction() {
    }

    @Test
    void closeEntityManagerFactory() {
    }
}