package database.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupTest {
    User mihaela = new User("mihaela1","12345");
    User nikoleta = new User("nikoletab","12345");
    User parichko = new User("cashgiver","12345");
    User leech = new User("bigleech","12345");

    List<User> membersOfGroup = Arrays.asList(mihaela, nikoleta, parichko, leech);
    List<User> membersOfNonFullGroup = Arrays.asList(mihaela, nikoleta, parichko);
    List<User> membersOfFullGroup = Arrays.asList(mihaela, nikoleta, parichko, leech);
    Group studentsWallet = new Group("studentsWallet", membersOfGroup);
    Group nonFullGroup = new Group("nonFullGroup", membersOfNonFullGroup);
    Group fullGroup = new Group("nonFullGroup", membersOfFullGroup);

    @Test
    void testGetName() {
        assertEquals("studentsWallet",studentsWallet.getName());
    }

    @Test
    void testGetMembers() {
        assertEquals(membersOfGroup,studentsWallet.getMembers());
    }

    @Test
    void testSetName() {
        studentsWallet.setName("newName");
        assertEquals("newName",studentsWallet.getName());
    }

    @Test
    void testAddMember() {
        nonFullGroup.addMember(leech);
        assertEquals(membersOfFullGroup,nonFullGroup.getMembers());
    }

    @Test
    void testRemoveMember() {
        fullGroup.removeMember(leech);
        assertEquals(membersOfNonFullGroup,fullGroup.getMembers());
    }
}