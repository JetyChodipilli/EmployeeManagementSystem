package com.example.employeemanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new LocalDateFormatter());
    }

    static class LocalDateFormatter implements Formatter<LocalDate> {

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Override
        public LocalDate parse(String text, Locale locale) throws ParseException {
            if (text == null || text.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(text, FORMATTER);
            } catch (Exception e) {
                throw new ParseException("Invalid date format: " + text, 0);
            }
        }

        @Override
        public String print(LocalDate object, Locale locale) {
            if (object == null) {
                return "";
            }
            return FORMATTER.format(object);
        }
    }
}