package ua.task.xmlloader.repository;

import ua.task.xmlloader.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("transactionsRepository")
public interface TransactionsRepository extends JpaRepository<Transaction, UUID> {
}
