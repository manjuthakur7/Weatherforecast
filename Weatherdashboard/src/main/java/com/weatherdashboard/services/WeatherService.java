package com.weatherdashboard.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherdashboard.config.WeatherConfig;
import com.weatherdashboard.model.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class WeatherService {

    @Autowired
    private WeatherConfig weatherConfig;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl = "https://api.openweathermap.org/data/2.5/";

    public Weather getCurrentWeather(String city) {
        String url = baseUrl + "weather?q=" + city + "&appid=" + weatherConfig.getApiKey();
        String response = restTemplate.getForObject(url, String.class);
        return parseWeatherResponse(response);
    }

    private Weather parseWeatherResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            Weather weather = new Weather();
            weather.setCity(root.path("name").asText());
            weather.setDescription(root.path("weather").get(0).path("description").asText());
            weather.setTemperature(root.path("main").path("temp").asDouble() - 273.15); // Convert Kelvin to Celsius
            weather.setFeelsLike(root.path("main").path("feels_like").asDouble() - 273.15); // Convert Kelvin to Celsius
            weather.setHumidity(root.path("main").path("humidity").asInt());
            weather.setWindSpeed(root.path("wind").path("speed").asDouble());
            return weather;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse weather data", e);
        }
    }

    public String getWeatherForecast(String city) {
        String url = baseUrl + "forecast?q=" + city + "&appid=" + weatherConfig.getApiKey();
        String response = restTemplate.getForObject(url, String.class);
        return filterNextDayForecast(response);
    }

    private String filterNextDayForecast(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode list = root.path("list");

            // Get current date and next day's date
            LocalDate nextDay = LocalDate.now().plusDays(1);
            String nextDayStr = nextDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Filter the forecast data for the next day
            StringBuilder nextDayForecast = new StringBuilder();
            java.util.Iterator<JsonNode> elements = list.elements();
            while (elements.hasNext()) {
                JsonNode element = elements.next();
                String dtTxt = element.path("dt_txt").asText();
                if (dtTxt.startsWith(nextDayStr)) {
                    nextDayForecast.append("Date and Time: ").append(dtTxt).append("\n");
                    nextDayForecast.append("Temperature: ").append(String.format("%.2f", element.path("main").path("temp").asDouble() - 273.15)).append(" °C\n");
                    nextDayForecast.append("Feels Like: ").append(String.format("%.2f", element.path("main").path("feels_like").asDouble() - 273.15)).append(" °C\n");
                    nextDayForecast.append("Weather: ").append(element.path("weather").get(0).path("description").asText()).append("\n");
                    nextDayForecast.append("Humidity: ").append(element.path("main").path("humidity").asInt()).append(" %\n");
                    nextDayForecast.append("Wind Speed: ").append(element.path("wind").path("speed").asDouble()).append(" m/s\n");
                    nextDayForecast.append("----------------------------------------\n");
                }
            }

            return nextDayForecast.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse forecast data", e);
        }
    }

   
}




