package server.services;

import database.model.Friendship;
import database.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import server.ClassesInitializer;
import server.RepositoryImplementationMapping;
import server.repository.FriendshipRepository;
import server.repository.GroupRepository;
import server.repository.TransactionRepository;
import server.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FriendshipServiceTest {

    private UserRepository userRepositoryMock;
    private FriendshipRepository friendshipRepositoryMock;
    private FriendshipService friendshipService;
    private static EntityManager entityManager;
    @BeforeAll
    void setUp() {
        userRepositoryMock = Mockito.mock(UserRepository.class);
        friendshipRepositoryMock = Mockito.mock(FriendshipRepository.class);
        String persistence_unit = "TestPersistenceUnit";
        entityManager = Persistence.createEntityManagerFactory(persistence_unit).createEntityManager();
        ClassesInitializer initializer = new ClassesInitializer(entityManager);
        initializer.setUserRepository(userRepositoryMock);
        initializer.setFriendshipRepository(friendshipRepositoryMock);
        friendshipService = new FriendshipService(initializer);
    }


    @Test
    void testAddFriend_Success() {

        String userUsername = "niki";
        String friendUsername = "mihi";

        User mockUser = new User();
        Mockito.when(userRepositoryMock.getUserByUsername(userUsername)).thenReturn(mockUser);

        User mockFriend = new User();
        Mockito.when(userRepositoryMock.getUserByUsername(friendUsername)).thenReturn(mockFriend);
        Mockito.doNothing().when(friendshipRepositoryMock).addNewFriendship(Mockito.any());
        String result = friendshipService.addFriend(userUsername, friendUsername);

        assertEquals("User mihi has been successfully added to your friends list.", result);
        //verify calling this method 2 times
        Mockito.verify(userRepositoryMock, Mockito.times(2)).getUserByUsername(Mockito.anyString());
        Mockito.verify(friendshipRepositoryMock, Mockito.times(1)).addNewFriendship(Mockito.any());

    }

    @Test
    void testAddFriend_UserNotFound() {
        Mockito.when(userRepositoryMock.getUserByUsername(Mockito.anyString())).thenReturn(null);

        String result = friendshipService.addFriend("mihi", "niki");

        assertEquals("User with username niki does not exist.", result);
        Mockito.verifyNoInteractions(friendshipRepositoryMock);
    }

    @Test
    void testGetAllFriendsList() {
        // Arrange
        String userUsername = "mihi";
        User mockUser = new User();
        mockUser.setUserName(userUsername);
        List<Friendship> friendshipList = new ArrayList<>();
        Friendship friendship1 = new Friendship(mockUser, new User("niki", "hehe"));
        Friendship friendship2 = new Friendship(mockUser, new User("tedi", "hehe"));

        friendshipList.add(friendship1);
        friendshipList.add(friendship2);

        Mockito.when(userRepositoryMock.getUserByUsername(userUsername)).thenReturn(mockUser);
        Mockito.when(friendshipRepositoryMock.getAllFriendships(mockUser)).thenReturn(friendshipList);

        String result = friendshipService.getAllFriendsList(userUsername);

        Assert.assertEquals("Friends: \nniki\ntedi\n" ,result);
    }
    
}
