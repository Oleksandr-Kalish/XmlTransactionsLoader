package ua.task.xmlloader.model;

import ua.task.xmlloader.service.dto.ClientDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "client")
public class Client {
    @Id
    private UUID id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String inn;
    private UUID transactionId;

    public void fromDto(ClientDto clientDto, UUID transactionId) {
        this.id = UUID.randomUUID();
        this.firstName = clientDto.getFirstName();
        this.lastName = clientDto.getLastName();
        this.middleName = clientDto.getMiddleName();
        this.inn = clientDto.getInn();
        this.transactionId = transactionId;
    }
}
