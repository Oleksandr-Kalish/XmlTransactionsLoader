CREATE TABLE IF NOT EXISTS transaction
(
    id       uuid,
    place    varchar(50),
    amount   float,
    currency varchar(3),
    card     varchar(14),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS client
(
    id             uuid,
    first_name      varchar(50),
    last_name       varchar(50),
    middle_name     varchar(50),
    inn            varchar(10),
    transaction_id uuid,
    PRIMARY KEY (id),
    foreign key (transaction_id) references transaction (id)
);
