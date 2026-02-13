package com.dorm.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "test-secret-key-for-jwt-testing-minimum-32-chars");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = jwtUtil.generateToken(1L, "testuser", 1);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void parseToken_ShouldExtractCorrectClaims() {
        Long userId = 1L;
        String username = "testuser";
        Integer role = 2;
        
        String token = jwtUtil.generateToken(userId, username, role);
        Claims claims = jwtUtil.parseToken(token);
        
        assertEquals(userId, claims.get("userId", Long.class));
        assertEquals(username, claims.get("username", String.class));
        assertEquals(role, claims.get("role", Integer.class));
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken(1L, "testuser", 1);
        
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void validateToken_ShouldReturnFalseForInvalidToken() {
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
    }

    @Test
    void validateToken_ShouldReturnFalseForEmptyToken() {
        assertFalse(jwtUtil.validateToken(""));
    }
}
