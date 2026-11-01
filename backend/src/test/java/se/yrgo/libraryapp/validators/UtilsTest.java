package se.yrgo.libraryapp.validators;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class UtilsTest {

    /*
        Testing whitespaces, åäö, leet, tabs, newlines, and no input
     */
    @ParameterizedTest
    @ValueSource(strings = {"Ab1 C-d_e!", "ÅÄö9x", "", "L33T 5P34K", "a\tb\nc"})
    void onlyLettersAndWhitespace(String input) {
        String result = Utils.onlyLettersAndWhitespace(input);

        assertThat(result).matches("[\\p{L}\\s]*");
        assertThat(result).isEqualTo(result.toLowerCase());
    }

    /*
        Leet conversion and cleaning. No digits or punctuation may remain.
    */
    @ParameterizedTest
    @ValueSource(strings = {"13456780", "4 b4n4n4-2!", "L33T 5P34K", ""})
    void cleanAndUnLeet(String input) {
        String result = Utils.cleanAndUnLeet(input);

        assertThat(result).matches("[\\p{L}\\s]*");
        assertThat(result).isEqualTo(result.toLowerCase());
    }
}