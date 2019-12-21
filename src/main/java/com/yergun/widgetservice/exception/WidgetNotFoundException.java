package com.yergun.widgetservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Widget not found!")
public class WidgetNotFoundException extends RuntimeException{

    public WidgetNotFoundException(UUID id) {
        super("Widget not found by id:[" + id + "]");
    }

}
