package server.repository;

import database.model.Group;
import database.model.Moneyflow;
import database.model.User;
import jakarta.persistence.*;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GroupRepositoryTest {

    private static GroupRepository groupRepository;
    private static EntityManager entityManager;
    private static Map<String, User> mockUsers;
    private static Map<String, Group> mockGroups;


    @BeforeAll
    static void setUp() {
        String persistence_unit = "TestPersistenceUnit";
        entityManager = Persistence.createEntityManagerFactory(persistence_unit).createEntityManager();
        groupRepository = new GroupRepository(entityManager);

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

    private User PrimitiveUserGet(String username)
    {
        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);


        try {
            return (User)query.getSingleResult();
        } catch (Exception e) {
            return null;
        }

    }

    @Test
    void testCreateGroup() {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            if (!transaction.isActive()) {
                transaction.begin();
            }

            Group group = new Group("TestGroup", List.of(PrimitiveUserGet("Mihinka"), PrimitiveUserGet("Tedinko"),PrimitiveUserGet("Daninko")));
            groupRepository.createGroup(group);

            transaction.commit();

            // Check if the group is persisted
            Group retrievedGroup = entityManager.find(Group.class, group.getId());
            assertEquals(group, retrievedGroup);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Test
    void testGetAllGroups() {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            if (!transaction.isActive()) {
                transaction.begin();
            }

            Group group1 = new Group("Group1", List.of(PrimitiveUserGet("Mihinka"),PrimitiveUserGet("Tedinko"),PrimitiveUserGet("Nikinko")));
            Group group2 = new Group("Group2", List.of(PrimitiveUserGet("Mihinka"), PrimitiveUserGet("Tedinko"), PrimitiveUserGet("Nikinko")));
            entityManager.persist(group1);
            entityManager.persist(group2);

            List<Group> groups = groupRepository.getAllGroups(PrimitiveUserGet("Nikinko"));

            transaction.commit();

            // Check if the user is a member of all groups
            assertEquals(2, groups.size());
            assertTrue(groups.contains(group1));
            assertTrue(groups.contains(group2));
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