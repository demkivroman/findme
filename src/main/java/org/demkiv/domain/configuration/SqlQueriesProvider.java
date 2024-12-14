package org.demkiv.domain.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
@PropertySource(value = "classpath:sql_queries.properties")
public class SqlQueriesProvider {
    @Value("${findPersonInformation}")
    String findPersonInformation;
    @Value("${personDetailedInformation}")
    String personDetailedInformation;
    @Value("${postsTotalQuery}")
    String postsTotalQuery;
    @Value("${personPosts}")
    String personPosts;
    @Value("${personIds}")
    String personIds;
    @Value("${selectPersonsAndThumbnailsByIds}")
    String selectPersonsAndThumbnailsByIds;
}
