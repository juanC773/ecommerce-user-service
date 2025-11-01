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
import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.AddressNotFoundException;
import com.selimhorri.app.service.AddressService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Address Resource Integration Tests")
class AddressResourceTest {

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@Mock
	private AddressService addressService;

	@InjectMocks
	private AddressResource addressResource;

	private AddressDto sampleAddress;

	@BeforeEach
	void initializeTestData() {
		mockMvc = MockMvcBuilders.standaloneSetup(addressResource)
				.setControllerAdvice(new com.selimhorri.app.exception.ApiExceptionHandler())
				.build();
		objectMapper = new ObjectMapper();

		UserDto owner = UserDto.builder()
				.userId(1)
				.firstName("David")
				.lastName("Miller")
				.email("david.miller@example.com")
				.phone("555-1111")
				.build();

		sampleAddress = AddressDto.builder()
				.addressId(1)
				.fullAddress("789 Maple Street, Suite 200")
				.postalCode("90210")
				.city("Beverly Hills")
				.userDto(owner)
				.build();
	}

	@Test
	@DisplayName("GET /api/address - Should retrieve all addresses")
	void testRetrieveAllAddresses() throws Exception {
		// Arrange
		AddressDto secondAddress = AddressDto.builder()
				.addressId(2)
				.fullAddress("456 Oak Boulevard")
				.postalCode("10001")
				.city("New York")
				.build();

		List<AddressDto> addresses = Arrays.asList(sampleAddress, secondAddress);
		when(addressService.findAll()).thenReturn(addresses);

		// Act & Assert
		mockMvc.perform(get("/api/address")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.collection").isArray())
				.andExpect(jsonPath("$.collection[0].addressId").value(1))
				.andExpect(jsonPath("$.collection[0].city").value("Beverly Hills"))
				.andExpect(jsonPath("$.collection[1].addressId").value(2));

		verify(addressService, times(1)).findAll();
	}

	@Test
	@DisplayName("GET /api/address/{id} - Should return address when found")
	void testGetAddressByIdFound() throws Exception {
		// Arrange
		when(addressService.findById(1)).thenReturn(sampleAddress);

		// Act & Assert
		mockMvc.perform(get("/api/address/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.addressId").value(1))
				.andExpect(jsonPath("$.fullAddress").value("789 Maple Street, Suite 200"))
				.andExpect(jsonPath("$.postalCode").value("90210"))
				.andExpect(jsonPath("$.city").value("Beverly Hills"));

		verify(addressService, times(1)).findById(1);
	}

	@Test
	@DisplayName("GET /api/address/{id} - Should return error when address not found")
	void testGetAddressByIdNotFound() throws Exception {
		// Arrange
		when(addressService.findById(999))
				.thenThrow(new AddressNotFoundException("Address with id: 999 not found"));

		// Act & Assert
		mockMvc.perform(get("/api/address/999")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		verify(addressService, times(1)).findById(999);
	}

	@Test
	@DisplayName("POST /api/address - Should add new address")
	void testAddNewAddress() throws Exception {
		// Arrange
		AddressDto newAddress = AddressDto.builder()
				.fullAddress("123 Pine Avenue")
				.postalCode("33139")
				.city("Miami")
				.build();

		AddressDto createdAddress = AddressDto.builder()
				.addressId(3)
				.fullAddress("123 Pine Avenue")
				.postalCode("33139")
				.city("Miami")
				.build();

		when(addressService.save(any(AddressDto.class))).thenReturn(createdAddress);

		// Act & Assert
		mockMvc.perform(post("/api/address")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newAddress)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.addressId").value(3))
				.andExpect(jsonPath("$.fullAddress").value("123 Pine Avenue"))
				.andExpect(jsonPath("$.city").value("Miami"));

		verify(addressService, times(1)).save(any(AddressDto.class));
	}

	@Test
	@DisplayName("PUT /api/address - Should modify existing address")
	void testModifyAddress() throws Exception {
		// Arrange
		AddressDto modifiedAddress = AddressDto.builder()
				.addressId(1)
				.fullAddress("789 Maple Street, Apartment 5B")
				.postalCode("90211")
				.city("Beverly Hills")
				.build();

		when(addressService.update(any(AddressDto.class))).thenReturn(modifiedAddress);

		// Act & Assert
		mockMvc.perform(put("/api/address")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(modifiedAddress)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.addressId").value(1))
				.andExpect(jsonPath("$.fullAddress").value("789 Maple Street, Apartment 5B"))
				.andExpect(jsonPath("$.postalCode").value("90211"));

		verify(addressService, times(1)).update(any(AddressDto.class));
	}

	@Test
	@DisplayName("PUT /api/address/{id} - Should modify address by identifier")
	void testModifyAddressById() throws Exception {
		// Arrange
		AddressDto updateData = AddressDto.builder()
				.fullAddress("999 Elm Drive")
				.postalCode("94102")
				.city("San Francisco")
				.build();

		AddressDto updatedResult = AddressDto.builder()
				.addressId(1)
				.fullAddress("999 Elm Drive")
				.postalCode("94102")
				.city("San Francisco")
				.build();

		when(addressService.update(eq(1), any(AddressDto.class))).thenReturn(updatedResult);

		// Act & Assert
		mockMvc.perform(put("/api/address/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateData)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.addressId").value(1))
				.andExpect(jsonPath("$.fullAddress").value("999 Elm Drive"))
				.andExpect(jsonPath("$.city").value("San Francisco"));

		verify(addressService, times(1)).update(eq(1), any(AddressDto.class));
	}

	@Test
	@DisplayName("DELETE /api/address/{id} - Should remove address")
	void testRemoveAddress() throws Exception {
		// Arrange
		doNothing().when(addressService).deleteById(1);

		// Act & Assert
		mockMvc.perform(delete("/api/address/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("true"));

		verify(addressService, times(1)).deleteById(1);
	}

	@Test
	@DisplayName("GET /api/address - Should return empty list when no addresses")
	void testGetAllAddressesEmptyResult() throws Exception {
		// Arrange
		when(addressService.findAll()).thenReturn(Arrays.asList());

		// Act & Assert
		mockMvc.perform(get("/api/address")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.collection").isArray())
				.andExpect(jsonPath("$.collection").isEmpty());

		verify(addressService, times(1)).findAll();
	}
}

