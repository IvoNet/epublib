package nl.siegmann.epublib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Various String utility functions.
 *
 * Most of the functions herein are re-implementations of the ones in apache
 * commons StringUtils. The reason for re-implementing this is that the
 * functions are fairly simple and using my own implementation saves the
 * inclusion of a 200Kb jar file.
 *
 * @author paul.siegmann
 *
 */
public class StringUtil {

    /**
     * Changes a path containing '..', '.' and empty dirs into a path that
     * doesn't. X/foo/../Y is changed into 'X/Y', etc. Does not handle invalid
     * paths like "../".
     *
     * @param path
     * @return the normalized path
     */
    public static String collapsePathDots(final String path) {
        final String[] stringParts = path.split("/");
        final List<String> parts = new ArrayList<String>(Arrays.asList(stringParts));
        for (int i = 0; i < (parts.size() - 1); i++) {
            final String currentDir = parts.get(i);
            if (currentDir.isEmpty() || ".".equals(currentDir)) {
                parts.remove(i);
                i--;
            } else if ("..".equals(currentDir)) {
                parts.remove(i - 1);
                parts.remove(i - 1);
                i -= 2;
            }
        }
        final StringBuilder result = new StringBuilder();
        if (path.startsWith("/")) {
            result.append('/');
        }
        for (int i = 0; i < parts.size(); i++) {
            result.append(parts.get(i));
            if (i < (parts.size() - 1)) {
                result.append('/');
            }
        }
        return result.toString();
    }

    /**
     * Whether the String is not null, not zero-length and does not contain of
     * only whitespace.
     *
     * @param text
     * @return Whether the String is not null, not zero-length and does not contain of
     */
    public static boolean isNotBlank(final String text) {
        return !isBlank(text);
    }

    /**
     * Whether the String is null, zero-length and does contain only whitespace.
     *
     * @return Whether the String is null, zero-length and does contain only whitespace.
     */
    public static boolean isBlank(final String text) {
        if (isEmpty(text)) {
            return true;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Whether the given string is null or zero-length.
     *
     * @param text the input for this method
     * @return Whether the given string is null or zero-length.
     */
    public static boolean isEmpty(final String text) {
        return (text == null) || (text.isEmpty());
    }

    /**
     * Whether the given source string ends with the given suffix, ignoring
     * case.
     *
     * @param source
     * @param suffix
     * @return Whether the given source string ends with the given suffix, ignoring case.
     */
    public static boolean endsWithIgnoreCase(final String source, final String suffix) {
        if (isEmpty(suffix)) {
            return true;
        }
        if (isEmpty(source)) {
            return false;
        }
        if (suffix.length() > source.length()) {
            return false;
        }
        return source.substring(source.length() - suffix.length())
                .toLowerCase().endsWith(suffix.toLowerCase());
    }

    /**
     * If the given text is null return "", the original text otherwise.
     *
     * @param text
     * @return If the given text is null "", the original text otherwise.
     */
    public static String defaultIfNull(final String text) {
        return defaultIfNull(text, "");
    }

    /**
     * If the given text is null return "", the given defaultValue otherwise.
     *
     * @param text
     * @param defaultValue
     * @return If the given text is null "", the given defaultValue otherwise.
     */
    public static String defaultIfNull(final String text, final String defaultValue) {
        if (text == null) {
            return defaultValue;
        }
        return text;
    }

    /**
     * Null-safe string comparator
     *
     * @param text1
     * @param text2
     * @return whether the two strings are equal
     */
    public static boolean equals(final String text1, final String text2) {
        if (text1 == null) {
            return (text2 == null);
        }
        return text1.equals(text2);
    }

    /**
     * Pretty toString printer.
     *
     * @param keyValues
     * @return a string representation of the input values
     */
    public static String toString(final Object... keyValues) {
        final StringBuilder result = new StringBuilder();
        result.append('[');
        for (int i = 0; i < keyValues.length; i += 2) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(keyValues[i]);
            result.append(": ");
            Object value = null;
            if ((i + 1) < keyValues.length) {
                value = keyValues[i + 1];
            }
            if (value == null) {
                result.append("<null>");
            } else {
                result.append('\'');
                result.append(value);
                result.append('\'');
            }
        }
        result.append(']');
        return result.toString();
    }

    public static int hashCode(final String... values) {
        int result = 31;
        for (final String value : values) {
            result ^= String.valueOf(value).hashCode();
        }
        return result;
    }

    /**
     * Gives the substring of the given text before the given separator.
     *
     * If the text does not contain the given separator then the given text is
     * returned.
     *
     * @param text
     * @param separator
     * @return the substring of the given text before the given separator.
     */
    public static String substringBefore(final String text, final char separator) {
        if (isEmpty(text)) {
            return text;
        }
        final int sepPos = text.indexOf(separator);
        if (sepPos < 0) {
            return text;
        }
        return text.substring(0, sepPos);
    }

    /**
     * Gives the substring of the given text before the last occurrence of the
     * given separator.
     *
     * If the text does not contain the given separator then the given text is
     * returned.
     *
     * @param text
     * @param separator
     * @return the substring of the given text before the last occurrence of the given separator.
     */
    public static String substringBeforeLast(final String text, final char separator) {
        if (isEmpty(text)) {
            return text;
        }
        final int cPos = text.lastIndexOf(separator);
        if (cPos < 0) {
            return text;
        }
        return text.substring(0, cPos);
    }

    /**
     * Gives the substring of the given text after the last occurrence of the
     * given separator.
     *
     * If the text does not contain the given separator then "" is returned.
     *
     * @param text
     * @param separator
     * @return the substring of the given text after the last occurrence of the given separator.
     */
    public static String substringAfterLast(final String text, final char separator) {
        if (isEmpty(text)) {
            return text;
        }
        final int cPos = text.lastIndexOf(separator);
        if (cPos < 0) {
            return "";
        }
        return text.substring(cPos + 1);
    }

    /**
     * Gives the substring of the given text after the given separator.
     *
     * If the text does not contain the given separator then "" is returned.
     *
     * @param text the input text
     * @param c the separator char
     * @return the substring of the given text after the given separator.
     */
    public static String substringAfter(final String text, final char c) {
        if (isEmpty(text)) {
            return text;
        }
        final int cPos = text.indexOf(c);
        if (cPos < 0) {
            return "";
        }
        return text.substring(cPos + 1);
    }
}
