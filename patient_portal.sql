-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Jan 04, 2026 at 05:04 AM
-- Server version: 9.1.0
-- PHP Version: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `patient_portal`
--

-- --------------------------------------------------------

--
-- Table structure for table `announcements`
--

DROP TABLE IF EXISTS `announcements`;
CREATE TABLE IF NOT EXISTS `announcements` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `level` enum('NEWS','URGENT') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NEWS',
  `title` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `author_user_id` bigint UNSIGNED DEFAULT NULL,
  `published_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_announce_author` (`author_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `announcements`
--

INSERT INTO `announcements` (`id`, `level`, `title`, `content`, `author_user_id`, `published_at`) VALUES
(1, 'NEWS', 'Thông báo bảo trì', 'Hệ thống bảo trì lúc 22:00', 1, '2025-10-28 13:49:13');

-- --------------------------------------------------------

--
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
CREATE TABLE IF NOT EXISTS `appointments` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `patient_id` bigint UNSIGNED NOT NULL,
  `doctor_id` bigint UNSIGNED NOT NULL,
  `service_id` bigint UNSIGNED DEFAULT NULL,
  `scheduled_at` datetime NOT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `current_stage_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_appt_patient_time` (`patient_id`,`scheduled_at`),
  KEY `idx_appt_doctor_time` (`doctor_id`,`scheduled_at`),
  KEY `fk_appt_service` (`service_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `appointments`
--

INSERT INTO `appointments` (`id`, `patient_id`, `doctor_id`, `service_id`, `scheduled_at`, `status`, `notes`, `created_at`, `updated_at`, `current_stage_id`) VALUES
(1, 24, 3, 1, '2025-12-22 08:30:00', 'CONFIRMED', 'seed', '2025-11-30 03:19:39', '2025-12-22 03:02:13', NULL),
(2, 2, 3, NULL, '2025-12-22 08:30:00', 'REQUESTED', 'US9 seed - WAITING', '2025-12-20 09:32:48', '2025-12-22 03:02:13', NULL),
(3, 13, 3, NULL, '2025-12-22 10:00:00', 'COMPLETED', 'US9 seed - DONE', '2025-12-20 09:32:48', '2025-12-22 03:02:13', NULL),
(4, 26, 3, NULL, '2025-12-22 08:30:00', 'REQUESTED', 'US9 seed - WAITING', '2025-12-20 15:00:00', '2025-12-22 03:02:13', NULL),
(5, 27, 3, NULL, '2025-12-22 10:00:00', 'COMPLETED', 'US9 seed - DONE', '2025-12-20 15:00:00', '2025-12-22 03:02:13', NULL),
(9, 28, 3, NULL, '2025-12-22 11:30:00', 'REQUESTED', 'Thêm bệnh nhân 28', '2025-12-21 15:15:08', '2025-12-22 03:02:13', NULL),
(10, 29, 3, NULL, '2025-12-22 12:00:00', 'CONFIRMED', 'Thêm bệnh nhân 29', '2025-12-21 15:15:08', '2025-12-22 03:02:13', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `care_flow_stages`
--

DROP TABLE IF EXISTS `care_flow_stages`;
CREATE TABLE IF NOT EXISTS `care_flow_stages` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `appointment_id` bigint UNSIGNED NOT NULL,
  `stage_order` int NOT NULL,
  `stage_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_appt_stage` (`appointment_id`,`stage_order`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `care_flow_stages`
--

INSERT INTO `care_flow_stages` (`id`, `appointment_id`, `stage_order`, `stage_name`, `status`, `updated_at`) VALUES
(1, 1, 1, 'Khám tổng quát', 'DONE', '2025-11-30 03:19:48'),
(2, 1, 2, 'Xét nghiệm máu', 'WAITING', '2025-11-30 03:19:48'),
(3, 1, 3, 'Tai - Mũi - Họng', 'NOT_STARTED', '2025-11-30 03:19:48'),
(4, 1, 4, 'Siêu âm bụng', 'NOT_STARTED', '2025-11-30 03:19:48');

-- --------------------------------------------------------

--
-- Table structure for table `doctor_profiles`
--

DROP TABLE IF EXISTS `doctor_profiles`;
CREATE TABLE IF NOT EXISTS `doctor_profiles` (
  `user_id` bigint UNSIGNED NOT NULL,
  `full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `specialty` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `department` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `license_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `bio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `doctor_profiles`
--

INSERT INTO `doctor_profiles` (`user_id`, `full_name`, `specialty`, `department`, `license_no`, `bio`) VALUES
(3, 'BS. Lê Văn C', 'Nội tổng quát', 'Khám tổng quát', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `documents`
--

DROP TABLE IF EXISTS `documents`;
CREATE TABLE IF NOT EXISTS `documents` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `patient_id` bigint UNSIGNED NOT NULL,
  `appointment_id` bigint UNSIGNED DEFAULT NULL,
  `doc_type` enum('LAB','IMAGING','INVOICE','OTHER') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `mime_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `created_by` bigint UNSIGNED NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_doc_patient` (`patient_id`),
  KEY `fk_doc_appt` (`appointment_id`),
  KEY `fk_doc_creator` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `invoices`
--

DROP TABLE IF EXISTS `invoices`;
CREATE TABLE IF NOT EXISTS `invoices` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `invoice_no` varchar(255) NOT NULL,
  `issue_date` date NOT NULL,
  `items_json` json NOT NULL,
  `patient_id` bigint NOT NULL,
  `status` enum('PAID','UNPAID','VOID') NOT NULL,
  `total_amount` decimal(38,2) NOT NULL,
  `appointment_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK465kfg5i8gam6kc8fnb96fqce` (`invoice_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
CREATE TABLE IF NOT EXISTS `messages` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `patient_id` bigint UNSIGNED NOT NULL,
  `doctor_id` bigint UNSIGNED NOT NULL,
  `sender_user_id` bigint UNSIGNED NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `sent_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `read_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_msg_pair_time` (`patient_id`,`doctor_id`,`sent_at`),
  KEY `fk_msg_doctor` (`doctor_id`),
  KEY `fk_msg_sender` (`sender_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
CREATE TABLE IF NOT EXISTS `notifications` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint UNSIGNED NOT NULL,
  `type` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `body` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `status` enum('UNREAD','READ') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'UNREAD',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `related_type` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `related_id` bigint UNSIGNED DEFAULT NULL,
  `patient_id` bigint DEFAULT NULL,
  `read_flag` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_notif_user_time` (`user_id`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `password_reset_tokens`
--

DROP TABLE IF EXISTS `password_reset_tokens`;
CREATE TABLE IF NOT EXISTS `password_reset_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `token` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `expiry_date` datetime NOT NULL,
  `used` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `fk_prt_user` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `password_reset_tokens`
--

INSERT INTO `password_reset_tokens` (`id`, `token`, `user_id`, `expiry_date`, `used`) VALUES
(1, '8f39d0af-4e19-49ff-a5a0-734ad6852dba', 10, '2025-11-05 21:20:01', 0),
(2, 'd69b1066-a118-4766-89c2-96b61aeceb8c', 10, '2025-11-05 21:20:17', 0),
(3, 'fdc0aa50-ba9f-49f5-b3fd-230a56f157ae', 10, '2025-11-05 21:25:02', 1),
(4, '0f603dac-73b6-471c-8a36-dedfc522b2b9', 12, '2025-11-10 21:26:36', 0),
(5, '134e51d8-6139-4ec1-b5f5-4d7af4395486', 12, '2025-11-10 21:26:47', 0),
(6, 'aade6e93-30a1-4dcd-aa12-832fe2084fc0', 10, '2025-11-10 21:27:15', 0),
(7, 'bbcb6dca-4e48-42a9-a8a7-ee36c0b36aef', 10, '2025-11-10 21:27:24', 0),
(8, '9073adb4-a96b-47f0-8de7-4dfceda0727e', 10, '2025-11-10 21:27:49', 1),
(9, '8f245887-7824-458a-8948-bfc74fe97efa', 10, '2025-11-10 21:38:33', 0),
(10, '22382bae-b1a4-48d5-a79a-1343689fa2a6', 13, '2025-11-17 09:35:20', 1),
(11, '580d8cdb-a8e6-45f4-8ef3-fac5d15ebedd', 4, '2025-11-17 16:39:43', 1),
(12, 'c5bd9c60-0fb3-4491-ac3a-f420d9d8cdb0', 4, '2025-11-17 16:46:28', 1),
(13, '662779e4-891e-4af7-ad2a-eed2c325951f', 21, '2025-11-18 19:31:29', 0),
(14, '96fe2017-4d10-41d2-8edd-10d4f1800b35', 4, '2025-11-18 19:42:05', 0),
(15, '48d1cd32-4b2b-4366-93d8-2169d0e02c87', 3, '2025-12-20 22:09:03', 1),
(16, 'd4d482b6-1073-475b-8344-b58c1900d272', 26, '2025-12-20 22:16:55', 1),
(17, '5ef57c3d-6909-44ff-86f0-9d0a3d6688bb', 27, '2025-12-20 22:17:51', 1),
(18, '96fa4684-589e-4ca5-aa09-63469848db96', 8, '2025-12-20 22:20:51', 1),
(19, 'c0c785e6-a59c-4652-8acd-c49ab2c9e98b', 7, '2026-01-04 10:59:19', 0),
(20, '9fe131c2-f68e-4135-bb16-e22a20415109', 4, '2026-01-04 11:01:12', 1);

-- --------------------------------------------------------

--
-- Table structure for table `patient_profiles`
--

DROP TABLE IF EXISTS `patient_profiles`;
CREATE TABLE IF NOT EXISTS `patient_profiles` (
  `user_id` bigint UNSIGNED NOT NULL,
  `full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `date_of_birth` date DEFAULT NULL,
  `sex` enum('M','F','O') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `insurance_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `emergency_contact_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `emergency_contact_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `avatar_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `patient_profiles`
--

INSERT INTO `patient_profiles` (`user_id`, `full_name`, `date_of_birth`, `sex`, `address`, `insurance_number`, `emergency_contact_name`, `emergency_contact_phone`, `avatar_url`) VALUES
(2, 'Nguyễn Văn A', '1995-05-10', 'M', NULL, NULL, NULL, NULL, NULL),
(13, 'PHAM GIA HIEP22', NULL, NULL, '', '', '', '', NULL),
(17, 'ANH HIEP', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(18, 'ANH HIEP1', '2004-07-05', NULL, 'phu yen', '12312412412', 'ANH ', 'HAI', NULL),
(19, 'DANG KY', '2004-07-05', NULL, 'PHU YEN', '12312312', 'GIA HIEP', '12321312', NULL),
(20, 'PHAM GIA HIEP21', '2004-07-05', NULL, 'PHU YEN', '', 'GIA HIEPA', '123123123', NULL),
(21, 'Nguyen Van A', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(22, 'Nguyen Van A', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(23, 'Nguyen Van A', '2000-01-01', NULL, '123 Duong ABC, Quan 1, TP.HCM', 'BHYT123456789', 'Nguyen Van B', '0909123456', NULL),
(24, 'vanhai123', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(25, 'vanhai12333', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(26, 'Bệnh nhân Seed 01', NULL, 'F', NULL, NULL, NULL, NULL, NULL),
(27, 'Bệnh nhân Seed 02', NULL, 'M', NULL, NULL, NULL, NULL, NULL),
(28, 'Bệnh nhân 28', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(29, 'Bệnh nhân 29', NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `process_logs`
--

DROP TABLE IF EXISTS `process_logs`;
CREATE TABLE IF NOT EXISTS `process_logs` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `appointment_id` bigint UNSIGNED NOT NULL,
  `stage_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `old_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `new_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `updated_by` bigint UNSIGNED DEFAULT NULL,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` datetime(6) DEFAULT NULL,
  `stage_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_log_appointment` (`appointment_id`),
  KEY `FKp6t6p8283f42h4di796uhaych` (`updated_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `services`
--

DROP TABLE IF EXISTS `services`;
CREATE TABLE IF NOT EXISTS `services` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `price` decimal(38,2) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `services`
--

INSERT INTO `services` (`id`, `code`, `name`, `price`, `active`, `description`) VALUES
(1, 'CONSULT', 'Khám tư vấn', 150000.00, 0, NULL),
(2, 'LAB_BLOOD', 'Xét nghiệm máu', 200000.00, 1, NULL),
(3, 'IMG_XRAY', 'Chụp X-quang', 350000.00, 1, 'Chụp X-quang trong cơ thể'),
(4, 'KHAM_RANG_53E8BC', 'Khám răng', 200000.00, 1, 'Răng - hàm - mặt'),
(5, 'KHAM_DA_DAY_E1AC9A', 'Khám dạ dày', 600000.00, 1, 'Khám dạ dày');

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
CREATE TABLE IF NOT EXISTS `students` (
  `id` int NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `role` enum('PATIENT','DOCTOR','ADMIN') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `status` enum('ACTIVE','LOCKED','DISABLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ACTIVE',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `auto_notification_enabled` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `email`, `phone`, `password_hash`, `role`, `status`, `created_at`, `updated_at`, `auto_notification_enabled`) VALUES
(1, 'admin2', 'admin@hospital.local', '0900000001', '123', 'ADMIN', 'ACTIVE', '2025-10-28 13:49:13', '2025-11-10 14:09:07', b'0'),
(2, 'patient01', 'patient01@example.com', '0900000002', '$2b$10$Y9l6Z0zUj0XJxJv9kYcNue6P2z9l8G8z6r0XG7qFQ4Z2k4q7E2RBu', 'PATIENT', 'ACTIVE', '2025-10-28 13:49:13', '2025-12-20 09:32:48', b'0'),
(3, 'doctor01', 'doctor01@example.com', '0900000003', '$2a$10$CfngtN0rbvHmHihVpnParOjEGZImGLn8zzw2CVAWE3gWNORwwlDyu', 'DOCTOR', 'ACTIVE', '2025-10-28 13:49:13', '2025-12-20 14:54:16', b'0'),
(4, 'hiep', 'hiepcc@gmail.com', '0123455678', '$2a$10$c/hzpsOggyOdJV6aktH4kenviHuJ85J1Hom8IDZlLfsRz4p/7Tvh6', 'ADMIN', 'ACTIVE', '2025-11-05 01:26:45', '2026-01-04 03:46:28', b'0'),
(7, 'admin02', 'admin01@hospital.local', '0900000001', '$2a$10$AyTR6V27P3iNktYAd7eQqOasv6z0sPwKmpn7WqgRGeQ1CCFzfp1hi', 'ADMIN', 'ACTIVE', '2025-11-05 01:12:15', '2025-11-05 01:12:15', b'0'),
(8, 'doctor02', 'doctor01@hospital.local', '0900000002', '$2a$10$rPd4d1yw772eOj.GF09yX.J57HDSttAaolwYlY1tmJNUSlCv8KR52', 'DOCTOR', 'ACTIVE', '2025-11-05 01:12:15', '2025-12-20 15:06:01', b'0'),
(9, 'patient02', 'patient01@hospital.local', '0900000003', '$2a$10$FVZoZ6ojkTVDEwob1xDHiOEHQXFF4hpNKnHzBbhrM6xpjxhbGHvSe', 'PATIENT', 'ACTIVE', '2025-11-05 01:12:15', '2025-11-05 01:12:15', b'0'),
(10, 'testuser', 'test@example.com', NULL, '$2a$10$wdCfDVBj7Eo99BnNvgQrROsv/NxwPLJpnc3uNlKfOjfhasI56i/Va', 'PATIENT', 'ACTIVE', '2025-11-05 01:38:47', '2025-11-10 14:14:48', b'0'),
(11, 'doctor', 'doctor@example.com', NULL, '$2a$10$FzjNXl39WeWkWz.VmXSl/.3mVBGcaiaNkHKHpwWVQtJC4Bh4YIK/y', 'DOCTOR', 'ACTIVE', '2025-11-10 14:08:14', '2025-11-10 14:08:14', b'0'),
(12, 'admin', 'admin@example.com', NULL, '$2a$10$wDeS4SQP2mk/YIV3VDLY5utn0Zv3g2DexRuFy/A0EMwib4iSeekXO', 'ADMIN', 'ACTIVE', '2025-11-10 14:09:13', '2025-11-10 14:09:13', b'0'),
(13, 'hiepcc22', '', '', '$2a$10$B3dsGkwu5TQpIgFER4FjOeop4iiIxXZCtRfHUzAVMFi2W8cwzeftS', 'PATIENT', 'ACTIVE', '2025-11-17 02:19:39', '2025-11-18 03:04:59', b'0'),
(14, '0532341234', '0532341234@phone.local', '0532341234', '$2a$10$2b.5mg1INMKGwRh5rkJ5je8thDEWSVwJrxfnRsDGsn8QxLHqqbiwW', 'PATIENT', 'ACTIVE', '2025-11-17 09:09:32', '2025-11-17 09:09:32', b'0'),
(15, '05323412342', '05323412342@phone.local', '05323412342', '$2a$10$KPZz.NuKoLF6tBTOFxSpCu0rHonlybImvNC79X7VwllNscdPLQSxC', 'PATIENT', 'ACTIVE', '2025-11-17 09:10:52', '2025-11-17 09:10:52', b'0'),
(16, 'hiepproo', 'hiepproo@gmail.com', NULL, '$2a$10$1OjMsYPWOHBaIfvRdEmMOONO3c8i871TEY/a.gbR611rQqz6qgrzG', 'PATIENT', 'ACTIVE', '2025-11-17 09:11:51', '2025-11-17 09:11:51', b'0'),
(17, '056123456', '056123456@phone.local', '056123456', '$2a$10$2Oa1O0DOkNhmwZ5aoFZlg.T1By8L4HvWtPO5y/CBSJ5CQ13EDOytq', 'PATIENT', 'ACTIVE', '2025-11-17 09:24:07', '2025-11-17 09:24:07', b'0'),
(18, '0564082621', '0564082621@phone.local', '0564082621', '$2a$10$HI/79rm1ePW7OT7mFzJ/tuLU2b8nMu8kP.TpvFy5W58JXytCA2CCu', 'PATIENT', 'ACTIVE', '2025-11-17 09:25:59', '2025-11-17 09:25:59', b'0'),
(19, 'dangky', 'dangky@gmail.com', '0123123123', '$2a$10$rusw5KFJN8Yit6mSXQflH.bl1fDh.oLaQKt0A5q3lCl211U0/UT/6', 'PATIENT', 'ACTIVE', '2025-11-17 09:30:12', '2025-11-17 09:30:48', b'0'),
(20, 'ahiep', 'ahiep@gmail.com', '056123123', '$2a$10$7SjNT7Fm3gUtCCFxdWBpqepkMsCB2w84cfuxTT5QkwuX2HYh4iyLm', 'PATIENT', 'ACTIVE', '2025-11-18 02:18:03', '2025-11-18 02:19:40', b'0'),
(21, 'a', 'a@example.com', NULL, '$2a$10$mkOg11RtiaX2h0tOAM4BMO.b1ix0qm2WEaUDDnt4NAMtM35.xPpyW', 'PATIENT', 'ACTIVE', '2025-11-18 12:16:11', '2025-11-18 12:16:11', b'0'),
(22, 'a2', 'a2@example.com', NULL, '$2a$10$NvFi5mfzFRZaE2saX1yy8eCq.Td06PT72BM4Ytj9qxoKYJ6zJECb2', 'PATIENT', 'ACTIVE', '2025-11-18 12:28:49', '2025-11-18 12:28:49', b'0'),
(23, '0541234211', 'aaa@example.com', '0909123456', '$2a$10$YTnyphaUA.3enUlbKox/0e6kH7UVMtX2RMuqeHPuUfc.jP1L7NWF6', 'PATIENT', 'ACTIVE', '2025-11-18 12:31:36', '2025-11-18 14:38:26', b'0'),
(24, 'vanhai12', 'vanhai12@gmail.com', NULL, '$2a$10$fQgv9Bd3w1AnkrAD.oQtlegu8plQojM3e4by69mdukN8tzcheBr7i', 'PATIENT', 'ACTIVE', '2025-11-30 03:17:50', '2025-11-30 03:17:50', b'0'),
(25, 'vanhai1233', 'vanhai1233@gmail.com', NULL, '$2a$10$56JTIj7XGbobVx.FDsMkJerQ5n8QDwhbb/aW2X508FFuWS8giT9xW', 'PATIENT', 'ACTIVE', '2025-11-30 03:59:05', '2025-11-30 03:59:05', b'0'),
(26, 'patient_seed01', 'patient_seed01@test.com', NULL, '$2a$10$3tI.BZ7Xi6Hdp.qkjbLSbeayNHJMSPyADl/zpn5ctlulKciTTyEgO', 'PATIENT', 'ACTIVE', '2025-12-20 15:00:00', '2025-12-20 15:02:06', b'0'),
(27, 'patient_seed02', 'patient_seed02@test.com', NULL, '$2a$10$MAB3OhJNV29iDie7T6DyluSehDKpFFhZAluDb.qukAZxmp.5Gaw6m', 'PATIENT', 'ACTIVE', '2025-12-20 15:00:00', '2025-12-20 15:03:02', b'0'),
(28, 'patient28', 'p28@test.com', NULL, '12345678', 'PATIENT', 'ACTIVE', '2025-12-21 15:01:24', NULL, b'0'),
(29, 'patient29', 'p29@test.com', NULL, '12345678', 'PATIENT', 'ACTIVE', '2025-12-21 15:01:24', NULL, b'0');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `announcements`
--
ALTER TABLE `announcements`
  ADD CONSTRAINT `fk_announce_author` FOREIGN KEY (`author_user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `appointments`
--
ALTER TABLE `appointments`
  ADD CONSTRAINT `fk_appt_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctor_profiles` (`user_id`),
  ADD CONSTRAINT `fk_appt_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient_profiles` (`user_id`),
  ADD CONSTRAINT `fk_appt_service` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`);

--
-- Constraints for table `care_flow_stages`
--
ALTER TABLE `care_flow_stages`
  ADD CONSTRAINT `fk_stage_appt` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`);

--
-- Constraints for table `doctor_profiles`
--
ALTER TABLE `doctor_profiles`
  ADD CONSTRAINT `fk_doctor_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `documents`
--
ALTER TABLE `documents`
  ADD CONSTRAINT `fk_doc_appt` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`),
  ADD CONSTRAINT `fk_doc_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `fk_doc_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient_profiles` (`user_id`);

--
-- Constraints for table `messages`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `fk_msg_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctor_profiles` (`user_id`),
  ADD CONSTRAINT `fk_msg_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient_profiles` (`user_id`),
  ADD CONSTRAINT `fk_msg_sender` FOREIGN KEY (`sender_user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `fk_notif_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `patient_profiles`
--
ALTER TABLE `patient_profiles`
  ADD CONSTRAINT `FK48bdvcabhgaa1bqphn9jijwn2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `process_logs`
--
ALTER TABLE `process_logs`
  ADD CONSTRAINT `FKhy6ylbokfasotkratjuy9b6xx` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`),
  ADD CONSTRAINT `FKp6t6p8283f42h4di796uhaych` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
