package com.yergun.widgetservice.repository;

import com.yergun.widgetservice.exception.WidgetNotFoundException;
import com.yergun.widgetservice.model.Widget;
import com.yergun.widgetservice.model.WidgetPatchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.yergun.widgetservice.TestUtils.fillWidgets;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class WidgetRepositoryInMemoryTest {

    WidgetRepositoryInMemory repository = new WidgetRepositoryInMemory();

    @BeforeEach
    void init() {
        repository.getStorage().clear();
    }

    @Test
    void save() {
        UUID id = UUID.randomUUID();
        Widget widget = Widget.builder()
                .id(id).width(10).height(10)
                .x(1).y(1).z(1).lastUpdated(LocalDateTime.now())
                .build();

        repository.save(widget);
        Widget saved = repository.getStorage().iterator().next();

        assertThat(saved).isEqualToComparingFieldByField(widget);
    }

    @Test
    void findFirstByOrderByZDesc_whenStorageEmpty_thenReturnsEmptyOptional() {
        assertThat(repository.findFirstByOrderByZDesc()).isEqualTo(Optional.empty());
    }

    @Test
    void findFirstByOrderByZDesc_whenStorageEmpty_thenReturnsTheHighestZWidget() {

        Widget w1 = Widget.builder()
                .id(UUID.randomUUID()).width(10).height(10)
                .x(1).y(1).z(1).lastUpdated(LocalDateTime.now())
                .build();

        Widget w2 = Widget.builder()
                .id(UUID.randomUUID()).width(10).height(10)
                .x(1).y(1).z(11).lastUpdated(LocalDateTime.now())
                .build();
        repository.save(w1);
        repository.save(w2);

        assertThat(repository.findFirstByOrderByZDesc()).isEqualTo(Optional.of(w2));
    }

    @Test
    void findByOrderByZAsc_whenCalledWithDefaultHaving10Widgets_thenReturnsWidgets() {
        fillWidgets(10, repository);
        Page<Widget> widgets = repository.findByOrderByZAsc(PageRequest.of(0, 10));

        assertThat(widgets.getTotalElements()).isEqualTo(10);
        assertThat(widgets.getContent().get(0).getZ()).isEqualTo(0); //lowest z first
    }

    @Test
    void findByOrderByZAsc_whenCalledWithDefaultHaving20Widgets_thenReturns10WidgetsWith2Page() {
        fillWidgets(20, repository);
        Page<Widget> widgets = repository.findByOrderByZAsc(PageRequest.of(0, 10));

        assertThat(widgets.getTotalElements()).isEqualTo(20);
        assertThat(widgets.getContent().size()).isEqualTo(10);
        assertThat(widgets.getTotalPages()).isEqualTo(2);
    }

    @Test
    void findByZGreaterThanEqualOrderByZAsc_whenCalled_ReturnsWidgetsWithHigherZ() {
        Widget w = Widget.builder()
                .z(5)
                .build();

        fillWidgets(10, repository); // z indexes from 0->9, has 5 higher Z than 5

        assertThat(repository.findByZGreaterThanEqualOrderByZAsc(w).size()).isEqualTo(5);
    }

    @Test
    void findFirstByZ_whenCalled_ReturnsWidgetWithZ() {
        fillWidgets(3, repository);
        int zIndex = 2;

        assertThat(repository.findFirstByZ(zIndex).get().getZ()).isEqualTo(zIndex);
    }

    @Test
    void findById_whenCalledById_ReturnsWidget() {
        UUID id = UUID.randomUUID();
        Widget w = new Widget(id, 10, 10, 10,
                10, 10, LocalDateTime.now());

        repository.getStorage().add(w);

        assertThat(repository.findById(id)).isEqualTo(Optional.of(w));
    }

    @Test
    void findById_whenNotFound_ReturnsEmptyOptional() {
        assertThat(repository.findById(UUID.randomUUID())).isEqualTo(Optional.empty());
    }

    @Test
    void deleteById_whenFound_thenDeletesWidgetAndReturnsTrue() {
        UUID id = UUID.randomUUID();
        Widget w = new Widget(id, 10, 10, 10,
                10, 10, LocalDateTime.now());

        repository.getStorage().add(w);

        assertThat(repository.deleteById(id)).isEqualTo(true);
        assertThat(repository.getStorage().size()).isEqualTo(0);
    }

    @Test
    void deleteById_whenNotFound_returnsFalse() {
        UUID id = UUID.randomUUID();
        Widget w = new Widget(id, 10, 10, 10,
                10, 10, LocalDateTime.now());

        repository.getStorage().add(w);

        assertThat(repository.deleteById(UUID.randomUUID())).isEqualTo(false);
        assertThat(repository.getStorage().size()).isEqualTo(1);
    }

    @Test
    void delete_whenFound_thenDeletesWidget() {
        UUID id = UUID.randomUUID();
        Widget w = new Widget(id, 10, 10, 10,
                10, 10, LocalDateTime.now());

        repository.getStorage().add(w);

        repository.delete(w);
        assertThat(repository.getStorage().size()).isEqualTo(0);
    }

    @Test
    void delete_whenNotFound_thenReturnsFalse() {
        UUID id = UUID.randomUUID();
        Widget w = new Widget(id, 10, 10, 10,
                10, 10, LocalDateTime.now());

        repository.getStorage().add(w);

        repository.delete(w);
        assertThat(repository.getStorage().size()).isEqualTo(0);
    }

    @Test
    void update_whenNotFound_ThrowsWidgetNotFoundException() {
        assertThatExceptionOfType(WidgetNotFoundException.class)
                .isThrownBy(() -> repository.update(UUID.randomUUID(), new WidgetPatchRequest()));
    }

    @Test
    void update_whenFoundAndZCollides_thenUpdatesWidgetAndMoveTheRest() {
        repository.getStorage().add(new Widget(UUID.randomUUID(), 1, 1, 1, 1, 0, LocalDateTime.now()));
        repository.getStorage().add(new Widget(UUID.randomUUID(), 1, 1, 1, 1, 1, LocalDateTime.now()));
        repository.getStorage().add(new Widget(UUID.randomUUID(), 1, 1, 1, 1, 2, LocalDateTime.now()));
        repository.getStorage().add(new Widget(UUID.randomUUID(), 1, 1, 1, 1, 3, LocalDateTime.now()));

        UUID id = UUID.randomUUID();
        Widget w = new Widget(id, 10, 10, 10,
                10, 4, LocalDateTime.now());
        repository.getStorage().add(w);

        repository.getStorage().add(new Widget(UUID.randomUUID(), 1, 1, 1, 1, 5, LocalDateTime.now()));


        WidgetPatchRequest wpr = new WidgetPatchRequest();
        wpr.setZ(1);
        Widget updatedWidget = repository.update(id, wpr);

        assertThat(updatedWidget.getZ()).isEqualTo(1);
        assertThat(repository.getStorage().last().getZ()).isEqualTo(6);
    }

    @Test
    void update_whenFound_thenUpdatesWidgetWithNonNulls() {
        UUID id = UUID.randomUUID();
        Widget w = new Widget(id, 10, 10, 10,
                10, 10, LocalDateTime.now());

        repository.getStorage().add(w);

        WidgetPatchRequest wpr = new WidgetPatchRequest();
        wpr.setX(111);
        Widget updatedWidget = repository.update(id, wpr);

        assertThat(updatedWidget.getX()).isEqualTo(111);
        assertThat(updatedWidget.getY()).isEqualTo(w.getY());
        assertThat(updatedWidget.getWidth()).isEqualTo(w.getWidth());
        assertThat(updatedWidget.getHeight()).isEqualTo(w.getHeight());
        assertThat(updatedWidget.getZ()).isEqualTo(w.getZ());
        assertThat(updatedWidget.getLastUpdated()).isAfterOrEqualTo(w.getLastUpdated());
    }


}