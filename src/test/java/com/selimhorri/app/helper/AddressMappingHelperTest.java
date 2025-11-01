package com.selimhorri.app.helper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.Address;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.dto.UserDto;

@DisplayName("AddressMappingHelper Test")
class AddressMappingHelperTest {
	
	private Address address;
	private AddressDto addressDto;
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
				.userDto(
					UserDto.builder()
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
	@DisplayName("Should map Address to AddressDto successfully")
	void testMap_AddressToDto_Success() {
		// When
		AddressDto result = AddressMappingHelper.map(address);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getAddressId());
		assertEquals("123 Main St", result.getFullAddress());
		assertEquals("12345", result.getPostalCode());
		assertEquals("New York", result.getCity());
		assertNotNull(result.getUserDto());
		assertEquals(1, result.getUserDto().getUserId());
		assertEquals("John", result.getUserDto().getFirstName());
	}
	
	@Test
	@DisplayName("Should map AddressDto to Address successfully")
	void testMap_DtoToAddress_Success() {
		// When
		Address result = AddressMappingHelper.map(addressDto);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getAddressId());
		assertEquals("123 Main St", result.getFullAddress());
		assertEquals("12345", result.getPostalCode());
		assertEquals("New York", result.getCity());
		assertNotNull(result.getUser());
		assertEquals(1, result.getUser().getUserId());
		assertEquals("John", result.getUser().getFirstName());
	}
	
	@Test
	@DisplayName("Should map Address with null user gracefully")
	void testMap_AddressWithNullUser() {
		// Given
		Address addressWithNullUser = Address.builder()
				.addressId(2)
				.fullAddress("456 Oak Ave")
				.postalCode("54321")
				.city("Los Angeles")
				.user(null)
				.build();
		
		// When
		AddressDto result = AddressMappingHelper.map(addressWithNullUser);
		
		// Then - Should handle null user gracefully without throwing exception
		assertNotNull(result);
		assertEquals(2, result.getAddressId());
		assertEquals("456 Oak Ave", result.getFullAddress());
		assertNull(result.getUserDto()); // user was null
	}
	
	@Test
	@DisplayName("Should map AddressDto with null userDto gracefully")
	void testMap_DtoWithNullUser() {
		// Given
		AddressDto dtoWithNullUser = AddressDto.builder()
				.addressId(2)
				.fullAddress("456 Oak Ave")
				.postalCode("54321")
				.city("Los Angeles")
				.userDto(null)
				.build();
		
		// When
		Address result = AddressMappingHelper.map(dtoWithNullUser);
		
		// Then - Should handle null userDto gracefully without throwing exception
		assertNotNull(result);
		assertEquals(2, result.getAddressId());
		assertEquals("456 Oak Ave", result.getFullAddress());
		assertNull(result.getUser()); // userDto was null
	}
	
	@Test
	@DisplayName("Should map Address with empty strings")
	void testMap_AddressWithEmptyStrings() {
		// Given
		Address addressWithEmpty = Address.builder()
				.addressId(3)
				.fullAddress("")
				.postalCode("")
				.city("")
				.user(user)
				.build();
		
		// When
		AddressDto result = AddressMappingHelper.map(addressWithEmpty);
		
		// Then
		assertNotNull(result);
		assertEquals(3, result.getAddressId());
		assertEquals("", result.getFullAddress());
		assertEquals("", result.getPostalCode());
		assertEquals("", result.getCity());
	}
	
	@Test
	@DisplayName("Should handle bidirectional mapping correctly")
	void testMap_BidirectionalMapping() {
		// Given
		AddressDto originalDto = addressDto;
		
		// When
		Address mappedAddress = AddressMappingHelper.map(originalDto);
		AddressDto mappedBackDto = AddressMappingHelper.map(mappedAddress);
		
		// Then
		assertEquals(originalDto.getAddressId(), mappedBackDto.getAddressId());
		assertEquals(originalDto.getFullAddress(), mappedBackDto.getFullAddress());
		assertEquals(originalDto.getPostalCode(), mappedBackDto.getPostalCode());
		assertEquals(originalDto.getCity(), mappedBackDto.getCity());
		assertEquals(originalDto.getUserDto().getUserId(), mappedBackDto.getUserDto().getUserId());
	}
	
	@Test
	@DisplayName("Should map Address with long address string")
	void testMap_AddressWithLongString() {
		// Given
		String longAddress = "1234 Very Long Street Name That Goes On And On And On "
				+ "And Has Many Words In It, Apartment Number 5678";
		Address addressWithLongString = Address.builder()
				.addressId(4)
				.fullAddress(longAddress)
				.postalCode("12345")
				.city("New York")
				.user(user)
				.build();
		
		// When
		AddressDto result = AddressMappingHelper.map(addressWithLongString);
		
		// Then
		assertNotNull(result);
		assertEquals(longAddress, result.getFullAddress());
	}
	
	@Test
	@DisplayName("Should handle Address with special characters")
	void testMap_AddressWithSpecialCharacters() {
		// Given
		Address addressWithSpecialChars = Address.builder()
				.addressId(5)
				.fullAddress("123 Main St., Apt #4B")
				.postalCode("10001-1234")
				.city("Saint John's")
				.user(user)
				.build();
		
		// When
		AddressDto result = AddressMappingHelper.map(addressWithSpecialChars);
		
		// Then
		assertNotNull(result);
		assertEquals("123 Main St., Apt #4B", result.getFullAddress());
		assertEquals("10001-1234", result.getPostalCode());
		assertEquals("Saint John's", result.getCity());
	}
	
	@Test
	@DisplayName("Should preserve all user fields in mapping")
	void testMap_PreserveUserFields() {
		// When
		AddressDto result = AddressMappingHelper.map(address);
		
		// Then
		assertNotNull(result.getUserDto());
		assertEquals("john.doe@example.com", result.getUserDto().getEmail());
		assertEquals("1234567890", result.getUserDto().getPhone());
		assertEquals("http://example.com/image.jpg", result.getUserDto().getImageUrl());
	}
	
	@Test
	@DisplayName("Should handle Address with international address format")
	void testMap_InternationalAddressFormat() {
		// Given
		Address internationalAddress = Address.builder()
				.addressId(6)
				.fullAddress("Calle Principal 123, 3º A")
				.postalCode("28001")
				.city("Madrid")
				.user(user)
				.build();
		
		// When
		AddressDto result = AddressMappingHelper.map(internationalAddress);
		
		// Then
		assertNotNull(result);
		assertEquals("Calle Principal 123, 3º A", result.getFullAddress());
		assertEquals("28001", result.getPostalCode());
		assertEquals("Madrid", result.getCity());
	}
	
}

