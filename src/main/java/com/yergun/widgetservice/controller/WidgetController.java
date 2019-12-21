package com.yergun.widgetservice.controller;

import com.yergun.widgetservice.exception.WidgetNotFoundException;
import com.yergun.widgetservice.model.Widget;
import com.yergun.widgetservice.model.WidgetPatchRequest;
import com.yergun.widgetservice.service.WidgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.validation.Valid;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/widgets")
public class WidgetController {

    private final WidgetService widgetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Widget create(@Valid @RequestBody Widget widget) {
        return widgetService.create(widget);
    }

    @GetMapping
    public Flux<Widget> findAll() {
        return widgetService.findAll();
    }

    @GetMapping(path = "/{id}")
    public Widget find(@PathVariable UUID id) {
        return widgetService.findById(id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        widgetService.deleteById(id);
    }

    @PatchMapping(path = "/{id}")
    public Widget update(@PathVariable UUID id, @RequestBody WidgetPatchRequest widgetPatchRequest) {
        return widgetService.update(id, widgetPatchRequest);
    }

}
