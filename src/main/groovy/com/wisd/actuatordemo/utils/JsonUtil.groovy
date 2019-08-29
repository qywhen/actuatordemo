package com.wisd.actuatordemo.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.GsonBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created with IntelliJ IDEA.
 * date: 2018-09-07
 * time: 15:55
 *
 * @author wqy
 */
@Component
class JsonUtil {
    static ObjectMapper objectMapper
    static gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create()
/**
 * 此方法用于一般对象的转化，带有{@http java.time.LocalDate}的对象不能用此方法，会造成内存益出
 * @param obj
 * @return
 */
    static String toJson(Object obj) {
        objectMapper.writeValueAsString obj
    }

    static String toJsonGson(Object obj) {
        gson.toJson(obj)
    }

    static <T> T fromJson(String str, Class<T> type) {
        objectMapper.readValue str, type
    }

    static Map<String, Object> parseToMap(String str) {
        def typeref = new TypeReference<Map<String, Object>>() {}
        objectMapper.readValue str, typeref
    }
 static List<String> parse2List(String str) {
        def typeref = new TypeReference<List<String>>() {}
        objectMapper.readValue str, typeref
    }

    @Autowired
    void confObjectMapper(ObjectMapper mapper) {
        objectMapper = mapper
    }


}
