package org.gouenji.financeapp.entity;

import java.time.LocalDate;

public interface Record {
    String getType();
    LocalDate getDate();
}
