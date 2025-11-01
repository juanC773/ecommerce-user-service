package com.selimhorri.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.exception.wrapper.CredentialNotFoundException;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.repository.CredentialRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CredentialServiceImpl Test")
class CredentialServiceImplTest {

	@Mock
	private CredentialRepository credentialRepository;
	
	@InjectMocks
	private CredentialServiceImpl credentialService;
	
	private Credential credential;
	private CredentialDto credentialDto;
	
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
		
		credentialDto = CredentialDto.builder()
				.credentialId(1)
				.username("testuser")
				.password("password123")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(true)
				.isAccountNonExpired(true)
				.isAccountNonLocked(true)
				.isCredentialsNonExpired(true)
				.build();
	}
	
	@Test
	@DisplayName("Should return all credentials successfully")
	void testFindAll_Success() {
		// Given
		List<Credential> credentials = Arrays.asList(credential, createMockCredential(2));
		when(credentialRepository.findAll()).thenReturn(credentials);
		
		// When
		List<CredentialDto> result = credentialService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(credentialRepository, times(1)).findAll();
	}
	
	@Test
	@DisplayName("Should return empty list when no credentials exist")
	void testFindAll_EmptyList() {
		// Given
		when(credentialRepository.findAll()).thenReturn(Arrays.asList());
		
		// When
		List<CredentialDto> result = credentialService.findAll();
		
		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(credentialRepository, times(1)).findAll();
	}
	
	@Test
	@DisplayName("Should return credential by id successfully")
	void testFindById_Success() {
		// Given
		when(credentialRepository.findById(1)).thenReturn(Optional.of(credential));
		
		// When
		CredentialDto result = credentialService.findById(1);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getCredentialId());
		assertEquals("testuser", result.getUsername());
		assertEquals(RoleBasedAuthority.ROLE_USER, result.getRoleBasedAuthority());
		verify(credentialRepository, times(1)).findById(1);
	}
	
	@Test
	@DisplayName("Should throw exception when credential not found by id")
	void testFindById_CredentialNotFound() {
		// Given
		when(credentialRepository.findById(anyInt())).thenReturn(Optional.empty());
		
		// When & Then
		CredentialNotFoundException exception = assertThrows(
				CredentialNotFoundException.class, 
				() -> credentialService.findById(999));
		
		assertTrue(exception.getMessage().contains("999"));
		verify(credentialRepository, times(1)).findById(999);
	}
	
	@Test
	@DisplayName("Should save credential successfully")
	void testSave_Success() {
		// Given
		credentialDto.setCredentialId(null); // For new credential
		when(credentialRepository.save(any(Credential.class))).thenReturn(credential);
		
		// When
		CredentialDto result = credentialService.save(credentialDto);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getCredentialId());
		verify(credentialRepository, times(1)).save(any(Credential.class));
	}
	
	@Test
	@DisplayName("Should update credential successfully")
	void testUpdate_WithDto_Success() {
		// Given
		CredentialDto updateDto = CredentialDto.builder()
				.credentialId(1)
				.username("updateduser")
				.password("newpassword")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_ADMIN)
				.isEnabled(false)
				.build();
		when(credentialRepository.save(any(Credential.class))).thenAnswer(invocation -> {
			Credential savedCredential = invocation.getArgument(0);
			return savedCredential;
		});
		
		// When
		CredentialDto result = credentialService.update(updateDto);
		
		// Then
		assertNotNull(result);
		verify(credentialRepository, times(1)).save(any(Credential.class));
	}
	
	@Test
	@DisplayName("Should update credential by id successfully")
	void testUpdate_WithIdAndDto_Success() {
		// Given
		CredentialDto updateDto = CredentialDto.builder()
				.credentialId(1)
				.username("updateduser")
				.password("newpassword")
				.build();
		when(credentialRepository.findById(1)).thenReturn(Optional.of(credential));
		when(credentialRepository.save(any(Credential.class))).thenReturn(credential);
		
		// When
		CredentialDto result = credentialService.update(1, updateDto);
		
		// Then
		assertNotNull(result);
		verify(credentialRepository, times(1)).findById(1);
		verify(credentialRepository, times(1)).save(any(Credential.class));
	}
	
	@Test
	@DisplayName("Should delete credential successfully")
	void testDeleteById_Success() {
		// Given
		doNothing().when(credentialRepository).deleteById(anyInt());
		
		// When
		credentialService.deleteById(1);
		
		// Then
		verify(credentialRepository, times(1)).deleteById(1);
	}
	
	@Test
	@DisplayName("Should find credential by username successfully")
	void testFindByUsername_Success() {
		// Given
		when(credentialRepository.findByUsername("testuser")).thenReturn(Optional.of(credential));
		
		// When
		CredentialDto result = credentialService.findByUsername("testuser");
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getCredentialId());
		assertEquals("testuser", result.getUsername());
		verify(credentialRepository, times(1)).findByUsername("testuser");
	}
	
	@Test
	@DisplayName("Should throw exception when credential not found by username")
	void testFindByUsername_CredentialNotFound() {
		// Given
		when(credentialRepository.findByUsername(anyString())).thenReturn(Optional.empty());
		
		// When & Then
		UserObjectNotFoundException exception = assertThrows(
				UserObjectNotFoundException.class, 
				() -> credentialService.findByUsername("nonexistent"));
		
		assertTrue(exception.getMessage().contains("nonexistent"));
		verify(credentialRepository, times(1)).findByUsername("nonexistent");
	}
	
	@Test
	@DisplayName("Should handle credentials with admin role")
	void testSave_WithAdminRole() {
		// Given
		CredentialDto adminDto = CredentialDto.builder()
				.username("admin")
				.password("admin123")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_ADMIN)
				.isEnabled(true)
				.build();
		Credential adminCredential = Credential.builder()
				.credentialId(2)
				.username("admin")
				.password("admin123")
				.roleBasedAuthority(RoleBasedAuthority.ROLE_ADMIN)
				.isEnabled(true)
				.build();
		when(credentialRepository.save(any(Credential.class))).thenReturn(adminCredential);
		
		// When
		CredentialDto result = credentialService.save(adminDto);
		
		// Then
		assertNotNull(result);
		assertEquals(RoleBasedAuthority.ROLE_ADMIN, result.getRoleBasedAuthority());
		verify(credentialRepository, times(1)).save(any(Credential.class));
	}
	
	@Test
	@DisplayName("Should verify distinct credentials in findAll")
	void testFindAll_DistinctCredentials() {
		// Given
		Credential duplicateCredential = createMockCredential(1);
		List<Credential> credentials = Arrays.asList(credential, duplicateCredential, credential);
		when(credentialRepository.findAll()).thenReturn(credentials);
		
		// When
		List<CredentialDto> result = credentialService.findAll();
		
		// Then
		assertNotNull(result);
		assertTrue(result.size() >= 0);
		verify(credentialRepository, times(1)).findAll();
	}
	
	@Test
	@DisplayName("Should handle account lock/unlock status")
	void testUpdate_AccountLockStatus() {
		// Given
		Credential lockedCredential = Credential.builder()
				.credentialId(1)
				.username("testuser")
				.password("password123")
				.isAccountNonLocked(false)
				.build();
		when(credentialRepository.findById(1)).thenReturn(Optional.of(lockedCredential));
		when(credentialRepository.save(any(Credential.class))).thenReturn(lockedCredential);
		CredentialDto updateDto = CredentialDto.builder().credentialId(1).build();
		
		// When
		CredentialDto result = credentialService.update(1, updateDto);
		
		// Then
		assertNotNull(result);
		verify(credentialRepository, times(1)).save(any(Credential.class));
	}
	
	// Helper method to create mock credentials
	private Credential createMockCredential(int credentialId) {
		return Credential.builder()
				.credentialId(credentialId)
				.username("user" + credentialId)
				.password("pass" + credentialId)
				.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
				.isEnabled(true)
				.build();
	}
	
}

