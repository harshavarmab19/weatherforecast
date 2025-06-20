import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class weatherapp {

    public static JSONObject getweatherdata(String locationName) {
        JSONArray loactionData = getlocationData(locationName);

        if (loactionData == null || loactionData.isEmpty()) {
            System.out.println("Location data is null or empty.");
            return null;
        }

        JSONObject location = (JSONObject) loactionData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = "https://historical-forecast-api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&start_date=2025-05-19&end_date=2025-06-02&hourly=temperature_2m,weather_code,wind_speed_10m,relative_humidity_2m&timezone=auto";

        try {
            HttpURLConnection conn = fetchAPIResponse(urlString);
            if (conn == null || conn.getResponseCode() != 200) {
                System.out.println("Error: Couldn't connect to the weather API.");
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            String weathercondition = convertweathercode((long) weathercode.get(index));

            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windSpeedData.get(index);

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weathercondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static JSONArray getlocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&count=1&language=en&format=json";

        try {
            HttpURLConnection conn = fetchAPIResponse(urlString);
            if (conn == null || conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to location API.");
                return null;
            }

            StringBuilder jsonText = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                jsonText.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject response = (JSONObject) parser.parse(jsonText.toString());

            JSONArray results = (JSONArray) response.get("results");

            if (results == null || results.isEmpty()) {
                System.out.println("No matching location found.");
                return null;
            }

            return results;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection fetchAPIResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timelist) {
        String currentTime = getCurrentTime();
        for (int i = 0; i < timelist.size(); i++) {
            String time = (String) timelist.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }
        return 0;
    }

    public static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        return currentDateTime.format(formatter);
    }

    private static String convertweathercode(long weathercode) {
        if (weathercode == 0L) return "clear";
        if (weathercode > 0L && weathercode <= 3L) return "cloudy";
        if ((weathercode >= 51L && weathercode <= 67L) || (weathercode >= 80L && weathercode <= 99L)) return "Rain";
        if (weathercode >= 71L && weathercode <= 77L) return "snow";
        return "unknown";
    }
}
