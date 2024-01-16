package server.repository;

import database.model.*;
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
import server.services.Logger;

import static javax.swing.UIManager.put;
import static org.junit.jupiter.api.Assertions.*;

class TransactionRepositoryTest {

    static private TransactionRepository transactionRepository;
    static private EntityManager entityManager;
    private static Map<String, User> mockUsers;
    @BeforeAll
    static void setUp() {

        String persistence_unit = "TestPersistenceUnit";
        entityManager = Persistence.createEntityManagerFactory(persistence_unit).createEntityManager();
        transactionRepository = new TransactionRepository(entityManager);
         mockUsers = new HashMap<>()
                {{
                        put("Mihinka", new User("Mihinka","12345"));
                        put("Nikinka", new User("Nikinka","12345"));
                        put("Tedinko", new User("Tedinko","12345"));
                        put("Daninko", new User("Daninko","12345"));
                }};
        //Create

        EntityTransaction transaction=null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            entityManager.persist(mockUsers.get("Mihinka"));
            entityManager.persist(mockUsers.get("Tedinko"));
            entityManager.persist(mockUsers.get("Daninko"));
            entityManager.persist(mockUsers.get("Nikinka"));

            Moneyflow m1 = new Moneyflow(mockUsers.get("Mihinka"),mockUsers.get("Nikinka"), 10, "Mock",true);
            Moneyflow m2 = new Moneyflow(mockUsers.get("Mihinka"),mockUsers.get("Daninko"), 10, "Mock",true);
            Moneyflow m3 = new Moneyflow(mockUsers.get("Mihinka"),mockUsers.get("Daninko"), 20, "Mock",true);
            Moneyflow m4 = new Moneyflow(mockUsers.get("Mihinka"),mockUsers.get("Daninko"), 10, "Mock",true);


            //Create mock transactions
            entityManager.persist(m1);
            entityManager.persist(m2);
            entityManager.persist(m3);
            entityManager.persist(m4);

            ArrayList<User> ourGroup = new ArrayList<>();
            ourGroup.add(mockUsers.get("Mihinka"));
            ourGroup.add(mockUsers.get("Nikinka"));
            ourGroup.add(mockUsers.get("Tedinko"));
            entityManager.persist(new Group("ourGroup",ourGroup));

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
    void testGetOwedMoneyFromIndividuals() {
        User userToGet = PrimitiveUserGet("Mihinka");
       Map<User,Double> result = transactionRepository.getOwedMoneyFrom(userToGet,null);
        Map<User,Double> expected = new HashMap<User,Double>()
        {{
            put(PrimitiveUserGet("Nikinka"),10.0);
            put(PrimitiveUserGet("Daninko"),40.0);

        }};
        assertEquals(result,expected);
    }
    @Test
    void testGetOwedMoneyToIndividuals() {
        User userToGet = PrimitiveUserGet("Daninko");
        Map<User,Double> result = transactionRepository.getOwedMoneyTo(userToGet,null);
        Map<User,Double> expected = new HashMap<User,Double>()
        {{
            put(PrimitiveUserGet("Mihinka"),40.0);

        }};
        assertEquals(expected,result);
    }
    @Test
    void testGetWantedGroupMembers() {

        ArrayList<User> ourGroup = new ArrayList<>();
        ourGroup.add(mockUsers.get("Mihinka"));
        ourGroup.add(mockUsers.get("Nikinka"));
        ourGroup.add(mockUsers.get("Tedinko"));

        ArrayList<User> result = (ArrayList<User>) transactionRepository.getWantedGroupMembers("ourGroup");
        boolean isDifferent=false;
       for (int i=0;i<ourGroup.size();i++){
           if(!ourGroup.get(i).equals(result.get(i))){
               isDifferent=true;
           }
       }
       assertEquals(false,isDifferent);
    }
}