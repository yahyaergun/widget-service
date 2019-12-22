package com.yergun.widgetservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yergun.widgetservice.model.Widget;
import com.yergun.widgetservice.model.WidgetPatchRequest;
import com.yergun.widgetservice.repository.WidgetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.yergun.widgetservice.TestUtils.fillWidgets;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WidgetScenariosIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WidgetRepository repository;

    @Test
    void widgetCreation_CollideOnZScenario_moveWidgetsWithGreaterZ() throws Exception {
        fillWidgets(5, repository);

        int expectedX = 6666;
        Widget widget = Widget.builder()
                .x(expectedX).y(10).z(3)
                .height(10).width(10)
                .build();

        mockMvc.perform(post("/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(widget)))
                .andExpect(status().isCreated());

        List<Widget> widgets = repository.findByOrderByZAsc(PageRequest.of(0, 10)).getContent();

        assertThat(widgets.size()).isEqualTo(6);
        assertThat(widgets.get(3).getX()).isEqualTo(expectedX);
        assertThat(widgets.get(5).getZ()).isEqualTo(5);
    }

    @Test
    void widgetPatch_CollideOnZScenario_moveWidgetsWithGreaterZ() throws Exception {
        fillWidgets(5, repository);

        int expectedX = 999;
        WidgetPatchRequest wpr = new WidgetPatchRequest();
        wpr.setX(expectedX);
        wpr.setZ(3);

        Widget widgetWithZ0 = repository.findByOrderByZAsc(PageRequest.of(0, 1)).getContent().get(0);

        mockMvc.perform(patch("/widgets/" + widgetWithZ0.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wpr)))
                .andExpect(status().isOk());

        List<Widget> widgets = repository.findByOrderByZAsc(PageRequest.of(0, 10)).getContent();

        assertThat(widgets.size()).isEqualTo(5);
        assertThat(widgets.get(2).getX()).isEqualTo(expectedX); // patched widget should be on nr.3
        assertThat(widgets.get(4).getZ()).isEqualTo(5); //last widget should have its z incremented
    }
}
