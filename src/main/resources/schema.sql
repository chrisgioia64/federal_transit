create table if not exists agency (
    id bigint NOT NULL PRIMARY KEY auto_increment,
    ntd_id bigint UNIQUE,
    agency_name VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    urbanized_area bigint,
    urbanized_population bigint,
    service_population bigint
);

create table if not exists agency_mode (
    id bigint NOT NULL PRIMARY KEY auto_increment,
	ntd_id bigint NOT NULL,
    FOREIGN KEY (ntd_id) REFERENCES agency(ntd_id),
    mode VARCHAR(255),
    type_of_service VARCHAR(255),
    report_year bigint,
    number_of_months bigint,
    passenger_miles bigint,
    upt bigint,
    fares bigint,
    operating_expenses bigint
);

ALTER TABLE agency_mode
ADD CONSTRAINT agency_mode_key UNIQUE (ntd_id, mode, type_of_service);

CREATE table if not exists transit_mode (
	id bigint NOT NULL PRIMARY KEY auto_increment,
	code VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL
);

CREATE table if not exists tos (
	id bigint NOT NULL PRIMARY KEY auto_increment,
	code VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL
);

CREATE table if not exists ridership_data (
	id bigint NOT NULL PRIMARY KEY auto_increment,
    agency_mode_id bigint NOT NULL,
    FOREIGN KEY (agency_mode_id) REFERENCES agency_mode(id),
    type VARCHAR(255) NOT NULL,
    year bigint NOT NULL,
    month bigint NOT NULL,
    data bigint NOT NULL
);

