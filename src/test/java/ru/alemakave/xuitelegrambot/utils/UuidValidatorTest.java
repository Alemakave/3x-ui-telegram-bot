package ru.alemakave.xuitelegrambot.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UuidValidatorTest {
    @Test
    @DisplayName("Test for UUID v1")
    public void isValidUUIDv1() {
        assertTrue(UuidValidator.isValidUUID("f47ac10b-58cc-11e7-907b-a6006ad3dba0"));
        assertTrue(UuidValidator.isValidUUID("6ba7b810-9dad-11d1-80b4-00c04fd430c8"));
    }

    @Test
    @DisplayName("Test for UUID v2")
    public void isValidUUIDv2() {
        assertTrue(UuidValidator.isValidUUID("000003e8-58cc-21e7-8080-808080808080"));
    }

    @Test
    @DisplayName("Test for UUID v3")
    public void isValidUUIDv3() {
        assertTrue(UuidValidator.isValidUUID("5df41881-3aed-3515-88a7-2f4a814cf09e"));
        assertTrue(UuidValidator.isValidUUID("9125a8dc-52ee-365b-a5aa-81b0b3681e6d"));
    }

    @Test
    @DisplayName("Test for UUID v4")
    public void isValidUUIDv4() {
        assertTrue(UuidValidator.isValidUUID("123e4567-e89b-12d3-a456-426614174000"));
        assertTrue(UuidValidator.isValidUUID("550e8400-e29b-41d4-a716-446655440000"));
    }

    @Test
    @DisplayName("Test for UUID v5")
    public void isValidUUIDv5() {
        assertTrue(UuidValidator.isValidUUID("2ed6657d-e927-568b-95e1-2665a8aea6a2"));
        assertTrue(UuidValidator.isValidUUID("c4a760a8-dbcf-5254-a0d9-6a4474bd1b62"));
    }

    @Test
    @DisplayName("Test for UUID v6")
    public void isValidUUIDv6() {
        assertTrue(UuidValidator.isValidUUID("1e758cc0-58cc-61e7-8080-808080808080"));
        assertTrue(UuidValidator.isValidUUID("2f4a814c-f4a8-61e7-8080-808080808080"));
    }

    @Test
    @DisplayName("Test for UUID v7")
    public void isValidUUIDv7() {
        assertTrue(UuidValidator.isValidUUID("018f3a99-7e7b-7e7b-8000-808080808080"));
    }
}