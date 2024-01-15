
package server.repository;

import database.model.Group;
import database.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.services.Logger;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GroupRepositoryTest {

    private GroupRepository groupRepository;
    private EntityManager entityManager;
    private EntityTransaction transaction;
    private Logger logger;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        transaction = mock(EntityTransaction.class);

        groupRepository = new GroupRepository("testRepo");
        logger = new Logger();


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
        verify(logger, times(1)).logError(anyString(), any(Throwable.class));
    }

    @Test
    void getAllGroups_Success() {
        User user = new User("testUser", "1");
        Query query = mock(Query.class);

        when(entityManager.createQuery(any(String.class), eq(Group.class))).thenReturn((TypedQuery<Group>) query);
        when(query.setParameter(any(String.class), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(new Group("TestGroup", Collections.emptyList())));

        List<Group> groups = groupRepository.getAllGroups(user);
        assertEquals(1, groups.size());
        verify(entityManager, times(1)).createQuery(any(String.class), eq(Group.class));
        verify(query, times(1)).setParameter(any(String.class), eq(user.getUsername()));
        verify(query, times(1)).getResultList();
        verify(logger, never()).logError(anyString(), any(Throwable.class));
    }

    @Test
    void getAllGroups_Failure() {
        User user = new User("testUser", "1");
        Query query = mock(Query.class);

        when(entityManager.createQuery(any(String.class), eq(Group.class))).thenReturn((TypedQuery<Group>) query);
        when(query.setParameter(any(String.class), any())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Simulating an error"));

        List<Group> groups = groupRepository.getAllGroups(user);

        assertEquals(0, groups.size());
        verify(entityManager, times(1)).createQuery(any(String.class), eq(Group.class));
        verify(query, times(1)).setParameter(any(String.class), eq(user.getUsername()));
        verify(query, times(1)).getResultList();
        verify(logger, times(1)).logError(anyString(), any(Throwable.class));
    }
}
