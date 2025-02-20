package br.com.nlw.events.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.nlw.events.model.EventModel;
import br.com.nlw.events.service.EventService;

@RestController
public class EventController {

    @Autowired
    private EventService service;

    @PostMapping("/events")
    public EventModel addNewEvent(@RequestBody EventModel newEvent) { 
        return service.addNewEvent(newEvent);
    }

    @GetMapping("/events")
    public List<EventModel> getAllEvents() {
        return service.getAllEvents();
    }

    @GetMapping("/events/{prettyName}")
    public ResponseEntity<EventModel> getEventbyPrettyName(@PathVariable String prettyName) {
        EventModel evt = service.getByPrettyName(prettyName);
        if (evt != null) {
            return ResponseEntity.ok().body(evt);
        } 
        return ResponseEntity.notFound().build();
    }
}