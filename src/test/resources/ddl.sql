-- 테이블 생성
CREATE TABLE concert (
                         concert_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         concert_name VARCHAR(255),
                         concert_date DATETIME NOT NULL
);

CREATE TABLE seat (
                              seat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              concert_id BIGINT,
                              seat_number BIGINT,
                              seat_status VARCHAR(10)
);

CREATE TABLE reservation (
                             reservation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             user_id BIGINT,
                             seat_id BIGINT,
                             reservation_status VARCHAR(10)
);

CREATE TABLE users (
                      user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      user_point BIGINT,
                      user_name VARCHAR(30)
);

CREATE TABLE queue (
                            queue_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            user_id BIGINT,
                            token VARCHAR(255),
                            user_queue_status VARCHAR(10),
                            expired_at DATETIME
);

CREATE TABLE ledger (
                        ledger_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT,
                        transaction_type VARCHAR(10),
                        amount BIGINT,
                        update_millis DATETIME
);

CREATE TABLE payment (
                         payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT,
                         reservation_id BIGINT,
                         payment_status VARCHAR(10),
                         amount BIGINT
);
