package com.neurospark.nerdnudge.userinsights.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
public class Commons {

    private static Commons instance;

    private Commons() {}

    public static Commons getInstance() {
        if (instance == null) {
            synchronized (Commons.class) {
                if (instance == null) {
                    instance = new Commons();
                }
            }
        }
        return instance;
    }

    public void housekeepDayJsonObject(JsonObject jsonObject, int retentionEntries) {
        Set<Map.Entry <String , JsonElement>> dailyQuotaKeys = jsonObject.entrySet();
        if(dailyQuotaKeys.size() <= retentionEntries)
            return;

        TreeSet<String> sortedKeys = new TreeSet<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            sortedKeys.add(entry.getKey());
        }

        while(jsonObject.entrySet().size() > retentionEntries) {
            String oldestKey = sortedKeys.pollFirst();
            if (oldestKey != null) {
                jsonObject.remove(oldestKey);
            }
        }
    }

    public JsonArray getUpdatedArray(JsonElement arrayElement, int totalCount, int correctCount) {
        JsonArray totalArray = (arrayElement == null || arrayElement.isJsonNull()) ? new JsonArray() : arrayElement.getAsJsonArray();
        JsonArray newArray = new JsonArray();

        if(totalArray.size() > 0) {
            newArray.add(new JsonPrimitive(totalArray.get(0).getAsInt() + totalCount));
            newArray.add(new JsonPrimitive(totalArray.get(1).getAsInt() + correctCount));
        } else {
            newArray.add(new JsonPrimitive(totalCount));
            newArray.add(new JsonPrimitive(correctCount));
        }

        return newArray;
    }

    public static String getDaystamp() {
        LocalDate date = LocalDate.now();
        int dayOfYear = date.getDayOfYear();
        int year = date.getYear() % 100;
        String dayOfYearStr = String.format("%03d", dayOfYear);
        String yearStr = String.format("%02d", year);
        return dayOfYearStr + yearStr;
    }

    public String getWeekstamp() {
        LocalDate now = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekOfYear = now.get(weekFields.weekOfYear());
        int year = now.getYear();
        return String.format("%04d%02d", year, weekOfYear); // e.g. 202538
    }

    public static String getDaystampBeforeXDays(int x) {
        LocalDate date = LocalDate.now().minusDays(x);
        int dayOfYear = date.getDayOfYear();
        int year = date.getYear() % 100;
        String dayOfYearStr = String.format("%03d", dayOfYear);
        String yearStr = String.format("%02d", year);
        return dayOfYearStr + yearStr;
    }

    public static String getWeekStampBeforeXWeeks(int x) {
        LocalDate date = LocalDate.now().minusWeeks(x);
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekOfYear = date.get(weekFields.weekOfYear());
        int year = date.getYear();
        return String.format("%04d%02d", year, weekOfYear); // e.g. 202536
    }

    public static int getDaysDifferenceFromToday(String currentKey) {
        int dayOfYearCurrentKey = Integer.parseInt(currentKey.substring(0, 3));
        int yearCurrentKey = Integer.parseInt(currentKey.substring(3, 5));

        String currentDay = getDaystamp();
        int dayOfYearToday = Integer.parseInt(currentDay.substring(0, 3));
        int yearToday = Integer.parseInt(currentDay.substring(3, 5));

        int daysDifference = 0;

        if (yearCurrentKey == yearToday) {
            daysDifference = dayOfYearToday - dayOfYearCurrentKey;
        } else if (yearCurrentKey < yearToday) {
            daysDifference = (365 - dayOfYearCurrentKey) + dayOfYearToday + (yearToday - yearCurrentKey - 1) * 365;
        }
        return daysDifference;
    }

    public static String convertDayStringToReadable(String dayString, String pattern) {
        int dayOfYear = Integer.parseInt(dayString.substring(0, 3));
        int year = Integer.parseInt("20" + dayString.substring(3, 5));

        LocalDate date = LocalDate.ofYearDay(year, dayOfYear);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }

    public static JsonObject getUserProfileDocument(String userId, NerdPersistClient userProfilesPersist) {
        JsonObject userData = userProfilesPersist.get(userId);
        if(userData == null) {
            log.info("User does not exist, creating a new user: {}", userId);
            userData = new JsonObject();
            userData.addProperty("registrationDate", Instant.now().getEpochSecond());
            userData.addProperty("type", "userProfile");
            userData.addProperty("accountType", "freemium");
            userData.addProperty("accountStartDate", getDaystamp());
        }

        return userData;
    }

    public static long getDaysDifference(long epochTimeInMillis) {
        LocalDate dateFromEpoch = Instant.ofEpochMilli(epochTimeInMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate currentDate = LocalDate.now();
        return ChronoUnit.DAYS.between(dateFromEpoch, currentDate);
    }

    public static double getPercentage(int total, int correct) {
        if(total == 0)
            return 0.0;

        double percentage = ((double) correct / total) * 100;
        return Math.round(percentage * 100.0) / 100.0;
    }
}
