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
import com.selimhorri.app.exception.wrapper.CredentialNotFoundException;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.service.CredentialService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Credential Resource Integration Tests")
class CredentialResourceTest {

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@Mock
	private CredentialService credentialService;

	@InjectMocks
	private CredentialResource credentialResource;

	private CredentialDto sampleCredential;

	@BeforeEach
	void prepareTestScenario() {
		mockMvc = MockMvcBuilders.standaloneSetup(credentialResource)
				.setControllerAdvice(new com.selimhorri.app.exception.ApiExceptionHandler())
				.build();
		objectMapper = new ObjectMapper();

		UserDto associatedUser = UserDto.builder()
				.userId(1)
				.firstName("Emma")
				.lastName("Wilson")
				.email("emma.wilson@example.com")
				.phone("555-2222")
				.build();

		sampleCredential = CredentialDto.builder()
				.credentialId(1)
				.username("emma_user")
				.password("$2a$10$hashedPasswordString")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(true)
				.isAccountNonExpired(true)
				.isAccountNonLocked(true)
				.isCredentialsNonExpired(true)
				.userDto(associatedUser)
				.build();
	}

	@Test
	@DisplayName("GET /api/credentials - Should fetch all credentials")
	void testFetchAllCredentials() throws Exception {
		// Arrange
		CredentialDto adminCredential = CredentialDto.builder()
				.credentialId(2)
				.username("admin_user")
				.password("$2a$10$adminHash")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_ADMIN)
				.isEnabled(true)
				.build();

		List<CredentialDto> credentials = Arrays.asList(sampleCredential, adminCredential);
		when(credentialService.findAll()).thenReturn(credentials);

		// Act & Assert
		mockMvc.perform(get("/api/credentials")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.collection").isArray())
				.andExpect(jsonPath("$.collection[0].credentialId").value(1))
				.andExpect(jsonPath("$.collection[0].username").value("emma_user"))
				.andExpect(jsonPath("$.collection[1].credentialId").value(2))
				.andExpect(jsonPath("$.collection[1].username").value("admin_user"));

		verify(credentialService, times(1)).findAll();
	}

	@Test
	@DisplayName("GET /api/credentials/{id} - Should return credential by id when exists")
	void testGetCredentialByIdExists() throws Exception {
		// Arrange
		when(credentialService.findById(1)).thenReturn(sampleCredential);

		// Act & Assert
		mockMvc.perform(get("/api/credentials/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.credentialId").value(1))
				.andExpect(jsonPath("$.username").value("emma_user"))
				.andExpect(jsonPath("$.roleBasedAuthority").value("ROLE_USER"))
				.andExpect(jsonPath("$.isEnabled").value(true));

		verify(credentialService, times(1)).findById(1);
	}

