package com.yergun.widgetservice.repository;

import com.yergun.widgetservice.model.Widget;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface WidgetRepository extends Repository<Widget, UUID> {

    Widget save(Widget widget);
    Flux<Widget> findAllByOrderByZIndexAsc();
    Flux<Widget> findAllByZIndexGreaterThanEqualOrderByZIndexDesc(Widget widget);
    Optional<Widget> findFirstByOrderByZIndexDesc();
    Optional<Widget> findByZIndex(Integer zIndex);
    Optional<Widget> findById(UUID id);
    boolean deleteById(UUID id);
    boolean delete(Widget widget);
}
