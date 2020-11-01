package ua.task.xmlloader.repository;

import ua.task.xmlloader.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("clientRepository")
public interface ClientRepository extends JpaRepository<Client, UUID> {
}
