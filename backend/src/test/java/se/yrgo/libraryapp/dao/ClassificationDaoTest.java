package se.yrgo.libraryapp.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.*;

import se.yrgo.libraryapp.entities.DdsClassification;

class ClassificationDaoTest extends DaoTestBase {

    @Test
    void getAvailableOnlyReturnsClassificationsWithBooks() {
        execute("INSERT INTO dewey_decimal_system VALUES (0, 'Computer science'), (800, 'Literature')",
                "INSERT INTO book_edition VALUES ('111', 'Java Basics', 'Alice', 0)");

        List<DdsClassification> result = new ClassificationDao(ds).getAvailable();

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getCode());
    }
}