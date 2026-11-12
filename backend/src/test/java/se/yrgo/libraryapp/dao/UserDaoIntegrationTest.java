package se.yrgo.libraryapp.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.radcortez.flyway.test.annotation.H2;

import se.yrgo.libraryapp.entities.LoginInfo;
import se.yrgo.libraryapp.entities.User;
import se.yrgo.libraryapp.entities.UserId;

@Tag("integration")
@H2
public class UserDaoIntegrationTest {
    private static final String TEST_HASH =
            "$argon2i$v=19$m=16,t=2,p=1$MTIzNDU2Nzg5MDEyMzQ1NjA$LmFqTZeUWwqsnbZCS2E8XQ";
    private static final int USER_ROLE_ID = 2;
    
    private static DataSource ds;
    private UserDao userDao;

    @BeforeAll
    static void initDataSource() {
        // this way we do not need to create a new datasource every time
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test");
        UserDaoIntegrationTest.ds = ds;
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDao(ds);
    }

    @Test
    void getUserById() {
        // this data comes from the test migration files
        final String username = "test";
        final UserId userId = UserId.of(1);
        UserDao userDao = new UserDao(ds);
        Optional<User> maybeUser = userDao.get(Integer.toString(userId.getId()));
        assertThat(maybeUser).isPresent();
        assertThat(maybeUser.get().getName()).isEqualTo(username);
        assertThat(maybeUser.get().getId()).isEqualTo(userId);
    }

    @Test
    void getUserMapsAllFields() {
        // realname comes from the migration that sets 'unknown'
        assertThat(userDao.get("2"))
                .contains(new User(UserId.of(2), "test2", "unknown"));
    }

    @Test
    void getNonExistingUser() {
        assertThat(userDao.get("999")).isEmpty();
    }

    @Test
    void getLoginInfoForExistingUser() {
        Optional<LoginInfo> maybeInfo = userDao.getLoginInfo("test");

        assertThat(maybeInfo).isPresent();
        assertThat(maybeInfo.get().getUserId()).isEqualTo(UserId.of(1));
        assertThat(maybeInfo.get().getPasswordHash()).isEqualTo(TEST_HASH);
    }

    @Test
    void getLoginInfoForNonExistingUser(){
        assertThat(userDao.getLoginInfo("nonExistingUser").isEmpty());
    }

    @Test
    void takenNameIsNotAvailable() {
        assertThat(userDao.isNameAvailable("test")).isFalse();
    }

    @Test
    void unusedNameIsAvailable() {
        assertThat(userDao.isNameAvailable("freshname")).isTrue();
    }

    @Test
    void registeredUserCanBeFetched() {
        assertThat(userDao.register("bosse", "Bo Svensson", TEST_HASH)).isTrue();

        Optional<LoginInfo> info = userDao.getLoginInfo("bosse");
        assertThat(info).isPresent();
        // the DAO must store the hash exactly as given
        assertThat(info.get().getPasswordHash()).isEqualTo(TEST_HASH);

        UserId newId = info.get().getUserId();
        assertThat(userDao.get(Integer.toString(newId.getId())))
                .contains(new User(newId, "bosse", "Bo Svensson"));
    }

    @Test
    void registeredUserGetsUserRole() throws SQLException {
        userDao.register("bosse", "Bo Svensson", TEST_HASH);
        int newId = userDao.getLoginInfo("bosse").get().getUserId().getId();

        assertThat(rolesOf(newId)).containsExactly(USER_ROLE_ID);
    }

    @Test
    void registerHandlesApostropheInName() {
        assertThat(userDao.register("ian", "Ian O'Toole", TEST_HASH)).isTrue();

        UserId id = userDao.getLoginInfo("ian").get().getUserId();
        assertThat(userDao.get(Integer.toString(id.getId())))
                .contains(new User(id, "ian", "Ian O'Toole"));
    }

    // Method to keep track of list of roles
    private List<Integer> rolesOf(int userId) throws SQLException {
        List<Integer> roles = new ArrayList<>();
        try (Connection conn = ds.getConnection();
                PreparedStatement ps = conn
                        .prepareStatement("SELECT role_id FROM user_role WHERE user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getInt("role_id"));
                }
            }
        }
        return roles;
    }
}