package com.selimhorri.app.resource;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Resource Integration Tests")
class UserResourceTest {

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@Mock
	private UserService userService;

	@InjectMocks
	private UserResource userResource;

	private UserDto sampleUser;

	@BeforeEach
	void setupTestEnvironment() {
		mockMvc = MockMvcBuilders.standaloneSetup(userResource)
				.setControllerAdvice(new com.selimhorri.app.exception.ApiExceptionHandler())
				.build();
		objectMapper = new ObjectMapper();

		CredentialDto credential = CredentialDto.builder()
				.credentialId(1)
				.username("testuser")
				.password("encoded123")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(true)
				.isAccountNonExpired(true)
				.isAccountNonLocked(true)
				.isCredentialsNonExpired(true)
				.build();

		sampleUser = UserDto.builder()
				.userId(1)
				.firstName("Alice")
				.lastName("Johnson")
				.email("alice.johnson@example.com")
				.phone("555-0123")
				.imageUrl("https://example.com/photo.jpg")
				.credentialDto(credential)
				.build();
	}

	@Test
	@DisplayName("GET /api/users - Should return list of all users")
	void testGetAllUsers() throws Exception {
		// Arrange
		UserDto secondUser = UserDto.builder()
				.userId(2)
				.firstName("Bob")
				.lastName("Smith")
				.email("bob.smith@example.com")
				.phone("555-0456")
				.build();

		List<UserDto> users = Arrays.asList(sampleUser, secondUser);
		when(userService.findAll()).thenReturn(users);

		// Act & Assert
		mockMvc.perform(get("/api/users")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.collection").isArray())
				.andExpect(jsonPath("$.collection[0].userId").value(1))
				.andExpect(jsonPath("$.collection[0].firstName").value("Alice"))
				.andExpect(jsonPath("$.collection[1].userId").value(2));

		verify(userService, times(1)).findAll();
	}

	@Test
	@DisplayName("GET /api/users/{id} - Should return user when exists")
	void testGetUserByIdWhenExists() throws Exception {
		// Arrange
		when(userService.findById(1)).thenReturn(sampleUser);

		// Act & Assert
		mockMvc.perform(get("/api/users/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").value(1))
				.andExpect(jsonPath("$.firstName").value("Alice"))
				.andExpect(jsonPath("$.lastName").value("Johnson"))
				.andExpect(jsonPath("$.email").value("alice.johnson@example.com"));

		verify(userService, times(1)).findById(1);
	}

	@Test
	@DisplayName("GET /api/users/{id} - Should return 404 when user not found")
	void testGetUserByIdWhenNotFound() throws Exception {
		// Arrange
		when(userService.findById(999))
				.thenThrow(new UserObjectNotFoundException("User with id: 999 not found"));

		// Act & Assert
		mockMvc.perform(get("/api/users/999")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		verify(userService, times(1)).findById(999);
	}

	@Test
	@DisplayName("GET /api/users/username/{username} - Should return user by username")
	void testGetUserByUsername() throws Exception {
		// Arrange
		when(userService.findByUsername("testuser")).thenReturn(sampleUser);

		// Act & Assert
		mockMvc.perform(get("/api/users/username/testuser")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").value(1));

		verify(userService, times(1)).findByUsername("testuser");
	}

	@Test
	@DisplayName("POST /api/users - Should create new user")
	void testCreateUser() throws Exception {
		// Arrange
		UserDto newUser = UserDto.builder()
				.firstName("Charlie")
				.lastName("Brown")
				.email("charlie.brown@example.com")
				.phone("555-0789")
				.build();

		UserDto savedUser = UserDto.builder()
				.userId(3)
				.firstName("Charlie")
				.lastName("Brown")
				.email("charlie.brown@example.com")
				.phone("555-0789")
				.build();

		when(userService.save(any(UserDto.class))).thenReturn(savedUser);

		// Act & Assert
		mockMvc.perform(post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newUser)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").value(3))
				.andExpect(jsonPath("$.firstName").value("Charlie"))
				.andExpect(jsonPath("$.email").value("charlie.brown@example.com"));

		verify(userService, times(1)).save(any(UserDto.class));
	}

	@Test
	@DisplayName("PUT /api/users - Should update existing user")
	void testUpdateUser() throws Exception {
		// Arrange
		UserDto updatedUser = UserDto.builder()
				.userId(1)
				.firstName("Alice")
				.lastName("Williams")
				.email("alice.williams@example.com")
				.phone("555-0123")
				.build();

		when(userService.update(any(UserDto.class))).thenReturn(updatedUser);

		// Act & Assert
		mockMvc.perform(put("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedUser)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").value(1))
				.andExpect(jsonPath("$.lastName").value("Williams"))
				.andExpect(jsonPath("$.email").value("alice.williams@example.com"));

		verify(userService, times(1)).update(any(UserDto.class));
	}

	@Test
	@DisplayName("PUT /api/users/{id} - Should update user by id")
	void testUpdateUserById() throws Exception {
		// Arrange
		UserDto updatedUser = UserDto.builder()
				.firstName("Alice")
				.lastName("Davis")
				.email("alice.davis@example.com")
				.phone("555-0123")
				.build();

		UserDto result = UserDto.builder()
				.userId(1)
				.firstName("Alice")
				.lastName("Davis")
				.email("alice.davis@example.com")
				.phone("555-0123")
				.build();

		when(userService.update(eq(1), any(UserDto.class))).thenReturn(result);

		// Act & Assert
		mockMvc.perform(put("/api/users/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedUser)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").value(1))
				.andExpect(jsonPath("$.lastName").value("Davis"));

		verify(userService, times(1)).update(eq(1), any(UserDto.class));
	}

	@Test
	@DisplayName("DELETE /api/users/{id} - Should delete user successfully")
	void testDeleteUser() throws Exception {
		// Arrange
		doNothing().when(userService).deleteById(1);

		// Act & Assert
		mockMvc.perform(delete("/api/users/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("true"));

		verify(userService, times(1)).deleteById(1);
	}

	@Test
	@DisplayName("GET /api/users - Should return empty collection when no users exist")
	void testGetAllUsersEmptyList() throws Exception {
		// Arrange
		when(userService.findAll()).thenReturn(Arrays.asList());

		// Act & Assert
		mockMvc.perform(get("/api/users")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.collection").isArray())
				.andExpect(jsonPath("$.collection").isEmpty());

		verify(userService, times(1)).findAll();
	}
}

