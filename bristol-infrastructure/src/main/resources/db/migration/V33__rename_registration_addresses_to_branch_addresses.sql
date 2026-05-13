-- V33: Drop distributor_registration_addresses
-- Registration no longer collects shipping addresses; address data lives directly on distributor_branches.

DROP TABLE IF EXISTS distributor_registration_addresses;
