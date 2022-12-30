# UPT Query
SELECT * FROM (SELECT COUNT(*), agency.urbanized_population, agency.metro, SUM(agency_mode.upt),
ROW_NUMBER() OVER (ORDER BY SUM(agency_mode.upt) DESC) upt_rank
FROM agency INNER JOIN agency_mode
WHERE agency_mode.ntd_id = agency.ntd_id
GROUP BY agency.metro ORDER BY agency.urbanized_population DESC) AS sub
WHERE sub.metro = 'Miami, FL';

# Population and Rank
SELECT * FROM (SELECT COUNT(*), agency.urbanized_population, agency.metro, SUM(agency_mode.upt),
ROW_NUMBER() OVER (ORDER BY agency.urbanized_population DESC) upt_rank
FROM agency INNER JOIN agency_mode
WHERE agency_mode.ntd_id = agency.ntd_id
GROUP BY agency.metro ORDER BY agency.urbanized_population DESC) AS sub
WHERE sub.metro = 'Miami, FL';

# UPT Total and Per Capita with Rank
SELECT metro, urbanized_population, upt, rate, upt_rank,
ROW_NUMBER() OVER (ORDER BY rate DESC) upt_rate_rank
 FROM (SELECT COUNT(*), agency.urbanized_population, agency.metro, SUM(agency_mode.upt) AS upt,
SUM(agency_mode.upt) / agency.urbanized_population AS rate,
ROW_NUMBER() OVER (ORDER BY SUM(agency_mode.upt) DESC) upt_rank
FROM agency INNER JOIN agency_mode
WHERE agency_mode.ntd_id = agency.ntd_id
GROUP BY agency.metro ORDER BY agency.urbanized_population DESC) AS sub
WHERE urbanized_population >= 500000
ORDER BY rate DESC;

# Passenger Miles Total and Per Capita with Rank
SELECT metro, urbanized_population, pm, rate, pm_rank,
ROW_NUMBER() OVER (ORDER BY rate DESC) rate_rank
 FROM (SELECT COUNT(*), agency.urbanized_population, agency.metro, SUM(agency_mode.passenger_miles) AS pm,
SUM(agency_mode.passenger_miles) / agency.urbanized_population AS rate,
ROW_NUMBER() OVER (ORDER BY SUM(agency_mode.passenger_miles) DESC) pm_rank
FROM agency INNER JOIN agency_mode
WHERE agency_mode.ntd_id = agency.ntd_id
GROUP BY agency.metro ORDER BY agency.urbanized_population DESC) AS sub
WHERE urbanized_population >= 500000
ORDER BY rate DESC;

# Passenger Miles on Heavy Rail
SELECT metro, count, urbanized_population, pm, rate, pm_rank,
ROW_NUMBER() OVER (ORDER BY rate DESC) rate_rank
 FROM (SELECT COUNT(*) AS count, agency.urbanized_population, agency.metro, SUM(agency_mode.passenger_miles) AS pm,
SUM(agency_mode.passenger_miles) / agency.urbanized_population AS rate,
ROW_NUMBER() OVER (ORDER BY SUM(agency_mode.passenger_miles) DESC) pm_rank
FROM agency INNER JOIN agency_mode
WHERE agency_mode.ntd_id = agency.ntd_id AND agency_mode.mode = 'HR'
GROUP BY agency.metro ORDER BY agency.urbanized_population DESC) AS sub
WHERE urbanized_population >= 500000
ORDER BY rate DESC;

# UPT (using generic placeholder name)
SELECT metro, count, urbanized_population, total, rate, total_rank,
ROW_NUMBER() OVER (ORDER BY rate DESC) rate_rank
 FROM (SELECT COUNT(*) AS count, agency.urbanized_population, agency.metro, SUM(agency_mode.upt) AS total,
SUM(agency_mode.upt) / agency.urbanized_population AS rate,
ROW_NUMBER() OVER (ORDER BY SUM(agency_mode.upt) DESC) total_rank
FROM agency INNER JOIN agency_mode
WHERE agency_mode.ntd_id = agency.ntd_id
GROUP BY agency.metro ORDER BY agency.urbanized_population DESC) AS sub
WHERE urbanized_population >= 500000
ORDER BY rate DESC;