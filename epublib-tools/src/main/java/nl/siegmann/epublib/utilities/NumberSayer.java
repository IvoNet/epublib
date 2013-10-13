package nl.siegmann.epublib.utilities;

final class NumberSayer {

    private static final String[] NUMBER_BELOW_20 = {"zero", "one", "two", "three", "four", "five", "six", "seven",
                                                     "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen",
                                                     "fifteen", "sixteen", "seventeen", "nineteen"};
    private static final String[] DECIMALS = {"zero", "ten", "twenty", "thirty", "fourty", "fifty", "sixty", "seventy",
                                              "eighty", "ninety"};
    private static final String[] ORDER_NUMBERS = {"hundred", "thousand", "million", "billion", "trillion"};

    private NumberSayer() {
    }


    public static String getNumberName(final int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Cannot handle numbers < 0 or > " + Integer.MAX_VALUE);
        }
        if (number < 20) {
            return NUMBER_BELOW_20[number];
        }
        if (number < 100) {
            return DECIMALS[number / 10] + NUMBER_BELOW_20[number % 10];
        }
        if ((number >= 100) && (number < 200)) {
            return ORDER_NUMBERS[0] + getNumberName(number - 100);
        }
        if (number < 1000) {
            return NUMBER_BELOW_20[number / 100] + ORDER_NUMBERS[0] + getNumberName(number % 100);
        }
        throw new IllegalArgumentException("Cannot handle numbers < 0 or > " + Integer.MAX_VALUE);
    }
}
