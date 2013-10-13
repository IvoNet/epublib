package nl.siegmann.epublib.util;

import junit.framework.TestCase;

import java.io.IOException;

public class StringUtilTest extends TestCase {

    public void testDefaultIfNull() {
        final Object[] testData = {null, "", "", "", " ", " ", "foo",
                                   "foo"};
        for (int i = 0; i < testData.length; i += 2) {
            final String actualResult = StringUtil
                    .defaultIfNull((String) testData[i]);
            final String expectedResult = (String) testData[i + 1];
            assertEquals((i / 2) + " : " + testData[i], expectedResult,
                         actualResult);
        }
    }

    public void testDefaultIfNull_with_default() {
        final Object[] testData = {null, null, null, "", null, "",
                                   null, "", "", "foo", "", "foo", "", "foo", "", " ", " ", " ",
                                   null, "foo", "foo",};
        for (int i = 0; i < testData.length; i += 3) {
            final String actualResult = StringUtil.defaultIfNull(
                    (String) testData[i], (String) testData[i + 1]);
            final String expectedResult = (String) testData[i + 2];
            assertEquals(
                    (i / 3) + " : " + testData[i] + ", " + testData[i + 1],
                    expectedResult, actualResult);
        }
    }

    public void testIsEmpty() {
        final Object[] testData = {null, true, "", true, " ", false,
                                   "asdfasfd", false};
        for (int i = 0; i < testData.length; i += 2) {
            final boolean actualResult = StringUtil.isEmpty((String) testData[i]);
            final boolean expectedResult = (Boolean) testData[i + 1];
            assertEquals(expectedResult, actualResult);
        }
    }

    public void testIsBlank() {
        final Object[] testData = {null, true, "", true, " ", true,
                                   "\t\t \n\n", true, "asdfasfd", false};
        for (int i = 0; i < testData.length; i += 2) {
            final boolean actualResult = StringUtil.isBlank((String) testData[i]);
            final boolean expectedResult = (Boolean) testData[i + 1];
            assertEquals(expectedResult, actualResult);
        }
    }

    public void testIsNotBlank() {
        final Object[] testData = {null, false, "", false, " ", false,
                                   "\t\t \n\n", false, "asdfasfd", true};
        for (int i = 0; i < testData.length; i += 2) {
            final boolean actualResult = StringUtil.isNotBlank((String) testData[i]);
            final boolean expectedResult = (Boolean) testData[i + 1];
            assertEquals((i / 2) + " : " + testData[i], expectedResult,
                         actualResult);
        }
    }

    public void testEquals() {
        final Object[] testData = {null, null, true, "", "", true,
                                   null, "", false, "", null, false, null, "foo", false, "foo",
                                   null, false, "", "foo", false, "foo", "", false, "foo", "bar",
                                   false, "foo", "foo", true};
        for (int i = 0; i < testData.length; i += 3) {
            final boolean actualResult = StringUtil.equals((String) testData[i],
                                                           (String) testData[i + 1]);
            final boolean expectedResult = (Boolean) testData[i + 2];
            assertEquals(
                    (i / 3) + " : " + testData[i] + ", " + testData[i + 1],
                    expectedResult, actualResult);
        }
    }

    public void testEndWithIgnoreCase() {
        final Object[] testData = {null, null, true, "", "", true, "",
                                   "foo", false, "foo", "foo", true, "foo.bar", "bar", true,
                                   "foo.bar", "barX", false, "foo.barX", "bar", false, "foo",
                                   "bar", false, "foo.BAR", "bar", true, "foo.bar", "BaR", true};
        for (int i = 0; i < testData.length; i += 3) {
            final boolean actualResult = StringUtil.endsWithIgnoreCase(
                    (String) testData[i], (String) testData[i + 1]);
            final boolean expectedResult = (Boolean) testData[i + 2];
            assertEquals(
                    (i / 3) + " : " + testData[i] + ", " + testData[i + 1],
                    expectedResult, actualResult);
        }
    }

