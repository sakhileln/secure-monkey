package za.co.securemonkey.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import za.co.securemonkey.dto.LoginRequest;
import za.co.securemonkey.dto.LoginResponse;
import za.co.securemonkey.entity.User;
import za.co.securemonkey.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @InjectMocks
    private AuthenticationService authenticationService;
    
    private User testUser;
    private LoginRequest loginRequest;
    
    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "test@example.com", "encodedPassword");
        testUser.setId(1L);
        
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");
    }
    
    @Test
    void loginUser_WithValidCredentials_ReturnsLoginResponse() {
        // Arrange
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(testUser.getEmail()))
                .thenReturn("test-jwt-token");
        
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        
        // Act
        LoginResponse response = authenticationService.loginUser(loginRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        assertEquals(1L, response.getId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getName());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("test@example.com");
        verify(jwtService).generateToken(testUser.getEmail());
    }
    
    @Test
    void loginUser_WithInvalidCredentials_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authenticationService.loginUser(loginRequest);
        });
    }
    
    @Test
    void registerUser_WithValidData_ReturnsUser() {
        // Arrange
        User newUser = new User("New User", "new@example.com", "password");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        
        // Act
        User savedUser = authenticationService.registerUser(newUser);
        
        // Assert
        assertNotNull(savedUser);
        assertEquals("New User", savedUser.getName());
        assertEquals("new@example.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        
        verify(userRepository).existsByEmail("new@example.com");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void registerUser_WithExistingEmail_ThrowsException() {
        // Arrange
        User newUser = new User("New User", "existing@example.com", "password");
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authenticationService.registerUser(newUser);
        });
    }
}
