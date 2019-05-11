package com.tykj.core.support;

import com.tykj.utils.JsonUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StringToMapConverter implements Converter<String,Map<String,Object>> {

    @Override
    public Map<String, Object> convert(String source) {
        if(source.startsWith("{") && source.endsWith("}")){
            return JsonUtils.toMap(source);
        }
        return null;
    }
}
