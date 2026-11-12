// WORKSHOP 2

package se.yrgo.libraryapp.services;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import se.yrgo.libraryapp.dao.UserDao;
import se.yrgo.libraryapp.entities.LoginInfo;
import se.yrgo.libraryapp.entities.UserId;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class UserServiceTest {
    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder encoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userDao, encoder);
    }

    @Test
    void correctLogin() {
        UserId id = UserId.of(1);
        when(userDao.getLoginInfo("testuser")).thenReturn(Optional.of(new LoginInfo(id, "hash")));
        when(encoder.matches("password", "hash")).thenReturn(true);

        assertThat(userService.validate("testuser", "password")).isEqualTo(Optional.of(id));
    }

//     @Test
//     @SuppressWarnings("deprecation")
//     void correctLogin() {
//         final String userId = "1";
//         final UserId id = UserId.of(userId);
//         final String username = "testuser";
//         final String password = "password";
//         final String passwordHash = "password";
//         final LoginInfo info = new LoginInfo(id, passwordHash);
//         final PasswordEncoder encoder = org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
//         when(userDao.getLoginInfo(username)).thenReturn(Optional.of(info));
//         UserService userService = new UserService(userDao, encoder);
//         assertThat(userService.validate(username,
//                 password)).isEqualTo(Optional.of(id));
//     }

    @Test
    void wrongPassword() {
        UserId id = UserId.of(1);
        when(userDao.getLoginInfo("testuser")).thenReturn(Optional.of(new LoginInfo(id, "hash")));
        when(encoder.matches("wrong", "hash")).thenReturn(false);

        assertThat(userService.validate("testuser", "wrong")).isEmpty();
    }

    @Test
    void unknownUserNeverChecksPassword() {
        when(userDao.getLoginInfo("nonExistingUser")).thenReturn(Optional.empty());

        assertThat(userService.validate("nonExistingUser", "password")).isEmpty();
        verifyNoInteractions(encoder);
    }

    @Test
    void registerHashesPasswordBeforeSaving() {
        when(encoder.encode("secret")).thenReturn("hashed");
        when(userDao.register("bosse", "Bosse Svensson", "hashed")).thenReturn(true);

        assertThat(userService.register("bosse", "Bosse Svensson", "secret")).isTrue();
        verify(userDao).register("bosse", "Bosse Svensson", "hashed");
    }

    @Test
    void registerReturnsFalseWhenDaoFails() {
        when(encoder.encode("secret")).thenReturn("hashed");
        when(userDao.register("bosse", "Bosse Svensson", "hashed")).thenReturn(false);

        assertThat(userService.register("bosse", "Bosse Svensson", "secret")).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "ag", "  ag  " })
    void invalidNamesAreRejectedWithoutAskingDatabase(String name) {
        assertThat(userService.isNameAvailable(name)).isFalse();
        verifyNoInteractions(userDao);
    }

    @Test
    void validAndFreeName() {
        when(userDao.isNameAvailable("bosse")).thenReturn(true);

        assertThat(userService.isNameAvailable("bosse")).isTrue();
    }

    @Test
    void validButTakenName() {
        when(userDao.isNameAvailable("bosse")).thenReturn(false);

        assertThat(userService.isNameAvailable("bosse")).isFalse();
    }
}