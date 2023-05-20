DROP TABLE IF EXISTS orders;

CREATE TABLE orders (
                        id                  BIGSERIAL PRIMARY KEY NOT NULL,
                        property_id           BIGSERIAL NOT NULL,
                        property_title           varchar(255),
                        property_price          float8,
                        status              varchar(255) NOT NULL,
                        created_date        timestamp NOT NULL,
                        last_modified_date  timestamp NOT NULL,
                        version             integer NOT NULL
);