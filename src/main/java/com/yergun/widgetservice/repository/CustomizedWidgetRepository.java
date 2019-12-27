package com.yergun.widgetservice.repository;

import com.yergun.widgetservice.model.Widget;
import com.yergun.widgetservice.model.WidgetPatchRequest;

import java.util.UUID;

public interface CustomizedWidgetRepository {
    Widget update(UUID id, WidgetPatchRequest patchRequest);
    Widget save(Widget widget);
}
