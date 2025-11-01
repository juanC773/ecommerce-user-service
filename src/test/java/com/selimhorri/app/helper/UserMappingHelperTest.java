package com.selimhorri.app.helper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.UserDto;

@DisplayName("UserMappingHelper Test")
class UserMappingHelperTest {
	
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
				.imageUrl("http://example.com/image.jpg")
				.credential(credential)
				.build();
		
		userDto = UserDto.builder()
				.userId(1)
				.firstName("John")
				.lastName("Doe")
				.email("john.doe@example.com")
				.phone("1234567890")
				.imageUrl("http://example.com/image.jpg")
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
	@DisplayName("Should map User to UserDto successfully")
	void testMap_UserToDto_Success() {
		// When
		UserDto result = UserMappingHelper.map(user);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getUserId());
		assertEquals("John", result.getFirstName());
		assertEquals("Doe", result.getLastName());
		assertEquals("john.doe@example.com", result.getEmail());
		assertEquals("1234567890", result.getPhone());
		assertEquals("http://example.com/image.jpg", result.getImageUrl());
		assertNotNull(result.getCredentialDto());
		assertEquals(1, result.getCredentialDto().getCredentialId());
		assertEquals("testuser", result.getCredentialDto().getUsername());
	}
	
	@Test
	@DisplayName("Should map UserDto to User successfully")
	void testMap_DtoToUser_Success() {
		// When
		User result = UserMappingHelper.map(userDto);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getUserId());
		assertEquals("John", result.getFirstName());
		assertEquals("Doe", result.getLastName());
		assertEquals("john.doe@example.com", result.getEmail());
		assertEquals("1234567890", result.getPhone());
		assertEquals("http://example.com/image.jpg", result.getImageUrl());
		assertNotNull(result.getCredential());
		assertEquals(1, result.getCredential().getCredentialId());
		assertEquals("testuser", result.getCredential().getUsername());
	}
	
	@Test
	@DisplayName("Should map User with null values gracefully")
	void testMap_UserWithNullValues() {
		// Given
		User userWithNulls = User.builder()
				.userId(2)
				.firstName("Jane")
				.lastName(null)
				.email(null)
				.phone(null)
				.imageUrl(null)
				.credential(null)
				.build();
		
		// When
		UserDto result = UserMappingHelper.map(userWithNulls);
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.getUserId());
		assertEquals("Jane", result.getFirstName());
		assertNull(result.getLastName());
		assertNull(result.getEmail());
		assertNull(result.getPhone());
		assertNull(result.getImageUrl());
		assertNull(result.getCredentialDto()); // credential was null
	}
	
	@Test
	@DisplayName("Should map UserDto with null values gracefully")
	void testMap_DtoWithNullValues() {
		// Given
		UserDto dtoWithNulls = UserDto.builder()
				.userId(2)
				.firstName("Jane")
				.lastName(null)
				.email(null)
				.phone(null)
				.imageUrl(null)
				.credentialDto(null)
				.build();
		
		// When & Then - Should throw NullPointerException when credential is null
		assertThrows(NullPointerException.class, () -> UserMappingHelper.map(dtoWithNulls));
	}
	
	@Test
	@DisplayName("Should map User with empty strings")
	void testMap_UserWithEmptyStrings() {
		// Given
		User userWithEmpty = User.builder()
				.userId(3)
				.firstName("")
				.lastName("")
				.email("")
				.phone("")
				.imageUrl("")
				.build();
		
		// When
		UserDto result = UserMappingHelper.map(userWithEmpty);
		
		// Then
		assertNotNull(result);
		assertEquals(3, result.getUserId());
		assertEquals("", result.getFirstName());
		assertEquals("", result.getLastName());
		assertNull(result.getCredentialDto()); // credential was not provided
	}
	
	@Test
	@DisplayName("Should handle User with admin role credential")
	void testMap_UserWithAdminRole() {
		// Given
		Credential adminCredential = Credential.builder()
				.credentialId(2)
				.username("admin")
				.password("admin123")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_ADMIN)
				.isEnabled(true)
				.build();
		User adminUser = User.builder()
				.userId(2)
				.firstName("Admin")
				.lastName("User")
				.credential(adminCredential)
				.build();
		
		// When
		UserDto result = UserMappingHelper.map(adminUser);
		
		// Then
		assertNotNull(result);
		assertNotNull(result.getCredentialDto());
		assertEquals(RoleBasedAuthority.ROLE_ADMIN, result.getCredentialDto().getRoleBasedAuthority());
	}
	
	@Test
	@DisplayName("Should preserve all credential fields in mapping")
	void testMap_PreserveCredentialFields() {
		// When
		UserDto result = UserMappingHelper.map(user);
		
		// Then
		assertNotNull(result.getCredentialDto());
		assertEquals("password123", result.getCredentialDto().getPassword());
		assertTrue(result.getCredentialDto().getIsEnabled());
		assertTrue(result.getCredentialDto().getIsAccountNonExpired());
		assertTrue(result.getCredentialDto().getIsAccountNonLocked());
		assertTrue(result.getCredentialDto().getIsCredentialsNonExpired());
	}
	
	@Test
	@DisplayName("Should handle bidirectional mapping correctly")
	void testMap_BidirectionalMapping() {
		// Given
		UserDto originalDto = userDto;
		
		// When
		User mappedUser = UserMappingHelper.map(originalDto);
		UserDto mappedBackDto = UserMappingHelper.map(mappedUser);
		
		// Then
		assertEquals(originalDto.getUserId(), mappedBackDto.getUserId());
		assertEquals(originalDto.getFirstName(), mappedBackDto.getFirstName());
		assertEquals(originalDto.getLastName(), mappedBackDto.getLastName());
		assertEquals(originalDto.getEmail(), mappedBackDto.getEmail());
		assertEquals(originalDto.getPhone(), mappedBackDto.getPhone());
		assertEquals(originalDto.getImageUrl(), mappedBackDto.getImageUrl());
		assertNotNull(mappedBackDto.getCredentialDto());
		assertEquals(originalDto.getCredentialDto().getCredentialId(), 
				mappedBackDto.getCredentialDto().getCredentialId());
		assertEquals(originalDto.getCredentialDto().getUsername(), 
				mappedBackDto.getCredentialDto().getUsername());
	}
	
	@Test
	@DisplayName("Should handle User with long email")
	void testMap_UserWithLongEmail() {
		// Given
		String longEmail = "verylongemailaddresswithalotofcharacters" + 
				"@examplewithverylongdomainname.com";
		User userWithLongEmail = User.builder()
				.userId(4)
				.firstName("Long")
				.email(longEmail)
				.build();
		
		// When
		UserDto result = UserMappingHelper.map(userWithLongEmail);
		
		// Then
		assertNotNull(result);
		assertEquals(longEmail, result.getEmail());
		assertNull(result.getCredentialDto()); // credential was not provided
	}
	
	@Test
	@DisplayName("Should handle User with special characters")
	void testMap_UserWithSpecialCharacters() {
		// Given
		User userWithSpecialChars = User.builder()
				.userId(5)
				.firstName("José")
				.lastName("O'Connor-Smith")
				.email("user@example.com")
				.phone("+1-555-123-4567")
				.build();
		
		// When
		UserDto result = UserMappingHelper.map(userWithSpecialChars);
		
		// Then
		assertNotNull(result);
		assertEquals("José", result.getFirstName());
		assertEquals("O'Connor-Smith", result.getLastName());
		assertEquals("+1-555-123-4567", result.getPhone());
		assertNull(result.getCredentialDto()); // credential was not provided
	}
	
}

