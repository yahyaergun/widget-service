package com.yergun.widgetservice.util;

import com.yergun.widgetservice.model.Widget;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectUtilsTest {

    @Test
    void getNullPropertyNames_whenCalledWith5Nulls_thenReturnsThem() {
        Widget w = Widget.builder()
                .height(10).x(10)
                .build();

        String[] nullPropertyNames = ObjectUtils.getNullPropertyNames(w);

        assertThat(nullPropertyNames.length).isEqualTo(5);
    }

    @Test
    void getNullPropertyNames_whenCalledWith0Nulls_thenReturnsEmptyArray() {
        Widget w = Widget.builder()
                .id(UUID.randomUUID()).height(10).width(10)
                .x(10).y(10).z(0)
                .lastUpdated(LocalDateTime.now())
                .build();

        String[] nullPropertyNames = ObjectUtils.getNullPropertyNames(w);

        assertThat(nullPropertyNames.length).isEqualTo(0);
    }
}
