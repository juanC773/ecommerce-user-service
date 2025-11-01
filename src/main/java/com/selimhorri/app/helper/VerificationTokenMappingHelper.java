package com.selimhorri.app.helper;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.VerificationToken;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.VerificationTokenDto;

public interface VerificationTokenMappingHelper {
	
	public static VerificationTokenDto map(final VerificationToken verificationToken) {
		if (verificationToken == null)
			return null;
		Credential cred = verificationToken.getCredential();
		CredentialDto credDto = null;
		if (cred != null) {
			credDto = CredentialDto.builder()
					.credentialId(cred.getCredentialId())
					.username(cred.getUsername())
					.password(cred.getPassword())
					.roleBasedAuthority(cred.getRoleBasedAuthority())
					.isEnabled(cred.getIsEnabled())
					.isAccountNonExpired(cred.getIsAccountNonExpired())
					.isAccountNonLocked(cred.getIsAccountNonLocked())
					.isCredentialsNonExpired(cred.getIsCredentialsNonExpired())
					.build();
		}
		return VerificationTokenDto.builder()
				.verificationTokenId(verificationToken.getVerificationTokenId())
				.token(verificationToken.getToken())
				.expireDate(verificationToken.getExpireDate())
				.credentialDto(credDto)
				.build();
	}
	
	public static VerificationToken map(final VerificationTokenDto verificationTokenDto) {
		if (verificationTokenDto == null)
			return null;
		Credential credential = null;
		if (verificationTokenDto.getCredentialDto() != null) {
			credential = Credential.builder()
					.credentialId(verificationTokenDto.getCredentialDto().getCredentialId())
					.username(verificationTokenDto.getCredentialDto().getUsername())
					.password(verificationTokenDto.getCredentialDto().getPassword())
					.roleBasedAuthority(verificationTokenDto.getCredentialDto().getRoleBasedAuthority())
					.isEnabled(verificationTokenDto.getCredentialDto().getIsEnabled())
					.isAccountNonExpired(verificationTokenDto.getCredentialDto().getIsAccountNonExpired())
					.isAccountNonLocked(verificationTokenDto.getCredentialDto().getIsAccountNonLocked())
					.isCredentialsNonExpired(verificationTokenDto.getCredentialDto().getIsCredentialsNonExpired())
					.build();
		}
		return VerificationToken.builder()
				.verificationTokenId(verificationTokenDto.getVerificationTokenId())
				.token(verificationTokenDto.getToken())
				.expireDate(verificationTokenDto.getExpireDate())
				.credential(credential)
				.build();
	}

	public static VerificationToken mapOnlyVerificationToken(final VerificationTokenDto verificationTokenDto) {
		if (verificationTokenDto == null)
			return null;
		return VerificationToken.builder()
				.verificationTokenId(verificationTokenDto.getVerificationTokenId())
				.token(verificationTokenDto.getToken())
				.expireDate(verificationTokenDto.getExpireDate())
				.build();
	}
	
	
	
}






