-- Sample station data for local development
INSERT INTO stations (station_id, name, location, total_slots, price_per_hour, deposit_amount, currency, release_api_url, release_api_key)
VALUES 
('ST001', 'Mumbai Central Mall', 'Ground Floor, Gate 3, Mumbai Central Mall, Mumbai', 6, 10.00, 50.00, 'INR', NULL, NULL),
('ST002', 'Andheri Metro Station', 'Exit Gate 2, Andheri Metro, Mumbai', 4, 8.00, 40.00, 'INR', NULL, NULL),
('ST003', 'Phoenix Mall Kurla', 'Food Court Level, Phoenix Mall, Kurla', 8, 12.00, 60.00, 'INR', NULL, NULL);

-- Sample slots for ST001
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST001', 1, 'AVAILABLE');
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST001', 2, 'AVAILABLE');
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST001', 3, 'AVAILABLE');
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST001', 4, 'OCCUPIED');
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST001', 5, 'AVAILABLE');
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST001', 6, 'MAINTENANCE');

-- Sample slots for ST002
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST002', 1, 'AVAILABLE');
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST002', 2, 'AVAILABLE');
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST002', 3, 'AVAILABLE');
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST002', 4, 'AVAILABLE');

-- Sample slots for ST003
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST003', 1, 'AVAILABLE');
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST003', 2, 'AVAILABLE');
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST003', 3, 'AVAILABLE');
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST003', 4, 'AVAILABLE');
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST003', 5, 'OCCUPIED');
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST003', 6, 'AVAILABLE');
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST003', 7, 'AVAILABLE');
INSERT INTO slots (station_id, slot_number, status) VALUES ('ST003', 8, 'AVAILABLE');
