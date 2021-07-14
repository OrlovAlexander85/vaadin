package ru.globaltruck.vaadin.selta;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Route {
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private String addressfrom;
    private String timing;
    private String addressto;
}
