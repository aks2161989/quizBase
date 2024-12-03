-- Drop the database if it exists (optional, for clean initialization)
DROP DATABASE IF EXISTS quizbase;

-- Create a new database
CREATE DATABASE quizbase;

-- Use the newly created database
USE quizbase;

-- Disable foreign key checks to prevent constraint errors during data reset
SET FOREIGN_KEY_CHECKS = 0;

-- Drop tables if they exist to ensure clean initialization
DROP TABLE IF EXISTS responses;
DROP TABLE IF EXISTS options;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS quizzes;
DROP TABLE IF EXISTS results;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS categories;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Create Table: Categories
CREATE TABLE categories (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(255) NOT NULL
);

-- Create Table: Users
CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       full_name VARCHAR(255) NOT NULL
);

-- Create Table: Quizzes
CREATE TABLE quizzes (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         title VARCHAR(255) NOT NULL,
                         description TEXT NOT NULL,
                         category_id INT NOT NULL,
                         creator_id INT NOT NULL,
                         created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
                         FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create Table: Questions
CREATE TABLE questions (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           text VARCHAR(255) NOT NULL,
                           quiz_id INT NOT NULL,
                           FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- Create Table: Options
CREATE TABLE options (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         text VARCHAR(255) NOT NULL,
                         is_correct BOOLEAN NOT NULL,
                         question_id INT NOT NULL,
                         FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- Create Table: Results
CREATE TABLE results (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         user_id INT NOT NULL,
                         quiz_id INT NOT NULL,
                         score INT NOT NULL,
                         attempted_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- Create Table: Responses
CREATE TABLE responses (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           question_id INT NOT NULL,
                           option_id INT NOT NULL,
                           result_id INT NOT NULL,
                           FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
                           FOREIGN KEY (option_id) REFERENCES options(id) ON DELETE CASCADE,
                           FOREIGN KEY (result_id) REFERENCES results(id) ON DELETE CASCADE
);

-- Insert Data: Categories
INSERT INTO categories (name) VALUES
                                  ('Arts'),
                                  ('Sports'),
                                  ('Science'),
                                  ('History'),
                                  ('Entertainment');

-- Insert Data: Users
INSERT INTO users (username, email, password, full_name) VALUES
                                                             ('john_doe', 'john@example.com', 'password123', 'John Doe'),
                                                             ('jane_smith', 'jane@example.com', 'password456', 'Jane Smith');

-- Insert Data: Quizzes
INSERT INTO quizzes (title, description, category_id, creator_id) VALUES
                                                                      ('Famous Paintings Quiz', 'A quiz about famous paintings and artists.', 1, 1),
                                                                      ('World Cup History Quiz', 'Test your knowledge of FIFA World Cup history.', 2, 2);

-- Insert Data: Questions for Quiz 1
INSERT INTO questions (text, quiz_id) VALUES
                                          ('Who painted the Mona Lisa?', 1),
                                          ('Which artist is famous for the Starry Night painting?', 1),
                                          ('The Sistine Chapel ceiling was painted by?', 1),
                                          ('The Persistence of Memory was painted by?', 1),
                                          ('Which artist painted The Last Supper?', 1);

-- Insert Data: Options for Quiz 1 Questions
INSERT INTO options (text, is_correct, question_id) VALUES
                                                        ('Leonardo da Vinci', TRUE, 1),
                                                        ('Vincent van Gogh', FALSE, 1),
                                                        ('Michelangelo', FALSE, 1),
                                                        ('Raphael', FALSE, 1),

                                                        ('Vincent van Gogh', TRUE, 2),
                                                        ('Claude Monet', FALSE, 2),
                                                        ('Paul Cézanne', FALSE, 2),
                                                        ('Edgar Degas', FALSE, 2),

                                                        ('Michelangelo', TRUE, 3),
                                                        ('Donatello', FALSE, 3),
                                                        ('Raphael', FALSE, 3),
                                                        ('Leonardo da Vinci', FALSE, 3),

                                                        ('Salvador Dalí', TRUE, 4),
                                                        ('Joan Miró', FALSE, 4),
                                                        ('Pablo Picasso', FALSE, 4),
                                                        ('Marc Chagall', FALSE, 4),

                                                        ('Leonardo da Vinci', TRUE, 5),
                                                        ('Caravaggio', FALSE, 5),
                                                        ('Sandro Botticelli', FALSE, 5),
                                                        ('Raphael', FALSE, 5);

-- Enable foreign key checks again
SET FOREIGN_KEY_CHECKS = 1;