    public void testSubstringBefore() {
        final Object[] testData = {"", ' ', "", "", 'X', "", "fox",
                                   'x', "fo", "foo.bar", 'b', "foo.", "aXbXc", 'X', "a",};
        for (int i = 0; i < testData.length; i += 3) {
            final String actualResult = StringUtil.substringBefore(
                    (String) testData[i], (Character) testData[i + 1]);
            final String expectedResult = (String) testData[i + 2];
            assertEquals(
                    (i / 3) + " : " + testData[i] + ", " + testData[i + 1],
                    expectedResult, actualResult);
        }
    }

    public void testSubstringBeforeLast() {
        final Object[] testData = {"", ' ', "", "", 'X', "", "fox",
                                   'x', "fo", "foo.bar", 'b', "foo.", "aXbXc", 'X', "aXb",};
        for (int i = 0; i < testData.length; i += 3) {
            final String actualResult = StringUtil.substringBeforeLast(
                    (String) testData[i], (Character) testData[i + 1]);
            final String expectedResult = (String) testData[i + 2];
            assertEquals(
                    (i / 3) + " : " + testData[i] + ", " + testData[i + 1],
                    expectedResult, actualResult);
        }
    }

    public void testSubstringAfter() {
        final Object[] testData = {"", ' ', "", "", 'X', "", "fox",
                                   'f', "ox", "foo.bar", 'b', "ar", "aXbXc", 'X', "bXc",};
        for (int i = 0; i < testData.length; i += 3) {
            final String actualResult = StringUtil.substringAfter(
                    (String) testData[i], (Character) testData[i + 1]);
            final String expectedResult = (String) testData[i + 2];
            assertEquals(
                    (i / 3) + " : " + testData[i] + ", " + testData[i + 1],
                    expectedResult, actualResult);
        }
    }

    public void testSubstringAfterLast() {
        final Object[] testData = {"", ' ', "", "", 'X', "", "fox",
                                   'f', "ox", "foo.bar", 'b', "ar", "aXbXc", 'X', "c",};
        for (int i = 0; i < testData.length; i += 3) {
            final String actualResult = StringUtil.substringAfterLast(
                    (String) testData[i], (Character) testData[i + 1]);
            final String expectedResult = (String) testData[i + 2];
            assertEquals(
                    (i / 3) + " : " + testData[i] + ", " + testData[i + 1],
                    expectedResult, actualResult);
        }
    }

    public void testToString() {
        assertEquals("[name: 'paul']", StringUtil.toString("name", "paul"));
        assertEquals("[name: 'paul', address: 'a street']",
                     StringUtil.toString("name", "paul", "address", "a street"));
        assertEquals("[name: <null>]", StringUtil.toString("name", null));
        assertEquals("[name: 'paul', address: <null>]",
                     StringUtil.toString("name", "paul", "address"));
    }

    public void testHashCode() {
        assertEquals(2522795, StringUtil.hashCode("isbn", "1234"));
        assertEquals(3499691, StringUtil.hashCode("ISBN", "1234"));
    }

    public void testReplacementForCollapsePathDots() throws IOException {
        // This used to test StringUtil.collapsePathDots(String path).
        // I have left it to confirm that the Apache commons
        // FilenameUtils.normalize
        // is a suitable replacement, but works where for "/a/b/../../c", which
        // the old method did not.
        final String[] testData = { //
                                    "/foo/bar.html", "/foo/bar.html",
                                    "/foo/../bar.html", "/bar.html", //
                                    "/foo/moo/../../bar.html", //
                                    "/bar.html", "/foo//bar.html", //
                                    "/foo/bar.html", "/foo/./bar.html", //
                                    "/foo/bar.html", //
                                    "/a/b/../../c", "/c", //
                                    "/foo/../sub/bar.html", "/sub/bar.html" //
        };
        for (int i = 0; i < testData.length; i += 2) {
            final String actualResult = StringUtil.collapsePathDots(testData[i]);
            assertEquals(testData[i], testData[i + 1], actualResult);
        }
    }

}
