package server.services;

import database.model.Group;
import database.model.Moneyflow;
import database.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import server.repository.FriendshipRepository;
import server.repository.GroupRepository;
import server.repository.TransactionRepository;
import server.repository.UserRepository;

import java.util.*;

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
        // Mocking dependencies
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
        TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);

        // Setting up test data
        String giverName = "Mihi";
        String takerName = "Niki";
        Double amount = 100.0;
        String reason = "Shared expense";

        User giver = new User(giverName,"12345");
        User taker = new User(takerName,"12345");

        Mockito.when(userRepository.getUserByUsername(giverName)).thenReturn(giver);
        Mockito.when(userRepository.getUserByUsername(takerName)).thenReturn(taker);

        // Creating an instance of the class that contains the split method (TransactionManager)
        ExpensesService expensesService = new ExpensesService(userRepository, groupRepository, transactionRepository);

        // Executing the method to be tested
        String result = expensesService.split(giverName, takerName, amount, reason);

        // Verifying the results
        assertEquals("Successfully split money ", result);

        // Verifying that the transactionRepository.createTransaction is called with the correct arguments
        Mockito.verify(transactionRepository, Mockito.times(1)).createTransaction(Mockito.any(Moneyflow.class));
    }

    @Test
    void splitGroup() {
        // Mocking dependencies
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
        TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
        Logger logger = Mockito.mock(Logger.class);

        // Setting up test data
        String giverName = "Mihi";
        String groupName = "Friends";
        Double amount = 100.0;
        String reason = "Shared expense";

        User giver = new User(giverName, "12345");
        List<User> membersOfGroup = Arrays.asList(
                new User("User1", "12345"),
                new User("User2", "12345"),
                new User("User3", "12345")
        );
        Group group=new Group(groupName,membersOfGroup);

        Mockito.when(userRepository.getUserByUsername(giverName)).thenReturn(giver);
        Mockito.when(transactionRepository.getWantedGroupMembers(groupName)).thenReturn(membersOfGroup);

        // Creating an instance of the class that contains the splitGroup method (ExpensesService)
        ExpensesService expensesService = new ExpensesService(userRepository, groupRepository, transactionRepository);

        // Executing the method to be tested
        String result = expensesService.splitGroup(giverName, groupName, amount, reason);

        // Verifying the results
        assertEquals("Successfully split money.", result);

        // Verifying that the transactionRepository.createTransaction is called with the correct arguments
        Mockito.verify(transactionRepository, Mockito.times(3)).createTransaction(Mockito.any(Moneyflow.class));

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