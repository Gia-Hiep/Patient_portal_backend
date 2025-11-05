-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1:3306
-- Thời gian đã tạo: Th10 05, 2025 lúc 02:17 PM
-- Phiên bản máy phục vụ: 5.7.31
-- Phiên bản PHP: 7.3.21

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `patient_portal`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `announcements`
--

DROP TABLE IF EXISTS `announcements`;
CREATE TABLE IF NOT EXISTS `announcements` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `level` enum('NEWS','URGENT') NOT NULL DEFAULT 'NEWS',
  `title` varchar(191) NOT NULL,
  `content` text NOT NULL,
  `author_user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `published_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_announce_author` (`author_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `announcements`
--

INSERT INTO `announcements` (`id`, `level`, `title`, `content`, `author_user_id`, `published_at`) VALUES
(1, 'NEWS', 'Thông báo bảo trì', 'Hệ thống bảo trì lúc 22:00', 1, '2025-10-28 13:49:13');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `appointments`
--

DROP TABLE IF EXISTS `appointments`;
CREATE TABLE IF NOT EXISTS `appointments` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `patient_id` bigint(20) UNSIGNED NOT NULL,
  `doctor_id` bigint(20) UNSIGNED NOT NULL,
  `service_id` bigint(20) UNSIGNED DEFAULT NULL,
  `scheduled_at` datetime NOT NULL,
  `status` enum('REQUESTED','CONFIRMED','CANCELLED','COMPLETED','NO_SHOW') NOT NULL DEFAULT 'REQUESTED',
  `notes` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_appt_patient_time` (`patient_id`,`scheduled_at`),
  KEY `idx_appt_doctor_time` (`doctor_id`,`scheduled_at`),
  KEY `fk_appt_service` (`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `care_flow_stages`
--

DROP TABLE IF EXISTS `care_flow_stages`;
CREATE TABLE IF NOT EXISTS `care_flow_stages` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `appointment_id` bigint(20) UNSIGNED NOT NULL,
  `stage_order` int(11) NOT NULL,
  `stage_name` varchar(128) NOT NULL,
  `status` enum('NOT_STARTED','WAITING','IN_PROGRESS','DONE','CANCELLED') NOT NULL DEFAULT 'NOT_STARTED',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_appt_stage` (`appointment_id`,`stage_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `doctor_profiles`
--

