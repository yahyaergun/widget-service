package com.yergun.widgetservice.service;

import com.yergun.widgetservice.exception.WidgetNotFoundException;
import com.yergun.widgetservice.model.Widget;
import com.yergun.widgetservice.model.WidgetPatchRequest;
import com.yergun.widgetservice.repository.WidgetRepository;
import com.yergun.widgetservice.util.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class WidgetService {

    private final WidgetRepository widgetRepository;

    public Widget create(Widget widget) {
        widget.setId(UUID.randomUUID());
        widget.setLastUpdated(LocalDateTime.now());

        if (widget.getZ() == null) {
            setWidgetToForeground(widget);
        } else {
            moveIfZIndexCollision(widget);
        }

        return widgetRepository.save(widget);
    }

    public Page<Widget> findAll(int pageCount, int size) {
        return widgetRepository.findByOrderByZAsc(PageRequest.of(pageCount, size, Sort.by(Sort.Direction.ASC, "z")));
    }

    public Widget findById(UUID id) {
        return widgetRepository.findById(id).orElseThrow(() -> new WidgetNotFoundException(id));
    }

    public void deleteById(UUID id) {
        boolean deleted = widgetRepository.deleteById(id);
        if (!deleted) {
            throw new WidgetNotFoundException(id);
        }
    }

    public Widget update(UUID id, WidgetPatchRequest widgetPatchRequest) {
        return widgetRepository.findById(id)
                .map(w -> applyPatchAndSave(widgetPatchRequest, w))
                .orElseThrow(() -> new WidgetNotFoundException(id));
    }

    private Widget applyPatchAndSave(WidgetPatchRequest widgetPatchRequest, Widget widget) {
        widgetRepository.delete(widget);
        BeanUtils.copyProperties(widgetPatchRequest, widget, ObjectUtils.getNullPropertyNames(widgetPatchRequest));
        widget.setLastUpdated(LocalDateTime.now());
        moveIfZIndexCollision(widget);
        return widgetRepository.save(widget);
    }

    private void moveIfZIndexCollision(Widget widget) {
        widgetRepository
                .findFirstByZ(widget.getZ())
                .ifPresent(this::moveWidgetsGreaterThanToForegroundByOne);
    }

    private void moveWidgetsGreaterThanToForegroundByOne(Widget widget) {
        widgetRepository.findByZGreaterThanEqualOrderByZAsc(widget)
                .forEach(w -> {
                    w.incrementZ();
                    w.setLastUpdated(LocalDateTime.now());
                    widgetRepository.save(w);
                });
    }

    private void setWidgetToForeground(Widget widget) {
        widgetRepository
                .findFirstByOrderByZDesc()
                .ifPresentOrElse(w -> widget.setZ(w.getZ() + 1),
                        () -> widget.setZ(0));
    }
}
