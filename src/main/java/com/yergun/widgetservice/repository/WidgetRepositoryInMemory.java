package com.yergun.widgetservice.repository;

import com.yergun.widgetservice.model.Widget;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@ConditionalOnProperty(name = "application.repository.type", havingValue = "memory")
@Repository
public class WidgetRepositoryInMemory implements WidgetRepository {

    SortedSet<Widget> storage = new ConcurrentSkipListSet<>(Comparator.comparingInt(Widget::getZ));

    @Override
    public Widget save(Widget widget) {
        storage.add(widget);
        return widget;
    }

    @Override
    public Optional<Widget> findFirstByOrderByZDesc() {
        return storage.isEmpty() ? Optional.empty() : Optional.of(storage.last());
    }

    @Override
    public Page<Widget> findByOrderByZAsc(Pageable pageable) {
        List<Widget> list = storage.stream()
                .limit(pageable.getPageSize())
                .skip(pageable.getOffset())
                .collect(Collectors.toList());

        return new PageImpl<>(list, pageable, storage.size());
    }

    @Override
    public Collection<Widget> findByZGreaterThanEqualOrderByZAsc(Widget widget) {
        return storage.tailSet(widget);
    }

    public Optional<Widget> findFirstByZ(Integer zIndex) {
        return storage.stream()
                .filter(w -> w.getZ().equals(zIndex))
                .findFirst();
    }

    @Override
    public Optional<Widget> findById(UUID id) {
        return storage.stream()
                .filter(w -> w.getId().equals(id))
                .findFirst();
    }

    @Override
    public boolean deleteById(UUID id) {
        return storage.removeIf(w -> w.getId().equals(id));
    }

    @Override
    public void delete(Widget widget) {
        storage.remove(widget);
    }



}
