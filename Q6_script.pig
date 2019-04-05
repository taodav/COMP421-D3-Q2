airports = LOAD '/data/airports.csv' USING PigStorage(',') AS (airport_id:INT, airport_code:CHARARRAY, city_name:CHARARRAY, state:CHARARRAY);
airplanes = LOAD '/data/airplanes.csv' USING PigStorage(',') AS (carrier_code:CHARARRAY, carrier_name:CHARARRAY, tail_number:CHARARRAY);
flights = LOAD '/data/flights.csv' USING PigStorage(',') AS (day:INT, flight_number:CHARARRAY, tail_number:CHARARRAY, origin_airport_id:INT, dest_airport_id:INT, delay:INT, distance:INT);

airports_id_code = FOREACH airports GENERATE airport_id, airport_code;
flights_reduced = FOREACH flights GENERATE tail_number, origin_airport_id AS airport_id;

group_airport_id = GROUP flights_reduced BY airport_id;
flights_count = FOREACH group_airport_id GENERATE ($0) AS airport_id, COUNT($1) AS numflights;
flights_count_with_code = JOIN flights_count BY airport_id, airports_id_code BY airport_id;
flights_count_final = FOREACH flights_count_with_code GENERATE flights_count::airport_id AS airport_id, flights_count::numflights AS numflights, airports_id_code::airport_code AS airport_code;

flights_airplanes = JOIN flights_reduced BY tail_number, airplanes BY tail_number;
distinct_carriers = DISTINCT(FOREACH flights_airplanes GENERATE airport_id AS airport_id, carrier_code);
group_carriers_by_id = GROUP distinct_carriers BY airport_id;
carriers_count = FOREACH group_carriers_by_id GENERATE ($0) AS airport_id, COUNT($1) AS numcarriers;

combined = JOIN carriers_count BY airport_id, flights_count_final BY airport_id;

final_table = FOREACH combined GENERATE flights_count_final::airport_code AS airport_code, carriers_count::numcarriers AS numcarriers, flights_count_final::numflights AS numflights;

sorted_final_table = ORDER final_table BY airport_code ASC;

--DUMP sorted_final_table;
STORE sorted_final_table INTO '~/q6/' USING PigStorage(',');