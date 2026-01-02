-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Jan 02, 2026 at 04:08 PM
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
  `level` enum('NEWS','URGENT') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NEWS',
  `title` varchar(191) COLLATE utf8mb4_general_ci NOT NULL,
  `content` text COLLATE utf8mb4_general_ci NOT NULL,
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
  `status` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `notes` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `current_stage_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_appt_patient_time` (`patient_id`,`scheduled_at`),
  KEY `idx_appt_doctor_time` (`doctor_id`,`scheduled_at`),
  KEY `fk_appt_service` (`service_id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `appointments`
--

INSERT INTO `appointments` (`id`, `patient_id`, `doctor_id`, `service_id`, `scheduled_at`, `status`, `notes`, `created_at`, `updated_at`, `current_stage_id`) VALUES
(24, 24, 3, NULL, '2025-12-23 20:27:20', 'IN_PROGRESS', 'Khám tổng quát – BN 24', '2025-12-23 13:27:20', '2025-12-24 16:22:36', 3),
(25, 25, 3, NULL, '2025-12-23 20:27:20', 'WAITING', 'Chờ xét nghiệm – BN 25', '2025-12-23 13:27:20', '2025-12-23 15:00:03', 1),
(26, 26, 3, NULL, '2025-12-23 20:27:20', 'WAITING', 'Chờ khám – BN 26', '2025-12-23 13:27:20', '2025-12-23 14:58:25', 3),
(40, 25, 3, NULL, '2025-12-24 23:25:27', 'COMPLETED', 'Xét nghiệm hoàn tất', '2025-12-24 16:25:27', '2025-12-24 16:25:27', 4),
(41, 24, 3, NULL, '2025-12-24 23:27:37', 'COMPLETED', 'Xét nghiệm hoàn tất', '2025-12-24 16:27:37', '2025-12-24 16:27:37', 4),
(42, 26, 3, NULL, '2025-12-24 23:27:45', 'COMPLETED', 'Xét nghiệm hoàn tất', '2025-12-24 16:27:45', '2025-12-24 17:08:04', 3),
(46, 27, 3, NULL, '2025-12-25 00:48:50', 'COMPLETED', 'Xét nghiệm hoàn tất', '2025-12-24 17:48:50', '2025-12-24 17:50:02', 3);

-- --------------------------------------------------------

--
-- Table structure for table `care_flow_stages`
--

DROP TABLE IF EXISTS `care_flow_stages`;
CREATE TABLE IF NOT EXISTS `care_flow_stages` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `stage_order` int NOT NULL,
  `stage_name` varchar(128) COLLATE utf8mb4_general_ci NOT NULL,
  `status` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `appointment_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `care_flow_stages`
--

INSERT INTO `care_flow_stages` (`id`, `stage_order`, `stage_name`, `status`, `updated_at`, `appointment_id`) VALUES
(1, 1, 'Đang khám', '', '2025-12-23 13:17:12', 0),
(2, 2, 'Chờ xét nghiệm', '', '2025-12-23 13:17:12', 0),
(3, 3, 'Hoàn tất', '', '2025-12-23 13:17:12', 0);

-- --------------------------------------------------------

--
-- Table structure for table `doctor_profiles`
--

DROP TABLE IF EXISTS `doctor_profiles`;
CREATE TABLE IF NOT EXISTS `doctor_profiles` (
  `user_id` bigint UNSIGNED NOT NULL,
  `full_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `specialty` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `department` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `license_no` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `bio` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `working_schedule` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `doctor_profiles`
--

INSERT INTO `doctor_profiles` (`user_id`, `full_name`, `specialty`, `department`, `license_no`, `bio`, `working_schedule`) VALUES
(3, 'BS. Lê Văn C', 'Nội tổng quát', 'Khám tổng quát', 'C1, C2', NULL, NULL),
(8, 'Bác sĩ Nguyễn L', 'Khám dạ dày', 'Nội khoa', 'N4', 'Bác sĩ chuyên khám dạ dày', 'T2-T5: 8:00 -> 11:00'),
(11, 'Bác sĩ Nguyễn D', 'Da', 'Da liễu', 'B2', 'Bác sĩ chuyên khám về da', 'T2-T4: 8:00 -> 11:00'),
(28, 'Nguyễn Văn A', 'Khám dạ dày', 'Nội', 'N3', NULL, 'Thứ 2, thứ 3'),
(29, 'Bác sĩ Lê Văn B', 'Răng', 'Răng - Hàm -Mặt', 'C1', 'Bác sĩ chuyên khám về răng', 'Thứ 2, Thứ 5, Thứ 7'),
(30, 'Bác sĩ Hoàng Văn D', 'Mắt', 'Mắt', 'C2', NULL, 'T2: 8:00 -> 11:00; T6: 14:00 -> 17:00'),
(31, 'Bác sĩ Ngô Quang H', 'Xương', 'Xương khớp', 'N4', NULL, ''),
(32, 'Văn Hải', NULL, NULL, NULL, NULL, NULL),
(33, 'HarryKane', NULL, NULL, NULL, NULL, NULL),
(34, 'BS Test 29', 'Răng', 'Răng - Hàm - Mặt', 'C1', NULL, 'T2, T5, T7'),
(35, 'BS Test 29 - Updated', 'Nội tổng quát', 'Khám tổng quát', 'LIC-29', 'Bác sĩ cập nhật bằng Postman', 'T2-T6 08:00-17:00'),
(37, 'Karim Benzema', 'Thể thao học đường', 'Thể thao', 'C1', NULL, 'T2; T4; T5; T6'),
(38, 'Ronaldo', 'Thể thao học đường', 'Thể thao', 'C1', NULL, 'T2; T4; T5; T6'),
(39, 'Messi', 'Thể thao học đường', 'Thể thao', 'C1', NULL, 'T2; T4; T5; T6');

-- --------------------------------------------------------

--
-- Table structure for table `documents`
--

DROP TABLE IF EXISTS `documents`;
CREATE TABLE IF NOT EXISTS `documents` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `patient_id` bigint UNSIGNED NOT NULL,
  `appointment_id` bigint UNSIGNED DEFAULT NULL,
  `doc_type` enum('LAB','IMAGING','INVOICE','OTHER') COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `file_path` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `mime_type` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
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
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `patient_id` bigint UNSIGNED NOT NULL,
  `appointment_id` bigint UNSIGNED DEFAULT NULL,
  `invoice_no` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `issue_date` date NOT NULL,
  `items_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `total_amount` decimal(38,2) NOT NULL,
  `status` enum('UNPAID','PAID','VOID') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'UNPAID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `invoice_no` (`invoice_no`),
  KEY `fk_invoice_patient` (`patient_id`),
  KEY `fk_invoice_appt` (`appointment_id`)
) ;

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
  `content` text COLLATE utf8mb4_general_ci NOT NULL,
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
  `status` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `related_type` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `related_id` bigint UNSIGNED DEFAULT NULL,
  `message` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `patient_id` bigint DEFAULT NULL,
  `read_flag` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_notif_user_time` (`user_id`,`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `notifications`
--

INSERT INTO `notifications` (`id`, `user_id`, `type`, `title`, `body`, `status`, `created_at`, `related_type`, `related_id`, `message`, `patient_id`, `read_flag`) VALUES
(1, 1, 'LAB_READY', 'Kết quả xét nghiệm', 'Kết quả xét nghiệm máu của bạn đã sẵn sàng.', 'UNREAD', '2025-11-30 06:15:20', 'DOCUMENT', 12, NULL, NULL, b'0'),
(2, 1, 'APPT_REMINDER', 'Đến lượt khám', 'Bạn sắp đến lượt khám tại phòng Tai – Mũi – Họng.', 'UNREAD', '2025-11-30 06:15:20', 'APPOINTMENT', 33, NULL, NULL, b'0'),
(3, 1, 'REVISIT_REMINDER', 'Nhắc tái khám', 'Bạn có lịch tái khám vào tuần tới.', 'UNREAD', '2025-11-30 06:15:20', 'NONE', NULL, NULL, NULL, b'0'),
(4, 24, 'LAB_READY', 'Kết quả xét nghiệm', 'Kết quả xét nghiệm máu của bạn đã sẵn sàng.', 'READ', '2025-11-30 06:15:47', 'DOCUMENT', 12, NULL, NULL, b'0'),
(5, 25, 'APPT_REMINDER', 'Đến lượt khám', 'Bạn sắp đến lượt khám tại phòng Tai – Mũi – Họng.', 'READ', '2025-11-30 06:15:47', 'APPOINTMENT', 33, NULL, NULL, b'0'),
(6, 24, 'REVISIT_REMINDER', 'Nhắc tái khám', 'Bạn có lịch tái khám vào tuần tới.', 'READ', '2025-11-30 06:15:47', 'NONE', NULL, NULL, NULL, b'0'),
(11, 24, NULL, 'Kết quả xét nghiệm', 'Có kết quả xét nghiệm máu mới.', 'READ', '2025-11-30 11:20:20', 'DOCUMENT', NULL, NULL, NULL, b'0'),
(12, 24, NULL, 'Đến lượt khám', 'Bạn sắp đến lượt khám tại phòng Tai – Mũi – Họng.', 'READ', '2025-11-30 11:20:20', 'APPOINTMENT', 33, NULL, NULL, b'0'),
(13, 24, NULL, 'Nhắc tái khám', 'Bạn có lịch tái khám vào tuần tới.', 'READ', '2025-11-30 11:20:20', 'NONE', NULL, NULL, NULL, b'0'),
(14, 24, NULL, 'Kết quả xét nghiệm', 'Có kết quả xét nghiệm máu mới.', 'READ', '2025-11-30 12:04:21', 'DOCUMENT', NULL, NULL, NULL, b'0'),
(15, 24, NULL, 'Đến lượt khám', 'Bạn sắp đến lượt khám tại phòng Tai – Mũi – Họng.', 'READ', '2025-11-30 12:04:21', 'APPOINTMENT', 33, NULL, NULL, b'0'),
(17, 24, NULL, 'Kết quả xét nghiệm', 'Có kết quả xét nghiệm máu mới.', 'READ', '2025-11-30 12:04:21', 'DOCUMENT', NULL, NULL, NULL, b'0'),
(18, 25, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'READ', '2025-12-24 17:04:00', NULL, NULL, NULL, 25, b'1'),
(19, 25, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'READ', '2025-12-24 17:04:07', NULL, NULL, NULL, 25, b'1'),
(20, 25, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'READ', '2025-12-24 17:04:15', NULL, NULL, NULL, 25, b'1'),
(21, 24, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'READ', '2025-12-24 17:05:12', NULL, NULL, NULL, 24, b'1'),
(22, 24, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'READ', '2025-12-24 17:05:21', NULL, NULL, NULL, 24, b'1'),
(23, 26, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'READ', '2025-12-24 17:07:12', NULL, NULL, NULL, 26, b'1'),
(24, 24, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'READ', '2025-12-24 17:08:16', NULL, NULL, NULL, 24, b'1'),
(25, 24, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'READ', '2025-12-24 17:09:17', NULL, NULL, NULL, 24, b'1'),
(26, 26, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'READ', '2025-12-24 17:11:46', NULL, NULL, NULL, 26, b'1'),
(27, 26, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'READ', '2025-12-24 17:14:42', NULL, NULL, NULL, 26, b'1'),
(28, 24, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'READ', '2025-12-24 17:14:59', NULL, NULL, NULL, 24, b'1'),
(29, 26, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'READ', '2025-12-24 17:15:47', NULL, NULL, NULL, 26, b'1'),
(30, 27, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'READ', '2025-12-24 17:50:09', NULL, NULL, NULL, 27, b'1'),
(31, 25, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'UNREAD', '2025-12-30 13:27:20', NULL, NULL, NULL, 25, b'0'),
(32, 25, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'UNREAD', '2025-12-30 13:27:24', NULL, NULL, NULL, 25, b'0'),
(33, 24, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'UNREAD', '2025-12-30 13:27:30', NULL, NULL, NULL, 24, b'0'),
(34, 26, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'UNREAD', '2025-12-30 13:27:32', NULL, NULL, NULL, 26, b'0'),
(35, 27, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'UNREAD', '2025-12-30 13:27:33', NULL, NULL, NULL, 27, b'0'),
(36, 24, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'UNREAD', '2025-12-30 13:36:01', NULL, NULL, NULL, 24, b'0'),
(37, 27, NULL, 'Kết quả xét nghiệm', 'Kết quả xét nghiệm của bạn đã sẵn sàng.', 'UNREAD', '2025-12-30 14:52:22', NULL, NULL, NULL, 27, b'0');

-- --------------------------------------------------------

--
-- Table structure for table `password_reset_tokens`
--

DROP TABLE IF EXISTS `password_reset_tokens`;
CREATE TABLE IF NOT EXISTS `password_reset_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `token` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `expiry_date` datetime NOT NULL,
  `used` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `fk_prt_user` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
(15, '25c5109f-5254-407f-8eee-b4c60fe8ed2b', 3, '2025-12-23 20:47:49', 1),
(16, 'd5f41a61-c6db-4ac7-b715-8f33cef0d4aa', 8, '2025-12-31 09:51:29', 1),
(17, '26607f94-69d1-4506-b67b-3852e2265f86', 4, '2025-12-31 18:10:50', 1);

-- --------------------------------------------------------

--
-- Table structure for table `patient_profiles`
--

DROP TABLE IF EXISTS `patient_profiles`;
CREATE TABLE IF NOT EXISTS `patient_profiles` (
  `user_id` bigint UNSIGNED NOT NULL,
  `full_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `date_of_birth` date DEFAULT NULL,
  `sex` enum('M','F','O') COLLATE utf8mb4_general_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `insurance_number` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `emergency_contact_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `emergency_contact_phone` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
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
(24, 'vanhai123', NULL, NULL, '', '', '', '', NULL),
(25, 'vanhai12333', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(26, 'vanhai1', NULL, NULL, '', '', '', '', NULL),
(27, 'vanhai1122', NULL, NULL, '', '', '', '', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `process_logs`
--

DROP TABLE IF EXISTS `process_logs`;
CREATE TABLE IF NOT EXISTS `process_logs` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `appointment_id` bigint UNSIGNED NOT NULL,
  `old_status` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `new_status` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `updated_by` bigint UNSIGNED DEFAULT NULL,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` datetime(6) DEFAULT NULL,
  `stage_id` bigint NOT NULL,
  `stage_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_log_appointment` (`appointment_id`),
  KEY `FKp6t6p8283f42h4di796uhaych` (`updated_by`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `process_logs`
--

INSERT INTO `process_logs` (`id`, `appointment_id`, `old_status`, `new_status`, `updated_by`, `updated_at`, `created_at`, `stage_id`, `stage_name`) VALUES
(1, 24, NULL, NULL, 3, '2025-12-23 13:36:01', '2025-12-23 20:36:01.000000', 1, ''),
(2, 24, NULL, NULL, 3, '2025-12-23 13:37:11', '2025-12-23 20:37:11.000000', 1, ''),
(3, 24, NULL, NULL, 3, '2025-12-23 13:38:23', '2025-12-23 20:38:23.000000', 3, ''),
(4, 24, NULL, NULL, 3, '2025-12-23 13:43:57', '2025-12-23 20:43:57.000000', 2, ''),
(5, 24, NULL, NULL, 3, '2025-12-23 13:47:03', '2025-12-23 20:47:03.000000', 3, ''),
(6, 24, NULL, NULL, 3, '2025-12-23 13:47:05', '2025-12-23 20:47:05.000000', 3, ''),
(7, 24, NULL, NULL, 3, '2025-12-23 13:53:33', '2025-12-23 20:53:33.000000', 3, ''),
(8, 24, NULL, NULL, 3, '2025-12-23 14:04:36', '2025-12-23 21:04:36.000000', 1, ''),
(9, 25, NULL, NULL, 3, '2025-12-23 14:29:36', '2025-12-23 21:29:36.000000', 1, ''),
(10, 25, NULL, NULL, 3, '2025-12-23 14:30:27', '2025-12-23 21:30:27.000000', 1, ''),
(11, 25, NULL, NULL, 3, '2025-12-23 14:30:57', '2025-12-23 21:30:57.000000', 2, ''),
(12, 25, NULL, NULL, 3, '2025-12-23 14:32:10', '2025-12-23 21:32:10.000000', 2, ''),
(13, 25, NULL, NULL, 3, '2025-12-23 14:37:14', '2025-12-23 21:37:14.000000', 3, ''),
(14, 25, NULL, NULL, 3, '2025-12-23 14:50:25', '2025-12-23 21:50:25.000000', 2, ''),
(15, 25, NULL, NULL, 3, '2025-12-23 14:50:55', '2025-12-23 21:50:55.000000', 1, ''),
(16, 25, NULL, NULL, 3, '2025-12-23 14:51:17', '2025-12-23 21:51:17.000000', 3, ''),
(17, 24, NULL, NULL, 3, '2025-12-23 14:57:43', '2025-12-23 21:57:43.000000', 1, ''),
(18, 24, NULL, NULL, 3, '2025-12-23 14:58:07', '2025-12-23 21:58:07.000000', 2, ''),
(19, 25, NULL, NULL, 3, '2025-12-23 14:58:12', '2025-12-23 21:58:12.000000', 3, ''),
(20, 26, NULL, NULL, 3, '2025-12-23 14:58:25', '2025-12-23 21:58:25.000000', 3, ''),
(21, 25, NULL, NULL, 3, '2025-12-23 15:00:03', '2025-12-23 22:00:03.000000', 1, ''),
(22, 26, NULL, NULL, 3, '2025-12-23 15:00:11', '2025-12-23 22:00:11.000000', 3, ''),
(23, 24, NULL, NULL, 3, '2025-12-23 15:00:17', '2025-12-23 22:00:17.000000', 2, ''),
(24, 25, NULL, NULL, 3, '2025-12-23 15:08:27', '2025-12-23 22:08:27.000000', 1, ''),
(25, 24, NULL, NULL, 3, '2025-12-24 10:54:50', '2025-12-24 17:54:50.000000', 2, ''),
(26, 24, NULL, NULL, 3, '2025-12-24 16:22:36', '2025-12-24 23:22:36.000000', 3, ''),
(27, 24, 'IN_PROGRESS', 'LAB_DONE', NULL, '2025-12-24 16:26:56', '2025-12-24 23:26:56.000000', 4, ''),
(28, 26, 'IN_PROGRESS', 'LAB_DONE', NULL, '2025-12-24 16:27:03', '2025-12-24 23:27:03.000000', 4, ''),
(29, 24, 'IN_PROGRESS', 'COMPLETED', 3, '2025-12-24 16:51:03', '2025-12-24 23:51:03.000000', 4, ''),
(30, 25, 'IN_PROGRESS', 'COMPLETED', 3, '2025-12-24 16:51:29', '2025-12-24 23:51:29.000000', 4, ''),
(31, 40, 'IN_PROGRESS', 'COMPLETED', 3, '2025-12-24 16:52:22', '2025-12-24 23:52:22.000000', 4, ''),
(32, 41, 'IN_PROGRESS', 'COMPLETED', 3, '2025-12-24 16:52:32', '2025-12-24 23:52:32.000000', 4, ''),
(33, 42, 'IN_PROGRESS', 'COMPLETED', 3, '2025-12-24 16:52:39', '2025-12-24 23:52:39.000000', 4, ''),
(34, 42, NULL, NULL, 3, '2025-12-24 17:08:04', '2025-12-25 00:08:04.000000', 3, ''),
(35, 46, NULL, NULL, 3, '2025-12-24 17:50:02', '2025-12-25 00:50:02.000000', 3, '');

-- --------------------------------------------------------

--
-- Table structure for table `services`
--

DROP TABLE IF EXISTS `services`;
CREATE TABLE IF NOT EXISTS `services` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `price` decimal(38,2) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `services`
--

INSERT INTO `services` (`id`, `code`, `name`, `price`, `active`) VALUES
(1, 'CONSULT', 'Khám tư vấn', 150000.00, 1),
(2, 'LAB_BLOOD', 'Xét nghiệm máu', 200000.00, 1),
(3, 'IMG_XRAY', 'Chụp X-quang', 300000.00, 1);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `phone` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `role` enum('PATIENT','DOCTOR','ADMIN') COLLATE utf8mb4_general_ci NOT NULL,
  `status` enum('ACTIVE','LOCKED','DISABLED') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ACTIVE',
  `auto_notification_enabled` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `email`, `phone`, `password_hash`, `role`, `status`, `auto_notification_enabled`, `created_at`, `updated_at`) VALUES
(1, 'admin2', 'admin@hospital.local', '0900000001', '123', 'ADMIN', 'ACTIVE', 1, '2025-10-28 13:49:13', '2025-11-10 14:09:07'),
(2, 'patient01', 'patient01@example.com', '0900000002', '$2y$10$dummypatienthash', 'PATIENT', 'ACTIVE', 1, '2025-10-28 13:49:13', NULL),
(3, 'doctor01', 'doctor01@example.com', '0900000003', '$2a$10$tHlVJ52pmwn4.w38lLsdxe7gq5CE1QLbPoc4wQ4MyZL7SJ/DFuJjS', 'DOCTOR', 'ACTIVE', 1, '2025-10-28 13:49:13', '2025-12-23 13:33:09'),
(4, 'hiep', 'hiepcc@gmail.com', '0123455678', '$2a$10$.W8/7UMy2eH.rb6JfFr7eOX0KX1zPvqti2UGZr.e7/1jke6ESaVA2', 'ADMIN', 'ACTIVE', 1, '2025-11-05 01:26:45', '2025-12-31 10:56:04'),
(7, 'admin02', 'admin01@hospital.local', '0900000001', '$2a$10$AyTR6V27P3iNktYAd7eQqOasv6z0sPwKmpn7WqgRGeQ1CCFzfp1hi', 'ADMIN', 'ACTIVE', 1, '2025-11-05 01:12:15', '2025-11-05 01:12:15'),
(8, 'doctor02', 'doctor01@hospital.local', '0900000002', '$2a$10$bjcn5DKJB7G.nT9.RNWBJeK.qJWZGtaa9FkwpBUk9QN0EsXXLhAhW', 'DOCTOR', 'ACTIVE', 1, '2025-11-05 01:12:15', '2025-12-31 02:36:48'),
(9, 'patient02', 'patient01@hospital.local', '0900000003', '$2a$10$FVZoZ6ojkTVDEwob1xDHiOEHQXFF4hpNKnHzBbhrM6xpjxhbGHvSe', 'PATIENT', 'ACTIVE', 1, '2025-11-05 01:12:15', '2025-11-05 01:12:15'),
(10, 'testuser', 'test@example.com', NULL, '$2a$10$wdCfDVBj7Eo99BnNvgQrROsv/NxwPLJpnc3uNlKfOjfhasI56i/Va', 'PATIENT', 'ACTIVE', 1, '2025-11-05 01:38:47', '2025-11-10 14:14:48'),
(11, 'doctor', 'doctor@example.com', NULL, '$2a$10$FzjNXl39WeWkWz.VmXSl/.3mVBGcaiaNkHKHpwWVQtJC4Bh4YIK/y', 'DOCTOR', 'ACTIVE', 1, '2025-11-10 14:08:14', '2025-11-10 14:08:14'),
(12, 'admin', 'admin@example.com', NULL, '$2a$10$wDeS4SQP2mk/YIV3VDLY5utn0Zv3g2DexRuFy/A0EMwib4iSeekXO', 'ADMIN', 'ACTIVE', 1, '2025-11-10 14:09:13', '2025-11-10 14:09:13'),
(13, 'hiepcc22', '', '', '$2a$10$B3dsGkwu5TQpIgFER4FjOeop4iiIxXZCtRfHUzAVMFi2W8cwzeftS', 'PATIENT', 'ACTIVE', 1, '2025-11-17 02:19:39', '2025-11-18 03:04:59'),
(14, '0532341234', '0532341234@phone.local', '0532341234', '$2a$10$2b.5mg1INMKGwRh5rkJ5je8thDEWSVwJrxfnRsDGsn8QxLHqqbiwW', 'PATIENT', 'ACTIVE', 1, '2025-11-17 09:09:32', '2025-11-17 09:09:32'),
(15, '05323412342', '05323412342@phone.local', '05323412342', '$2a$10$KPZz.NuKoLF6tBTOFxSpCu0rHonlybImvNC79X7VwllNscdPLQSxC', 'PATIENT', 'ACTIVE', 1, '2025-11-17 09:10:52', '2025-11-17 09:10:52'),
(16, 'hiepproo', 'hiepproo@gmail.com', NULL, '$2a$10$1OjMsYPWOHBaIfvRdEmMOONO3c8i871TEY/a.gbR611rQqz6qgrzG', 'PATIENT', 'ACTIVE', 1, '2025-11-17 09:11:51', '2025-11-17 09:11:51'),
(17, '056123456', '056123456@phone.local', '056123456', '$2a$10$2Oa1O0DOkNhmwZ5aoFZlg.T1By8L4HvWtPO5y/CBSJ5CQ13EDOytq', 'PATIENT', 'ACTIVE', 1, '2025-11-17 09:24:07', '2025-11-17 09:24:07'),
(18, '0564082621', '0564082621@phone.local', '0564082621', '$2a$10$HI/79rm1ePW7OT7mFzJ/tuLU2b8nMu8kP.TpvFy5W58JXytCA2CCu', 'PATIENT', 'ACTIVE', 1, '2025-11-17 09:25:59', '2025-11-17 09:25:59'),
(19, 'dangky', 'dangky@gmail.com', '0123123123', '$2a$10$rusw5KFJN8Yit6mSXQflH.bl1fDh.oLaQKt0A5q3lCl211U0/UT/6', 'PATIENT', 'ACTIVE', 1, '2025-11-17 09:30:12', '2025-11-17 09:30:48'),
(20, 'ahiep', 'ahiep@gmail.com', '056123123', '$2a$10$7SjNT7Fm3gUtCCFxdWBpqepkMsCB2w84cfuxTT5QkwuX2HYh4iyLm', 'PATIENT', 'ACTIVE', 1, '2025-11-18 02:18:03', '2025-11-18 02:19:40'),
(21, 'a', 'a@example.com', NULL, '$2a$10$mkOg11RtiaX2h0tOAM4BMO.b1ix0qm2WEaUDDnt4NAMtM35.xPpyW', 'PATIENT', 'ACTIVE', 1, '2025-11-18 12:16:11', '2025-11-18 12:16:11'),
(22, 'a2', 'a2@example.com', NULL, '$2a$10$NvFi5mfzFRZaE2saX1yy8eCq.Td06PT72BM4Ytj9qxoKYJ6zJECb2', 'PATIENT', 'ACTIVE', 1, '2025-11-18 12:28:49', '2025-11-18 12:28:49'),
(23, '0541234211', 'aaa@example.com', '0909123456', '$2a$10$YTnyphaUA.3enUlbKox/0e6kH7UVMtX2RMuqeHPuUfc.jP1L7NWF6', 'PATIENT', 'ACTIVE', 1, '2025-11-18 12:31:36', '2025-11-18 14:38:26'),
(24, 'vanhai12', 'vanhai12@gmail.com', '', '$2a$10$fQgv9Bd3w1AnkrAD.oQtlegu8plQojM3e4by69mdukN8tzcheBr7i', 'PATIENT', 'ACTIVE', 1, '2025-11-30 03:17:50', '2025-12-23 15:07:49'),
(25, 'vanhai1233', 'vanhai1233@gmail.com', NULL, '$2a$10$56JTIj7XGbobVx.FDsMkJerQ5n8QDwhbb/aW2X508FFuWS8giT9xW', 'PATIENT', 'ACTIVE', 1, '2025-11-30 03:59:05', '2025-11-30 03:59:05'),
(26, 'vanhai124', 'vanhai124@gmail.com', '', '$2a$10$ei.nMsWoK/eMP7IXBL9.CehHqKPa4yV5ilfBpZzuWJcxXNuFg/3vO', 'PATIENT', 'ACTIVE', 1, '2025-12-03 02:59:59', '2025-12-24 17:06:33'),
(27, 'vanhai1122', 'vanhai1122@gmail.com', '', '$2a$10$b4cSb4Xv9A9wJdPj9MxRnupwR6ITPesCGeTCgXviRrW4FtJ1U5p5G', 'PATIENT', 'ACTIVE', 1, '2025-12-24 17:19:17', '2025-12-24 17:42:47'),
(28, 'BSNguyenVanA', 'nguyenvana@gmail.com', NULL, '$2a$10$iwjGd2ak59BoZASP16ENF.iOjKpYowuD0HHo45mcAxsL9B./MRmQ6', 'DOCTOR', 'DISABLED', 1, '2026-01-01 02:44:58', '2026-01-01 03:18:57'),
(29, 'lego', 'concac@gmail.com', NULL, '$2a$10$VewTy1rkRhBjm6niXdNxhe/0yrYWVaT00vpd7.9G0RolB6Y5igvSi', 'DOCTOR', 'DISABLED', 1, '2026-01-01 03:20:52', '2026-01-01 03:40:44'),
(30, 'bacsiAsics', 'abs@gmail.com', NULL, '$2a$10$TRuGhcardL2IZsGwNdS4o.jmbtnVuBT.kdH.yzKhfaJjQA8Qra9Ka', 'DOCTOR', 'ACTIVE', 1, '2026-01-01 03:42:43', '2026-01-01 03:42:43'),
(31, 'ngoH', 'danmach@gmail.com', NULL, '$2a$10$xwpZd93O2Qcx1CnoaQ4oOuwj2vznwB8InYEGot8zuLyV7uy71P0D2', 'DOCTOR', 'ACTIVE', 1, '2026-01-01 04:10:22', '2026-01-01 04:10:22'),
(32, 'vanhai', 'vanhai@gmail.com', NULL, '$2a$10$ZYIUZjQfv7Z/bhXqnIM0VOg29xfp331GG.60lGXwOCHeENWyaJrP2', 'DOCTOR', 'DISABLED', 1, '2026-01-01 09:36:05', '2026-01-01 09:36:49'),
(33, 'HarryKane', 'hello@gmail.com', NULL, '$2a$10$6E.SgUPdbQc/oWdN30rAvOZwVX0EInVQUgg703j6LDROUSr8/CXwa', 'DOCTOR', 'ACTIVE', 1, '2026-01-01 10:19:43', '2026-01-01 10:19:43'),
(34, 'doctor29', 'doctor29@mail.com', NULL, '$2a$10$oza.LtGuV8TpN/bH9/KDKOU6QK8Fg1YbFL448ospQ2RU7zmXTTdnq', 'DOCTOR', 'DISABLED', 1, '2026-01-01 13:26:20', '2026-01-01 14:08:50'),
(35, 'marcoreus', 'lego@gmail.com', NULL, '$2a$10$vTdagDBiO6ezMAa8KSM5U.PXfCn4DfDsg2G.HaoY60OFBFHXh0h6G', 'DOCTOR', 'ACTIVE', 1, '2026-01-01 13:35:35', '2026-01-01 13:35:35'),
(36, 'Mitoma', 'mitoma@gmail.com', NULL, '$2a$10$1Qf/FFLe0qnLUcvwr1OSpe6ybjWw3eMPXA9NtxFqWfKmcF/etpHpy', 'DOCTOR', 'DISABLED', 1, '2026-01-02 08:54:24', '2026-01-02 08:55:36'),
(37, 'Benzema', 'benzema@gmail.com', NULL, '$2a$10$NgER5metuBSYis4FXMhDmemvyfIaMk7Rg.WSEt/wCj1P6aJxrQK1m', 'DOCTOR', 'DISABLED', 1, '2026-01-02 08:59:52', '2026-01-02 09:17:25'),
(38, 'CR7', 'cr7@gmail.com', NULL, '$2a$10$t2.CXjSJ4tMOp6pBiqq8QeB7OvI/gZzKy4uh4cZ/9Sh8oxOG/rh9e', 'DOCTOR', 'DISABLED', 1, '2026-01-02 09:01:50', '2026-01-02 09:17:27'),
(39, 'LM10', 'lm10@gmail.com', NULL, '$2a$10$N2DIOLW/GBYfFq6LqLO9cuwjm69Vpj8ZxQrhEDqlCYTKEVuViPB2a', 'DOCTOR', 'ACTIVE', 1, '2026-01-02 09:27:04', '2026-01-02 09:27:04');

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
-- Constraints for table `invoices`
--
ALTER TABLE `invoices`
  ADD CONSTRAINT `fk_invoice_appt` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`),
  ADD CONSTRAINT `fk_invoice_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient_profiles` (`user_id`);

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
  ADD CONSTRAINT `fk_patient_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `process_logs`
--
ALTER TABLE `process_logs`
  ADD CONSTRAINT `fk_log_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FKp6t6p8283f42h4di796uhaych` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
