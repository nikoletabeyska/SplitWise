package server.services;

import org.hibernate.sql.ast.tree.predicate.BooleanExpressionPredicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import database.model.User;
import server.RepositoryImplementationMapping;
import server.repository.FriendshipRepository;
import server.repository.UserRepository;
import server.services.UserManager;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserManagerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserManager userManager;

    @BeforeEach
    void setUp() {
        userRepository= Mockito.mock(UserRepository.class);
        RepositoryImplementationMapping.addOrReplace(UserRepository.class,userRepository);
        userManager = new UserManager();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_Success() {
        String username = "bobo";
        String password = "123";
        UserRepository u= (UserRepository)RepositoryImplementationMapping.get(UserRepository.class);
        when(u.getUserByUsername(username)).thenReturn(null);
        String result = userManager.registerUser(username, password, null);
        assertEquals("User registered successfully! Please login using your credentials.", result);

        verify(u, times(1)).getUserByUsername(username);
        verify(u, times(1)).createUser(any());
    }

    @Test
    void registerUser_UsernameTaken() {
        String username = "niki";
        String password = "123";
        UserRepository u= (UserRepository)RepositoryImplementationMapping.get(UserRepository.class);
        when(u.getUserByUsername(username)).thenReturn(new User(username, "123"));
        String result = userManager.registerUser(username, password, null);
        assertEquals("Username is already taken. Please choose another one.", result);
        verify(u, times(1)).getUserByUsername(username);
        verify(u, never()).createUser(any(User.class));
    }

    @Test
    void loginUser_Success() {
        String username = "mihi";
        String password = "test";

        User existingUser = new User(username, password);
        when(userRepository.getUserByUsername(username)).thenReturn(existingUser);
        //TODO login user method changed declaration, test must be udpated to match
//        String result = userManager.loginUser(username, password, false);
//        assertEquals("Login successful! Welcome, mihi!", result);
//        verify(userRepository, times(1)).getUserByUsername(username);
    }

    @Test
    void loginUser_InvalidCredentials() {
        String username = "miki";
        String password = "test";
        String incorrectPassword = "no";
        User existingUser = new User(username, password);
        UserRepository u= (UserRepository)RepositoryImplementationMapping.get(UserRepository.class);
        when(u.getUserByUsername(username)).thenReturn(existingUser);
        Map<String, Boolean> result = userManager.loginUser(username,incorrectPassword);
        assertEquals("[Invalid username or password. Please try again]", result.keySet().toString());
        //TODO login user method changed declaration, test must be udpated to match
        //String result = userManager.loginUser(username, incorrectPassword, false);
        //assertEquals("Invalid username or password. Please try again.", result);
        verify(u, times(1)).getUserByUsername(username);
    }
}