	@Test
	@DisplayName("GET /api/credentials/{id} - Should return error when credential missing")
	void testGetCredentialByIdMissing() throws Exception {
		// Arrange
		when(credentialService.findById(999))
				.thenThrow(new CredentialNotFoundException("Credential with id: 999 not found"));

		// Act & Assert
		mockMvc.perform(get("/api/credentials/999")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		verify(credentialService, times(1)).findById(999);
	}

	@Test
	@DisplayName("GET /api/credentials/username/{username} - Should return credential by username")
	void testGetCredentialByUsername() throws Exception {
		// Arrange
		when(credentialService.findByUsername("emma_user")).thenReturn(sampleCredential);

		// Act & Assert
		mockMvc.perform(get("/api/credentials/username/emma_user")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.credentialId").value(1))
				.andExpect(jsonPath("$.username").value("emma_user"));

		verify(credentialService, times(1)).findByUsername("emma_user");
	}

	@Test
	@DisplayName("GET /api/credentials/username/{username} - Should return 404 for non-existent username")
	void testGetCredentialByUsernameNotFound() throws Exception {
		// Arrange
		when(credentialService.findByUsername("nonexistent"))
				.thenThrow(new UserObjectNotFoundException("Credential not found"));

		// Act & Assert
		mockMvc.perform(get("/api/credentials/username/nonexistent")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		verify(credentialService, times(1)).findByUsername("nonexistent");
	}

	@Test
	@DisplayName("POST /api/credentials - Should register new credential")
	void testRegisterNewCredential() throws Exception {
		// Arrange
		CredentialDto newCredential = CredentialDto.builder()
				.username("newuser")
				.password("plainPassword123")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(true)
				.isAccountNonExpired(true)
				.isAccountNonLocked(true)
				.isCredentialsNonExpired(true)
				.build();

		CredentialDto savedCredential = CredentialDto.builder()
				.credentialId(3)
				.username("newuser")
				.password("$2a$10$hashedNewPassword")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(true)
				.isAccountNonExpired(true)
				.isAccountNonLocked(true)
				.isCredentialsNonExpired(true)
				.build();

		when(credentialService.save(any(CredentialDto.class))).thenReturn(savedCredential);

		// Act & Assert
		mockMvc.perform(post("/api/credentials")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCredential)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.credentialId").value(3))
				.andExpect(jsonPath("$.username").value("newuser"))
				.andExpect(jsonPath("$.isEnabled").value(true));

		verify(credentialService, times(1)).save(any(CredentialDto.class));
	}

	@Test
	@DisplayName("POST /api/credentials - Should reject duplicate username")
	void testRegisterDuplicateUsername() throws Exception {
		// Arrange
		CredentialDto duplicate = CredentialDto.builder()
				.username("emma_user")
				.password("password123")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.build();

		when(credentialService.save(any(CredentialDto.class)))
				.thenThrow(new CredentialNotFoundException("Username already exists"));

		// Act & Assert
		mockMvc.perform(post("/api/credentials")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(duplicate)))
				.andExpect(status().isBadRequest());

		verify(credentialService, times(1)).save(any(CredentialDto.class));
	}

	@Test
	@DisplayName("PUT /api/credentials - Should update credential information")
	void testUpdateCredentialInfo() throws Exception {
		// Arrange
		CredentialDto updated = CredentialDto.builder()
				.credentialId(1)
				.username("emma_user")
				.password("$2a$10$newHashedPassword")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(false)
				.isAccountNonExpired(true)
				.isAccountNonLocked(true)
				.isCredentialsNonExpired(true)
				.build();

		when(credentialService.update(any(CredentialDto.class))).thenReturn(updated);

		// Act & Assert
		mockMvc.perform(put("/api/credentials")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updated)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.credentialId").value(1))
				.andExpect(jsonPath("$.isEnabled").value(false));

		verify(credentialService, times(1)).update(any(CredentialDto.class));
	}

	@Test
	@DisplayName("PUT /api/credentials/{id} - Should update credential using id")
	void testUpdateCredentialWithId() throws Exception {
		// Arrange
		CredentialDto updateData = CredentialDto.builder()
				.username("emma_user")
				.password("$2a$10$updatedHash")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(true)
				.build();

		CredentialDto updatedResult = CredentialDto.builder()
				.credentialId(1)
				.username("emma_user")
				.password("$2a$10$updatedHash")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(true)
				.build();

		when(credentialService.update(eq(1), any(CredentialDto.class))).thenReturn(updatedResult);

		// Act & Assert
		mockMvc.perform(put("/api/credentials/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateData)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.credentialId").value(1))
				.andExpect(jsonPath("$.username").value("emma_user"));

		verify(credentialService, times(1)).update(eq(1), any(CredentialDto.class));
	}

	@Test
	@DisplayName("DELETE /api/credentials/{id} - Should remove credential")
	void testRemoveCredential() throws Exception {
		// Arrange
		doNothing().when(credentialService).deleteById(1);

		// Act & Assert
		mockMvc.perform(delete("/api/credentials/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("true"));

		verify(credentialService, times(1)).deleteById(1);
	}

	@Test
	@DisplayName("DELETE /api/credentials/{id} - Should return error when deleting non-existent credential")
	void testRemoveNonExistentCredential() throws Exception {
		// Arrange
		doThrow(new CredentialNotFoundException("Credential not found"))
				.when(credentialService).deleteById(999);

		// Act & Assert
		mockMvc.perform(delete("/api/credentials/999")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		verify(credentialService, times(1)).deleteById(999);
	}
}

