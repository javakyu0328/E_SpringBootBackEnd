-- 영화 테이블 생성
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

-- 영화 추천 테이블 생성
CREATE TABLE IF NOT EXISTS movie_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    member_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_recommendation (movie_id, member_id),
    INDEX idx_movie_id (movie_id),
    INDEX idx_member_id (member_id),
    
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);