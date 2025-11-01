package com.selimhorri.app.helper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.domain.VerificationToken;
import com.selimhorri.app.dto.VerificationTokenDto;

@DisplayName("VerificationTokenMappingHelper Test")
class VerificationTokenMappingHelperTest {
	
	private VerificationToken verificationToken;
	private VerificationTokenDto verificationTokenDto;
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
	@DisplayName("Should map VerificationToken to VerificationTokenDto successfully")
	void testMap_TokenToDto_Success() {
		// When
		VerificationTokenDto result = VerificationTokenMappingHelper.map(verificationToken);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getVerificationTokenId());
		assertEquals("abc123token", result.getToken());
		assertEquals(LocalDate.now().plusDays(7), result.getExpireDate());
		assertNotNull(result.getCredentialDto());
		assertEquals(1, result.getCredentialDto().getCredentialId());
		assertEquals("testuser", result.getCredentialDto().getUsername());
	}
	
	@Test
	@DisplayName("Should map VerificationTokenDto to VerificationToken successfully")
	void testMap_DtoToToken_Success() {
		// When
		VerificationToken result = VerificationTokenMappingHelper.map(verificationTokenDto);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getVerificationTokenId());
		assertEquals("abc123token", result.getToken());
		assertEquals(LocalDate.now().plusDays(7), result.getExpireDate());
		assertNotNull(result.getCredential());
		assertEquals(1, result.getCredential().getCredentialId());
		assertEquals("testuser", result.getCredential().getUsername());
	}
	
	@Test
	@DisplayName("Should map VerificationToken with expired date")
	void testMap_TokenWithExpiredDate() {
		// Given
		VerificationToken expiredToken = VerificationToken.builder()
				.verificationTokenId(2)
				.token("expired")
				.expireDate(LocalDate.now().minusDays(1))
				.credential(credential)
				.build();
		
		// When
		VerificationTokenDto result = VerificationTokenMappingHelper.map(expiredToken);
		
		// Then
		assertNotNull(result);
		assertTrue(result.getExpireDate().isBefore(LocalDate.now()));
	}
	
	@Test
	@DisplayName("Should map VerificationToken with long expiration")
	void testMap_TokenWithLongExpiration() {
		// Given
		VerificationToken longToken = VerificationToken.builder()
				.verificationTokenId(3)
				.token("longtoken")
				.expireDate(LocalDate.now().plusYears(1))
				.credential(credential)
				.build();
		
		// When
		VerificationTokenDto result = VerificationTokenMappingHelper.map(longToken);
		
		// Then
		assertNotNull(result);
		assertTrue(result.getExpireDate().isAfter(LocalDate.now()));
	}
	
	@Test
	@DisplayName("Should map VerificationToken with null credential")
	void testMap_TokenWithNullCredential() {
		// Given
		VerificationToken tokenWithNullCredential = VerificationToken.builder()
				.verificationTokenId(4)
				.token("token")
				.expireDate(LocalDate.now().plusDays(1))
				.credential(null)
				.build();
		
		// When
		VerificationTokenDto result = VerificationTokenMappingHelper.map(tokenWithNullCredential);
		
		// Then - Should handle null credential gracefully without throwing exception
		assertNotNull(result);
		assertEquals(4, result.getVerificationTokenId());
		assertEquals("token", result.getToken());
		assertNull(result.getCredentialDto()); // credential was null
	}
	
	@Test
	@DisplayName("Should map VerificationTokenDto with null credentialDto")
	void testMap_DtoWithNullCredential() {
		// Given
		VerificationTokenDto dtoWithNullCredential = VerificationTokenDto.builder()
				.verificationTokenId(4)
				.token("token")
				.expireDate(LocalDate.now().plusDays(1))
				.credentialDto(null)
				.build();
		
		// When
		VerificationToken result = VerificationTokenMappingHelper.map(dtoWithNullCredential);
		
		// Then - Should handle null credentialDto gracefully without throwing exception
		assertNotNull(result);
		assertEquals(4, result.getVerificationTokenId());
		assertEquals("token", result.getToken());
		assertNull(result.getCredential()); // credentialDto was null
	}
	
	@Test
	@DisplayName("Should handle bidirectional mapping correctly")
	void testMap_BidirectionalMapping() {
		// Given
		VerificationTokenDto originalDto = verificationTokenDto;
		
		// When
		VerificationToken mappedToken = VerificationTokenMappingHelper.map(originalDto);
		VerificationTokenDto mappedBackDto = VerificationTokenMappingHelper.map(mappedToken);
		
		// Then
		assertEquals(originalDto.getVerificationTokenId(), mappedBackDto.getVerificationTokenId());
		assertEquals(originalDto.getToken(), mappedBackDto.getToken());
		assertEquals(originalDto.getExpireDate(), mappedBackDto.getExpireDate());
		assertEquals(originalDto.getCredentialDto().getCredentialId(), 
				mappedBackDto.getCredentialDto().getCredentialId());
	}
	
	@Test
	@DisplayName("Should map VerificationToken with special characters in token")
	void testMap_TokenWithSpecialCharacters() {
		// Given
		VerificationToken specialToken = VerificationToken.builder()
				.verificationTokenId(5)
				.token("a!b@c#d$e%f&g*h")
				.expireDate(LocalDate.now().plusDays(5))
				.credential(credential)
				.build();
		
		// When
		VerificationTokenDto result = VerificationTokenMappingHelper.map(specialToken);
		
		// Then
		assertNotNull(result);
		assertEquals("a!b@c#d$e%f&g*h", result.getToken());
	}
	
	@Test
	@DisplayName("Should map VerificationToken with long token string")
	void testMap_TokenWithLongToken() {
		// Given
		String longToken = "a".repeat(256) + "b".repeat(256) + "c".repeat(256);
		VerificationToken tokenWithLongString = VerificationToken.builder()
				.verificationTokenId(6)
				.token(longToken)
				.expireDate(LocalDate.now().plusDays(1))
				.credential(credential)
				.build();
		
		// When
		VerificationTokenDto result = VerificationTokenMappingHelper.map(tokenWithLongString);
		
		// Then
		assertNotNull(result);
		assertEquals(longToken, result.getToken());
	}
	
	@Test
	@DisplayName("Should preserve all credential fields in mapping")
	void testMap_PreserveCredentialFields() {
		// When
		VerificationTokenDto result = VerificationTokenMappingHelper.map(verificationToken);
		
		// Then
		assertNotNull(result.getCredentialDto());
		assertEquals("password123", result.getCredentialDto().getPassword());
		assertTrue(result.getCredentialDto().getIsEnabled());
		assertTrue(result.getCredentialDto().getIsAccountNonExpired());
		assertTrue(result.getCredentialDto().getIsAccountNonLocked());
		assertTrue(result.getCredentialDto().getIsCredentialsNonExpired());
	}
	
	@Test
	@DisplayName("Should map VerificationToken with today expiration date")
	void testMap_TokenExpiringToday() {
		// Given
		VerificationToken tokenExpiringToday = VerificationToken.builder()
				.verificationTokenId(7)
				.token("todayToken")
				.expireDate(LocalDate.now())
				.credential(credential)
				.build();
		
		// When
		VerificationTokenDto result = VerificationTokenMappingHelper.map(tokenExpiringToday);
		
		// Then
		assertNotNull(result);
		assertEquals(LocalDate.now(), result.getExpireDate());
	}
	
}

