package com.yergun.widgetservice.controller;

import com.yergun.widgetservice.exception.WidgetNotFoundException;
import com.yergun.widgetservice.model.Widget;
import com.yergun.widgetservice.service.WidgetService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.yergun.widgetservice.TestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WidgetController.class)
class WidgetControllerTest {

    static final String BASE_WIDGETS_URL = "/widgets";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WidgetService widgetService;

    @Test
    void whenValidRequestOnPostWidgets_thenReturns201() throws Exception {
        mockMvc.perform(post(BASE_WIDGETS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(POST_WIDGET_JSON_VALID))
                .andExpect(status().isCreated());
    }

    @Test
    void whenInvalidRequestWithNoXParameterPostWidgets_thenReturns400() throws Exception {
        mockMvc.perform(post(BASE_WIDGETS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(POST_WIDGET_JSON_INVALID_NO_X))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenInvalidRequestWithNoYParameterPostWidgets_thenReturns400() throws Exception {
        mockMvc.perform(post(BASE_WIDGETS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(POST_WIDGET_JSON_INVALID_NO_Y))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenInvalidRequestWithZeroWidthPostWidgets_thenReturns400() throws Exception {
        mockMvc.perform(post(BASE_WIDGETS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(POST_WIDGET_JSON_INVALID_ZERO_WIDTH))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenInvalidRequestWithZeroHeightPostWidgets_thenReturns400() throws Exception {
        mockMvc.perform(post(BASE_WIDGETS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(POST_WIDGET_JSON_INVALID_ZERO_HEIGHT))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findAll_whenValidRequest_thenReturns200() throws Exception {
        mockMvc.perform(get(BASE_WIDGETS_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findAll_whenValidRequestWithPageAndSize_thenReturns200() throws Exception {
        mockMvc.perform(get(BASE_WIDGETS_URL)
                .param("page", "1")
                .param("size", "50")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findById_whenValidId_thenReturns200() throws Exception {
        UUID uuid = UUID.randomUUID();
        Widget widget = new Widget();
        widget.setId(uuid);

        Mockito.when(widgetService.findById(uuid)).thenReturn(widget);

        mockMvc.perform(get(BASE_WIDGETS_URL + "/" + uuid.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findById_whenNotFoundById_thenReturns404() throws Exception {
        Mockito.when(widgetService.findById(any())).thenThrow(WidgetNotFoundException.class);

        mockMvc.perform(get(BASE_WIDGETS_URL + "/" + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_whenValidId_thenReturns204() throws Exception {
        UUID uuid = UUID.randomUUID();
        Widget widget = new Widget();
        widget.setId(uuid);

        Mockito.when(widgetService.findById(uuid)).thenReturn(widget);

        mockMvc.perform(delete(BASE_WIDGETS_URL + "/" + uuid.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_whenNotFoundById_thenReturns404() throws Exception {
        Mockito.when(widgetService.findById(any())).thenThrow(WidgetNotFoundException.class);

        mockMvc.perform(get(BASE_WIDGETS_URL + "/" + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void patch_whenValidRequest_thenReturns200() throws Exception {
        mockMvc.perform(patch(BASE_WIDGETS_URL + "/" + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(PATCH_JSON_VALID))
                .andExpect(status().isOk());
    }

    @Test
    void patch_whenNotFoundById_thenReturns404() throws Exception {
        Mockito.when(widgetService.update(any(), any())).thenThrow(WidgetNotFoundException.class);

        mockMvc.perform(patch(BASE_WIDGETS_URL + "/" + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(PATCH_JSON_VALID))
                .andExpect(status().isNotFound());
    }


}