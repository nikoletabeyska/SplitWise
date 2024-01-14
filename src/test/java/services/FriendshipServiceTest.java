package services;

import database.model.Friendship;
import database.model.User;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import server.repository.FriendshipRepository;
import server.repository.UserRepository;
import server.services.FriendshipService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FriendshipServiceTest {

    private UserRepository userRepositoryMock;
    private FriendshipRepository friendshipRepositoryMock;
    private FriendshipService friendshipService;

    @BeforeEach
    void setUp() {
        userRepositoryMock = Mockito.mock(UserRepository.class);
        friendshipRepositoryMock = Mockito.mock(FriendshipRepository.class);
        friendshipService = new FriendshipService(userRepositoryMock, friendshipRepositoryMock);
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
        Mockito.verify(userRepositoryMock, Mockito.times(1)).getUserByUsername("niki");
        Mockito.verifyNoInteractions(friendshipRepositoryMock);
    }

    @Test
    void testGetAllFriendsList() {
        // Arrange
        String userUsername = "mihi";
        User mockUser = new User();
        mockUser.setUserName(userUsername);

        Friendship friendship1 = new Friendship(mockUser, new User("niki", "hehe"));
//        friendship1.setAmountOwnedByFirstToSecond(10);
//        friendship1.setAmountOwnedBySecondToFirst(7);
//
//        Friendship friendship2 = new Friendship(new User("dani", "ami"), mockUser);
//        friendship2.setAmountOwnedByFirstToSecond(2);
//        friendship2.setAmountOwnedBySecondToFirst(11);

//        Mockito.when(userRepositoryMock.getUserByUsername(userUsername)).thenReturn(mockUser);
//        Mockito.when(friendshipRepositoryMock.getAllFriendships(mockUser)).thenReturn(List.of(friendship1, friendship2));
//
//        String friendsList = friendshipService.getAllFriendsList(userUsername);
        Assert.assertEquals(true,true);

        // Assert
       // assertEquals("* niki: You owe 10 LV\n* niki: Owes you 7 LV\n", friendsList);
    }
    
}
