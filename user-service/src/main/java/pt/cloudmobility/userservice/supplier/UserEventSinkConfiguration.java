package pt.cloudmobility.userservice.supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.cloudmobility.userservice.dto.UserEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@Configuration
public class UserEventSinkConfiguration {

    @Bean
    public Sinks.Many<UserEvent> userEventSink() {
        // this will create a sink that will send n signals to a unique subscriber
        // on back pressure the sink will buffer the results
        return Sinks.many().unicast().onBackpressureBuffer();
    }

    @Bean
    public Supplier<Flux<UserEvent>> userEventSupplier() {
        return () -> this.userEventSink().asFlux();
    }
}
