package ru.fateev.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TodoDto {
    private String title;
    private String description;
    private LocalDate deadline;
}