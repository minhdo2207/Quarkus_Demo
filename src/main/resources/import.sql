-- Create the database
CREATE DATABASE sample_database;

-- Connect to the new database
USE sample_database;

-- Create the persons table
CREATE TABLE persons (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(50),
                         age INT,
                         email VARCHAR(50)
);