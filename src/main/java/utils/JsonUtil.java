package utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;


public final class JsonUtil {
    private static final ObjectMapper MAPPER;

    static {
        ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.registerModule(new JavaTimeModule());
        MAPPER = objectMapper;
    }

    public JsonUtil() {
    }

    public static <T> byte[] convertObjectToBytes(T t) throws JsonProcessingException {
        return MAPPER.writeValueAsBytes(t);
    }

    public static <T> T convertJsonToObject(String json, Class<T> outputType) throws JsonProcessingException {
        return MAPPER.readValue(json, outputType);
    }

    public static String convertToJson(Object object) throws JsonProcessingException {
        ObjectWriter ow = MAPPER.writer();
        return StringUtils.equals(ow.writeValueAsString(object), "null") ? null : ow.writeValueAsString(object);
    }

    public static String convertToJsonDefaultNull(Object object) {
        ObjectWriter ow = MAPPER.writer();
        try {
            return StringUtils.equals(ow.writeValueAsString(object), "null") ? null : ow.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
