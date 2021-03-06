package com.endava.cats.model;

import com.endava.cats.model.strategy.*;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class FuzzingStrategyTest {

    @Test
    void givenTheFuzzingStrategyClass_whenCallingTheStaticCreateMethods_thenProperInstancesAreReturned() {
        Assertions.assertThat(FuzzingStrategy.trail()).isInstanceOf(TrailFuzzingStrategy.class);
        Assertions.assertThat(FuzzingStrategy.replace()).isInstanceOf(ReplaceFuzzingStrategy.class);
        Assertions.assertThat(FuzzingStrategy.prefix()).isInstanceOf(PrefixFuzzingStrategy.class);
        Assertions.assertThat(FuzzingStrategy.noop()).isInstanceOf(NoopFuzzingStrategy.class);
        Assertions.assertThat(FuzzingStrategy.skip()).isInstanceOf(SkipFuzzingStrategy.class);
    }

    @Test
    void givenANullString_whenMergingTheFuzzingWithAnotherString_thenTheReplaceFuzzingStrategyIsApplied() {
        String result = FuzzingStrategy.mergeFuzzing(null, "air", "  ");

        Assertions.assertThat(result).isEqualTo("  ");
    }

    @Test
    void givenASpacedPrefixedString_whenMergingTheFuzzingWithAnotherString_thenThePrefixFuzzingStrategyIsApplied() {
        String result = FuzzingStrategy.mergeFuzzing(" test", "air", "  ");

        Assertions.assertThat(result).isEqualTo("  air");
    }

    @Test
    void givenASpaceTrailingString_whenMergingTheFuzzingWithAnotherString_thenTheTrailFuzzingStrategyIsApplied() {
        String result = FuzzingStrategy.mergeFuzzing("test  ", "air", "  ");

        Assertions.assertThat(result).isEqualTo("air  ");
    }

    @Test
    void givenAnEmptyString_whenMergingTheFuzzingWithAnotherString_thenTheReplaceFuzzingStrategyIsApplied() {
        String result = FuzzingStrategy.mergeFuzzing("  ", "air", "replaced");

        Assertions.assertThat(result).isEqualTo("replaced");
    }

    @Test
    void givenAStringWithNoSpaces_whenMergingTheFuzzingWithAnotherString_thenTheSuppliedValueIsUnchanged() {
        String result = FuzzingStrategy.mergeFuzzing("test", "air", "replaced");

        Assertions.assertThat(result).isEqualTo("test");
    }

    @Test
    void givenAFuzzingStrategy_whenSettingAnInnerValueAboveTruncationThreshold_thenTheValueIsProperlyTruncated() {
        FuzzingStrategy strategy = FuzzingStrategy.replace().withData(StringUtils.repeat("t", 50));

        Assertions.assertThat(strategy.truncatedValue()).isEqualTo(strategy.name() + " with " + StringUtils.repeat("t", 30) + "...");
        Assertions.assertThat(strategy).hasToString(strategy.name() + " with " + StringUtils.repeat("t", 50));
    }

    @Test
    void givenAFuzzingStrategy_whenSettingAnInnerValueBelowTruncationThreshold_thenTheValueIsNotTruncated() {
        FuzzingStrategy strategy = FuzzingStrategy.replace().withData(StringUtils.repeat("t", 30));

        Assertions.assertThat(strategy).hasToString(strategy.truncatedValue());
    }

    @Test
    void givenAFuzzingStrategy_whenSettingAnInnerValueWithNull_thenToStringMatchesName() {
        FuzzingStrategy strategy = FuzzingStrategy.replace().withData(null);

        Assertions.assertThat(strategy).hasToString(strategy.name());
    }

    @Test
    void shouldTruncateValue() {
        FuzzingStrategy strategy = FuzzingStrategy.replace().withData(StringUtils.repeat("a", 31));

        Assertions.assertThat(strategy.truncatedValue()).contains("REPLACE", "with", StringUtils.repeat("a", 30), "...");
    }

    @Test
    void shouldReturnNameOnlyWhenDataIsNull() {
        FuzzingStrategy strategy = FuzzingStrategy.replace().withData(null);

        Assertions.assertThat(strategy.truncatedValue()).isEqualTo(strategy.name());
    }
}