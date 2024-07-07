package com.weatherdashboard.controller;

import com.weatherdashboard.model.Weather;
import com.weatherdashboard.services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/current-weather")
    public String getCurrentWeather(@RequestParam("city") String city, Model model) {
        Weather weather = weatherService.getCurrentWeather(city);
        model.addAttribute("weather", weather);
        return "weather";
    }
    @GetMapping("/weather-forecast")
    public String getWeatherForecast(@RequestParam("city") String city, Model model) {
        String forecast = weatherService.getWeatherForecast(city);
        model.addAttribute("forecast", forecast);
        model.addAttribute("city", city);
        return "forecast";
    }

  
}




