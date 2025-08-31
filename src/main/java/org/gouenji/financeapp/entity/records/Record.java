package org.gouenji.financeapp.entity.records;

import java.time.LocalDate;

public interface Record {
    String getType();
    LocalDate getDate();
}
