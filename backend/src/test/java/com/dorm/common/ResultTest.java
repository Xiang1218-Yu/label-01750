package com.dorm.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void success_WithData_ShouldReturnCorrectResult() {
        String data = "test data";
        Result<String> result = Result.success(data);

        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals(data, result.getData());
    }

    @Test
    void success_WithoutData_ShouldReturnCorrectResult() {
        Result<Void> result = Result.success();

        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void error_WithMessage_ShouldReturnCorrectResult() {
        String errorMessage = "Something went wrong";
        Result<Void> result = Result.error(errorMessage);

        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void error_WithCodeAndMessage_ShouldReturnCorrectResult() {
        int code = 400;
        String errorMessage = "Bad request";
        Result<Void> result = Result.error(code, errorMessage);

        assertEquals(code, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
    }
}
