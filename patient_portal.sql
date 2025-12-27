-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1:3306
-- Thời gian đã tạo: Th12 21, 2025 lúc 04:12 AM
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
  `status` varchar(255) NOT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_appt_patient_time` (`patient_id`,`scheduled_at`),
  KEY `idx_appt_doctor_time` (`doctor_id`,`scheduled_at`),
  KEY `fk_appt_service` (`service_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `appointments`
--

INSERT INTO `appointments` (`id`, `patient_id`, `doctor_id`, `service_id`, `scheduled_at`, `status`, `notes`, `created_at`, `updated_at`) VALUES
(1, 13, 3, 1, '2025-01-10 08:30:00', 'COMPLETED', 'Chẩn đoán: Viêm họng cấp. Đơn thuốc: Paracetamol 500mg, Vitamin C 500mg', '2025-11-30 01:50:42', '2025-11-30 01:55:42'),
(2, 13, 3, 2, '2025-02-05 09:00:00', 'COMPLETED', 'Chẩn đoán: Nghi ngờ thiếu máu nhẹ. Đơn thuốc: Sắt 325mg, B12.', '2025-11-30 01:50:42', '2025-11-30 01:55:42'),
(3, 13, 3, 3, '2025-03-15 15:45:00', 'COMPLETED', 'Chẩn đoán: Không phát hiện tổn thương. Chỉ định nghỉ ngơi.', '2025-11-30 01:50:42', '2025-11-30 01:55:42'),
(4, 13, 3, 1, '2025-11-20 09:00:00', 'COMPLETED', 'Đau đầu, chóng mặt - Chẩn đoán: Thiếu máu nhẹ. Kê Paracetamol 500mg x 3 lần/ngày.', '2025-11-30 02:12:49', NULL),
(6, 13, 3, NULL, '2025-12-16 09:51:16', 'COMPLETED', 'Khám tổng quát', '2025-12-19 02:51:16', NULL),
(8, 13, 11, NULL, '2025-12-16 10:11:30', 'COMPLETED', 'Khám tổng quát', '2025-12-19 03:11:30', NULL),
(10, 18, 11, NULL, '2025-12-16 10:15:04', 'COMPLETED', 'Khám tổng quát', '2025-12-19 03:15:04', NULL);

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
  `status` varchar(255) NOT NULL,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_appt_stage` (`appointment_id`,`stage_order`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `care_flow_stages`
--

INSERT INTO `care_flow_stages` (`id`, `appointment_id`, `stage_order`, `stage_name`, `status`, `updated_at`) VALUES
(1, 1, 1, 'Tiếp nhận', 'DONE', '2025-11-30 01:51:24'),
(2, 1, 2, 'Khám lâm sàng', 'DONE', '2025-11-30 01:51:24'),
(3, 1, 3, 'Xét nghiệm', 'DONE', '2025-11-30 01:51:24'),
(4, 1, 4, 'Trả kết quả', 'DONE', '2025-11-30 01:51:24'),
(5, 2, 1, 'Tiếp nhận', 'DONE', '2025-11-30 01:51:24'),
(6, 2, 2, 'Xét nghiệm máu', 'DONE', '2025-11-30 01:51:24'),
(7, 2, 3, 'Trả kết quả', 'DONE', '2025-11-30 01:51:24'),
(8, 3, 1, 'Tiếp nhận', 'DONE', '2025-11-30 01:51:24'),
(9, 3, 2, 'Chụp X-quang', 'DONE', '2025-11-30 01:51:24'),
(10, 3, 3, 'Trả kết quả', 'DONE', '2025-11-30 01:51:24');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `doctor_profiles`
--

DROP TABLE IF EXISTS `doctor_profiles`;
CREATE TABLE IF NOT EXISTS `doctor_profiles` (
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `specialty` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `license_no` varchar(255) DEFAULT NULL,
  `bio` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `doctor_profiles`
--

INSERT INTO `doctor_profiles` (`user_id`, `full_name`, `specialty`, `department`, `license_no`, `bio`) VALUES
(3, 'BS. Lê Văn C', 'Nội tổng quát', 'Khám tổng quát', NULL, NULL),
(11, 'ASDASDSA', 'DSADSADS', 'DSADSADSA', 'DASDSA', 'DSADSA');

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
  `title` varchar(255) NOT NULL,
  `file_path` varchar(255) NOT NULL,
  `mime_type` varchar(255) DEFAULT NULL,
  `created_by` bigint(20) UNSIGNED NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_doc_patient` (`patient_id`),
  KEY `fk_doc_appt` (`appointment_id`),
  KEY `fk_doc_creator` (`created_by`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `documents`
--

INSERT INTO `documents` (`id`, `patient_id`, `appointment_id`, `doc_type`, `title`, `file_path`, `mime_type`, `created_by`, `created_at`) VALUES
(1, 13, 1, 'LAB', 'Kết quả xét nghiệm máu - lần 1', '/uploads/lab_1.pdf', 'application/pdf', 3, '2025-11-30 01:51:06'),
(2, 13, 2, 'LAB', 'Kết quả xét nghiệm máu - lần 2', '/uploads/lab_2.pdf', 'application/pdf', 3, '2025-11-30 01:51:06'),
(3, 13, 3, 'IMAGING', 'Ảnh X-quang phổi', '/uploads/xray_1.pdf', 'application/pdf', 3, '2025-11-30 01:51:06'),
(4, 13, 1, 'LAB', 'Kết quả xét nghiệm máu - 20/11/2025', '/uploads/invoice-0002.pdf', 'application/pdf', 3, '2025-11-30 02:12:57'),
(5, 13, 1, 'INVOICE', 'Hóa đơn INV-20250110-0002', '/uploads/invoice-0002.pdf', 'application/pdf', 3, '2025-12-07 02:44:18'),
(8, 13, 1, 'INVOICE', 'Hóa đơn INV-20250110-0002', '/uploads/invoice-0002.pdf', 'application/pdf', 3, '2025-12-07 02:46:19');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `invoices`
--

DROP TABLE IF EXISTS `invoices`;
CREATE TABLE IF NOT EXISTS `invoices` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `patient_id` bigint(20) UNSIGNED NOT NULL,
  `appointment_id` bigint(20) UNSIGNED DEFAULT NULL,
  `invoice_no` varchar(255) NOT NULL,
  `issue_date` date NOT NULL,
  `items_json` json NOT NULL,
  `total_amount` decimal(38,2) NOT NULL,
  `status` enum('UNPAID','PAID','VOID') NOT NULL DEFAULT 'UNPAID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `invoice_no` (`invoice_no`),
  KEY `fk_invoice_patient` (`patient_id`),
  KEY `fk_invoice_appt` (`appointment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `invoices`
--

INSERT INTO `invoices` (`id`, `patient_id`, `appointment_id`, `invoice_no`, `issue_date`, `items_json`, `total_amount`, `status`, `created_at`) VALUES
(1, 13, 1, 'INV-20251120-0003', '2025-12-01', '[{\"qty\": 1, \"code\": \"CONSULT\", \"name\": \"Khám tư vấn\", \"price\": 150000}, {\"qty\": 1, \"code\": \"LAB_BLOOD\", \"name\": \"Xét nghiệm máu\", \"price\": 600000}]', '750000.00', 'PAID', '2025-12-13 03:05:06'),
(2, 13, 2, 'INV-20251120-0004', '2025-12-01', '[{\"qty\": 1, \"code\": \"CONSULT\", \"name\": \"Khám tư vấn\", \"price\": 150000}, {\"qty\": 1, \"code\": \"LAB_BLOOD\", \"name\": \"Xét nghiệm máu\", \"price\": 600000}]', '750000.00', 'PAID', '2025-12-13 03:05:06'),
(3, 13, 4, 'INV-20251120-0001', '2025-11-20', '[{\"qty\": 1, \"code\": \"CONSULT\", \"name\": \"Khám tư vấn\", \"price\": 150000}, {\"qty\": 1, \"code\": \"LAB_BLOOD\", \"name\": \"Xét nghiệm máu\", \"price\": 200000}]', '350000.00', 'UNPAID', '2025-12-07 02:44:18'),
(4, 13, 1, 'INV-20250110-0002', '2025-01-10', '[{\"qty\": 1, \"code\": \"CONSULT\", \"name\": \"Khám tư vấn\", \"price\": 150000}, {\"qty\": 1, \"code\": \"IMG_XRAY\", \"name\": \"Chụp X-quang\", \"price\": 300000}]', '450000.00', 'PAID', '2025-12-07 02:44:18');

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
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `messages`
--

INSERT INTO `messages` (`id`, `patient_id`, `doctor_id`, `sender_user_id`, `content`, `sent_at`, `read_at`) VALUES
(2, 13, 3, 1, 'Chào bác sĩ, em thấy hơi đau đầu sau khi khám.', '2025-12-19 02:41:35', NULL),
(3, 13, 3, 2, 'Chào bạn, tình trạng đau đầu kéo dài bao lâu rồi?', '2025-12-19 02:43:49', NULL),
(4, 13, 3, 1, 'Khoảng 2 ngày nay ạ, thỉnh thoảng hơi chóng mặt.', '2025-12-19 02:47:02', NULL),
(5, 13, 3, 13, 'hello', '2025-12-18 19:55:35', NULL),
(6, 13, 3, 3, 'Chào em co việc gì ko?', '2025-12-18 19:58:24', NULL),
(7, 13, 3, 3, 'em thấy đau đầu', '2025-12-18 19:59:06', NULL),
(8, 13, 3, 13, 'em thấy đau đầu', '2025-12-18 19:59:25', NULL),
(9, 13, 3, 3, 'kệ cmmm', '2025-12-18 19:59:34', NULL),
(10, 13, 11, 13, 'Hello', '2025-12-18 20:11:42', NULL),
(11, 13, 11, 11, 'CC', '2025-12-18 20:12:55', NULL),
(12, 13, 11, 11, 'CMM BS', '2025-12-18 20:13:01', NULL),
(13, 13, 11, 11, 'M THICH GI', '2025-12-18 20:13:12', NULL),
(14, 13, 11, 13, 'CMM', '2025-12-18 20:13:15', NULL),
(15, 13, 3, 13, 'c,,,', '2025-12-18 20:15:32', NULL),
(16, 18, 11, 18, 'CON CHO', '2025-12-18 20:15:53', NULL);

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
) ENGINE=MyISAM AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `password_reset_tokens`
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
(15, '682ef01d-6b7b-4ff6-b55e-0a579fe03528', 4, '2025-12-03 07:49:57', 0),
(16, 'c0613222-ddbe-4eed-8c86-4d85685f4087', 3, '2025-12-19 10:11:40', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `patient_profiles`
--

DROP TABLE IF EXISTS `patient_profiles`;
CREATE TABLE IF NOT EXISTS `patient_profiles` (
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `date_of_birth` date DEFAULT NULL,
  `sex` enum('M','F','O') DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `insurance_number` varchar(255) DEFAULT NULL,
  `emergency_contact_name` varchar(255) DEFAULT NULL,
  `emergency_contact_phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `patient_profiles`
--

INSERT INTO `patient_profiles` (`user_id`, `full_name`, `date_of_birth`, `sex`, `address`, `insurance_number`, `emergency_contact_name`, `emergency_contact_phone`) VALUES
(2, 'Nguyễn Văn A', '1995-05-10', 'M', NULL, NULL, NULL, NULL),
(13, 'PHAM GIA HIEP22', NULL, NULL, '', '', '', ''),
(17, 'ANH HIEP', NULL, NULL, NULL, NULL, NULL, NULL),
(18, 'ANH HIEP1', '2004-07-05', NULL, 'phu yen', '12312412412', 'ANH ', 'HAI'),
(19, 'DANG KY', '2004-07-05', NULL, 'PHU YEN', '12312312', 'GIA HIEP', '12321312'),
(20, 'PHAM GIA HIEP21', '2004-07-05', NULL, 'PHU YEN', '', 'GIA HIEPA', '123123123'),
(21, 'Nguyen Van A', NULL, NULL, NULL, NULL, NULL, NULL),
(22, 'Nguyen Van A', NULL, NULL, NULL, NULL, NULL, NULL),
(23, 'Nguyen Van A', '2000-01-01', NULL, '123 Duong ABC, Quan 1, TP.HCM', 'BHYT123456789', 'Nguyen Van B', '0909123456');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `process_logs`
--

DROP TABLE IF EXISTS `process_logs`;
CREATE TABLE IF NOT EXISTS `process_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `appointment_id` bigint(20) NOT NULL,
  `new_status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `old_status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `stage_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `updated_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `services`
--

DROP TABLE IF EXISTS `services`;
CREATE TABLE IF NOT EXISTS `services` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `price` decimal(38,2) NOT NULL,
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
-- Cấu trúc bảng cho bảng `students`
--

DROP TABLE IF EXISTS `students`;
CREATE TABLE IF NOT EXISTS `students` (
  `id` int(11) NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`id`, `username`, `email`, `phone`, `password_hash`, `role`, `status`, `created_at`, `updated_at`) VALUES
(1, 'admin2', 'admin@hospital.local', '0900000001', '123', 'ADMIN', 'ACTIVE', '2025-10-28 13:49:13', '2025-11-10 14:09:07'),
(2, 'patient01', 'patient01@example.com', '0900000002', '$2y$10$dummypatienthash', 'PATIENT', 'ACTIVE', '2025-10-28 13:49:13', NULL),
(3, 'doctor01', 'doctor01@example.com', '0900000003', '$2a$10$ZlwAENL6nWv5..Ja8tmKs.YQPsHkPJ4uzZdnWmsJW7mgqoW/3zGgi', 'DOCTOR', 'ACTIVE', '2025-10-28 13:49:13', '2025-12-19 02:57:17'),
(4, 'hiep', 'hiepcc@gmail.com', '0123455678', '$2a$10$nF5RV.q4AKk1OBq.6972CuzCfWfQ69VIsoDMgY5nFmGUhkf0Oamq6', 'ADMIN', 'ACTIVE', '2025-11-05 01:26:45', '2025-11-17 09:32:03'),
(7, 'admin02', 'admin01@hospital.local', '0900000001', '$2a$10$AyTR6V27P3iNktYAd7eQqOasv6z0sPwKmpn7WqgRGeQ1CCFzfp1hi', 'ADMIN', 'ACTIVE', '2025-11-05 01:12:15', '2025-11-05 01:12:15'),
(8, 'doctor02', 'doctor01@hospital.local', '0900000002', '$2a$10$uM0yI6QCF/tHykvGOxl6.ekfVr4sd3e6nS0XUJvvfIag0eO52nTO', 'DOCTOR', 'ACTIVE', '2025-11-05 01:12:15', '2025-11-05 01:12:15'),
(9, 'patient02', 'patient01@hospital.local', '0900000003', '$2a$10$FVZoZ6ojkTVDEwob1xDHiOEHQXFF4hpNKnHzBbhrM6xpjxhbGHvSe', 'PATIENT', 'ACTIVE', '2025-11-05 01:12:15', '2025-11-05 01:12:15'),
(10, 'testuser', 'test@example.com', NULL, '$2a$10$wdCfDVBj7Eo99BnNvgQrROsv/NxwPLJpnc3uNlKfOjfhasI56i/Va', 'PATIENT', 'ACTIVE', '2025-11-05 01:38:47', '2025-11-10 14:14:48'),
(11, 'doctor', 'doctor@example.com', NULL, '$2a$10$FzjNXl39WeWkWz.VmXSl/.3mVBGcaiaNkHKHpwWVQtJC4Bh4YIK/y', 'DOCTOR', 'ACTIVE', '2025-11-10 14:08:14', '2025-11-10 14:08:14'),
(12, 'admin', 'admin@example.com', NULL, '$2a$10$wDeS4SQP2mk/YIV3VDLY5utn0Zv3g2DexRuFy/A0EMwib4iSeekXO', 'ADMIN', 'ACTIVE', '2025-11-10 14:09:13', '2025-11-10 14:09:13'),
(13, 'hiepcc22', '', '', '$2a$10$B3dsGkwu5TQpIgFER4FjOeop4iiIxXZCtRfHUzAVMFi2W8cwzeftS', 'PATIENT', 'ACTIVE', '2025-11-17 02:19:39', '2025-11-18 03:04:59'),
(14, '0532341234', '0532341234@phone.local', '0532341234', '$2a$10$2b.5mg1INMKGwRh5rkJ5je8thDEWSVwJrxfnRsDGsn8QxLHqqbiwW', 'PATIENT', 'ACTIVE', '2025-11-17 09:09:32', '2025-11-17 09:09:32'),
(15, '05323412342', '05323412342@phone.local', '05323412342', '$2a$10$KPZz.NuKoLF6tBTOFxSpCu0rHonlybImvNC79X7VwllNscdPLQSxC', 'PATIENT', 'ACTIVE', '2025-11-17 09:10:52', '2025-11-17 09:10:52'),
(16, 'hiepproo', 'hiepproo@gmail.com', NULL, '$2a$10$1OjMsYPWOHBaIfvRdEmMOONO3c8i871TEY/a.gbR611rQqz6qgrzG', 'PATIENT', 'ACTIVE', '2025-11-17 09:11:51', '2025-11-17 09:11:51'),
(17, '056123456', '056123456@phone.local', '056123456', '$2a$10$2Oa1O0DOkNhmwZ5aoFZlg.T1By8L4HvWtPO5y/CBSJ5CQ13EDOytq', 'PATIENT', 'ACTIVE', '2025-11-17 09:24:07', '2025-11-17 09:24:07'),
(18, '0564082621', '0564082621@phone.local', '0564082621', '$2a$10$HI/79rm1ePW7OT7mFzJ/tuLU2b8nMu8kP.TpvFy5W58JXytCA2CCu', 'PATIENT', 'ACTIVE', '2025-11-17 09:25:59', '2025-11-17 09:25:59'),
(19, 'dangky', 'dangky@gmail.com', '0123123123', '$2a$10$rusw5KFJN8Yit6mSXQflH.bl1fDh.oLaQKt0A5q3lCl211U0/UT/6', 'PATIENT', 'ACTIVE', '2025-11-17 09:30:12', '2025-11-17 09:30:48'),
(20, 'ahiep', 'ahiep@gmail.com', '056123123', '$2a$10$7SjNT7Fm3gUtCCFxdWBpqepkMsCB2w84cfuxTT5QkwuX2HYh4iyLm', 'PATIENT', 'ACTIVE', '2025-11-18 02:18:03', '2025-11-18 02:19:40'),
(21, 'a', 'a@example.com', NULL, '$2a$10$mkOg11RtiaX2h0tOAM4BMO.b1ix0qm2WEaUDDnt4NAMtM35.xPpyW', 'PATIENT', 'ACTIVE', '2025-11-18 12:16:11', '2025-11-18 12:16:11'),
(22, 'a2', 'a2@example.com', NULL, '$2a$10$NvFi5mfzFRZaE2saX1yy8eCq.Td06PT72BM4Ytj9qxoKYJ6zJECb2', 'PATIENT', 'ACTIVE', '2025-11-18 12:28:49', '2025-11-18 12:28:49'),
(23, '0541234211', 'aaa@example.com', '0909123456', '$2a$10$YTnyphaUA.3enUlbKox/0e6kH7UVMtX2RMuqeHPuUfc.jP1L7NWF6', 'PATIENT', 'ACTIVE', '2025-11-18 12:31:36', '2025-11-18 14:38:26');

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
