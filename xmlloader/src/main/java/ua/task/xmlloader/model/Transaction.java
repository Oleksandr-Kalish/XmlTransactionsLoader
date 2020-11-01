package ua.task.xmlloader.model;

import lombok.EqualsAndHashCode;
import ua.task.xmlloader.service.dto.TransactionDto;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class Transaction {
    @Id
    @EqualsAndHashCode.Exclude
    private UUID id;
    private String place;
    private double amount;
    private String currency;
    private String card;

    public void fromDto(TransactionDto transactionDto) {
        this.setId(UUID.randomUUID());
        this.place = transactionDto.getPlace();
        this.amount = transactionDto.getAmount();
        this.currency = transactionDto.getCurrency();
        this.card = transactionDto.getCard();
    }
}
