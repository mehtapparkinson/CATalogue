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
(1, 2, 'Black', 'Female', 0, 'Jewels'),
(2, 3, 'British Short Hair', 'Male', 0, 'Coco'),
(3, 10, 'Tabby', 'Male', 0, 'Keday'),
(4, 2, 'Siamese', 'Female', 0, 'Whiskers'),
(5, 7, 'Chincilla', 'Male', 0, 'Kedis'),
(6, 1, 'Turkish Van Cat', 'Female', 0, 'Minnie'),
(7, 1, 'Calico', 'Female', 0, 'Lola'),
(8, 8, 'Tabby', 'Female', 0, 'Lulu');

