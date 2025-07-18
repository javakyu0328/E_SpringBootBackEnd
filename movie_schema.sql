-- Movie Management System Database Schema
-- This script creates the tables for movie information and recommendations

USE db_member;

-- Create movies table
CREATE TABLE IF NOT EXISTS movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100),
    release_date VARCHAR(20),
    description TEXT,
    poster_url VARCHAR(500),
    recommendation_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (title),
    INDEX idx_genre (genre),
    INDEX idx_recommendation_count (recommendation_count),
    INDEX idx_created_at (created_at)
);

-- Create movie_recommendations table
CREATE TABLE IF NOT EXISTS movie_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    member_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_recommendation (movie_id, member_id),
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    INDEX idx_movie_id (movie_id),
    INDEX idx_member_id (member_id)
);

-- Insert sample movie data for testing
INSERT INTO movies (title, genre, release_date, description, poster_url, recommendation_count) VALUES
('The Shawshank Redemption', 'Drama', '1994-09-23', 'Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.', 'https://example.com/shawshank.jpg', 0),
('The Godfather', 'Crime', '1972-03-24', 'The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.', 'https://example.com/godfather.jpg', 0),
('The Dark Knight', 'Action', '2008-07-18', 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests.', 'https://example.com/darkknight.jpg', 0),
('Pulp Fiction', 'Crime', '1994-10-14', 'The lives of two mob hitmen, a boxer, a gangster and his wife intertwine in four tales of violence and redemption.', 'https://example.com/pulpfiction.jpg', 0),
('Forrest Gump', 'Drama', '1994-07-06', 'The presidencies of Kennedy and Johnson, the Vietnam War, and other historical events unfold from the perspective of an Alabama man.', 'https://example.com/forrestgump.jpg', 0);

COMMIT;