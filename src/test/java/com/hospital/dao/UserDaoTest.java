package com.hospital.dao;

import com.hospital.dao.impl.UserDaoImpl;
import com.hospital.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserDaoTest {
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private Statement mockStatement;
    
    @Mock
    private ResultSet mockResultSet;
    
    private UserDao userDao;
    
    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        
        // Mock Connection behavior
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        
        // Mock PreparedStatement behavior
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        
        // Create UserDao with mocked dependencies
        userDao = new UserDaoImpl() {
            @Override
            protected Connection getConnection() throws SQLException {
                return mockConnection;
            }
        };
    }
    
    @Test
    public void testAuthenticate_Success() throws SQLException {
        // Arrange
        String username = "admin";
        String password = "admin123";
        
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("username")).thenReturn(username);
        when(mockResultSet.getString("password")).thenReturn(password);
        when(mockResultSet.getString("role")).thenReturn("ADMIN");
        
        // Act
        User user = userDao.authenticate(username, password);
        
        // Assert
        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(User.Role.ADMIN, user.getRole());
        
        // Verify
        verify(mockPreparedStatement).setString(1, username);
        verify(mockPreparedStatement).setString(2, password);
        verify(mockPreparedStatement).executeQuery();
    }
    
    @Test
    public void testAuthenticate_Failure() throws SQLException {
        // Arrange
        String username = "admin";
        String password = "wrongpassword";
        
        when(mockResultSet.next()).thenReturn(false);
        
        // Act
        User user = userDao.authenticate(username, password);
        
        // Assert
        assertNull(user);
        
        // Verify
        verify(mockPreparedStatement).setString(1, username);
        verify(mockPreparedStatement).setString(2, password);
        verify(mockPreparedStatement).executeQuery();
    }
    
    @Test
    public void testAdd_Success() throws SQLException {
        // Arrange
        User user = new User("newuser", "password123", User.Role.NURSE);
        
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(2);
        
        // Act
        int userId = userDao.add(user);
        
        // Assert
        assertEquals(2, userId);
        
        // Verify
        verify(mockPreparedStatement).setString(1, user.getUsername());
        verify(mockPreparedStatement).setString(2, user.getPassword());
        verify(mockPreparedStatement).setString(3, user.getRole().name());
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).getGeneratedKeys();
    }
    
    @Test
    public void testGetById_Success() throws SQLException {
        // Arrange
        int userId = 1;
        
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(userId);
        when(mockResultSet.getString("username")).thenReturn("admin");
        when(mockResultSet.getString("password")).thenReturn("admin123");
        when(mockResultSet.getString("role")).thenReturn("ADMIN");
        
        // Act
        User user = userDao.getById(userId);
        
        // Assert
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals("admin", user.getUsername());
        assertEquals("admin123", user.getPassword());
        assertEquals(User.Role.ADMIN, user.getRole());
        
        // Verify
        verify(mockPreparedStatement).setInt(1, userId);
        verify(mockPreparedStatement).executeQuery();
    }
} 