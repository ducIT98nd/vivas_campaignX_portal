package com.vivas.campaignx.service;

import com.vivas.campaignx.common.ResponseEntity;
import com.vivas.campaignx.entity.Users;

import java.util.Optional;

public interface AuthService {

    ResponseEntity forgotPassword(String email);

    Optional<Users> findByEmail(String email);
}
