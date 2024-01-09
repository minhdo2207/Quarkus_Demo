package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class DateUtils {
    public static final String DATE_TIME_TYPE_RESPONSE = "dd/MM/yyyy HH:mm:ss";

    public static final String LOCALDATE_TO_STRING_FORMAT = "yyyy-MM-dd";


    public static Date convertToDate(LocalDateTime dateToConvert) {
        return Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }

    public static long convertToLong(LocalDateTime input) {
        return input.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static Date convertToDate(LocalDate dateToConvert) {
        return Date.from(dateToConvert.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate converttoLocalDate(long input) {
        return Instant.ofEpochMilli(input)
                .atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate convertToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static LocalDate convertToLocalDate(Date date, ZoneId zoneId) {
        return date.toInstant()
                .atZone(zoneId)
                .toLocalDate();
    }

    public static LocalDateTime convertToLocalDateTime(Date input) {
        return input.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static LocalDateTime convertToLocalDateTime(Date input, ZoneId zoneId) {
        return input.toInstant()
                .atZone(zoneId)
                .toLocalDateTime();
    }

    public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {

        List<LocalDate> dates = startDate.datesUntil(endDate)
                .collect(Collectors.toList());
        dates.add(endDate);
        return dates;
    }

    public static LocalDateTime convertToLocalDateTime(String inputDate, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime dateTime = LocalDateTime.parse(inputDate, formatter);
        return dateTime;
    }

    public static LocalDate convertToLocalDate(String input, String format) {
        return LocalDate.parse(input, DateTimeFormatter.ofPattern(format));
    }

    public static String convertDateToString(LocalDate date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    public static String convertDateToString(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static String convertDateTimeToString(LocalDateTime date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    public static Date convertStringToDate(String dateStr, String format) {
        DateFormat df1 = new SimpleDateFormat(format);
        try {
            return df1.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date convertStringToDate(String dateStr, String format, TimeZone timeZone) {
        DateFormat df1 = new SimpleDateFormat(format);
        df1.setTimeZone(timeZone);
        try {
            return df1.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }



    public static boolean isDateGreaterThanDays(LocalDate fromDate, LocalDate toDate, int limitDate) {
        LocalDate dateDaysAgo = toDate.minus(limitDate, ChronoUnit.DAYS);
        return fromDate.isAfter(dateDaysAgo);
    }
}
