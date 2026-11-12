package se.yrgo.libraryapp.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.pac4j.core.exception.*;

import io.jooby.*;
import se.yrgo.libraryapp.dao.*;
import se.yrgo.libraryapp.entities.*;
import se.yrgo.libraryapp.entities.forms.*;
import se.yrgo.libraryapp.services.*;

public class LoginControllerTest {

    private UserService userService;
    private SessionDao sessionDao;
    private Context context;
    private LoginData loginData;
    private LoginController controller;
    private RoleDao roleDao;

    private final UserId userId = UserId.of(1);
    private final List<Role> roles = List.of(Role.USER);
    private final UUID sessionId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        roleDao = mock(RoleDao.class);
        sessionDao = mock(SessionDao.class);
        context = mock(Context.class);
        loginData = mock(LoginData.class);
        controller = new LoginController(userService, roleDao, sessionDao);
        when(loginData.getUsername()).thenReturn("user");
        when(loginData.getPassword()).thenReturn("pass");
    }

    @Test
    void loginWithValidCredentialsReturnsRolesAndSetsCookie() {
        when(userService.validate("user", "pass")).thenReturn(Optional.of(userId));
        when(sessionDao.create(userId)).thenReturn(sessionId);
        when(roleDao.get(userId)).thenReturn(roles);

        List<Role> result = controller.login(context, null, loginData);

        assertEquals(roles, result);
        ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
        verify(context).setResponseCookie(captor.capture());
        assertEquals(sessionId.toString(), captor.getValue().getValue());
    }

    @Test
    void loginWithWrongCredentialsReturnsUnauthorized() {
        when(userService.validate("user", "pass")).thenReturn(Optional.empty());

        List<Role> result = controller.login(context, null, loginData);

        assertTrue(result.isEmpty());
        verify(context).setResponseCode(StatusCode.UNAUTHORIZED);
        verify(sessionDao, never()).create(any());
    }

    @Test
    void loginWhenAlreadyLoggedInDoesNothing() {
        when(sessionDao.validate(sessionId)).thenReturn(userId);

        List<Role> result = controller.login(context, sessionId.toString(), loginData);

        assertTrue(result.isEmpty());
        verifyNoInteractions(userService);
    }

    @Test
    void isLoggedInWithValidSessionReturnsRoles() {
        when(sessionDao.validate(sessionId)).thenReturn(userId);
        when(roleDao.get(userId)).thenReturn(roles);

        assertEquals(roles, controller.isLoggedIn(sessionId.toString()));
    }

    @Test
    void isLoggedInWithInvalidSessionReturnsEmptyList() {
        when(sessionDao.validate(sessionId)).thenThrow(new CredentialsException("invalid"));

        assertTrue(controller.isLoggedIn(sessionId.toString()).isEmpty());
    }
}
