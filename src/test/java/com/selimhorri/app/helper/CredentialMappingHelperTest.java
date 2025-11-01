package com.selimhorri.app.helper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.CredentialDto;

@DisplayName("CredentialMappingHelper Test")
class CredentialMappingHelperTest {
	
	private Credential credential;
	private CredentialDto credentialDto;
	private User user;
	
	@BeforeEach
	void setUp() {
		user = User.builder()
				.userId(1)
				.firstName("John")
				.lastName("Doe")
				.email("john.doe@example.com")
				.phone("1234567890")
				.imageUrl("http://example.com/image.jpg")
				.build();
		
		credential = Credential.builder()
				.credentialId(1)
				.username("testuser")
				.password("password123")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(true)
				.isAccountNonExpired(true)
				.isAccountNonLocked(true)
				.isCredentialsNonExpired(true)
				.user(user)
				.build();
		
		credentialDto = CredentialDto.builder()
				.credentialId(1)
				.username("testuser")
				.password("password123")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(true)
				.isAccountNonExpired(true)
				.isAccountNonLocked(true)
				.isCredentialsNonExpired(true)
				.userDto(
					com.selimhorri.app.dto.UserDto.builder()
						.userId(1)
						.firstName("John")
						.lastName("Doe")
						.email("john.doe@example.com")
						.phone("1234567890")
						.imageUrl("http://example.com/image.jpg")
						.build())
				.build();
	}
	
