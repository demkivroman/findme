package org.demkiv.domain.configuration;

import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.domain.service.SavePersonService;
import org.demkiv.web.model.PersonForm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Services {
    @Bean
    EntitySaver<PersonForm, Boolean> getSaveService() {
        return new SavePersonService();
    }
}
