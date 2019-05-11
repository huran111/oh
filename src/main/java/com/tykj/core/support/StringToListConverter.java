package com.tykj.core.support;

import org.springframework.core.convert.converter.Converter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
@Component
public class StringToListConverter implements Converter<String,List<String>> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<String> convert(String source) {
        if(source.startsWith("[") && source.endsWith("]")){
            try {
                return objectMapper.readValue(source,new TypeReference<List<String>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
