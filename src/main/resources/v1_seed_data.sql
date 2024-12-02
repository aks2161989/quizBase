-- Create Database
CREATE DATABASE IF NOT EXISTS quizbase;

-- Use the Database
USE quizbase;

-- Create Table: Categories
CREATE TABLE IF NOT EXISTS categories (
                                          id INT PRIMARY KEY AUTO_INCREMENT,
                                          name VARCHAR(255) NOT NULL
    );

-- Create Table: Users
CREATE TABLE IF NOT EXISTS users (
                                     id INT PRIMARY KEY AUTO_INCREMENT,
                                     username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL
    );

-- Create Table: Quizzes
CREATE TABLE IF NOT EXISTS quizzes (
                                       id INT PRIMARY KEY AUTO_INCREMENT,
                                       title VARCHAR(255) NOT NULL,
    category_id INT NOT NULL,
    creator_id INT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Create Table: Questions
CREATE TABLE IF NOT EXISTS questions (
                                         id INT PRIMARY KEY AUTO_INCREMENT,
                                         text VARCHAR(255) NOT NULL,
    quiz_id INT NOT NULL,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
    );

-- Create Table: Options
CREATE TABLE IF NOT EXISTS options (
                                       id INT PRIMARY KEY AUTO_INCREMENT,
                                       text VARCHAR(255) NOT NULL,
    question_id INT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
    );

-- Delete existing data (for re-initialization)
DELETE FROM options;
DELETE FROM questions;
DELETE FROM quizzes;
DELETE FROM users;
DELETE FROM categories;

-- Reset AUTO_INCREMENT values (optional)
ALTER TABLE options AUTO_INCREMENT = 1;
ALTER TABLE questions AUTO_INCREMENT = 1;
ALTER TABLE quizzes AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE categories AUTO_INCREMENT = 1;

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
INSERT INTO quizzes (title, category_id, creator_id) VALUES
                                                         ('Famous Paintings Quiz', 1, 1), -- Arts Quiz by John Doe
                                                         ('World Cup History Quiz', 2, 2); -- Sports Quiz by Jane Smith

-- Insert Data: Questions for Quiz 1
INSERT INTO questions (text, quiz_id) VALUES
                                          ('Who painted the Mona Lisa?', 1),
                                          ('Which artist is famous for the Starry Night painting?', 1),
                                          ('The Sistine Chapel ceiling was painted by?', 1),
                                          ('The Persistence of Memory was painted by?', 1),
                                          ('Which artist painted The Last Supper?', 1),
                                          ('Who painted The Girl with a Pearl Earring?', 1),
                                          ('The Birth of Venus was painted by?', 1),
                                          ('Guernica was created by?', 1),
                                          ('Who painted the American Gothic?', 1),
                                          ('The Hay Wain is a painting by?', 1);

-- Insert Data: Options for Quiz 1 Questions
INSERT INTO options (text, question_id, is_correct) VALUES
                                                        ('Leonardo da Vinci', 1, TRUE),
                                                        ('Vincent van Gogh', 1, FALSE),
                                                        ('Michelangelo', 1, FALSE),
                                                        ('Raphael', 1, FALSE),

                                                        ('Vincent van Gogh', 2, TRUE),
                                                        ('Claude Monet', 2, FALSE),
                                                        ('Paul Cézanne', 2, FALSE),
                                                        ('Edgar Degas', 2, FALSE),

                                                        ('Michelangelo', 3, TRUE),
                                                        ('Donatello', 3, FALSE),
                                                        ('Raphael', 3, FALSE),
                                                        ('Leonardo da Vinci', 3, FALSE),

                                                        ('Salvador Dalí', 4, TRUE),
                                                        ('Joan Miró', 4, FALSE),
                                                        ('Pablo Picasso', 4, FALSE),
                                                        ('Marc Chagall', 4, FALSE),

                                                        ('Leonardo da Vinci', 5, TRUE),
                                                        ('Caravaggio', 5, FALSE),
                                                        ('Sandro Botticelli', 5, FALSE),
                                                        ('Raphael', 5, FALSE),

                                                        ('Johannes Vermeer', 6, TRUE),
                                                        ('Rembrandt', 6, FALSE),
                                                        ('Frans Hals', 6, FALSE),
                                                        ('Jan Steen', 6, FALSE),

                                                        ('Sandro Botticelli', 7, TRUE),
                                                        ('Titian', 7, FALSE),
                                                        ('Giorgione', 7, FALSE),
                                                        ('Leonardo da Vinci', 7, FALSE),

                                                        ('Pablo Picasso', 8, TRUE),
                                                        ('Georges Braque', 8, FALSE),
                                                        ('Joan Miró', 8, FALSE),
                                                        ('Henri Matisse', 8, FALSE),

                                                        ('Grant Wood', 9, TRUE),
                                                        ('Edward Hopper', 9, FALSE),
                                                        ('Norman Rockwell', 9, FALSE),
                                                        ('Andrew Wyeth', 9, FALSE),

                                                        ('John Constable', 10, TRUE),
                                                        ('J.M.W. Turner', 10, FALSE),
                                                        ('Thomas Gainsborough', 10, FALSE),
                                                        ('William Blake', 10, FALSE);

-- Insert Data: Questions for Quiz 2
INSERT INTO questions (text, quiz_id) VALUES
                                          ('Which country won the first FIFA World Cup?', 2),
                                          ('Who is the top scorer in World Cup history?', 2),
                                          ('Which year did England win the World Cup?', 2),
                                          ('Which player has won the most World Cups?', 2),
                                          ('Which country has hosted the most World Cups?', 2),
                                          ('Who scored the fastest goal in World Cup history?', 2),
                                          ('Which country has lost the most World Cup finals?', 2),
                                          ('Which World Cup had the most goals scored?', 2),
                                          ('What was the nickname of the 2010 World Cup ball?', 2),
                                          ('Which year did VAR debut in the World Cup?', 2);

-- Insert Data: Options for Quiz 2 Questions
INSERT INTO options (text, question_id, is_correct) VALUES
                                                        ('Uruguay', 11, TRUE),
                                                        ('Brazil', 11, FALSE),
                                                        ('Italy', 11, FALSE),
                                                        ('Argentina', 11, FALSE),

                                                        ('Miroslav Klose', 12, TRUE),
                                                        ('Ronaldo', 12, FALSE),
                                                        ('Pelé', 12, FALSE),
                                                        ('Just Fontaine', 12, FALSE),

                                                        ('1966', 13, TRUE),
                                                        ('1958', 13, FALSE),
                                                        ('1970', 13, FALSE),
                                                        ('1990', 13, FALSE),

                                                        ('Pelé', 14, TRUE),
                                                        ('Diego Maradona', 14, FALSE),
                                                        ('Zinedine Zidane', 14, FALSE),
                                                        ('Franz Beckenbauer', 14, FALSE),

                                                        ('Brazil', 15, TRUE),
                                                        ('Germany', 15, FALSE),
                                                        ('Italy', 15, FALSE),
                                                        ('France', 15, FALSE),

                                                        ('Hakan Şükür', 16, TRUE),
                                                        ('Cristiano Ronaldo', 16, FALSE),
                                                        ('Diego Maradona', 16, FALSE),
                                                        ('Lionel Messi', 16, FALSE),

                                                        ('Germany', 17, TRUE),
                                                        ('Netherlands', 17, FALSE),
                                                        ('Argentina', 17, FALSE),
                                                        ('Italy', 17, FALSE),

                                                        ('1998', 18, TRUE),
                                                        ('2018', 18, FALSE),
                                                        ('2014', 18, FALSE),
                                                        ('2002', 18, FALSE),

                                                        ('Jabulani', 19, TRUE),
                                                        ('Brazuca', 19, FALSE),
                                                        ('Telstar', 19, FALSE),
                                                        ('Teamgeist', 19, FALSE),

                                                        ('2018', 20, TRUE),
                                                        ('2014', 20, FALSE),
                                                        ('2010', 20, FALSE),
                                                        ('2006', 20, FALSE);
