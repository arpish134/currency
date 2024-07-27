CREATE TABLE fx_rate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    source_currency VARCHAR(3) NOT NULL,
    target_currency VARCHAR(3) NOT NULL,
    exchange_rate DECIMAL(10, 4) NOT NULL,
    UNIQUE KEY (date, source_currency, target_currency)
);