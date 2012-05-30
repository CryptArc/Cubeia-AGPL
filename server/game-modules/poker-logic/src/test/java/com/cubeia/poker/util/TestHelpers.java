package com.cubeia.poker.util;

import junitx.framework.ListAssert;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.List;

public class TestHelpers {

    static class IsLessThanMatcher extends BaseMatcher<Integer> {

        private int referenceValue;

        public IsLessThanMatcher(int i) {
            this.referenceValue = i;
        }

        @Override
        public boolean matches(Object o) {
            Integer valueToCheck = (Integer) o;
            return valueToCheck < referenceValue;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a value less than " + referenceValue);
        }
    }

    public static Matcher<Integer> isLessThan(int i) {
        return new IsLessThanMatcher(i);
    }

    public static void assertSameListsDisregardingOrder(List expected, List actual) {
        ListAssert.assertEquals(expected, actual);
    }


}
