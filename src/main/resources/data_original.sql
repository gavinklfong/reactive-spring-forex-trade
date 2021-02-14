DROP TABLE IF EXISTS currency_rates;

CREATE TABLE rates (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	date DATE NOT NULL,
	base_currency VARCHAR(3) NOT NULL,
	currency VARCHAR(3) NOT NULL,
	rate DOUBLE NOT NULL
);

CREATE TABLE traders (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	ranking INT
)

CREATE TABLE orders (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	timestamp TIMESTAMP NOT NULL,
	rate DOUBLE NOT NULL,
	source_currency VARCHAR(3) NOT NULL,
	target_currency VARCHAR(3) NOT NULL,
	source_currency_amount DECIMAL(14,3) NOT NULL, 
	trade_id BIGINT NOT NULL
)


INSERT INTO rates (date, base_currency, currency, rate) VALUES ('2021-02-08', 'GBP', 'USD', 1.3690754045);
INSERT INTO rates (date, base_currency, currency, rate) VALUES ('2021-02-08', 'GBP', 'EUR', 1.1385242449);

INSERT INTO traders (name) VALUES ('killer')

