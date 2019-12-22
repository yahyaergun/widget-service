package com.yergun.widgetservice.repository;

import com.yergun.widgetservice.model.Widget;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "application.repository.type", havingValue = "h2")
public interface WidgetRepository extends Repository<Widget, UUID> {

    // I really wanted the method signature like this
    // so I can utilize `tailSet` method in inMemory implementation as it's amortised O(1)
    @Query("select w from Widget w where w.z >= :#{#widget.z} order by w.z asc")
    Collection<Widget> findByZGreaterThanEqualOrderByZAsc(Widget widget);
    Page<Widget> findByOrderByZAsc(Pageable pageable);
    Widget save(Widget widget);
    Optional<Widget> findFirstByOrderByZDesc();
    Optional<Widget> findFirstByZ(Integer z);
    Optional<Widget> findById(UUID id);
    boolean deleteById(UUID id);
    void delete(Widget widget);
}
