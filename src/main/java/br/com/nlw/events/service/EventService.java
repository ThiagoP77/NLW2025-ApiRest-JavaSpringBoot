package br.com.nlw.events.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.nlw.events.model.EventModel;
import br.com.nlw.events.repo.EventRepo;

@Service
public class EventService {

    @Autowired
    private EventRepo eventRepo;

    public EventModel addNewEvent (EventModel event) {
        event.setPrettyName(event.getTitle().toLowerCase().replace(" ", "-"));
        return eventRepo.save(event);
    }

    public List<EventModel> getAllEvents() {
        return (List<EventModel>) eventRepo.findAll();
    }

    public EventModel getByPrettyName(String prettyName) {
        return eventRepo.findByPrettyName(prettyName);
    }
}
