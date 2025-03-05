package com.kubling.teiid.core.types.basic;

import com.kubling.teiid.core.types.Transform;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseDatetimeTransform extends Transform {

    protected static final List<DateTimeFormatter> formatters = new ArrayList<>();

    protected static final Pattern literalsRegexPattern = Pattern.compile("\\{(d|t|ts) '([^']*)'\\}");

    static {
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
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.S"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SS"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS"));
    }

    protected static Timestamp normalizeToTimestamp(String dt) {

        // Extract value if JDBC escape format is detected
        final Matcher matcher = literalsRegexPattern.matcher(dt);
        if (matcher.matches() && matcher.group(1).equals("ts")) {
            dt = matcher.group(2); // Extract raw value and process normally
        }

        // Try predefined formatters
        for (DateTimeFormatter formatter : formatters) {
            try {
                return Timestamp.valueOf(LocalDateTime.parse(dt, formatter));
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        // Try epoch timestamp
        try {
            return Timestamp.from(Instant.ofEpochSecond(Long.parseLong(dt)));
        } catch (NumberFormatException e) {
            // Try generic instant parse
        }

        try {
            return Timestamp.from(Instant.parse(dt));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid timestamp format: " + dt);
        }
    }

    protected static Date normalizeToDate(String dt) {

        // Extract value if JDBC escape format {d 'YYYY-MM-DD'} is detected
        final Matcher matcher = literalsRegexPattern.matcher(dt);
        if (matcher.matches() && matcher.group(1).equals("d")) {
            dt = matcher.group(2); // Extract raw value and process normally
        }

        // Try predefined formatters
        for (DateTimeFormatter formatter : formatters) {
            try {
                return Date.valueOf(LocalDate.parse(dt, formatter));
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        // Try epoch timestamp
        try {
            return (Date) Date.from(Instant.ofEpochSecond(Long.parseLong(dt)));
        } catch (NumberFormatException e) {
            // Try generic instant parse
        }

        try {
            return (Date) Date.from(Instant.parse(dt));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid date format: " + dt);
        }
    }

    protected static Time normalizeToTime(String dt) {

        // Extract value if JDBC escape format {t 'HH:mm:ss'} is detected
        final Matcher matcher = literalsRegexPattern.matcher(dt);
        if (matcher.matches() && matcher.group(1).equals("t")) {
            dt = matcher.group(2); // Extract raw value and process normally
        }

        // Try predefined formatters
        for (DateTimeFormatter formatter : formatters) {
            try {
                return Time.valueOf(LocalTime.parse(dt, formatter));
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        try {
            return (Time) Time.from(Instant.ofEpochSecond(Long.parseLong(dt)));
        } catch (NumberFormatException e) {
            // Try generic instant parse
        }

        try {
            return (Time) Time.from(Instant.parse(dt));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid time: " + dt);
        }
    }

}
