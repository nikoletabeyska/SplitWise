package server.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import database.model.User;
import server.repository.UserRepository;
import server.services.UserManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserManagerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserManager userManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_Success() {
        String username = "bobo";
        String password = "123";
        when(userRepository.getUserByUsername(username)).thenReturn(null);
        String result = userManager.registerUser(username, password, null);


        assertEquals("User registered successfully!", result);

        verify(userRepository, times(1)).getUserByUsername(username);
        verify(userRepository, times(1)).createUser(any());
    }

    @Test
    void registerUser_UsernameTaken() {
        String username = "niki";
        String password = "123";
        when(userRepository.getUserByUsername(username)).thenReturn(new User(username, "123"));
        String result = userManager.registerUser(username, password, null);
        assertEquals("Username is already taken. Please choose another one.", result);
        verify(userRepository, times(1)).getUserByUsername(username);
        verify(userRepository, never()).createUser(any(User.class));
    }

    @Test
    void loginUser_Success() {
        String username = "mihi";
        String password = "test";

        User existingUser = new User(username, password);
        when(userRepository.getUserByUsername(username)).thenReturn(existingUser);
        String result = userManager.loginUser(username, password, false);
        assertEquals("Login successful! Welcome, mihi!", result);
        verify(userRepository, times(1)).getUserByUsername(username);
    }

    @Test
    void loginUser_InvalidCredentials() {
        String username = "miki";
        String password = "test";
        String incorrectPassword = "no";
        User existingUser = new User(username, password);
        when(userRepository.getUserByUsername(username)).thenReturn(existingUser);
        String result = userManager.loginUser(username, incorrectPassword, false);
        assertEquals("Invalid username or password. Please try again.", result);
        verify(userRepository, times(1)).getUserByUsername(username);
    }
}

