CREATE DATABASE IF NOT EXISTS cat_adoption_center;
USE cat_adoption_center;


CREATE TABLE cat (
    id INT PRIMARY KEY,
    age INT,
    breed VARCHAR(255),
    gender VARCHAR(50),
    is_adopted BOOLEAN,
    name VARCHAR(255)
);


INSERT INTO cat (id, age, breed, gender, is_adopted, name) VALUES
(1, 2, 'Black', 'Female', false, 'Jewels'),
(2, 3, 'British Short Hair', 'Male', false, 'Coco'),
(3, 10, 'Tabby', 'Male', true, 'Keday'),
(4, 2, 'Siamese', 'Female', false, 'Whiskers'),
(5, 7, 'Chincilla', 'Male', true, 'Kedis'),
(6, 1, 'Turkish Van Cat', 'Female', false, 'Minnie'),
(7, 1, 'Calico', 'Female', true, 'Lola'),
(8, 8, 'Tabby', 'Female', false, 'Lulu'),
(9, 1, 'Siberian', 'Male', false, 'Leo');

SELECT * FROM cat;

