package com.selimhorri.app.resource;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.VerificationTokenDto;
import com.selimhorri.app.exception.wrapper.VerificationTokenNotFoundException;
import com.selimhorri.app.service.VerificationTokenService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Verification Token Resource Integration Tests")
class VerificationTokenResourceTest {

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@Mock
	private VerificationTokenService verificationTokenService;

	@InjectMocks
	private VerificationTokenResource verificationTokenResource;

	private VerificationTokenDto sampleToken;

	@BeforeEach
	void configureTestEnvironment() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		mockMvc = MockMvcBuilders.standaloneSetup(verificationTokenResource)
				.setControllerAdvice(new com.selimhorri.app.exception.ApiExceptionHandler())
				.build();

		CredentialDto linkedCredential = CredentialDto.builder()
				.credentialId(1)
				.username("testuser")
				.password("$2a$10$hashedPass")
				.build();

		sampleToken = VerificationTokenDto.builder()
				.verificationTokenId(1)
				.token("abc123def456")
				.expireDate(LocalDate.now().plusDays(7))
				.credentialDto(linkedCredential)
				.build();
	}

	@Test
	@DisplayName("GET /api/verificationTokens - Should return all verification tokens")
	void testRetrieveAllTokens() throws Exception {
		// Arrange
		VerificationTokenDto secondToken = VerificationTokenDto.builder()
				.verificationTokenId(2)
				.token("xyz789uvw012")
				.expireDate(LocalDate.now().plusDays(14))
				.build();

		List<VerificationTokenDto> tokens = Arrays.asList(sampleToken, secondToken);
		when(verificationTokenService.findAll()).thenReturn(tokens);

		// Act & Assert
		mockMvc.perform(get("/api/verificationTokens")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.collection").isArray())
				.andExpect(jsonPath("$.collection[0].verificationTokenId").value(1))
				.andExpect(jsonPath("$.collection[0].token").value("abc123def456"))
				.andExpect(jsonPath("$.collection[1].verificationTokenId").value(2));

		verify(verificationTokenService, times(1)).findAll();
	}

	@Test
	@DisplayName("GET /api/verificationTokens/{id} - Should return token when present")
	void testGetTokenByIdPresent() throws Exception {
		// Arrange
		when(verificationTokenService.findById(1)).thenReturn(sampleToken);

		// Act & Assert
		mockMvc.perform(get("/api/verificationTokens/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.verificationTokenId").value(1))
				.andExpect(jsonPath("$.token").value("abc123def456"));

		verify(verificationTokenService, times(1)).findById(1);
	}

	@Test
	@DisplayName("GET /api/verificationTokens/{id} - Should return error when token absent")
	void testGetTokenByIdAbsent() throws Exception {
		// Arrange
		when(verificationTokenService.findById(999))
				.thenThrow(new VerificationTokenNotFoundException("Token with id: 999 not found"));

		// Act & Assert
		mockMvc.perform(get("/api/verificationTokens/999")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		verify(verificationTokenService, times(1)).findById(999);
	}

	@Test
	@DisplayName("POST /api/verificationTokens - Should create new verification token")
	void testCreateNewToken() throws Exception {
		// Arrange
		VerificationTokenDto newToken = VerificationTokenDto.builder()
				.token("newToken789")
				.expireDate(LocalDate.now().plusDays(30))
				.credentialDto(CredentialDto.builder()
						.credentialId(2)
						.username("anotheruser")
						.build())
				.build();

		VerificationTokenDto createdToken = VerificationTokenDto.builder()
				.verificationTokenId(3)
				.token("newToken789")
				.expireDate(LocalDate.now().plusDays(30))
				.credentialDto(CredentialDto.builder()
						.credentialId(2)
						.username("anotheruser")
						.build())
				.build();

		when(verificationTokenService.save(any(VerificationTokenDto.class))).thenReturn(createdToken);

		// Act & Assert
		mockMvc.perform(post("/api/verificationTokens")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.verificationTokenId").value(3))
				.andExpect(jsonPath("$.token").value("newToken789"));

		verify(verificationTokenService, times(1)).save(any(VerificationTokenDto.class));
	}

	@Test
	@DisplayName("PUT /api/verificationTokens/{id} - Should update token details")
	void testUpdateTokenDetails() throws Exception {
		// Arrange
		VerificationTokenDto updateData = VerificationTokenDto.builder()
				.token("updatedToken456")
				.expireDate(LocalDate.now().plusDays(21))
				.credentialDto(CredentialDto.builder()
						.credentialId(1)
						.username("testuser")
						.build())
				.build();

		VerificationTokenDto updatedResult = VerificationTokenDto.builder()
				.verificationTokenId(1)
				.token("updatedToken456")
				.expireDate(LocalDate.now().plusDays(21))
				.credentialDto(CredentialDto.builder()
						.credentialId(1)
						.username("testuser")
						.build())
				.build();

		when(verificationTokenService.update(eq(1), any(VerificationTokenDto.class)))
				.thenReturn(updatedResult);

		// Act & Assert
		mockMvc.perform(put("/api/verificationTokens/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateData)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.verificationTokenId").value(1))
				.andExpect(jsonPath("$.token").value("updatedToken456"));

		verify(verificationTokenService, times(1)).update(eq(1), any(VerificationTokenDto.class));
	}

	@Test
	@DisplayName("DELETE /api/verificationTokens/{id} - Should delete token successfully")
	void testDeleteTokenSuccessfully() throws Exception {
		// Arrange
		doNothing().when(verificationTokenService).deleteById(1);

		// Act & Assert
		mockMvc.perform(delete("/api/verificationTokens/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("true"));

		verify(verificationTokenService, times(1)).deleteById(1);
	}

	@Test
	@DisplayName("DELETE /api/verificationTokens/{id} - Should handle missing token deletion")
	void testDeleteMissingToken() throws Exception {
		// Arrange
		doThrow(new VerificationTokenNotFoundException("Token not found"))
				.when(verificationTokenService).deleteById(999);

		// Act & Assert
		mockMvc.perform(delete("/api/verificationTokens/999")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		verify(verificationTokenService, times(1)).deleteById(999);
	}

	@Test
	@DisplayName("GET /api/verificationTokens - Should return empty collection when no tokens")
	void testGetAllTokensEmptyCollection() throws Exception {
		// Arrange
		when(verificationTokenService.findAll()).thenReturn(Arrays.asList());

		// Act & Assert
		mockMvc.perform(get("/api/verificationTokens")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.collection").isArray())
				.andExpect(jsonPath("$.collection").isEmpty());

		verify(verificationTokenService, times(1)).findAll();
	}

	@Test
	@DisplayName("POST /api/verificationTokens - Should handle token with extended expiration")
	void testCreateTokenWithExtendedExpiration() throws Exception {
		// Arrange
		VerificationTokenDto longExpirationToken = VerificationTokenDto.builder()
				.token("longLivedToken")
				.expireDate(LocalDate.now().plusDays(90))
				.credentialDto(CredentialDto.builder()
						.credentialId(1)
						.username("testuser")
						.build())
				.build();

		VerificationTokenDto saved = VerificationTokenDto.builder()
				.verificationTokenId(4)
				.token("longLivedToken")
				.expireDate(LocalDate.now().plusDays(90))
				.credentialDto(CredentialDto.builder()
						.credentialId(1)
						.username("testuser")
						.build())
				.build();

		when(verificationTokenService.save(any(VerificationTokenDto.class))).thenReturn(saved);

		// Act & Assert
		mockMvc.perform(post("/api/verificationTokens")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(longExpirationToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.verificationTokenId").value(4))
				.andExpect(jsonPath("$.token").value("longLivedToken"));

		verify(verificationTokenService, times(1)).save(any(VerificationTokenDto.class));
	}
}

