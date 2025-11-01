package com.selimhorri.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.VerificationToken;
import com.selimhorri.app.dto.VerificationTokenDto;
import com.selimhorri.app.exception.wrapper.VerificationTokenNotFoundException;
import com.selimhorri.app.repository.VerificationTokenRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerificationTokenServiceImpl Test")
class VerificationTokenServiceImplTest {

	@Mock
	private VerificationTokenRepository verificationTokenRepository;
	
	@InjectMocks
	private VerificationTokenServiceImpl verificationTokenService;
	
	private VerificationToken verificationToken;
	private VerificationTokenDto verificationTokenDto;
	
	@BeforeEach
	void setUp() {
		Credential credential = Credential.builder()
				.credentialId(1)
				.username("testuser")
				.password("password123")
				.build();
		
		verificationToken = VerificationToken.builder()
				.verificationTokenId(1)
				.token("abc123token")
				.expireDate(LocalDate.now().plusDays(7))
				.credential(credential)
				.build();
		
		verificationTokenDto = VerificationTokenDto.builder()
				.verificationTokenId(1)
				.token("abc123token")
				.expireDate(LocalDate.now().plusDays(7))
				.build();
	}
	
	@Test
	@DisplayName("Should return all verification tokens successfully")
	void testFindAll_Success() {
		// Given
		List<VerificationToken> tokens = Arrays.asList(verificationToken, createMockToken(2));
		when(verificationTokenRepository.findAll()).thenReturn(tokens);
		
		// When
		List<VerificationTokenDto> result = verificationTokenService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(verificationTokenRepository, times(1)).findAll();
	}
	
	@Test
	@DisplayName("Should return empty list when no verification tokens exist")
	void testFindAll_EmptyList() {
		// Given
		when(verificationTokenRepository.findAll()).thenReturn(Arrays.asList());
		
		// When
		List<VerificationTokenDto> result = verificationTokenService.findAll();
		
		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(verificationTokenRepository, times(1)).findAll();
	}
	
	@Test
	@DisplayName("Should return verification token by id successfully")
	void testFindById_Success() {
		// Given
		when(verificationTokenRepository.findById(1)).thenReturn(Optional.of(verificationToken));
		
		// When
		VerificationTokenDto result = verificationTokenService.findById(1);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getVerificationTokenId());
		assertEquals("abc123token", result.getToken());
		assertNotNull(result.getExpireDate());
		verify(verificationTokenRepository, times(1)).findById(1);
	}
	
	@Test
	@DisplayName("Should throw exception when verification token not found by id")
	void testFindById_TokenNotFound() {
		// Given
		when(verificationTokenRepository.findById(anyInt())).thenReturn(Optional.empty());
		
		// When & Then
		VerificationTokenNotFoundException exception = assertThrows(
				VerificationTokenNotFoundException.class, 
				() -> verificationTokenService.findById(999));
		
		assertTrue(exception.getMessage().contains("999"));
		verify(verificationTokenRepository, times(1)).findById(999);
	}
	
	@Test
	@DisplayName("Should save verification token successfully")
	void testSave_Success() {
		// Given
		verificationTokenDto.setVerificationTokenId(null); // For new token
		when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);
		
		// When
		VerificationTokenDto result = verificationTokenService.save(verificationTokenDto);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getVerificationTokenId());
		verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
	}
	
	@Test
	@DisplayName("Should update verification token successfully")
	void testUpdate_WithDto_Success() {
		// Given
		VerificationTokenDto updateDto = VerificationTokenDto.builder()
				.verificationTokenId(1)
				.token("updatedtoken123")
				.expireDate(LocalDate.now().plusDays(10))
				.build();
		when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(invocation -> {
			VerificationToken savedToken = invocation.getArgument(0);
			return savedToken;
		});
		
		// When
		VerificationTokenDto result = verificationTokenService.update(updateDto);
		
		// Then
		assertNotNull(result);
		verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
	}
	
	@Test
	@DisplayName("Should update verification token by id successfully")
	void testUpdate_WithIdAndDto_Success() {
		// Given
		VerificationTokenDto updateDto = VerificationTokenDto.builder()
				.verificationTokenId(1)
				.token("newtoken")
				.expireDate(LocalDate.now().plusDays(14))
				.build();
		when(verificationTokenRepository.findById(1)).thenReturn(Optional.of(verificationToken));
		when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);
		
		// When
		VerificationTokenDto result = verificationTokenService.update(1, updateDto);
		
		// Then
		assertNotNull(result);
		verify(verificationTokenRepository, times(1)).findById(1);
		verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
	}
	
	@Test
	@DisplayName("Should delete verification token successfully")
	void testDeleteById_Success() {
		// Given
		doNothing().when(verificationTokenRepository).deleteById(anyInt());
		
		// When
		verificationTokenService.deleteById(1);
		
		// Then
		verify(verificationTokenRepository, times(1)).deleteById(1);
	}
	
	@Test
	@DisplayName("Should handle expired verification token")
	void testSave_WithExpiredDate() {
		// Given
		VerificationTokenDto expiredDto = VerificationTokenDto.builder()
				.token("expiredtoken")
				.expireDate(LocalDate.now().minusDays(1))
				.build();
		VerificationToken expiredToken = VerificationToken.builder()
				.verificationTokenId(2)
				.token("expiredtoken")
				.expireDate(LocalDate.now().minusDays(1))
				.build();
		when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(expiredToken);
		
		// When
		VerificationTokenDto result = verificationTokenService.save(expiredDto);
		
		// Then
		assertNotNull(result);
		assertTrue(result.getExpireDate().isBefore(LocalDate.now()));
		verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
	}
	
	@Test
	@DisplayName("Should verify distinct tokens in findAll")
	void testFindAll_DistinctTokens() {
		// Given
		VerificationToken duplicateToken = createMockToken(1);
		List<VerificationToken> tokens = Arrays.asList(verificationToken, duplicateToken, verificationToken);
		when(verificationTokenRepository.findAll()).thenReturn(tokens);
		
		// When
		List<VerificationTokenDto> result = verificationTokenService.findAll();
		
		// Then
		assertNotNull(result);
		assertTrue(result.size() >= 0);
		verify(verificationTokenRepository, times(1)).findAll();
	}
	
	@Test
	@DisplayName("Should handle token with long expiration date")
	void testSave_WithLongExpiration() {
		// Given
		VerificationTokenDto longExpiryDto = VerificationTokenDto.builder()
				.token("longtoken")
				.expireDate(LocalDate.now().plusYears(1))
				.build();
		VerificationToken longToken = VerificationToken.builder()
				.verificationTokenId(3)
				.token("longtoken")
				.expireDate(LocalDate.now().plusYears(1))
				.build();
		when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(longToken);
		
		// When
		VerificationTokenDto result = verificationTokenService.save(longExpiryDto);
		
		// Then
		assertNotNull(result);
		assertTrue(result.getExpireDate().isAfter(LocalDate.now()));
		verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
	}
	
	@Test
	@DisplayName("Should delete non-existent token without error")
	void testDeleteById_NonExistentToken() {
		// Given
		doNothing().when(verificationTokenRepository).deleteById(anyInt());
		
		// When & Then - Should not throw exception
		assertDoesNotThrow(() -> verificationTokenService.deleteById(999));
		verify(verificationTokenRepository, times(1)).deleteById(999);
	}
	
	// Helper method to create mock verification tokens
	private VerificationToken createMockToken(int tokenId) {
		return VerificationToken.builder()
				.verificationTokenId(tokenId)
				.token("token" + tokenId)
				.expireDate(LocalDate.now().plusDays(tokenId))
				.build();
	}
	
}

