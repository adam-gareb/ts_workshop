package se.yrgo.libraryapp.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.*;
import org.pac4j.core.exception.CredentialsException;

import se.yrgo.libraryapp.entities.UserId;

class SessionDaoTest extends DaoTestBase {

    private SessionDao dao;

    @BeforeEach
    void setUp() {
        dao = new SessionDao(ds);
    }

    @Test
    void createdSessionCanBeValidated() {
        UUID session = dao.create(UserId.of(1));

        assertEquals(UserId.of(1), dao.validate(session));
    }

    @Test
    void unknownSessionIsRejected() {
        UUID unknown = UUID.randomUUID();

        assertThrows(CredentialsException.class, () -> dao.validate(unknown));
    }

    @Test
    void expiredSessionIsRejected() {
        UUID session = UUID.randomUUID();
        execute("INSERT INTO session VALUES ('" + session + "', 1, DATEADD('DAY', -8, CURRENT_TIMESTAMP))");

        assertThrows(CredentialsException.class, () -> dao.validate(session));
    }

    @Test
    void deletedSessionIsRejected() {
        UUID session = dao.create(UserId.of(1));

        dao.delete(session);

        assertThrows(CredentialsException.class, () -> dao.validate(session));
    }
}