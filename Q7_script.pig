airports = LOAD '/data/airports.csv' USING PigStorage(',') AS (airport_id:INT, airport_code:CHARARRAY, city_name:CHARARRAY, state:CHARARRAY);
airplanes = LOAD '/data/airplanes.csv' USING PigStorage(',') AS (carrier_code:CHARARRAY, carrier_name:CHARARRAY, tail_number:CHARARRAY);
flights = LOAD '/data/flights.csv' USING PigStorage(',') AS (day:INT, flight_number:CHARARRAY, tail_number:CHARARRAY, origin_airport_id:INT, dest_airport_id:INT, delay:INT, distance:INT);

--LAX = FILTER airports BY airport_code == 'LAX';
--JFK = FILTER airports BY airport_code == 'JFK';
--
--delta_planes = FILTER airplanes BY carrier_code == 'DL';

airports_flights_origin = JOIN airports BY airport_id, flights BY origin_airport_id;
airports_flights_origin_reduced =
    FOREACH airports_flights_origin
    GENERATE airports::airport_code AS origin_airport_code,
        flights::origin_airport_id AS origin_airport_id,
        flights::dest_airport_id AS dest_airport_id,
        flights::tail_number AS tail_number,
        flights::distance AS distance;

airports_origin_dest = JOIN airports_flights_origin_reduced BY dest_airport_id, airports BY airport_id;