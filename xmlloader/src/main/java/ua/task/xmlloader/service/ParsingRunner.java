package ua.task.xmlloader.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Path;

@Component
@Slf4j
@RequiredArgsConstructor
public class ParsingRunner {
    private final WoodstoxParser woodstoxParser;
    @Value("${application.xml-path}")
    private String path;

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        log.info("Starting parsing xml at {}", path);
        try {
            woodstoxParser.parse(Path.of(path));
        } catch (IOException e) {
            log.error("Cannot find the file from path {}", path, e);
        } catch (XMLStreamException e) {
            log.error("Cannot parse xml {}", path, e);
        }

    }

}
