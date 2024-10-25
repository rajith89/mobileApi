package com.udipoc.api.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public final class DateUtils {

    private static final String UTC_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static String getUTCCurrentDateTimeString() {
        return OffsetDateTime.now().toInstant().toString();
    }

    public static String getUTCCurrentDate() {
        return String.valueOf(Instant.now());
    }

    public static String formatToISOString(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(UTC_DATETIME_PATTERN));
    }

    public static LocalDateTime getUTCCurrentDateTime() {
        return ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();
    }

    public static Boolean validateServiceTokenExpire(Date currentdate,Date tokenGenerateDate, int hours) {
        Calendar tokenGenCalendarDate = Calendar.getInstance();
        tokenGenCalendarDate.setTime(tokenGenerateDate);
        tokenGenCalendarDate.add(Calendar.HOUR_OF_DAY, hours);
        if (tokenGenCalendarDate.getTime().before(currentdate)){
            return true;
        }else {
         return false;
        }
    }
}
