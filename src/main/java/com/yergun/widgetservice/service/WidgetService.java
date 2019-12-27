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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class WidgetService {

    private final WidgetRepository widgetRepository;

    @Transactional(isolation= Isolation.READ_COMMITTED)
    public Widget create(Widget widget) {
        widget.setId(UUID.randomUUID());
        widget.setLastUpdated(LocalDateTime.now());

        if (widget.getZ() == null) {
            setWidgetToForeground(widget);
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

    @Transactional(isolation= Isolation.READ_COMMITTED)
    public Widget update(UUID id, WidgetPatchRequest widgetPatchRequest) {
        return widgetRepository.update(id, widgetPatchRequest);
    }

    private void setWidgetToForeground(Widget widget) {
        widgetRepository
                .findFirstByOrderByZDesc()
                .ifPresentOrElse(w -> widget.setZ(w.getZ() + 1),
                        () -> widget.setZ(0));
    }
}
