package com.yergun.widgetservice.model;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = "id")
public class Widget {

    private UUID id;

    @NotNull
    private Integer x;

    @NotNull
    private Integer y;

    private Integer zIndex;

    @Min(1)
    private Integer width;

    @Min(1)
    private Integer height;
    private LocalDateTime lastUpdated;

}
