package se.yrgo.libraryapp.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.*;

import se.yrgo.libraryapp.entities.*;

class RoleDaoTest extends DaoTestBase {

    @Test
    void getReturnsTheRolesOfTheUser() {
        execute("INSERT INTO user_role VALUES (1, 1), (1, 2), (2, 2)");
        RoleDao dao = new RoleDao(ds);

        assertEquals(Set.of(Role.ADMIN, Role.USER), Set.copyOf(dao.get(UserId.of(1))));
        assertEquals(Set.of(Role.USER), Set.copyOf(dao.get(UserId.of(2))));
        assertTrue(dao.get(UserId.of(99)).isEmpty());
    }
}