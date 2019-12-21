package com.yergun.widgetservice.service;

import com.yergun.widgetservice.exception.WidgetNotFoundException;
import com.yergun.widgetservice.model.Widget;
import com.yergun.widgetservice.model.WidgetPatchRequest;
import com.yergun.widgetservice.repository.WidgetRepository;
import com.yergun.widgetservice.util.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class WidgetService {

    private final WidgetRepository widgetRepository;

    public Widget create(Widget widget) {
        widget.setId(UUID.randomUUID());
        widget.setLastUpdated(LocalDateTime.now());

        if (widget.getZIndex() == null) {
            setWidgetToForeground(widget);
        } else {
            moveIfZIndexCollision(widget);
        }

        return widgetRepository.save(widget);
    }

    public Flux<Widget> findAll() {
        return widgetRepository.findAllByOrderByZIndexAsc();
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
                .findByZIndex(widget.getZIndex())
                .ifPresent(this::moveWidgetsGreaterThanToForegroundByOne);
    }

    private void moveWidgetsGreaterThanToForegroundByOne(Widget widget) {
        widgetRepository
                .findAllByZIndexGreaterThanEqualOrderByZIndexDesc(widget)
                .doOnNext(w -> w.setZIndex(w.getZIndex() + 1))
                .doOnNext(w -> w.setLastUpdated(LocalDateTime.now()))
                .doOnNext(widgetRepository::save)
                .log()
                .subscribe();
    }

    private void setWidgetToForeground(Widget widget) {
        widgetRepository
                .findFirstByOrderByZIndexDesc()
                .ifPresentOrElse(w -> widget.setZIndex(w.getZIndex() + 1),
                        () -> widget.setZIndex(0));
    }
}
