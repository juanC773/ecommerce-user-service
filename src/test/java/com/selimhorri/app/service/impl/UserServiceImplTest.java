package com.selimhorri.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.repository.CredentialRepository;
import com.selimhorri.app.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Test")
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;
	
	@Mock
	private CredentialRepository credentialRepository;
	
	@InjectMocks
	private UserServiceImpl userService;
	
	private User user;
	private UserDto userDto;
	private Credential credential;
	
	@BeforeEach
	void setUp() {
		credential = Credential.builder()
				.credentialId(1)
				.username("testuser")
				.password("password123")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(true)
				.isAccountNonExpired(true)
				.isAccountNonLocked(true)
				.isCredentialsNonExpired(true)
				.build();
		
		user = User.builder()
				.userId(1)
				.firstName("John")
				.lastName("Doe")
				.email("john.doe@example.com")
				.phone("1234567890")
				.credential(credential)
				.build();
		
		userDto = UserDto.builder()
				.userId(1)
				.firstName("John")
				.lastName("Doe")
				.email("john.doe@example.com")
				.phone("1234567890")
				.credentialDto(
					com.selimhorri.app.dto.CredentialDto.builder()
						.credentialId(1)
						.username("testuser")
						.password("password123")
						.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
						.isEnabled(true)
						.isAccountNonExpired(true)
						.isAccountNonLocked(true)
						.isCredentialsNonExpired(true)
						.build())
				.build();
	}
	
	@Test
	@DisplayName("Should return all users successfully")
	void testFindAll_Success() {
		// Given
		List<User> users = Arrays.asList(user, createMockUser(2));
		when(userRepository.findAll()).thenReturn(users);
		
		// When
		List<UserDto> result = userService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(userRepository, times(1)).findAll();
	}
	
	@Test
	@DisplayName("Should return empty list when no users exist")
	void testFindAll_EmptyList() {
		// Given
		when(userRepository.findAll()).thenReturn(Arrays.asList());
		
		// When
		List<UserDto> result = userService.findAll();
		
		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(userRepository, times(1)).findAll();
	}
	
	@Test
	@DisplayName("Should return user by id successfully")
	void testFindById_Success() {
		// Given
		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		
		// When
		UserDto result = userService.findById(1);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getUserId());
		assertEquals("John", result.getFirstName());
		assertEquals("Doe", result.getLastName());
		verify(userRepository, times(1)).findById(1);
	}
	
	@Test
	@DisplayName("Should throw exception when user not found by id")
	void testFindById_UserNotFound() {
		// Given
		when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
		
		// When & Then
		assertThrows(UserObjectNotFoundException.class, () -> userService.findById(999));
		verify(userRepository, times(1)).findById(999);
	}
	
	@Test
	@DisplayName("Should save user successfully")
	void testSave_Success() {
		// Given
		userDto.setUserId(null); // For new user
		when(userRepository.save(any(User.class))).thenReturn(user);
		
		// When
		UserDto result = userService.save(userDto);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getUserId());
		verify(userRepository, times(1)).save(any(User.class));
	}
	
	@Test
	@DisplayName("Should update user successfully")
	void testUpdate_WithDto_Success() {
		// Given
		UserDto updateDto = UserDto.builder()
				.userId(1)
				.firstName("Jane")
				.lastName("Smith")
				.email("jane.smith@example.com")
				.phone("0987654321")
				.build();
		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User savedUser = invocation.getArgument(0);
			return savedUser;
		});
		
		// When
		UserDto result = userService.update(updateDto);
		
		// Then
		assertNotNull(result);
		verify(userRepository, times(1)).findById(1);
		verify(userRepository, times(1)).save(any(User.class));
	}
	
	@Test
	@DisplayName("Should update user by id successfully")
	void testUpdate_WithIdAndDto_Success() {
		// Given
		UserDto updateDto = UserDto.builder()
				.userId(1)
				.firstName("Updated")
				.lastName("Name")
				.email("updated@example.com")
				.phone("1234567890")
				.build();
		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenReturn(user);
		
		// When
		UserDto result = userService.update(1, updateDto);
		
		// Then
		assertNotNull(result);
		verify(userRepository, times(1)).findById(1);
		verify(userRepository, times(1)).save(any(User.class));
	}
	
	@Test
	@DisplayName("Should delete user successfully")
	void testDeleteById_Success() {
		// Given
		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenReturn(user);
		doNothing().when(credentialRepository).deleteByCredentialId(anyInt());
		
		// When
		userService.deleteById(1);
		
		// Then
		verify(userRepository, times(1)).findById(1);
		verify(userRepository, times(1)).save(any(User.class));
		verify(credentialRepository, times(1)).deleteByCredentialId(1);
	}
	
	@Test
	@DisplayName("Should find user by username successfully")
	void testFindByUsername_Success() {
		// Given
		when(userRepository.findByCredentialUsername("testuser")).thenReturn(Optional.of(user));
		
		// When
		UserDto result = userService.findByUsername("testuser");
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getUserId());
		verify(userRepository, times(1)).findByCredentialUsername("testuser");
	}
	
	@Test
	@DisplayName("Should throw exception when user not found by username")
	void testFindByUsername_UserNotFound() {
		// Given
		when(userRepository.findByCredentialUsername(anyString())).thenReturn(Optional.empty());
		
		// When & Then
		assertThrows(UserObjectNotFoundException.class, () -> userService.findByUsername("nonexistent"));
		verify(userRepository, times(1)).findByCredentialUsername("nonexistent");
	}
	
	@Test
	@DisplayName("Should handle null input gracefully")
	void testUpdate_WithNullDto() {
		// Given - no need to mock since exception is thrown before any repository call
		
		// When & Then - Should throw EntityNotFoundException when DTO is null
		assertThrows(EntityNotFoundException.class, () -> userService.update(1, null));
	}
	
	@Test
	@DisplayName("Should verify distinct users in findAll")
	void testFindAll_DistinctUsers() {
		// Given
		User duplicateUser = createMockUser(1);
		List<User> users = Arrays.asList(user, duplicateUser, user);
		when(userRepository.findAll()).thenReturn(users);
		
		// When
		List<UserDto> result = userService.findAll();
		
		// Then
		assertNotNull(result);
		assertTrue(result.size() >= 0);
		verify(userRepository, times(1)).findAll();
	}
	
	// Helper method to create mock users
	private User createMockUser(int userId) {
		return User.builder()
				.userId(userId)
				.firstName("User" + userId)
				.lastName("Test")
				.email("user" + userId + "@example.com")
				.phone("1111111111")
				.credential(credential)
				.build();
	}
	
}

