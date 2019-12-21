package com.yergun.widgetservice.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = "id")
@Entity
public class Widget {

    @Id
    private UUID id;

    @NotNull
    private Integer x;

    @NotNull
    private Integer y;

    private Integer z;

    @Min(1)
    private Integer width;

    @Min(1)
    private Integer height;
    private LocalDateTime lastUpdated;


    public void incrementZ() {
        z++;
    }

}
