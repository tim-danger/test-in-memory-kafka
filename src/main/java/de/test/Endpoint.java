package de.test;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;

@Path("employee")
public class Endpoint {

    @Inject
    @Channel("rest")
    @OnOverflow(value = OnOverflow.Strategy.NONE)
    Emitter<EmployeeDto> emitter;

    @Path("/create")
    @POST
    public EmployeeDto createEmployee(EmployeeDto dto) {
        emitter.send(dto);
        return dto;
    }
}
