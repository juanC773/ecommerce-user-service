package com.selimhorri.app.helper;

import com.selimhorri.app.domain.Address;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.dto.UserDto;

public interface AddressMappingHelper {
	
	public static AddressDto map(final Address address) {
		if (address == null)
			return null;
		User user = address.getUser();
		UserDto userDto = null;
		if (user != null) {
			userDto = UserDto.builder()
					.userId(user.getUserId())
					.firstName(user.getFirstName())
					.lastName(user.getLastName())
					.imageUrl(user.getImageUrl())
					.email(user.getEmail())
					.phone(user.getPhone())
					.build();
		}
		return AddressDto.builder()
				.addressId(address.getAddressId())
				.fullAddress(address.getFullAddress())
				.postalCode(address.getPostalCode())
				.city(address.getCity())
				.userDto(userDto)
				.build();
	}
	
	public static Address map(final AddressDto addressDto) {
		if (addressDto == null)
			return null;
		User user = null;
		if (addressDto.getUserDto() != null) {
			user = User.builder()
					.userId(addressDto.getUserDto().getUserId())
					.firstName(addressDto.getUserDto().getFirstName())
					.lastName(addressDto.getUserDto().getLastName())
					.imageUrl(addressDto.getUserDto().getImageUrl())
					.email(addressDto.getUserDto().getEmail())
					.phone(addressDto.getUserDto().getPhone())
					.build();
		}
		return Address.builder()
				.addressId(addressDto.getAddressId())
				.fullAddress(addressDto.getFullAddress())
				.postalCode(addressDto.getPostalCode())
				.city(addressDto.getCity())
				.user(user)
				.build();
	}
	
	
	
}










