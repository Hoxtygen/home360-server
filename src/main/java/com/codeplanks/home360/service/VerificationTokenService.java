package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.verificationToken.VerificationToken;

public interface VerificationTokenService {
  VerificationToken generateNewVerificationToken(String oldVerificationToken);
  VerificationToken validateVerificationToken(String token);
}
