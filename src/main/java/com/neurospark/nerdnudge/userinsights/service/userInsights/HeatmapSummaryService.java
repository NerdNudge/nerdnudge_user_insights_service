package com.neurospark.nerdnudge.userinsights.service.userInsights;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.userinsights.utils.Commons;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HeatmapSummaryService {
    public Map<String, Integer[]> getHeatmapEntity(JsonObject userData) {
        Map<String, Integer[]> heatMap = new HashMap<>();
        if(! userData.has("dayQuota"))
            return heatMap;

        JsonObject dayQuotaObject = userData.get("dayQuota").getAsJsonObject();
        Iterator<Map.Entry<String, JsonElement>> dayQuotaIterator = dayQuotaObject.entrySet().iterator();
        while (dayQuotaIterator.hasNext()) {
            Map.Entry<String, JsonElement> thisEntry = dayQuotaIterator.next();
            int dayDifference = Commons.getDaysDifferenceFromToday(thisEntry.getKey());
            if(dayDifference < 0 || dayDifference > 180)
                continue;

            String readableDate = Commons.convertDayStringToReadable(thisEntry.getKey(), "yyyy-MM-dd");
            JsonArray thisQuota = thisEntry.getValue().getAsJsonArray();
            heatMap.put(readableDate, new Integer[] {thisQuota.get(0).getAsInt(), thisQuota.get(1).getAsInt()});
        }
        return heatMap;
    }
}
