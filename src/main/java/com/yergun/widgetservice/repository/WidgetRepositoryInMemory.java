package com.yergun.widgetservice.repository;

import com.yergun.widgetservice.exception.WidgetNotFoundException;
import com.yergun.widgetservice.model.Widget;
import com.yergun.widgetservice.model.WidgetPatchRequest;
import com.yergun.widgetservice.util.ObjectUtils;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@ConditionalOnProperty(name = "application.repository.type", havingValue = "memory")
@Repository
@Getter
public class WidgetRepositoryInMemory implements WidgetRepository {

    private final SortedSet<Widget> storage = new TreeSet<>(Comparator.comparingInt(Widget::getZ));
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    @Override
    public Widget save(Widget widget) {
        lock.writeLock().lock();
        try {
            moveIfZIndexCollision(widget);
            storage.add(widget);
            return widget;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Widget> findFirstByOrderByZDesc() {
        lock.readLock().lock();
        try {
            return storage.isEmpty() ? Optional.empty() : Optional.of(storage.last());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Page<Widget> findByOrderByZAsc(Pageable pageable) {
        lock.readLock().lock();
        try {
            List<Widget> list = storage.stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .collect(Collectors.toList());
            return new PageImpl<>(list, pageable, storage.size());
        } finally {
            lock.readLock().unlock();
        }

    }

    @Override
    public Collection<Widget> findByZGreaterThanEqualOrderByZAsc(Widget widget) {
        lock.readLock().lock();
        try {
            return storage.tailSet(widget);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Optional<Widget> findFirstByZ(Integer zIndex) {
        lock.readLock().lock();
        try {
            return storage.stream()
                    .filter(w -> w.getZ().equals(zIndex))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Optional<Widget> findById(UUID id) {
        lock.readLock().lock();
        try {
            return storage.stream()
                    .filter(w -> w.getId().equals(id))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean deleteById(UUID id) {
        lock.writeLock().lock();
        try {
            return storage.removeIf(w -> w.getId().equals(id));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(Widget widget) {
        lock.writeLock().lock();
        try {
            storage.remove(widget);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Widget update(UUID id, WidgetPatchRequest patchRequest) {
        lock.writeLock().lock();
        try {
            Widget widget = storage.stream()
                    .filter(w -> w.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new WidgetNotFoundException(id));
            storage.remove(widget);
            BeanUtils.copyProperties(patchRequest, widget, ObjectUtils.getNullPropertyNames(patchRequest));
            widget.setLastUpdated(LocalDateTime.now());
            moveIfZIndexCollision(widget);
            storage.add(widget);
            return widget;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * used only under writeLock
     * @param widget
     */
    private void moveIfZIndexCollision(Widget widget) {
        storage.stream()
                .filter(w -> w.getZ().equals(widget.getZ()))
                .findFirst()
                .ifPresent(this::moveWidgetsGreaterThanToForegroundByOne);
    }

    /**
     * used only under writeLock
     * @param widget
     */
    private void moveWidgetsGreaterThanToForegroundByOne(Widget widget) {
        this.findByZGreaterThanEqualOrderByZAsc(widget)
                .forEach(w -> {
                    w.incrementZ();
                    w.setLastUpdated(LocalDateTime.now());
                    storage.add(w);
                });
    }
}