	@Test
	@DisplayName("Should map Credential to CredentialDto successfully")
	void testMap_CredentialToDto_Success() {
		// When
		CredentialDto result = CredentialMappingHelper.map(credential);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getCredentialId());
		assertEquals("testuser", result.getUsername());
		assertEquals("password123", result.getPassword());
		assertEquals(RoleBasedAuthority.ROLE_USER, result.getRoleBasedAuthority());
		assertTrue(result.getIsEnabled());
		assertTrue(result.getIsAccountNonExpired());
		assertTrue(result.getIsAccountNonLocked());
		assertTrue(result.getIsCredentialsNonExpired());
		assertNotNull(result.getUserDto());
		assertEquals(1, result.getUserDto().getUserId());
	}
	
	@Test
	@DisplayName("Should map CredentialDto to Credential successfully")
	void testMap_DtoToCredential_Success() {
		// When
		Credential result = CredentialMappingHelper.map(credentialDto);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getCredentialId());
		assertEquals("testuser", result.getUsername());
		assertEquals("password123", result.getPassword());
		assertEquals(RoleBasedAuthority.ROLE_USER, result.getRoleBasedAuthority());
		assertTrue(result.getIsEnabled());
		assertTrue(result.getIsAccountNonExpired());
		assertTrue(result.getIsAccountNonLocked());
		assertTrue(result.getIsCredentialsNonExpired());
		assertNotNull(result.getUser());
		assertEquals(1, result.getUser().getUserId());
	}
	
	@Test
	@DisplayName("Should map Credential with admin role")
	void testMap_CredentialWithAdminRole() {
		// Given
		Credential adminCredential = Credential.builder()
				.credentialId(2)
				.username("admin")
				.password("admin123")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_ADMIN)
				.isEnabled(true)
				.user(user)
				.build();
		
		// When
		CredentialDto result = CredentialMappingHelper.map(adminCredential);
		
		// Then
		assertNotNull(result);
		assertEquals(RoleBasedAuthority.ROLE_ADMIN, result.getRoleBasedAuthority());
	}
	
	@Test
	@DisplayName("Should map Credential with disabled account")
	void testMap_DisabledCredential() {
		// Given
		Credential disabledCredential = Credential.builder()
				.credentialId(3)
				.username("disabled")
				.password("pass")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(false)
				.isAccountNonExpired(false)
				.isAccountNonLocked(false)
				.isCredentialsNonExpired(false)
				.user(user)
				.build();
		
		// When
		CredentialDto result = CredentialMappingHelper.map(disabledCredential);
		
		// Then
		assertNotNull(result);
		assertFalse(result.getIsEnabled());
		assertFalse(result.getIsAccountNonExpired());
		assertFalse(result.getIsAccountNonLocked());
		assertFalse(result.getIsCredentialsNonExpired());
	}
	
	@Test
	@DisplayName("Should map Credential with null user")
	void testMap_CredentialWithNullUser() {
		// Given
		Credential credentialWithNullUser = Credential.builder()
				.credentialId(4)
				.username("user")
				.password("pass")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(true)
				.user(null)
				.build();
		
		// When
		CredentialDto result = CredentialMappingHelper.map(credentialWithNullUser);
		
		// Then - Should handle null user gracefully without throwing exception
		assertNotNull(result);
		assertEquals(4, result.getCredentialId());
		assertEquals("user", result.getUsername());
		assertNull(result.getUserDto()); // user was null
	}
	
	@Test
	@DisplayName("Should map CredentialDto with null userDto")
	void testMap_DtoWithNullUser() {
		// Given
		CredentialDto dtoWithNullUser = CredentialDto.builder()
				.credentialId(4)
				.username("user")
				.password("pass")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(true)
				.userDto(null)
				.build();
		
		// When
		Credential result = CredentialMappingHelper.map(dtoWithNullUser);
		
		// Then - Should handle null userDto gracefully without throwing exception
		assertNotNull(result);
		assertEquals(4, result.getCredentialId());
		assertEquals("user", result.getUsername());
		assertNull(result.getUser()); // userDto was null
	}
	
	@Test
	@DisplayName("Should handle bidirectional mapping correctly")
	void testMap_BidirectionalMapping() {
		// Given
		CredentialDto originalDto = credentialDto;
		
		// When
		Credential mappedCredential = CredentialMappingHelper.map(originalDto);
		CredentialDto mappedBackDto = CredentialMappingHelper.map(mappedCredential);
		
		// Then
		assertEquals(originalDto.getCredentialId(), mappedBackDto.getCredentialId());
		assertEquals(originalDto.getUsername(), mappedBackDto.getUsername());
		assertEquals(originalDto.getPassword(), mappedBackDto.getPassword());
		assertEquals(originalDto.getRoleBasedAuthority(), mappedBackDto.getRoleBasedAuthority());
		assertEquals(originalDto.getIsEnabled(), mappedBackDto.getIsEnabled());
		assertEquals(originalDto.getUserDto().getUserId(), mappedBackDto.getUserDto().getUserId());
	}
	
	@Test
	@DisplayName("Should map Credential with special characters in username")
	void testMap_CredentialWithSpecialUsername() {
		// Given
		Credential specialCredential = Credential.builder()
				.credentialId(5)
				.username("user.name_123")
				.password("p@ssw0rd!")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(true)
				.user(user)
				.build();
		
		// When
		CredentialDto result = CredentialMappingHelper.map(specialCredential);
		
		// Then
		assertNotNull(result);
		assertEquals("user.name_123", result.getUsername());
		assertEquals("p@ssw0rd!", result.getPassword());
	}
	
	@Test
	@DisplayName("Should handle locked account status")
	void testMap_LockedAccount() {
		// Given
		Credential lockedCredential = Credential.builder()
				.credentialId(6)
				.username("lockeduser")
				.password("pass")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(true)
				.isAccountNonLocked(false)
				.user(user)
				.build();
		
		// When
		CredentialDto result = CredentialMappingHelper.map(lockedCredential);
		
		// Then
		assertNotNull(result);
		assertTrue(result.getIsEnabled());
		assertFalse(result.getIsAccountNonLocked());
	}
	
	@Test
	@DisplayName("Should preserve all user fields in mapping")
	void testMap_PreserveUserFields() {
		// When
		CredentialDto result = CredentialMappingHelper.map(credential);
		
		// Then
		assertNotNull(result.getUserDto());
		assertEquals("john.doe@example.com", result.getUserDto().getEmail());
		assertEquals("1234567890", result.getUserDto().getPhone());
		assertEquals("http://example.com/image.jpg", result.getUserDto().getImageUrl());
	}
	
}

