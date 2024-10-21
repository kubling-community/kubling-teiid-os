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
import java.util.regex.Pattern;

public abstract class BaseDatetimeTransform extends Transform {
    protected static final List<DateTimeFormatter> formatters = new ArrayList<>();

    protected static final Pattern literalsRegexPattern = Pattern.compile("\\{(d|t|dt) '([^']*)'\\}");

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

        for (DateTimeFormatter formatter : formatters) {
            try {
                return Timestamp.valueOf(LocalDateTime.parse(dt, formatter));
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }

        try {
            return Timestamp.from(Instant.ofEpochSecond(Long.parseLong(dt)));
        } catch (NumberFormatException e) {
            // Try generic instant parse
        }

        try {
            return Timestamp.from(Instant.parse(dt));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid timestamp format: " + dt);
        }
    }

    protected static Date normalizeToDate(String dt) {

        for (DateTimeFormatter formatter : formatters) {
            try {
                return Date.valueOf(LocalDate.parse(dt, formatter));
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }

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

        for (DateTimeFormatter formatter : formatters) {
            try {
                return Time.valueOf(LocalTime.parse(dt, formatter));
            } catch (DateTimeParseException e) {
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
