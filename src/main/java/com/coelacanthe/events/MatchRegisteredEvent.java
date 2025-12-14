package com.coelacanthe.events;

import com.coelacanthe.entities.MatchEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MatchRegisteredEvent extends ApplicationEvent {

    private final MatchEntity match;

    public MatchRegisteredEvent(Object source, MatchEntity match) {
        super(source);
        this.match = match;
    }
}
