package server.services;

import database.model.Group;
import database.model.Moneyflow;
import database.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpensesServiceTest {

    private UserRepository userRepository;
    private GroupRepository groupRepository;
    private TransactionRepository transactionRepository;
    private static ExpensesService expensesService;
    private static EntityManager entityManager;

    @BeforeAll
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        groupRepository = Mockito.mock(GroupRepository.class);
        transactionRepository = Mockito.mock(TransactionRepository.class);

        String persistence_unit = "TestPersistenceUnit";
        entityManager = Persistence.createEntityManagerFactory(persistence_unit).createEntityManager();
        ClassesInitializer initializer = new ClassesInitializer(entityManager);
        initializer.setUserRepository(userRepository);
        initializer.setGroupRepository(groupRepository);
        initializer.setTransactionRepository(transactionRepository);
        expensesService = new ExpensesService(initializer);

    }
    @Test
    void split() {
        // Setting up test data
        String giverName = "Mihi";
        String takerName = "Niki";
        Double amount = 100.0;
        String reason = "Shared expense";

        User giver = new User(giverName,"12345");
        User taker = new User(takerName,"12345");

        Mockito.when(userRepository.getUserByUsername(giverName)).thenReturn(giver);
        Mockito.when(userRepository.getUserByUsername(takerName)).thenReturn(taker);

        String result = expensesService.split(giverName, takerName, amount, reason);
        assertEquals("Successfully split money ", result);
    }

    @Test
    void splitGroup() {
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
        Group group = new Group(groupName,membersOfGroup);
        Mockito.when(userRepository.getUserByUsername(giverName)).thenReturn(giver);
        Mockito.when(transactionRepository.getWantedGroupMembers(groupName)).thenReturn(membersOfGroup);

        String result = expensesService.splitGroup(giverName, groupName, amount, reason);
        assertEquals("Successfully split money.", result);
        Mockito.verify(transactionRepository, Mockito.times(3)).createTransaction(Mockito.any(Moneyflow.class));
    }

    @Test
    void getStatus_Individual() {
        User mockUser = new User("Mihi", "123");
        Mockito.when(userRepository.getUserByUsername(mockUser.getUsername())).thenReturn(mockUser);
        Map<User,Double> owedFromMock = new HashMap<>();
        owedFromMock.put(new User("Mihinka","1254"),25.0);
        owedFromMock.put(new User("Grishinka","1254"),15.0);

        Map<User,Double> owedToMock = new HashMap<>();
        owedToMock.put(new User("Ba","1254"),125.0);
        owedToMock.put(new User("Ra","1254"),1995.0);

        Mockito.when(transactionRepository.getOwedMoneyFrom(mockUser,null)).thenReturn(owedFromMock);
        Mockito.when(transactionRepository.getOwedMoneyTo(mockUser,null)).thenReturn(owedToMock);
        String result = expensesService.getStatus(mockUser.getUsername());
        //assertEquals("User mihi has been successfully added to your friends list.", result);
    }
    @Test
    void getStatus_Groups() {

        User mockUser = new User("Mihi", "123");
        User mihinka =new User("Mihinka","1254");
        User grishinka =new User("Grishinka","1254");
        User ba = new User("Ba","1254");
        User ra = new User("Ra","1254");
        Mockito.when(userRepository.getUserByUsername(mockUser.getUsername())).thenReturn(mockUser);
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

        String result = expensesService.getStatus(mockUser.getUsername());
        String resultSuccess = """
        
        To/From    Individuals
        Groups
        To/From    G
             You owe    Ra:    1995.0
             You owe    Ba:    2500.0
             Mihinka    owes you:   25.0
             Grishinka    owes you:   15.0 
        """;
        assertEquals(resultSuccess, result);
    }
}