package de.test;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class MyMessagingApplication {

    @Inject
    PersonRepository repository;

    /**
     * Consume the uppercase channel (in-memory) and print the messages.
     **/
    @Incoming("test")
    public void sink(EmployeeDto word) {
        this.repository.savePerson(word);
    }
}
