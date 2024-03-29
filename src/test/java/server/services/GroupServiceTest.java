package server.services;

import database.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import server.ClassesInitializer;
import server.RepositoryImplementationMapping;
import server.repository.FriendshipRepository;
import server.repository.GroupRepository;
import server.repository.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import database.model.Group;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GroupServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    private FriendshipRepository friendshipRepository;
    @InjectMocks
    private GroupService groupService;
    private static EntityManager entityManager;

    @BeforeAll
    void setUp() {
        userRepository= Mockito.mock(UserRepository.class);
        groupRepository= Mockito.mock(GroupRepository.class);
        friendshipRepository= Mockito.mock(FriendshipRepository.class);
        String persistence_unit = "TestPersistenceUnit";
        entityManager = Persistence.createEntityManagerFactory(persistence_unit).createEntityManager();
        ClassesInitializer initializer = new ClassesInitializer(entityManager);
        initializer.setUserRepository(userRepository);
        initializer.setGroupRepository(groupRepository);
        initializer.setFriendshipRepository(friendshipRepository);
        groupService= new GroupService(initializer);
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void createGroup_NonExistentUser_Error() {
        String groupName = "TestGroup";
        ArrayList<String> users = new ArrayList<>();
        users.add("user1");
        when(userRepository.getUserByUsername("user1")).thenReturn(null);
        String result = groupService.createGroup(groupName, users, "someone");
        assertEquals("User with username user1 does not exist.", result);
        verify(userRepository, times(1)).getUserByUsername("user1");
        verifyNoInteractions(groupRepository);
    }


    @Test
        void createGroup_ValidInput_Success() {
        String groupName = "TestGroup";
        ArrayList<String> users = new ArrayList<>(Arrays.asList("user1", "user2"));

        when(userRepository.getUserByUsername("user1")).thenReturn(new User("user1", "123"));
        when(userRepository.getUserByUsername("user2")).thenReturn(new User("user2", "1234"));

        String result = groupService.createGroup(groupName, users, "s");

        //assertEquals("Group " + groupName + " has been successfully created.", result);

        //verify(groupRepository).createGroup(any());
    }
    @Test
    void createGroup_InvalidGroupName_Error() {
        String groupName = "";
        ArrayList<String> users = new ArrayList<>(Collections.singletonList("user1"));

        String result = groupService.createGroup(groupName, users, "s");

        assertEquals("Invalid input. Group name is required.", result);

        verifyNoInteractions(groupRepository);
    }

    @Test
    void getGroups_ValidUser_Success() {
        User participant = new User("testUser", "1999");





































        when(groupRepository.getAllGroups(participant)).thenReturn(Arrays.asList(new Group("Group1"), new Group("Group2")));

        String result = groupService.getGroups(participant);

        // Add assertions based on the expected output or behavior
        assertTrue(result.contains("Groups:"));
        assertTrue(result.contains("Group1"));
        assertTrue(result.contains("Group2"));
    }
}

