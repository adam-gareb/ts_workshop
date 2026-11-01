package se.yrgo.libraryapp.validators;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class RealNameTest {

    // Testing if the bad words are recognized and returning false as it should
    @ParameterizedTest
    @ValueSource(strings = {"fuck", "shit", "cunt", "socksucker"})
    void everyWordInFileIsRejected(String word) {
        assertThat(RealName.validate(word)).isFalse();
    }

    // Testing the bad words with other non-bad words, in different orders
    @ParameterizedTest
    @ValueSource(strings = {"shit smith", "john shit smith", "john smith socksucker", " fuck"})
    void badWordIsFoundAnywhereInName(String name) {
        assertThat(RealName.validate(name)).isFalse();
    }

    // Input is cleaned before the lookup, so case and leet speak must not help.
    @ParameterizedTest
    @ValueSource(strings = {"FUCK", "fUcK", "5hIt", "john s0cksucKeR smith", "cUn7"})
    void caseAndLeetSpeakDoNotBypassTheCheck(String name) {
        assertThat(RealName.validate(name)).isFalse();
    }

    // Normal/non-bad words should pass without any issue
    @ParameterizedTest
    @ValueSource(strings = {"John Smith", "Anna-Lisa Svensson", "Åsa Öberg", "x", ""})
    void ordinaryNamesAreAccepted(String name) {
        assertThat(RealName.validate(name)).isTrue();
    }

    // Only whole words are matched, so a name that merely contains a bad word is OK.
    @Test
    void wordContainingBadWordIsAccepted() {
        assertThat(RealName.validate("shitsworth")).isTrue();
    }

}
