package com.yergun.widgetservice.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WidgetPatchRequest {
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private Integer z;
}
