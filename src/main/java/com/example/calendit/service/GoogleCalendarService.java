package com.example.calendit.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@Slf4j
public class GoogleCalendarService {
    
    private static final String APPLICATION_NAME = "Calendit";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    
    public String createCalendarEvent(String accessToken, String ownerEmail, String bookerEmail, 
                                     LocalDate date, LocalTime time, String title) {
        try {
            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
            
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            
            Event event = new Event()
                    .setSummary(title != null ? title : "Meeting via Calendit")
                    .setDescription("Booking created through Calendit");
            
            ZonedDateTime startDateTime = ZonedDateTime.of(date, time, ZoneId.systemDefault());
            ZonedDateTime endDateTime = startDateTime.plusHours(1);
            
            EventDateTime start = new EventDateTime()
                    .setDateTime(new DateTime(java.util.Date.from(startDateTime.toInstant())))
                    .setTimeZone(ZoneId.systemDefault().getId());
            event.setStart(start);
            
            EventDateTime end = new EventDateTime()
                    .setDateTime(new DateTime(java.util.Date.from(endDateTime.toInstant())))
                    .setTimeZone(ZoneId.systemDefault().getId());
            event.setEnd(end);
            
            event = service.events().insert("primary", event).execute();
            log.info("Event created: {}", event.getHtmlLink());
            
            return event.getId();
        } catch (Exception e) {
            log.error("Error creating calendar event", e);
            return null;
        }
    }
    
    public void deleteCalendarEvent(String accessToken, String eventId) {
        try {
            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
            
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            
            service.events().delete("primary", eventId).execute();
            log.info("Event deleted: {}", eventId);
        } catch (Exception e) {
            log.error("Error deleting calendar event", e);
        }
    }
}
