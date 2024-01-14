package server.services;

import database.model.Group;
import database.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import server.repository.FriendshipRepository;
import server.repository.GroupRepository;
import server.repository.TransactionRepository;
import server.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExpensesServiceTest {

    private UserRepository userRepositoryMock;
    private GroupRepository groupRepository;
    private TransactionRepository transactionRepository;
    private ExpensesService expensesService;

    @BeforeEach
    void setUp() {

        userRepositoryMock = Mockito.mock(UserRepository.class);
        groupRepository = Mockito.mock(GroupRepository.class);
        transactionRepository = Mockito.mock(TransactionRepository.class);
        expensesService = new ExpensesService(userRepositoryMock,groupRepository,transactionRepository);
    }


    @Test
    void split() {
    }

    @Test
    void splitGroup() {
    }

    @Test
    void getStatus_Individual() {

        //Mock user finding
        User mockUser = new User("Mihi", "123");
        Mockito.when(userRepositoryMock.getUserByUsername(mockUser.getUsername())).thenReturn(mockUser);
        Map<User,Double> owedFromMock= new HashMap<>()
                {{
                        put(new User("Mihinka","1254"),25.0);
                    put(new User("Grishinka","1254"),15.0);
                }};
        Map<User,Double> owedToMock= new HashMap<>()
        {{
            put(new User("Ba","1254"),125.0);
            put(new User("Ra","1254"),1995.0);
        }};
        Mockito.when(transactionRepository.getOwedMoneyFrom(mockUser,null)).thenReturn(owedFromMock);
        Mockito.when(transactionRepository.getOwedMoneyTo(mockUser,null)).thenReturn(owedToMock);


        String result =expensesService.getStatus(mockUser.getUsername());
        assertEquals("User mihi has been successfully added to your friends list.", result);
    }
    @Test
    void getStatus_Groups() {

        //Mock user finding
        User mockUser = new User("Mihi", "123");
        User mihinka =new User("Mihinka","1254");
        User grishinka =new User("Grishinka","1254");
        User ba = new User("Ba","1254");
        User ra = new User("Ra","1254");
        Mockito.when(userRepositoryMock.getUserByUsername(mockUser.getUsername())).thenReturn(mockUser);
        Map<User,Double> owedFromMock= new HashMap<>()
        {{
            put(mihinka,25.0);
            put(grishinka,15.0);
        }};
        Map<User,Double> owedToMock= new HashMap<>()
        {{
            put(ba, 2500.0);
            put(ra,1995.0);
        }};
        Group g = new Group("G", new ArrayList<>());
        g.addMember(mockUser);
        g.addMember(mihinka);
        g.addMember(grishinka);
        g.addMember(ba);
        g.addMember(ra);
        Mockito.when(transactionRepository.getOwedMoneyFrom(mockUser,g)).thenReturn(owedFromMock);
        Mockito.when(transactionRepository.getOwedMoneyTo(mockUser,g)).thenReturn(owedToMock);



        ArrayList<Group> GroupListMock = new ArrayList<>();
        GroupListMock.add(g);
        Mockito.when(groupRepository.getAllGroups(mockUser)).thenReturn(GroupListMock);

        String result =expensesService.getStatus(mockUser.getUsername());
        assertEquals("User mihi has been successfully added to your friends list.", result);
    }
}