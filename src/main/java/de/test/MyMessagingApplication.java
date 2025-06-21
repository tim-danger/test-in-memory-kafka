package de.test;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class MyMessagingApplication {

    @Inject
    PersonRepository repository;

    @Outgoing("rest")
    @Incoming("test")
    public void sink(EmployeeDto word) {
        this.repository.savePerson(word);
    }
}
