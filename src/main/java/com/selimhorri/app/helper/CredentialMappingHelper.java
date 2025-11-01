package com.selimhorri.app.helper;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;

public interface CredentialMappingHelper {
	
	public static CredentialDto map(final Credential credential) {
		if (credential == null)
			return null;
		User user = credential.getUser();
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
		return CredentialDto.builder()
				.credentialId(credential.getCredentialId())
				.username(credential.getUsername())
				.password(credential.getPassword())
				.roleBasedAuthority(credential.getRoleBasedAuthority())
				.isEnabled(credential.getIsEnabled())
				.isAccountNonExpired(credential.getIsAccountNonExpired())
				.isAccountNonLocked(credential.getIsAccountNonLocked())
				.isCredentialsNonExpired(credential.getIsCredentialsNonExpired())
				.userDto(userDto)
				.build();
	}
	
	public static Credential map(final CredentialDto credentialDto) {
		if (credentialDto == null)
			return null;
		User user = null;
		if (credentialDto.getUserDto() != null) {
			user = User.builder()
					.userId(credentialDto.getUserDto().getUserId())
					.firstName(credentialDto.getUserDto().getFirstName())
					.lastName(credentialDto.getUserDto().getLastName())
					.imageUrl(credentialDto.getUserDto().getImageUrl())
					.email(credentialDto.getUserDto().getEmail())
					.phone(credentialDto.getUserDto().getPhone())
					.build();
		}
		Credential credential = Credential.builder()
				.credentialId(credentialDto.getCredentialId())
				.username(credentialDto.getUsername())
				.password(credentialDto.getPassword())
				.roleBasedAuthority(credentialDto.getRoleBasedAuthority())
				.isEnabled(credentialDto.getIsEnabled())
				.isAccountNonExpired(credentialDto.getIsAccountNonExpired())
				.isAccountNonLocked(credentialDto.getIsAccountNonLocked())
				.isCredentialsNonExpired(credentialDto.getIsCredentialsNonExpired())
				.user(user)
				.build();
		
		if (user != null) {
			user.setCredential(credential); // establecer la relación inversa
		}
		
		return credential;
	}
	
	
	
}






