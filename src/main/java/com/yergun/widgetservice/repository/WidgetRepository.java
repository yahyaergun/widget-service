package com.yergun.widgetservice.repository;

import com.yergun.widgetservice.model.Widget;
import com.yergun.widgetservice.model.WidgetPatchRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "application.repository.type", havingValue = "db")
public interface WidgetRepository extends Repository<Widget, UUID>, CustomizedWidgetRepository {

    // I really wanted the method signature like this
    // so I can utilize `tailSet` method in inMemory implementation as it's amortised O(1)
    @Query("select w from Widget w where w.z >= :#{#widget.z} order by w.z asc")
    Collection<Widget> findByZGreaterThanEqualOrderByZAsc(Widget widget);
    Page<Widget> findByOrderByZAsc(Pageable pageable);
    Optional<Widget> findFirstByOrderByZDesc();
    Optional<Widget> findFirstByZ(Integer z);
    Optional<Widget> findById(UUID id);
    void deleteById(UUID id);
    void delete(Widget widget);
}
