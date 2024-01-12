package database.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FriendshipTest {

    Friendship mihiAndNiki = new Friendship(1,2,10.35);
    Friendship nikiAndMihi = new Friendship(5,6,10.35);
    Friendship testFriendship = new Friendship(3,4);

    Friendship oneAndTwo = new Friendship(7,8,10.35);

    @Test
    void testGetAmountOwnedByFirstToSecond() {
        assertEquals(10.35,mihiAndNiki.getAmountOwnedByFirstToSecond());
    }

    @Test
    void getAmountOwnedBySecondToFirst() {
        assertEquals(-10.35,mihiAndNiki.getAmountOwnedBySecondToFirst());
    }

    @Test
    void setAmountOwnedByFirstToSecond() {
        testFriendship.setAmountOwnedByFirstToSecond(5);
        assertEquals(5,testFriendship.getAmountOwnedByFirstToSecond());
    }

    @Test
    void setAmountOwnedBySecondToFirst() {
        testFriendship.setAmountOwnedBySecondToFirst(5);
        assertEquals(5,testFriendship.getAmountOwnedBySecondToFirst());
    }

    @Test
    void giveMoneyForSecond() {
        nikiAndMihi.giveMoneyForSecond(5);
        assertEquals(5.35,nikiAndMihi.getAmountOwnedByFirstToSecond());
    }

    @Test
    void giveMoneyForFirst() {
        oneAndTwo.giveMoneyForFirst(5);
        assertEquals(15.35,oneAndTwo.getAmountOwnedByFirstToSecond());
    }
}