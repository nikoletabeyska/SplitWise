
package server.repository;

import database.model.Group;
import database.model.Moneyflow;
import database.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.services.Logger;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GroupRepositoryTest {

    private GroupRepository groupRepository;
    private EntityManager entityManager;
    private EntityTransaction transaction;

    private Map<String, User> mockUsers;
    @BeforeEach
    void setUp() {

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

    @Test
    void createGroup_Success() {
        Group group = new Group("TestGroup", Collections.emptyList());
        when(entityManager.getTransaction()).thenReturn(transaction);
        groupRepository.createGroup(group);

        verify(transaction, times(1)).begin();
        verify(entityManager, times(1)).persist(group);
        verify(transaction, times(1)).commit();
        verify(transaction, never()).rollback();
    }

    @Test
    void createGroup_Failure() {
        // Given
        Group group = new Group("TestGroup", Collections.emptyList());
        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException("Simulating an error")).when(entityManager).persist(group);

        // When
        groupRepository.createGroup(group);

        // Then
        verify(transaction, times(1)).begin();
        verify(entityManager, times(1)).persist(group);
        verify(transaction, times(1)).rollback();
        //verify(logger, times(1)).logError(anyString(), any(Throwable.class));
    }

    @Test
    void getAllGroups_Success() {
        List<Group> groups = groupRepository.getAllGroups(mockUsers.get("Mihinka"));
        assertEquals(1, groups.size());

    }

    @Test
    void getAllGroups_Failure() {
        List<Group> groups = groupRepository.getAllGroups(mockUsers.get("Daninko"));
        assertEquals(0, groups.size());
    }
}
