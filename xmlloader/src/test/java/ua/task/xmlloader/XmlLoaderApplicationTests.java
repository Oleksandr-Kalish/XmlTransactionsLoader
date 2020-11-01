package ua.task.xmlloader;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ua.task.xmlloader.repository.ClientRepository;
import ua.task.xmlloader.repository.TransactionsRepository;

@SpringBootTest
@ActiveProfiles("test")
public class XmlLoaderApplicationTests {
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Value("${application.xml-path}")
    String path;

    @Test
    public void checkParser() {
        //parsing start at application load
        Assertions.assertThat(transactionsRepository.findAll().size()).isEqualTo(12);
        Assertions.assertThat(clientRepository.findAll().size()).isEqualTo(12);

    }

}
