package se.yrgo.libraryapp.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UsernameTest {
    // @Test
    // void correctUsername() {
    // assertTrue(Username.validate("bosse"));
    // }

    // @Test
    // void incorrectUsername() {
    // assertFalse(Username.validate("name with space"));
    // }

    @ParameterizedTest
    @ValueSource(strings = { "ada@_0192-.", "123455", "43784927", "bosse" })
    void testValidUsernames(String username) {
        assertThat(Username.validate(username)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = { "fa", "abc", "öäöäåsd", "name with space", "ab!cd", " " })
    @EmptySource
    void testNonValidUsernames(String username) {
        assertThat(Username.validate(username)).isFalse();
    }

    @Test
    void veryLongUsernameIsValid() {
        assertThat(Username.validate("a".repeat(1000))).isTrue();
    }
}
