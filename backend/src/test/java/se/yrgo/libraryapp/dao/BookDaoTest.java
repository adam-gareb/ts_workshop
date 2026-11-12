package se.yrgo.libraryapp.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import se.yrgo.libraryapp.entities.*;

class BookDaoTest extends DaoTestBase {

    private BookDao dao;

    @BeforeEach
    void setUp() {
        execute("INSERT INTO user VALUES (1, 'anna', 'Anna A', 'hash'), (2, 'bo', 'Bo B', 'hash')",
                "INSERT INTO dewey_decimal_system VALUES (0, 'Computer science'), (800, 'Literature')",
                "INSERT INTO book_edition VALUES ('111', 'Java Basics', 'Alice', 0), ('222', 'Poems', 'Bob', 800)",
                "INSERT INTO book VALUES (1, '111'), (2, '111'), (3, '222')",
                "INSERT INTO book_loan VALUES (1, 1, DATEADD('DAY', 10, CURRENT_DATE))");
        dao = new BookDao(ds);
    }

    @Test
    void getReportsAvailability() {
        assertFalse(dao.get(BookId.of(1)).orElseThrow().getAvailable());
        assertTrue(dao.get(BookId.of(2)).orElseThrow().getAvailable());
        assertTrue(dao.get(BookId.of(99)).isEmpty());
    }

    @Test
    void findWithoutCriteriaReturnsNothing() {
        assertTrue(dao.find(null, "  ", "").isEmpty());
    }

    @Test
    void findByTitleReturnsMatchingEditions() {
        assertEquals(1, dao.find(null, "Java", null).size());
    }

    @Test
    void withClassReturnsEditionsInThatClass() {
        assertEquals(1, dao.withClass(new DdsClassification(800, "Literature")).size());
    }

    @Test
    void loansReturnsOnlyLoansOfTheUser() {
        assertEquals(1, dao.loans(UserId.of(1)).size());
        assertTrue(dao.loans(UserId.of(2)).isEmpty());
    }

    @Test
    void overdueLoansOnlyReturnsLoansPastReturnDate() {
        execute("INSERT INTO book_loan VALUES (3, 2, DATEADD('DAY', -5, CURRENT_DATE))");

        assertEquals(1, dao.overdueLoans().size());
    }

    @Test
    void returnBookRemovesTheLoan() {
        assertTrue(dao.returnBook(BookId.of(1)));
        assertFalse(dao.returnBook(BookId.of(1)));
    }

}
