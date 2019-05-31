package com.scottlogic.deg.generator.restrictions;

import com.scottlogic.deg.generator.restrictions.set.SetRestrictions;
import org.junit.Assert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

class SetRestrictionsTests {
    @Nested
    class fromWhitelist {
        @Test
        void copiesWhitelistAndUsesEmptyBlacklist() {
            SetRestrictions objectUnderTest = SetRestrictions.fromWhitelist(set(1, 2, 3));

            Assert.assertThat(objectUnderTest.getWhitelist().orElse(null), equalTo(set(1, 2, 3)));
            Assert.assertThat(objectUnderTest.getBlacklist(), equalTo(set()));
        }
    }

    @Nested
    class allowNoValues {
        @Test
        void isSameAsEmptyWhitelist() {
            assertEqual(
                SetRestrictions.allowNoValues(),
                SetRestrictions.fromWhitelist(Collections.emptySet()));
        }
    }

    @Nested
    class fromBlacklist {
        @Test
        void copiesBlacklistAndUsesNullWhitelist() {
            SetRestrictions objectUnderTest = SetRestrictions.fromBlacklist(set(1, 2, 3));

            Assert.assertThat(objectUnderTest.getWhitelist().orElse(null), nullValue());
            Assert.assertThat(objectUnderTest.getBlacklist(), equalTo(set(1, 2, 3)));
        }
    }

    @Nested
    class merge {
        @Test
        void removesBlacklistItemsFromWhitelist() {
            expectMerge(
                SetRestrictions.fromWhitelist(set(1, 2, 3)),
                SetRestrictions.fromBlacklist(set(1)),

                SetRestrictions.fromWhitelist(set(2, 3)));
        }

        @Test
        void intersectsWhitelists() {
            expectMerge(
                SetRestrictions.fromWhitelist(set(1, 2, 3)),
                SetRestrictions.fromWhitelist(set(2, 3, 4)),

                SetRestrictions.fromWhitelist(set(2, 3)));
        }

        @Test
        void unionsBlacklists() {
            expectMerge(
                SetRestrictions.fromBlacklist(set(1, 2, 3)),
                SetRestrictions.fromBlacklist(set(2, 3, 4)),

                SetRestrictions.fromBlacklist(set(1, 2, 3, 4)));
        }

        @Test
        void discardsBlacklistIfWhitelistPopulated() {
            expectMerge(
                SetRestrictions.fromWhitelist(set(1, 2, 3)),
                SetRestrictions.fromBlacklist(set(7, 8)),

                SetRestrictions.fromWhitelist(set(1, 2, 3)));
        }

        @Test
        void cannotMergeWhitelistsWithNoCommonItems() {
            expectUnmergeable(
                SetRestrictions.fromWhitelist(set(1, 2)),
                SetRestrictions.fromWhitelist(set(3, 4)));
        }
    }

    @Nested
    class equality {
        @Test
        void isNotEqualToNull() {
            assertNotEqual(
                SetRestrictions.fromWhitelist(set("Test")),
                null);
        }

        @Test
        void isNotEqualToNonSetRestrictionsObject() {
            assertNotEqual(
                SetRestrictions.fromWhitelist(set("Test")),
                "String");
        }

        @Test
        void twoEmptyWhitelistsAreEqual() {
            assertEqual(
                SetRestrictions.fromWhitelist(Collections.emptySet()),
                SetRestrictions.fromWhitelist(Collections.emptySet()));
        }

        @Test
        void twoEmptyBlacklistsAreEqual() {
            assertEqual(
                SetRestrictions.fromBlacklist(Collections.emptySet()),
                SetRestrictions.fromBlacklist(Collections.emptySet()));
        }

        @Test
        void twoDifferentBlacklistsAreNotEqual() {
            assertNotEqual( // same type
                SetRestrictions.fromBlacklist(set("Test")),
                SetRestrictions.fromBlacklist(set("Parrot")));

            assertNotEqual( // different type
                SetRestrictions.fromBlacklist(set("Test")),
                SetRestrictions.fromBlacklist(set(1)));
        }

        @Test
        void twoIdenticalBlacklistsAreEqual() {
            assertEqual(
                SetRestrictions.fromBlacklist(set("Test")),
                SetRestrictions.fromBlacklist(set("Test")));
        }

        @Test
        void twoDifferentWhitelistsAreNotEqual() {
            assertNotEqual( // same types
                SetRestrictions.fromWhitelist(set("Test")),
                SetRestrictions.fromWhitelist(set("Parrot")));

            assertNotEqual( // different types
                SetRestrictions.fromWhitelist(set("Test")),
                SetRestrictions.fromWhitelist(set(1)));
        }

        @Test
        void twoIdenticalWhitelistsAreEqual() {
            assertEqual(
                SetRestrictions.fromWhitelist(set("Test")),
                SetRestrictions.fromWhitelist(set("Test")));
        }
    }

    private static void expectMerge(SetRestrictions a, SetRestrictions b, SetRestrictions expected) {
        BiConsumer<SetRestrictions, SetRestrictions> assertMergeInner = (aInner, bInner) -> {
            MergeResult<SetRestrictions> result = SetRestrictions.merge(aInner, bInner);

            Assert.assertTrue(result.successful);
            Assert.assertThat(result.restrictions, equalTo(expected));
        };

        // make sure the result is the same regardless of the operand order
        assertMergeInner.accept(a, b);
        assertMergeInner.accept(b, a);
    }

    private static void expectUnmergeable(SetRestrictions a, SetRestrictions b) {
        MergeResult<SetRestrictions> result = SetRestrictions.merge(a, b);

        Assert.assertFalse(result.successful);
    }

    private static void assertEqual(Object a, Object b) {
        Assert.assertEquals(a, b);
        Assert.assertEquals(b, a);

        if (a != null && b != null) Assert.assertEquals(a.hashCode(), b.hashCode());
    }

    private static void assertNotEqual(Object a, Object b) {
        Assert.assertNotEquals(a, b);
        Assert.assertNotEquals(b, a);

        // not sure about this; hash collisions aren't intrinsically functional errors
        if (a != null && b != null) Assert.assertNotEquals(a.hashCode(), b.hashCode());
    }

    private static Set<Object> set(Object... values) {
        return new HashSet<>(Arrays.asList(values));
    }
}
