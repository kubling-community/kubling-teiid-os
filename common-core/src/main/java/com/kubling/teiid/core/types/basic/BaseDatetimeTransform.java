package com.kubling.teiid.core.types.basic;

import com.kubling.teiid.core.types.Transform;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseDatetimeTransform extends Transform {

    protected static final List<DateTimeFormatter> formatters = new ArrayList<>();

    protected static final Pattern literalsRegexPattern = Pattern.compile("\\{(d|t|ts) '([^']*)'\\}");

    private static final Pattern nanoPrecisionPattern = Pattern.compile(
            "(?<prefix>.*?\\d{2}:\\d{2}:\\d{2})\\.(?<fraction>\\d{4,9})"
    );

    static {
        // Base formatters for common patterns
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        formatters.add(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"));
        formatters.add(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss.S"));
        formatters.add(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss.SS"));
        formatters.add(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss.SSS"));
        formatters.add(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        formatters.add(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.S"));
        formatters.add(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SS"));
        formatters.add(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        formatters.add(DateTimeFormatter.ofPattern("HH:mm:ss"));
        formatters.add(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }

    protected static Timestamp normalizeToTimestamp(String dt) {
        // Handle JDBC escape format
        final Matcher matcher = literalsRegexPattern.matcher(dt);
        if (matcher.matches() && matcher.group(1).equals("ts")) {
            dt = matcher.group(2);
        }

        // Handle nano/microsecond precision manually
        Matcher nanoMatch = nanoPrecisionPattern.matcher(dt);
        if (nanoMatch.matches()) {
            try {
                String base = nanoMatch.group("prefix");
                StringBuilder fraction = new StringBuilder(nanoMatch.group("fraction"));

                if (fraction.length() > 9) fraction = new StringBuilder(fraction.substring(0, 9));
                while (fraction.length() < 9) fraction.append("0"); // pad to nanoseconds

                LocalDateTime ldt = LocalDateTime.parse(base, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                ldt = ldt.withNano(Integer.parseInt(fraction.toString()));
                return Timestamp.valueOf(ldt);
            } catch (Exception ignored) {
                // Fallback to normal parsing
            }
        }

        // Try predefined formatters
        for (DateTimeFormatter formatter : formatters) {
            try {
                TemporalAccessor accessor = formatter.parse(dt);

                // Check if the parsed result includes time info
                if (accessor.isSupported(ChronoField.HOUR_OF_DAY)) {
                    LocalDateTime ldt = LocalDateTime.from(accessor);
                    return Timestamp.valueOf(ldt);
                }

                // Otherwise it's just a date
                LocalDate ld = LocalDate.from(accessor);
                return Timestamp.valueOf(ld.atStartOfDay());

            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        // Epoch seconds
        try {
            return Timestamp.from(Instant.ofEpochSecond(Long.parseLong(dt)));
        } catch (NumberFormatException ignored) {
        }

        // ISO-8601
        try {
            return Timestamp.from(Instant.parse(dt));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid timestamp format: " + dt);
        }
    }

    protected static Date normalizeToDate(String dt) {

        final Matcher matcher = literalsRegexPattern.matcher(dt);
        if (matcher.matches() && matcher.group(1).equals("d")) {
            dt = matcher.group(2);
        }

        for (DateTimeFormatter formatter : formatters) {
            try {
                return Date.valueOf(LocalDate.parse(dt, formatter));
            } catch (DateTimeParseException ignored) {
            }
        }

        try {
            return new Date(Long.parseLong(dt) * 1000);
        } catch (NumberFormatException ignored) {
        }

        try {
            return new Date(Instant.parse(dt).toEpochMilli());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + dt);
        }
    }

    protected static Time normalizeToTime(String dt) {

        final Matcher matcher = literalsRegexPattern.matcher(dt);
        if (matcher.find() && matcher.group(1).equals("t")) {
            dt = matcher.group(2);
        }


        Matcher nanoMatch = nanoPrecisionPattern.matcher(dt);
        if (nanoMatch.matches()) {
            try {
                String base = nanoMatch.group("prefix");
                StringBuilder fraction = new StringBuilder(nanoMatch.group("fraction"));
                while (fraction.length() < 9) fraction.append("0");
                LocalTime lt = LocalTime.parse(base, DateTimeFormatter.ofPattern("HH:mm:ss"))
                        .withNano(Integer.parseInt(fraction.toString()));
                return Time.valueOf(lt);
            } catch (Exception ignored) {}
        }

        for (DateTimeFormatter formatter : formatters) {
            try {
                return Time.valueOf(LocalTime.parse(dt, formatter));
            } catch (DateTimeParseException ignored) {}
        }

        try {
            long epoch = Long.parseLong(dt);
            Instant instant = Instant.ofEpochSecond(epoch);
            LocalTime time = instant.atZone(ZoneOffset.UTC).toLocalTime(); // force UTC
            return Time.valueOf(time);
        } catch (NumberFormatException | DateTimeException ignored) {}

        // Try generic ISO-8601
        try {
            Instant instant = Instant.parse(dt);
            LocalTime time = instant.atZone(ZoneOffset.UTC).toLocalTime(); // force UTC
            return Time.valueOf(time);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid time format: " + dt);
        }
    }
}