DROP TABLE IF EXISTS `doctor_profiles`;
CREATE TABLE IF NOT EXISTS `doctor_profiles` (
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `full_name` varchar(191) NOT NULL,
  `specialty` varchar(128) DEFAULT NULL,
  `department` varchar(128) DEFAULT NULL,
  `license_no` varchar(64) DEFAULT NULL,
  `bio` text,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `doctor_profiles`
--

INSERT INTO `doctor_profiles` (`user_id`, `full_name`, `specialty`, `department`, `license_no`, `bio`) VALUES
(3, 'BS. Lê Văn C', 'Nội tổng quát', 'Khám tổng quát', NULL, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `documents`
--

DROP TABLE IF EXISTS `documents`;
CREATE TABLE IF NOT EXISTS `documents` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `patient_id` bigint(20) UNSIGNED NOT NULL,
  `appointment_id` bigint(20) UNSIGNED DEFAULT NULL,
  `doc_type` enum('LAB','IMAGING','INVOICE','OTHER') NOT NULL,
  `title` varchar(191) NOT NULL,
  `file_path` varchar(255) NOT NULL,
  `mime_type` varchar(64) DEFAULT NULL,
  `created_by` bigint(20) UNSIGNED NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_doc_patient` (`patient_id`),
  KEY `fk_doc_appt` (`appointment_id`),
  KEY `fk_doc_creator` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `invoices`
--

DROP TABLE IF EXISTS `invoices`;
CREATE TABLE IF NOT EXISTS `invoices` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `patient_id` bigint(20) UNSIGNED NOT NULL,
  `appointment_id` bigint(20) UNSIGNED DEFAULT NULL,
  `invoice_no` varchar(32) NOT NULL,
  `issue_date` date NOT NULL,
  `items_json` json NOT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `status` enum('UNPAID','PAID','VOID') NOT NULL DEFAULT 'UNPAID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `invoice_no` (`invoice_no`),
  KEY `fk_invoice_patient` (`patient_id`),
  KEY `fk_invoice_appt` (`appointment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `messages`
--

DROP TABLE IF EXISTS `messages`;
CREATE TABLE IF NOT EXISTS `messages` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `patient_id` bigint(20) UNSIGNED NOT NULL,
  `doctor_id` bigint(20) UNSIGNED NOT NULL,
  `sender_user_id` bigint(20) UNSIGNED NOT NULL,
  `content` text NOT NULL,
  `sent_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `read_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_msg_pair_time` (`patient_id`,`doctor_id`,`sent_at`),
  KEY `fk_msg_doctor` (`doctor_id`),
  KEY `fk_msg_sender` (`sender_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `notifications`
--

DROP TABLE IF EXISTS `notifications`;
CREATE TABLE IF NOT EXISTS `notifications` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `type` enum('LAB_READY','APPT_REMINDER','REVISIT_REMINDER','SYSTEM','QUEUE_CALL') NOT NULL,
  `title` varchar(191) NOT NULL,
  `body` varchar(512) NOT NULL,
  `status` enum('UNREAD','READ') NOT NULL DEFAULT 'UNREAD',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `related_type` enum('APPOINTMENT','DOCUMENT','INVOICE','NONE') NOT NULL DEFAULT 'NONE',
  `related_id` bigint(20) UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_notif_user_time` (`user_id`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `password_reset_tokens`
--

DROP TABLE IF EXISTS `password_reset_tokens`;
CREATE TABLE IF NOT EXISTS `password_reset_tokens` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `expiry_date` datetime NOT NULL,
  `used` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `fk_prt_user` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `password_reset_tokens`
--

INSERT INTO `password_reset_tokens` (`id`, `token`, `user_id`, `expiry_date`, `used`) VALUES
(1, '8f39d0af-4e19-49ff-a5a0-734ad6852dba', 10, '2025-11-05 21:20:01', 0),
(2, 'd69b1066-a118-4766-89c2-96b61aeceb8c', 10, '2025-11-05 21:20:17', 0),
(3, 'fdc0aa50-ba9f-49f5-b3fd-230a56f157ae', 10, '2025-11-05 21:25:02', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `patient_profiles`
--

DROP TABLE IF EXISTS `patient_profiles`;
CREATE TABLE IF NOT EXISTS `patient_profiles` (
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `full_name` varchar(191) NOT NULL,
  `date_of_birth` date DEFAULT NULL,
  `sex` enum('M','F','O') DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `insurance_number` varchar(64) DEFAULT NULL,
  `emergency_contact_name` varchar(128) DEFAULT NULL,
  `emergency_contact_phone` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `patient_profiles`
--

INSERT INTO `patient_profiles` (`user_id`, `full_name`, `date_of_birth`, `sex`, `address`, `insurance_number`, `emergency_contact_name`, `emergency_contact_phone`) VALUES
(2, 'Nguyễn Văn A', '1995-05-10', 'M', NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `services`
--

DROP TABLE IF EXISTS `services`;
CREATE TABLE IF NOT EXISTS `services` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` varchar(32) NOT NULL,
  `name` varchar(191) NOT NULL,
  `price` decimal(12,2) NOT NULL DEFAULT '0.00',
  `active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `services`
--

INSERT INTO `services` (`id`, `code`, `name`, `price`, `active`) VALUES
(1, 'CONSULT', 'Khám tư vấn', '150000.00', 1),
(2, 'LAB_BLOOD', 'Xét nghiệm máu', '200000.00', 1),
(3, 'IMG_XRAY', 'Chụp X-quang', '300000.00', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('PATIENT','DOCTOR','ADMIN') NOT NULL,
  `status` enum('ACTIVE','LOCKED','DISABLED') NOT NULL DEFAULT 'ACTIVE',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`id`, `username`, `email`, `phone`, `password_hash`, `role`, `status`, `created_at`, `updated_at`) VALUES
(1, 'admin', 'admin@hospital.local', '0900000001', '123', 'ADMIN', 'ACTIVE', '2025-10-28 13:49:13', '2025-11-05 01:23:30'),
(2, 'patient01', 'patient01@example.com', '0900000002', '$2y$10$dummypatienthash', 'PATIENT', 'ACTIVE', '2025-10-28 13:49:13', NULL),
(3, 'doctor01', 'doctor01@example.com', '0900000003', '$2y$10$dummydoctorhash', 'DOCTOR', 'ACTIVE', '2025-10-28 13:49:13', NULL),
(4, 'hiep', 'hiepcc@gmail.com', '0123455678', '123', 'ADMIN', 'ACTIVE', '2025-11-05 01:26:45', NULL),
(7, 'admin02', 'admin01@hospital.local', '0900000001', '$2a$10$AyTR6V27P3iNktYAd7eQqOasv6z0sPwKmpn7WqgRGeQ1CCFzfp1hi', 'ADMIN', 'ACTIVE', '2025-11-05 01:12:15', '2025-11-05 01:12:15'),
(8, 'doctor02', 'doctor01@hospital.local', '0900000002', '$2a$10$uM0yI6QCF/tHykvGOxl6.ekfVr4sd3e6nS0XUJvvfIag0eO52nTO', 'DOCTOR', 'ACTIVE', '2025-11-05 01:12:15', '2025-11-05 01:12:15'),
(9, 'patient02', 'patient01@hospital.local', '0900000003', '$2a$10$FVZoZ6ojkTVDEwob1xDHiOEHQXFF4hpNKnHzBbhrM6xpjxhbGHvSe', 'PATIENT', 'ACTIVE', '2025-11-05 01:12:15', '2025-11-05 01:12:15'),
(10, 'testuser', 'test@example.com', NULL, '$2a$10$9yJwp1XpgObG78awnfc0Me.AFyHGfusPDFklcXDYddcp7nJyF/TPq', 'PATIENT', 'ACTIVE', '2025-11-05 01:38:47', '2025-11-05 14:10:32');

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `announcements`
--
ALTER TABLE `announcements`
  ADD CONSTRAINT `fk_announce_author` FOREIGN KEY (`author_user_id`) REFERENCES `users` (`id`);

--
-- Các ràng buộc cho bảng `appointments`
--
ALTER TABLE `appointments`
  ADD CONSTRAINT `fk_appt_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctor_profiles` (`user_id`),
  ADD CONSTRAINT `fk_appt_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient_profiles` (`user_id`),
  ADD CONSTRAINT `fk_appt_service` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`);

--
-- Các ràng buộc cho bảng `care_flow_stages`
--
ALTER TABLE `care_flow_stages`
  ADD CONSTRAINT `fk_stage_appt` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`);

--
-- Các ràng buộc cho bảng `doctor_profiles`
--
ALTER TABLE `doctor_profiles`
  ADD CONSTRAINT `fk_doctor_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Các ràng buộc cho bảng `documents`
--
ALTER TABLE `documents`
  ADD CONSTRAINT `fk_doc_appt` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`),
  ADD CONSTRAINT `fk_doc_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `fk_doc_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient_profiles` (`user_id`);

--
-- Các ràng buộc cho bảng `invoices`
--
ALTER TABLE `invoices`
  ADD CONSTRAINT `fk_invoice_appt` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`),
  ADD CONSTRAINT `fk_invoice_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient_profiles` (`user_id`);

--
-- Các ràng buộc cho bảng `messages`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `fk_msg_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctor_profiles` (`user_id`),
  ADD CONSTRAINT `fk_msg_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient_profiles` (`user_id`),
  ADD CONSTRAINT `fk_msg_sender` FOREIGN KEY (`sender_user_id`) REFERENCES `users` (`id`);

--
-- Các ràng buộc cho bảng `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `fk_notif_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Các ràng buộc cho bảng `patient_profiles`
--
ALTER TABLE `patient_profiles`
  ADD CONSTRAINT `fk_patient_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
