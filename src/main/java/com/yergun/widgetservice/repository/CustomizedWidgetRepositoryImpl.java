package com.yergun.widgetservice.repository;

import com.yergun.widgetservice.exception.WidgetNotFoundException;
import com.yergun.widgetservice.model.Widget;
import com.yergun.widgetservice.model.WidgetPatchRequest;
import com.yergun.widgetservice.util.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class CustomizedWidgetRepositoryImpl implements CustomizedWidgetRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Widget update(UUID id, WidgetPatchRequest patchRequest) {

        Widget widget = entityManager.find(Widget.class, id);
        if(widget == null) {
            throw new WidgetNotFoundException(id);
        }

        this.moveIfZIndexCollision(patchRequest.getZ());
        BeanUtils.copyProperties(patchRequest, widget, ObjectUtils.getNullPropertyNames(patchRequest));
        widget.setLastUpdated(LocalDateTime.now());
        entityManager.persist(widget);
        return widget;
    }

    @Override
    public Widget save(Widget widget) {
        this.moveIfZIndexCollision(widget.getZ());
        entityManager.persist(widget);
        return widget;
    }

    private void moveIfZIndexCollision(Integer z) {
        entityManager
                .createQuery("Select w FROM Widget w WHERE w.z = :z", Widget.class)
                .setParameter("z", z)
                .getResultStream()
                .findFirst()
                .ifPresent( w -> incrementGreaterZs(w.getZ()));
    }

    private void incrementGreaterZs(Integer z) {
        List<Widget> widgets = entityManager
                .createQuery("Select w FROM Widget w WHERE w.z >= :z", Widget.class)
                .setParameter("z", z).getResultList();

        widgets.forEach(w -> {
                    w.incrementZ();
                    w.setLastUpdated(LocalDateTime.now());
                    entityManager.persist(w);
                });
    }
}
