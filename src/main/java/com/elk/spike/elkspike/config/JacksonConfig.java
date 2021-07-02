package com.elk.spike.elkspike.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;

import java.text.SimpleDateFormat;

public class JacksonConfig {

    @Bean
    @Description("source: https://www.concretepage.com/jackson-api/jackson-jsonfilter-example")
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // https://www.baeldung.com/jackson-serialize-dates
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        mapper.setDateFormat(df);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		mapper.setSerializationInclusion(Include.NON_ABSENT);
        return mapper;
    }
}
