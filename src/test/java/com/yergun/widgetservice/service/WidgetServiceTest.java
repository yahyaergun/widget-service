package com.yergun.widgetservice.service;

import com.yergun.widgetservice.exception.WidgetNotFoundException;
import com.yergun.widgetservice.model.Widget;
import com.yergun.widgetservice.model.WidgetPatchRequest;
import com.yergun.widgetservice.repository.WidgetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WidgetServiceTest {

    @Mock
    private WidgetRepository widgetRepository;

    @InjectMocks
    private WidgetService widgetService;

    @Test
    void create_whenCalledWithNoID_thenCreatesTheId() {
        Widget widgetToBeCreated = Widget.builder()
                .height(10).width(10)
                .x(5).y(5)
                .build();
        when(widgetRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Widget createdWidget = widgetService.create(widgetToBeCreated);

        assertThat(createdWidget.getId()).isNotNull();
    }

    @Test
    void create_whenCalledWithID_thenCreatesANewId() {
        UUID paramId = UUID.randomUUID();
        Widget widgetToBeCreated = Widget.builder()
                .id(paramId)
                .height(10).width(10)
                .x(5).y(5)
                .build();
        when(widgetRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Widget createdWidget = widgetService.create(widgetToBeCreated);

        assertThat(createdWidget.getId()).isNotEqualByComparingTo(paramId);
    }


    @Test
    void create_whenCalled_thenCreatesLastUpdated() {
        Widget widgetToBeCreated = Widget.builder()
                .height(10).width(10)
                .x(5).y(5)
                .build();
        when(widgetRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Widget createdWidget = widgetService.create(widgetToBeCreated);

        assertThat(createdWidget.getLastUpdated()).isNotNull();
    }

    @Test
    void create_whenCalledWithoutZAndNoWidgetsInTheRepository_thenSetsZToZero() {
        Widget widgetToBeCreated = Widget.builder()
                .height(10).width(10)
                .x(5).y(5)
                .build();

        when(widgetRepository.findFirstByOrderByZDesc()).thenReturn(Optional.empty());
        when(widgetRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Widget createdWidget = widgetService.create(widgetToBeCreated);

        assertThat(createdWidget.getZ()).isZero();
    }

    @Test
    void create_whenCalledWithoutZAndThereAreWidgetsInTheRepository_thenSetsZToHighest() {
        Widget foregroundWidget = Widget.builder()
                .height(1).width(1).x(5).y(5).z(13)
                .build();

        Widget widgetToBeCreated = Widget.builder()
                .height(10).width(10).x(5).y(5)
                .build();

        when(widgetRepository.findFirstByOrderByZDesc()).thenReturn(Optional.of(foregroundWidget));
        when(widgetRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Widget createdWidget = widgetService.create(widgetToBeCreated);

        assertThat(createdWidget.getZ()).isGreaterThan(foregroundWidget.getZ());
    }

    @Test
    void findAll_whenCalled_ReturnsSomeWidgets() {
        Widget w1 = Widget.builder()
                .height(1).width(1).x(5).y(5).z(13)
                .build();
        Widget w2 = Widget.builder()
                .height(10).width(10).x(5).y(5).z(15)
                .build();
        when(widgetRepository.findByOrderByZAsc(any())).thenReturn(new PageImpl<>(List.of(w1, w2), Pageable.unpaged(), 2));

        Page<Widget> widgets = widgetService.findAll(0, 10);

        assertThat(widgets.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findById_whenFound_ReturnsWidget() {
        UUID id = UUID.randomUUID();
        Widget widget = Widget.builder().id(id).build();

        when(widgetRepository.findById(id)).thenReturn(Optional.of(widget));

        Widget received = widgetService.findById(id);
        assertThat(received.getId()).isEqualByComparingTo(id);
    }

    @Test
    void findById_whenNotFound_ThrowsWidgetNotFoundException() {
        when(widgetRepository.findById(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(WidgetNotFoundException.class)
                .isThrownBy(() -> widgetService.findById(UUID.randomUUID()));
    }
}