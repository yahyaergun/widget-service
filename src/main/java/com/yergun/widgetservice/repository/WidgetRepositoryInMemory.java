package com.yergun.widgetservice.repository;

import com.yergun.widgetservice.model.Widget;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

//@ConditionalOnProperty(name = "application.repository.type", havingValue = "memory")
@Repository
public class WidgetRepositoryInMemory implements WidgetRepository {

    SortedSet<Widget> storage = new ConcurrentSkipListSet<>(Comparator.comparingInt(Widget::getZIndex));

    @Override
    public Widget save(Widget widget) {
        storage.add(widget);
        return widget;
    }

    @Override
    public Optional<Widget> findFirstByOrderByZIndexDesc() {
        return storage.isEmpty() ? Optional.empty() : Optional.of(storage.last());
    }

    @Override
    public Flux<Widget> findAllByOrderByZIndexAsc() {
        return Flux.fromIterable(storage);
    }

    @Override
    public Flux<Widget> findAllByZIndexGreaterThanEqualOrderByZIndexDesc(Widget widget) {
        return Flux.fromIterable(storage.tailSet(widget));
    }

    public Optional<Widget> findByZIndex(Integer zIndex) {
        return storage.stream()
                .filter( w -> w.getZIndex().equals(zIndex))
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
    public boolean delete(Widget widget) {
        return storage.remove(widget);
    }



}
