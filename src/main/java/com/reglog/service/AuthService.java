package com.reglog.service;

import com.reglog.dto.AuthResponse;
import com.reglog.dto.LoginRequest;
import com.reglog.dto.UserResponse;
import com.reglog.entity.JwtToken;
import com.reglog.entity.User;
import com.reglog.exception.AppExceptions;
import com.reglog.repository.JwtTokenRepository;
import com.reglog.repository.UserRepository;
import com.reglog.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenRepository jwtTokenRepository;
    private final JwtService jwtService;

    @Value("${jwt.cookie.name:jwt_token}")
    private String jwtCookieName;

    @Value("${jwt.cookie.max-age:86400}")
    private long cookieMaxAge;

    @Value("${jwt.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${jwt.cookie.same-site:Lax}")
    private String cookieSameSite;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtTokenRepository jwtTokenRepository,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenRepository = jwtTokenRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginResult login(LoginRequest request) {
        // 1. Authenticate credentials via Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getName().trim(), request.getPassword())
        );

        // 2. Load User details from DB
        User user = userRepository.findByName(authentication.getName())
                .or(() -> userRepository.findByEmail(authentication.getName()))
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("User not found: " + request.getName()));

        // 3. Generate JWT with claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());

        String jwt = jwtService.generateToken(user.getName(), claims);
        Date expirationDate = jwtService.extractExpiration(jwt);
        LocalDateTime expiresAt = expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // 4. Save token in jwt_tokens table
        JwtToken jwtToken = new JwtToken(
                null,
                user,
                jwt,
                expiresAt,
                LocalDateTime.now()
        );
        jwtTokenRepository.save(jwtToken);

        // 5. Create HttpOnly cookie
        ResponseCookie cookie = ResponseCookie.from(jwtCookieName, jwt)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(cookieMaxAge)
                .build();

        UserResponse userResponse = new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone());
        AuthResponse authResponse = new AuthResponse("Login successful", userResponse);

        return new LoginResult(cookie, authResponse);
    }

    public ResponseCookie logout() {
        return ResponseCookie.from(jwtCookieName, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(0)
                .build();
    }

    public static class LoginResult {
        private final ResponseCookie cookie;
        private final AuthResponse authResponse;

        public LoginResult(ResponseCookie cookie, AuthResponse authResponse) {
            this.cookie = cookie;
            this.authResponse = authResponse;
        }

        public ResponseCookie getCookie() {
            return cookie;
        }

        public AuthResponse getAuthResponse() {
            return authResponse;
        }
    }
}
