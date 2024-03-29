package server.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.hibernate.sql.ast.tree.predicate.BooleanExpressionPredicate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import database.model.User;
import server.ClassesInitializer;
import server.RepositoryImplementationMapping;
import server.repository.FriendshipRepository;
import server.repository.UserRepository;
import server.services.UserManager;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserManagerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserManager userManager;
    private static EntityManager entityManager;

    @BeforeAll
    void setUp() {
        userRepository= Mockito.mock(UserRepository.class);
        String persistence_unit = "TestPersistenceUnit";
        entityManager = Persistence.createEntityManagerFactory(persistence_unit).createEntityManager();
        ClassesInitializer initializer = new ClassesInitializer(entityManager);
        initializer.setUserRepository(userRepository);
        userManager = new UserManager(initializer);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_Success() {
        String username = "bobo";
        String password = "123";
        when(userRepository.getUserByUsername(username)).thenReturn(null);

        String result = userManager.registerUser(username, password, null);

        assertEquals("User registered successfully! Please login using your credentials.", result);
        verify(userRepository, times(1)).getUserByUsername(username);
        verify(userRepository, times(1)).createUser(any());
    }

    @Test
    void registerUser_UsernameTaken() {
        String username = "niki";
        String password = "123";
        User user = new User("niki","1234");
        when(userRepository.getUserByUsername(username)).thenReturn(user);

        String result = userManager.registerUser(username, password, null);

        assertEquals("Username is already taken. Please choose another one.", result);
        verify(userRepository, times(1)).getUserByUsername(username);
        verify(userRepository, never()).createUser(any(User.class));
    }

    @Test
    void loginUser_Success() {
        String username = "mihi";
        String password = "123";
        String salt = BCrypt.gensalt();
        String storedPassword = BCrypt.hashpw(password, salt);
        User existingUser = new User(username, storedPassword);
        when(userRepository.getUserByUsername(username)).thenReturn(existingUser);
        when(userRepository.getUserByUsername(username)).thenReturn(existingUser);

        Map<String, Boolean> result = userManager.loginUser(username, password);
        Map<String, Boolean> resultSuccess = new HashMap<>();
        resultSuccess.put("Login successful! Welcome, mihi!", true);

        assertEquals(resultSuccess, result);
        verify(userRepository, times(1)).getUserByUsername(username);
    }

    @Test
    void loginUser_InvalidCredentials() {
        String username = "miki";
        String password = "test";
        String incorrectPassword = "no";
        User existingUser = new User(username, password);
        when(userRepository.getUserByUsername(username)).thenReturn(existingUser);

        Map<String, Boolean> result = userManager.loginUser(username, incorrectPassword);
        Map<String, Boolean> resultFail = new HashMap<>();
        resultFail.put("Invalid username or password. Please try again", false);

        assertEquals(resultFail, result);
        verify(userRepository, times(1)).getUserByUsername(username);
    }
}

