package com.yergun.widgetservice;

import com.yergun.widgetservice.model.Widget;
import com.yergun.widgetservice.repository.WidgetRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TestUtils {

    String POST_WIDGET_JSON_VALID = "{\n" +
            "  \"x\": 1,\n" +
            "  \"y\": 1,\n" +
            "  \"width\": 10,\n" +
            "  \"height\": 10\n" +
            "}";

    String POST_WIDGET_JSON_INVALID_NO_X = "{\n" +
            "  \"y\": 1,\n" +
            "  \"width\": 10,\n" +
            "  \"height\": 10\n" +
            "}";

    String POST_WIDGET_JSON_INVALID_NO_Y = "{\n" +
            "  \"x\": 1,\n" +
            "  \"width\": 10,\n" +
            "  \"height\": 10\n" +
            "}";

    String POST_WIDGET_JSON_INVALID_ZERO_WIDTH = "{\n" +
            "  \"x\": 1,\n" +
            "  \"y\": 1,\n" +
            "  \"width\": 0,\n" +
            "  \"height\": 10\n" +
            "}";

    String POST_WIDGET_JSON_INVALID_ZERO_HEIGHT = "{\n" +
            "  \"x\": 1,\n" +
            "  \"y\": 1,\n" +
            "  \"width\": 10,\n" +
            "  \"height\": 0\n" +
            "}";

    String PATCH_JSON_VALID = "{\n" +
            "  \"x\": 555\n" +
            "}";

    static void fillWidgets(int count, WidgetRepository repository) {
        for (int i = 0; i < count; i++) {
            repository.save(new Widget(UUID.randomUUID(),
                    10, 10, i,
                    10, i, LocalDateTime.now()));
        }
    }
}
