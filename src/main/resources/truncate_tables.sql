TRUNCATE ridership_data;
TRUNCATE tos;
TRUNCATE transit_mode;
delete from agency_mode;
ALTER TABLE agency_mode AUTO_INCREMENT = 1;
delete from agency;