package database.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FriendshipTest {
    User mihaela = new User("mihaela1","12345");
    User nikoleta = new User("nikoletab","12345");

    User testUser1 = new User("testUser1","12345");
    User testUser2 = new User("testUser2","12345");
    Friendship testFriendship = new Friendship(testUser1,testUser2);

    @Test
    void getFirstFriend() {
        assertEquals(testUser1,testFriendship.getFirstFriend());
    }

    @Test
    void getSecondFriend() {
        assertEquals(testUser2,testFriendship.getSecondFriend());
    }
}