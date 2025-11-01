package com.selimhorri.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import com.selimhorri.app.domain.Address;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.exception.wrapper.AddressNotFoundException;
import com.selimhorri.app.repository.AddressRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressServiceImpl Test")
class AddressServiceImplTest {

	@Mock
	private AddressRepository addressRepository;
	
	@InjectMocks
	private AddressServiceImpl addressService;
	
	private Address address;
	private AddressDto addressDto;
	
	@BeforeEach
	void setUp() {
		User user = User.builder()
				.userId(1)
				.firstName("John")
				.lastName("Doe")
				.build();
		
		address = Address.builder()
				.addressId(1)
				.fullAddress("123 Main St")
				.postalCode("12345")
				.city("New York")
				.user(user)
				.build();
		
		addressDto = AddressDto.builder()
				.addressId(1)
				.fullAddress("123 Main St")
				.postalCode("12345")
				.city("New York")
				.build();
	}
	
	@Test
	@DisplayName("Should return all addresses successfully")
	void testFindAll_Success() {
		// Given
		List<Address> addresses = Arrays.asList(address, createMockAddress(2));
		when(addressRepository.findAll()).thenReturn(addresses);
		
		// When
		List<AddressDto> result = addressService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(addressRepository, times(1)).findAll();
	}
	
	@Test
	@DisplayName("Should return empty list when no addresses exist")
	void testFindAll_EmptyList() {
		// Given
		when(addressRepository.findAll()).thenReturn(Arrays.asList());
		
		// When
		List<AddressDto> result = addressService.findAll();
		
		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(addressRepository, times(1)).findAll();
	}
	
	@Test
	@DisplayName("Should return address by id successfully")
	void testFindById_Success() {
		// Given
		when(addressRepository.findById(1)).thenReturn(Optional.of(address));
		
		// When
		AddressDto result = addressService.findById(1);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getAddressId());
		assertEquals("123 Main St", result.getFullAddress());
		assertEquals("12345", result.getPostalCode());
		assertEquals("New York", result.getCity());
		verify(addressRepository, times(1)).findById(1);
	}
	
	@Test
	@DisplayName("Should throw exception when address not found by id")
	void testFindById_AddressNotFound() {
		// Given
		when(addressRepository.findById(anyInt())).thenReturn(Optional.empty());
		
		// When & Then
		AddressNotFoundException exception = assertThrows(
				AddressNotFoundException.class, 
				() -> addressService.findById(999));
		
		assertTrue(exception.getMessage().contains("999"));
		verify(addressRepository, times(1)).findById(999);
	}
	
	@Test
	@DisplayName("Should save address successfully")
	void testSave_Success() {
		// Given
		addressDto.setAddressId(null); // For new address
		when(addressRepository.save(any(Address.class))).thenReturn(address);
		
		// When
		AddressDto result = addressService.save(addressDto);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getAddressId());
		verify(addressRepository, times(1)).save(any(Address.class));
	}
	
	@Test
	@DisplayName("Should update address successfully")
	void testUpdate_WithDto_Success() {
		// Given
		AddressDto updateDto = AddressDto.builder()
				.addressId(1)
				.fullAddress("456 Oak Ave")
				.postalCode("54321")
				.city("Los Angeles")
				.build();
		when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
			Address savedAddress = invocation.getArgument(0);
			return savedAddress;
		});
		
		// When
		AddressDto result = addressService.update(updateDto);
		
		// Then
		assertNotNull(result);
		verify(addressRepository, times(1)).save(any(Address.class));
	}
	
	@Test
	@DisplayName("Should update address by id successfully")
	void testUpdate_WithIdAndDto_Success() {
		// Given
		AddressDto updateDto = AddressDto.builder()
				.addressId(1)
				.fullAddress("Updated Address")
				.city("Chicago")
				.build();
		when(addressRepository.findById(1)).thenReturn(Optional.of(address));
		when(addressRepository.save(any(Address.class))).thenReturn(address);
		
		// When
		AddressDto result = addressService.update(1, updateDto);
		
		// Then
		assertNotNull(result);
		verify(addressRepository, times(1)).findById(1);
		verify(addressRepository, times(1)).save(any(Address.class));
	}
	
	@Test
	@DisplayName("Should delete address successfully")
	void testDeleteById_Success() {
		// Given
		doNothing().when(addressRepository).deleteById(anyInt());
		
		// When
		addressService.deleteById(1);
		
		// Then
		verify(addressRepository, times(1)).deleteById(1);
	}
	
	@Test
	@DisplayName("Should handle multiple addresses with distinct values")
	void testFindAll_DistinctAddresses() {
		// Given
		Address addr2 = createMockAddress(2);
		Address addr3 = createMockAddress(3);
		List<Address> addresses = Arrays.asList(address, addr2, addr3);
		when(addressRepository.findAll()).thenReturn(addresses);
		
		// When
		List<AddressDto> result = addressService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(3, result.size());
		verify(addressRepository, times(1)).findAll();
	}
	
	@Test
	@DisplayName("Should update address with null fields gracefully")
	void testUpdate_WithNullFields() {
		// Given
		AddressDto updateDto = AddressDto.builder()
				.addressId(1)
				.fullAddress(null)
				.city(null)
				.build();
		when(addressRepository.findById(1)).thenReturn(Optional.of(address));
		when(addressRepository.save(any(Address.class))).thenReturn(address);
		
		// When
		AddressDto result = addressService.update(1, updateDto);
		
		// Then
		assertNotNull(result);
		verify(addressRepository, times(1)).save(any(Address.class));
	}
	
	@Test
	@DisplayName("Should handle delete on non-existent address")
	void testDeleteById_NonExistentAddress() {
		// Given
		doNothing().when(addressRepository).deleteById(anyInt());
		
		// When & Then - Should not throw exception
		assertDoesNotThrow(() -> addressService.deleteById(999));
		verify(addressRepository, times(1)).deleteById(999);
	}
	
	@Test
	@DisplayName("Should save address with all optional fields")
	void testSave_WithAllFields() {
		// Given
		AddressDto fullDto = AddressDto.builder()
				.fullAddress("789 Pine Rd")
				.postalCode("98765")
				.city("Miami")
				.build();
		Address savedAddress = Address.builder()
				.addressId(1)
				.fullAddress("789 Pine Rd")
				.postalCode("98765")
				.city("Miami")
				.build();
		when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);
		
		// When
		AddressDto result = addressService.save(fullDto);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getAddressId());
		verify(addressRepository, times(1)).save(any(Address.class));
	}
	
	// Helper method to create mock addresses
	private Address createMockAddress(int addressId) {
		return Address.builder()
				.addressId(addressId)
				.fullAddress("Address " + addressId)
				.postalCode("00000")
				.city("City " + addressId)
				.build();
	}
	
}

