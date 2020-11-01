package ua.task.xmlloader;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ua.task.xmlloader.model.Client;
import ua.task.xmlloader.model.Transaction;
import ua.task.xmlloader.repository.ClientRepository;
import ua.task.xmlloader.repository.TransactionsRepository;
import ua.task.xmlloader.service.dto.ClientDto;
import ua.task.xmlloader.service.dto.TransactionDto;

@SpringBootTest
@ActiveProfiles("testSingle")
public class LoadSingleTests {
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Value("${application.xml-path}")
    String path;

    @Test
    public void checkParser() {
        //parsing start at application load
        var transaction = transactionsRepository.findAll().get(0);
        var client = clientRepository.findAll().get(0);
        TransactionDto t = new TransactionDto();
        t.setAmount(0.01);
        t.setCard("111111****1111");
        t.setCurrency("UAH");
        t.setPlace("p");
        Transaction expectedT = new Transaction();
        expectedT.fromDto(t);
        Assertions.assertThat(transaction).isEqualTo(expectedT);

        ClientDto c = new ClientDto();
        c.setFirstName("fn");
        c.setInn("1234567890");
        c.setLastName("ln");
        c.setMiddleName("mn");
        Client expectedC = new Client();
        expectedC.fromDto(c, expectedT.getId());
        Assertions.assertThat(client).isEqualTo(expectedC);

    }

}
