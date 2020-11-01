package ua.task.xmlloader.service.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class TransactionDto {
    private String place;
    private double amount;
    private String currency;
    private String card;

    public boolean isComplete() {
        return Objects.nonNull(this.place) &&
                Objects.nonNull(this.currency) && Objects.nonNull(this.card);
    }
}
