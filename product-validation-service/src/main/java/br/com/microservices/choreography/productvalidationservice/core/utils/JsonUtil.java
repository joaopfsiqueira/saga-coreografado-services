package br.com.microservices.choreography.productvalidationservice.core.utils;


import br.com.microservices.choreography.productvalidationservice.core.dto.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

// vai ser um singleton
@Component
@AllArgsConstructor // instancia automaticamente dentro de um construtor
public class JsonUtil {

    private final ObjectMapper objectMapper;

    public String toJson(Object object){
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return "";
        }
    }

    public Event toEvent(String json) {
        try {
            return objectMapper.readValue(json, Event.class);
        } catch (Exception e) {
            return null;
        }
    }
}
