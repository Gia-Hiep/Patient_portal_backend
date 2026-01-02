
-- TẠO BẢNG process_logs LƯU LỊCH SỬ THAY ĐỔI TRẠNG THÁI QUY TRÌNH KHÁM
CREATE TABLE IF NOT EXISTS process_logs (
    id BIGINT(20) UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    appointment_id BIGINT(20) UNSIGNED NOT NULL,
    stage_name VARCHAR(255) NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50),
    updated_by BIGINT(20) UNSIGNED,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_log_appointment
        FOREIGN KEY (appointment_id)
        REFERENCES appointments(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
