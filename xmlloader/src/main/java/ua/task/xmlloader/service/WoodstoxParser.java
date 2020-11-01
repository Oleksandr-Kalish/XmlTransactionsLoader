package ua.task.xmlloader.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.springframework.stereotype.Component;
import ua.task.xmlloader.model.Client;
import ua.task.xmlloader.model.Transaction;
import ua.task.xmlloader.repository.ClientRepository;
import ua.task.xmlloader.repository.TransactionsRepository;
import ua.task.xmlloader.service.dto.ClientDto;
import ua.task.xmlloader.service.dto.TransactionDto;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

@RequiredArgsConstructor
@Component
@Slf4j
public class WoodstoxParser {
    private final TransactionsRepository transactionsRepository;
    private final ClientRepository clientRepository;

    public void parse(Path path) throws IOException, XMLStreamException {
        XMLInputFactory2 f = (XMLInputFactory2) XMLInputFactory2.newFactory();
        f.configureForSpeed();
        XMLStreamReader2 sr = null;
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            sr = (XMLStreamReader2) f.createXMLStreamReader(br);

            // fast forward to beginning 'transactions' tag (will throw if we don't find the tag at all)
            boolean finished2 = false;
            String transactions = "transactions";
            var transactionsList = new LinkedList<Transaction>();
            var clientList = new LinkedList<Client>();
            do {
                if (XMLStreamReader2.START_ELEMENT == sr.getEventType() && transactions.equalsIgnoreCase(sr.getLocalName())) {
                    finished2 = true;
                    break;
                }
            } while (sr.hasNext() && sr.next() >= 0);
            if (!finished2) {
                throw new IllegalStateException("xml document ended without callback returning true");
            }

            final StringBuilder sb = new StringBuilder(); // reuse a single string builder for all character aggregation

            // now keep processing unless we reach closing 'woodstoxTransactions' tag
            // now we're finally reached a 'transaction', can start processing it
            // processing the meat of a 'transaction' tag
            // split it into a function of its own to not clutter the main loop
            // we're done processing a 'transaction' only when we reach the ending 'transaction' tag
            // processing the meat of a 'transaction' tag
            // split it into a function of its own to not clutter the main loop
            // we're done processing a 'transaction' only when we reach the ending 'transaction' tag
            // we've reached the end of a 'transaction'
            boolean finished1 = false;
            do {
                if (((ConditionCallback) sr1 -> {
                    if (XMLStreamReader2.END_ELEMENT == sr1.getEventType() && transactions.equalsIgnoreCase(sr1.getLocalName()))
                        return true;

                    String transaction = "transaction";
                    if (XMLStreamReader2.START_ELEMENT == sr1.getEventType() && transaction.equalsIgnoreCase(sr1.getLocalName())) {
                        // now we're finally reached a 'transaction', can start processing it

                        int idIndex = sr1.getAttributeInfo().findAttributeIndex("", "place");

                        ParserTransaction p = new ParserTransaction();
                        sr1.next();
                        // processing the meat of a 'transaction' tag
                        // split it into a function of its own to not clutter the main loop
                        // we're done processing a 'transaction' only when we reach the ending 'transaction' tag
                        boolean finished = false;
                        do {
                            if (((ConditionCallback) sr2 -> {
                                // processing the meat of a 'transaction' tag
                                // split it into a function of its own to not clutter the main loop
                                if (XMLStreamReader2.END_ELEMENT == sr2.getEventType() && transaction.equalsIgnoreCase(sr2.getLocalName()))
                                    return true; // we're done processing a 'transaction' only when we reach the ending 'transaction' tag

                                if (XMLStreamReader2.START_ELEMENT == sr2.getEventType()) {
                                    boolean finished3 = false;
                                    final String tagName = sr2.getLocalName();
                                    sb.setLength(0); // clear our buffer, preparing to read the characters inside
                                    // ending condition
                                    // let the caller do whatever they need with text contents of the tag
                                    do {
                                        if (((ConditionCallback) sr11 -> {
                                            switch (sr11.getEventType()) {
                                                case XMLStreamReader2.END_ELEMENT: // ending condition
                                                    ((TagPairCallback) p).tagContents(tagName, sb); // let the caller do whatever they need with text contents of the tag
                                                    return true;
                                                case XMLStreamReader2.CHARACTERS:
                                                    sb.append(sr11.getText());
                                                    break;
                                            }
                                            return false;
                                        }).processXml(sr2)) {
                                            finished3 = true;
                                            break;
                                        }
                                    } while (sr2.hasNext() && sr2.next() >= 0);
                                    if (!finished3) {
                                        throw new IllegalStateException("xml document ended without callback returning true");
                                    }
                                }

                                return false;
                            }).processXml(sr1)) {
                                finished = true;
                                break;
                            }
                        } while (sr1.hasNext() && sr1.next() >= 0);
                        if (!finished) {
                            throw new IllegalStateException("xml document ended without callback returning true");
                        }
                        // we've reached the end of a 'transaction'
                        if (p.isComplete()) {

                            var t = new Transaction();
                            var c = new Client();
                            t.fromDto(p);
                            c.fromDto(p.getClientDto(), t.getId());
                            clientList.add(c);
                            transactionsList.add(t);

                        } else {
                            throw new IllegalStateException("Whoa, a transaction had incomplete data");
                        }
                    }

                    return false;
                }).processXml(sr)) {
                    finished1 = true;
                    break;
                }
            } while (sr.hasNext() && sr.next() >= 0);
            log.info("Parsed {} transactions and {} clients.", transactionsList.size(), clientList.size());
            int transactionCount = transactionsRepository.saveAll(transactionsList).size();
            log.info("Saved {} transactions.", transactionCount);
            int clientCount = clientRepository.saveAll(clientList).size();
            log.info("Saved {} clients.", clientCount);
            if (!finished1) {
                throw new IllegalStateException("xml document ended without callback returning true");
            }


        } finally {
            if (sr != null)
                sr.close();
        }

    }

    @FunctionalInterface
    interface ConditionCallback {
        boolean processXml(XMLStreamReader2 sr) throws XMLStreamException;
    }

    interface TagPairCallback {
        void tagStart(String tagName, XMLStreamReader2 sr) throws XMLStreamException;

        void tagContents(String tagName, StringBuilder sb);
    }

    @Getter
    public static class ParserTransaction extends TransactionDto implements TagPairCallback {
        public ParserTransaction() {

        }

        private ClientDto clientDto;

        @Override
        public void tagStart(String tagName, XMLStreamReader2 sr)   {
            if ("place".equals(tagName)) {
                this.setPlace(sr.getAttributeValue(sr.getAttributeIndex("", "place")));
            }
        }

        @Override
        public void tagContents(String tagName, StringBuilder sb) {
            switch (tagName) {
                case "place":
                    this.setPlace(sb.toString());
                    break;
                case "amount":
                    this.setAmount(Double.parseDouble(sb.toString()));
                    break;
                case "currency":
                    this.setCurrency(sb.toString());
                    break;
                case "card":
                    this.setCard(sb.toString());
                    break;
                case "client":
                    this.clientDto = new ClientDto();
                    this.clientDto.setFirstName(sb.toString().trim());
                    break;

                case "lastName":
                    this.clientDto.setLastName(sb.toString());
                    break;
                case "middleName":
                    this.clientDto.setMiddleName(sb.toString());
                    break;
                case "inn":
                    this.clientDto.setInn(sb.toString());
                    break;

            }
        }
    }
}
