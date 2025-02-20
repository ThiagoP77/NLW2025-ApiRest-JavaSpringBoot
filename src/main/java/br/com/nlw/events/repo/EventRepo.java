package br.com.nlw.events.repo;

import org.springframework.data.repository.CrudRepository;

import br.com.nlw.events.model.EventModel;

public interface EventRepo extends CrudRepository<EventModel, Integer>{

    public EventModel findByPrettyName(String prettyName);

}
