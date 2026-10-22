package com.cosmicpvp.cosmicutils.utils;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Money amounts typed in commands, shared by every plugin that takes one.
 * <p>
 * A plain number or a number with a shorthand suffix (any case): 100k = 100,000, 1.5m = 1,500,000,
 * 1b = 1,000,000,000. Also t = trillion and the words Astro's money commands already accepted
 * (mil/million, bil/billion, tril/trillion). Commas as thousands separators and a leading $ are allowed.
 * Anything else, a negative number, or a value above {@link Long#MAX_VALUE} is rejected (null).
 */
public final class MoneyParser {

    private static final Pattern AMOUNT = Pattern.compile(
            "\\$?((?:\\d{1,3}(?:,\\d{3})+|\\d+)(?:\\.\\d+)?|\\.\\d+)(k|m|mil|million|b|bil|billion|t|tril|trillion)?",
            Pattern.CASE_INSENSITIVE);
    private static final BigDecimal MAX = BigDecimal.valueOf(Long.MAX_VALUE);

    private MoneyParser() {
    }

    /** the amount, or null when the text isn't a valid amount */
    public static BigDecimal parse(String input) {
        if (input == null) return null;
        Matcher matcher = AMOUNT.matcher(input.trim());
        if (!matcher.matches()) return null;
        BigDecimal value = new BigDecimal(matcher.group(1).replace(",", ""));
        String suffix = matcher.group(2);
        if (suffix != null) {
            switch (suffix.toLowerCase(Locale.ROOT)) {
                case "k": value = value.movePointRight(3); break;
                case "m": case "mil": case "million": value = value.movePointRight(6); break;
                case "b": case "bil": case "billion": value = value.movePointRight(9); break;
                default: value = value.movePointRight(12); break;
            }
        }
        value = value.stripTrailingZeros();
        if (value.compareTo(MAX) > 0) return null;
        return value.scale() < 0 ? value.setScale(0) : value;
    }

    /** the amount as whole dollars, or null when it isn't a valid whole amount (1.5k is 1500; 1.5 is rejected) */
    public static Long parseWhole(String input) {
        BigDecimal value = parse(input);
        if (value == null || value.scale() > 0) return null;
        return value.longValueExact();
    }

    /** the amount written as a plain number (1.5m becomes "1500000"), or null when it isn't valid */
    public static String toPlain(String input) {
        BigDecimal value = parse(input);
        return value == null ? null : value.toPlainString();
    }
}
