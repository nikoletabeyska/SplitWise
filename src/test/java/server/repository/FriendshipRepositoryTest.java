package server.repository;

import database.model.Friendship;
import database.model.Group;
import database.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.services.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FriendshipRepositoryTest {

        private static FriendshipRepository friendshipRepository;
        private static EntityManager entityManager;
        private static Map<String, User> mockUsers;

        @BeforeAll
        static void setUp() {
            String persistence_unit = "TestPersistenceUnit";
            entityManager = Persistence.createEntityManagerFactory(persistence_unit).createEntityManager();
            friendshipRepository = new FriendshipRepository(entityManager);

            mockUsers = new HashMap<>()
            {{
                put("Mihinka", new User("Mihinka","12345"));
                put("Nikinka", new User("Nikinka","12345"));
                put("Tedinko", new User("Tedinko","12345"));
                put("Daninko", new User("Daninko","12345"));
            }};

            EntityTransaction transaction = null;
            try {
                transaction = entityManager.getTransaction();
                transaction.begin();
                entityManager.persist(mockUsers.get("Mihinka"));
                entityManager.persist(mockUsers.get("Tedinko"));
                entityManager.persist(mockUsers.get("Daninko"));
                entityManager.persist(mockUsers.get("Nikinka"));

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                e.printStackTrace();
            }

        }

        private User PrimitiveUserGet(String username) {
            Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);

            try {
                return (User)query.getSingleResult();
            } catch (Exception e) {
                return null;
            }

        }

    @Test
    void testCreateFriendship() {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            if (!transaction.isActive()) {
                transaction.begin();
            }

            User user1 = PrimitiveUserGet("Mihinka");
            User user2 = PrimitiveUserGet("Nikinka");

            Friendship createdFriendship = friendshipRepository.createNewFriendship(user1, user2);
            friendshipRepository.addNewFriendship(createdFriendship);

            transaction.commit();

            Friendship retrievedFriendship = entityManager.find(Friendship.class, createdFriendship.getId());
            assertEquals(createdFriendship, retrievedFriendship);

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Test
    void testGetAllFriendships() {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            if (!transaction.isActive()) {
                transaction.begin();
            }


            User user1 = PrimitiveUserGet("Tedinko");
            User user2 = PrimitiveUserGet("Mihinka");
            User user3 = PrimitiveUserGet("Nikinka");

            Friendship createdFriendship1 = friendshipRepository.createNewFriendship(user1, user2);
            Friendship createdFriendship2 = friendshipRepository.createNewFriendship(user1, user3);
            entityManager.persist(createdFriendship1);
            entityManager.persist(createdFriendship2);

            List<Friendship> friendships = friendshipRepository.getAllFriendships(user1);

            transaction.commit();

            // Check if the user is a member of all groups
            assertEquals(2, friendships.size());
            assertTrue(friendships.contains(createdFriendship1));
            assertTrue(friendships.contains(createdFriendship2));
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }
    }

}