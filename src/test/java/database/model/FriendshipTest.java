package database.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FriendshipTest {
    User mihaela = new User("mihaela1","12345");
    User nikoleta = new User("nikoletab","12345");

    User testUser1 = new User("testUser1","12345");
    User testUser2 = new User("testUser2","12345");

    Friendship mihiAndNiki = new Friendship(mihaela,nikoleta,10.35);
    Friendship nikiAndMihi = new Friendship(nikoleta,mihaela,10.35);

    Friendship testFriendship = new Friendship(testUser2,testUser1);

    Friendship oneAndTwo = new Friendship(testUser1,testUser2,10.35);

    @Test
    void testGetAmountOwnedByFirstToSecond() {
       // assertEquals(10.35,mihiAndNiki.getAmountOwnedByFirstToSecond());
    }

    @Test
    void getAmountOwnedBySecondToFirst() {
        //assertEquals(-10.35,mihiAndNiki.getAmountOwnedBySecondToFirst());
    }

    @Test
    void setAmountOwnedByFirstToSecond() {
        //testFriendship.setAmountOwnedByFirstToSecond(5);
        //assertEquals(5,testFriendship.getAmountOwnedByFirstToSecond());
    }

    @Test
    void setAmountOwnedBySecondToFirst() {
        //testFriendship.setAmountOwnedBySecondToFirst(5);
        //assertEquals(5,testFriendship.getAmountOwnedBySecondToFirst());
    }

    @Test
    void giveMoneyForSecond() {
        //nikiAndMihi.giveMoneyForSecond(5);
        //assertEquals(5.35,nikiAndMihi.getAmountOwnedByFirstToSecond());
    }

    @Test
    void giveMoneyForFirst() {
        //oneAndTwo.giveMoneyForFirst(5);
       // assertEquals(15.35,oneAndTwo.getAmountOwnedByFirstToSecond());
    }
}