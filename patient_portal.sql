-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th12 23, 2025 lúc 04:14 PM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

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

CREATE TABLE `announcements` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `level` enum('NEWS','URGENT') NOT NULL DEFAULT 'NEWS',
  `title` varchar(191) NOT NULL,
  `content` text NOT NULL,
  `author_user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `published_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `announcements`
--

INSERT INTO `announcements` (`id`, `level`, `title`, `content`, `author_user_id`, `published_at`) VALUES
(1, 'NEWS', 'Thông báo bảo trì', 'Hệ thống bảo trì lúc 22:00', 1, '2025-10-28 13:49:13');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `appointments`
--

CREATE TABLE `appointments` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `patient_id` bigint(20) UNSIGNED NOT NULL,
  `doctor_id` bigint(20) UNSIGNED NOT NULL,
  `service_id` bigint(20) UNSIGNED DEFAULT NULL,
  `scheduled_at` datetime NOT NULL,
  `status` varchar(255) NOT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp(),
  `current_stage_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `appointments`
--

INSERT INTO `appointments` (`id`, `patient_id`, `doctor_id`, `service_id`, `scheduled_at`, `status`, `notes`, `created_at`, `updated_at`, `current_stage_id`) VALUES
(24, 24, 3, NULL, '2025-12-23 20:27:20', 'IN_PROGRESS', 'Khám tổng quát – BN 24', '2025-12-23 13:27:20', '2025-12-23 14:58:07', 2),
(25, 25, 3, NULL, '2025-12-23 20:27:20', 'WAITING', 'Chờ xét nghiệm – BN 25', '2025-12-23 13:27:20', '2025-12-23 15:00:03', 1),
(26, 26, 3, NULL, '2025-12-23 20:27:20', 'WAITING', 'Chờ khám – BN 26', '2025-12-23 13:27:20', '2025-12-23 14:58:25', 3);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `care_flow_stages`
--

CREATE TABLE `care_flow_stages` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `stage_order` int(11) NOT NULL,
  `stage_name` varchar(128) NOT NULL,
  `status` varchar(255) NOT NULL,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `appointment_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `care_flow_stages`
--

INSERT INTO `care_flow_stages` (`id`, `stage_order`, `stage_name`, `status`, `updated_at`, `appointment_id`) VALUES
(1, 1, 'Đang khám', '', '2025-12-23 13:17:12', 0),
(2, 2, 'Chờ xét nghiệm', '', '2025-12-23 13:17:12', 0),
(3, 3, 'Hoàn tất', '', '2025-12-23 13:17:12', 0);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `doctor_profiles`
--

CREATE TABLE `doctor_profiles` (
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `full_name` varchar(191) NOT NULL,
  `specialty` varchar(128) DEFAULT NULL,
  `department` varchar(128) DEFAULT NULL,
  `license_no` varchar(64) DEFAULT NULL,
  `bio` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `doctor_profiles`
--

INSERT INTO `doctor_profiles` (`user_id`, `full_name`, `specialty`, `department`, `license_no`, `bio`) VALUES
(3, 'BS. Lê Văn C', 'Nội tổng quát', 'Khám tổng quát', NULL, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `documents`
--

CREATE TABLE `documents` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `patient_id` bigint(20) UNSIGNED NOT NULL,
  `appointment_id` bigint(20) UNSIGNED DEFAULT NULL,
  `doc_type` enum('LAB','IMAGING','INVOICE','OTHER') NOT NULL,
  `title` varchar(191) NOT NULL,
  `file_path` varchar(255) NOT NULL,
  `mime_type` varchar(64) DEFAULT NULL,
  `created_by` bigint(20) UNSIGNED NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `invoices`
--

CREATE TABLE `invoices` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `patient_id` bigint(20) UNSIGNED NOT NULL,
  `appointment_id` bigint(20) UNSIGNED DEFAULT NULL,
  `invoice_no` varchar(32) NOT NULL,
  `issue_date` date NOT NULL,
  `items_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`items_json`)),
  `total_amount` decimal(12,2) NOT NULL,
  `status` enum('UNPAID','PAID','VOID') NOT NULL DEFAULT 'UNPAID',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `messages`
--

CREATE TABLE `messages` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `patient_id` bigint(20) UNSIGNED NOT NULL,
  `doctor_id` bigint(20) UNSIGNED NOT NULL,
  `sender_user_id` bigint(20) UNSIGNED NOT NULL,
  `content` text NOT NULL,
  `sent_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `read_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `notifications`
--

CREATE TABLE `notifications` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `body` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `related_type` varchar(255) DEFAULT NULL,
  `related_id` bigint(20) UNSIGNED DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `patient_id` bigint(20) DEFAULT NULL,
  `read_flag` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `notifications`
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
(17, 24, NULL, 'Kết quả xét nghiệm', 'Có kết quả xét nghiệm máu mới.', 'READ', '2025-11-30 12:04:21', 'DOCUMENT', NULL, NULL, NULL, b'0');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `password_reset_tokens`
--

CREATE TABLE `password_reset_tokens` (
  `id` bigint(20) NOT NULL,
  `token` varchar(191) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `expiry_date` datetime NOT NULL,
  `used` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
(15, '25c5109f-5254-407f-8eee-b4c60fe8ed2b', 3, '2025-12-23 20:47:49', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `patient_profiles`
--

CREATE TABLE `patient_profiles` (
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `date_of_birth` date DEFAULT NULL,
  `sex` enum('M','F','O') DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `insurance_number` varchar(255) DEFAULT NULL,
  `emergency_contact_name` varchar(255) DEFAULT NULL,
  `emergency_contact_phone` varchar(255) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `patient_profiles`
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
(26, 'vanhai1', NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `process_logs`
--

CREATE TABLE `process_logs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `appointment_id` bigint(20) UNSIGNED NOT NULL,
  `old_status` varchar(255) DEFAULT NULL,
  `new_status` varchar(255) DEFAULT NULL,
  `updated_by` bigint(20) UNSIGNED DEFAULT NULL,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `created_at` datetime(6) DEFAULT NULL,
  `stage_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `process_logs`
--

INSERT INTO `process_logs` (`id`, `appointment_id`, `old_status`, `new_status`, `updated_by`, `updated_at`, `created_at`, `stage_id`) VALUES
(1, 24, NULL, NULL, 3, '2025-12-23 13:36:01', '2025-12-23 20:36:01.000000', 1),
(2, 24, NULL, NULL, 3, '2025-12-23 13:37:11', '2025-12-23 20:37:11.000000', 1),
(3, 24, NULL, NULL, 3, '2025-12-23 13:38:23', '2025-12-23 20:38:23.000000', 3),
(4, 24, NULL, NULL, 3, '2025-12-23 13:43:57', '2025-12-23 20:43:57.000000', 2),
(5, 24, NULL, NULL, 3, '2025-12-23 13:47:03', '2025-12-23 20:47:03.000000', 3),
(6, 24, NULL, NULL, 3, '2025-12-23 13:47:05', '2025-12-23 20:47:05.000000', 3),
(7, 24, NULL, NULL, 3, '2025-12-23 13:53:33', '2025-12-23 20:53:33.000000', 3),
(8, 24, NULL, NULL, 3, '2025-12-23 14:04:36', '2025-12-23 21:04:36.000000', 1),
(9, 25, NULL, NULL, 3, '2025-12-23 14:29:36', '2025-12-23 21:29:36.000000', 1),
(10, 25, NULL, NULL, 3, '2025-12-23 14:30:27', '2025-12-23 21:30:27.000000', 1),
(11, 25, NULL, NULL, 3, '2025-12-23 14:30:57', '2025-12-23 21:30:57.000000', 2),
(12, 25, NULL, NULL, 3, '2025-12-23 14:32:10', '2025-12-23 21:32:10.000000', 2),
(13, 25, NULL, NULL, 3, '2025-12-23 14:37:14', '2025-12-23 21:37:14.000000', 3),
(14, 25, NULL, NULL, 3, '2025-12-23 14:50:25', '2025-12-23 21:50:25.000000', 2),
(15, 25, NULL, NULL, 3, '2025-12-23 14:50:55', '2025-12-23 21:50:55.000000', 1),
(16, 25, NULL, NULL, 3, '2025-12-23 14:51:17', '2025-12-23 21:51:17.000000', 3),
(17, 24, NULL, NULL, 3, '2025-12-23 14:57:43', '2025-12-23 21:57:43.000000', 1),
(18, 24, NULL, NULL, 3, '2025-12-23 14:58:07', '2025-12-23 21:58:07.000000', 2),
(19, 25, NULL, NULL, 3, '2025-12-23 14:58:12', '2025-12-23 21:58:12.000000', 3),
(20, 26, NULL, NULL, 3, '2025-12-23 14:58:25', '2025-12-23 21:58:25.000000', 3),
(21, 25, NULL, NULL, 3, '2025-12-23 15:00:03', '2025-12-23 22:00:03.000000', 1),
(22, 26, NULL, NULL, 3, '2025-12-23 15:00:11', '2025-12-23 22:00:11.000000', 3),
(23, 24, NULL, NULL, 3, '2025-12-23 15:00:17', '2025-12-23 22:00:17.000000', 2),
(24, 25, NULL, NULL, 3, '2025-12-23 15:08:27', '2025-12-23 22:08:27.000000', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `services`
--

CREATE TABLE `services` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `code` varchar(32) NOT NULL,
  `name` varchar(191) NOT NULL,
  `price` decimal(12,2) NOT NULL DEFAULT 0.00,
  `active` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `services`
--

INSERT INTO `services` (`id`, `code`, `name`, `price`, `active`) VALUES
(1, 'CONSULT', 'Khám tư vấn', 150000.00, 1),
(2, 'LAB_BLOOD', 'Xét nghiệm máu', 200000.00, 1),
(3, 'IMG_XRAY', 'Chụp X-quang', 300000.00, 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

CREATE TABLE `users` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `username` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('PATIENT','DOCTOR','ADMIN') NOT NULL,
  `status` enum('ACTIVE','LOCKED','DISABLED') NOT NULL DEFAULT 'ACTIVE',
  `auto_notification_enabled` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`id`, `username`, `email`, `phone`, `password_hash`, `role`, `status`, `auto_notification_enabled`, `created_at`, `updated_at`) VALUES
(1, 'admin2', 'admin@hospital.local', '0900000001', '123', 'ADMIN', 'ACTIVE', 1, '2025-10-28 13:49:13', '2025-11-10 14:09:07'),
(2, 'patient01', 'patient01@example.com', '0900000002', '$2y$10$dummypatienthash', 'PATIENT', 'ACTIVE', 1, '2025-10-28 13:49:13', NULL),
(3, 'doctor01', 'doctor01@example.com', '0900000003', '$2a$10$tHlVJ52pmwn4.w38lLsdxe7gq5CE1QLbPoc4wQ4MyZL7SJ/DFuJjS', 'DOCTOR', 'ACTIVE', 1, '2025-10-28 13:49:13', '2025-12-23 13:33:09'),
(4, 'hiep', 'hiepcc@gmail.com', '0123455678', '$2a$10$nF5RV.q4AKk1OBq.6972CuzCfWfQ69VIsoDMgY5nFmGUhkf0Oamq6', 'ADMIN', 'ACTIVE', 1, '2025-11-05 01:26:45', '2025-11-17 09:32:03'),
(7, 'admin02', 'admin01@hospital.local', '0900000001', '$2a$10$AyTR6V27P3iNktYAd7eQqOasv6z0sPwKmpn7WqgRGeQ1CCFzfp1hi', 'ADMIN', 'ACTIVE', 1, '2025-11-05 01:12:15', '2025-11-05 01:12:15'),
(8, 'doctor02', 'doctor01@hospital.local', '0900000002', '$2a$10$uM0yI6QCF/tHykvGOxl6.ekfVr4sd3e6nS0XUJvvfIag0eO52nTO', 'DOCTOR', 'ACTIVE', 1, '2025-11-05 01:12:15', '2025-11-05 01:12:15'),
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
(26, 'vanhai124', 'vanhai124@gmail.com', NULL, '$2a$10$ei.nMsWoK/eMP7IXBL9.CehHqKPa4yV5ilfBpZzuWJcxXNuFg/3vO', 'PATIENT', 'ACTIVE', 1, '2025-12-03 02:59:59', '2025-12-03 02:59:59');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `announcements`
--
ALTER TABLE `announcements`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_announce_author` (`author_user_id`);

--
-- Chỉ mục cho bảng `appointments`
--
ALTER TABLE `appointments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_appt_patient_time` (`patient_id`,`scheduled_at`),
  ADD KEY `idx_appt_doctor_time` (`doctor_id`,`scheduled_at`),
  ADD KEY `fk_appt_service` (`service_id`);

--
-- Chỉ mục cho bảng `care_flow_stages`
--
ALTER TABLE `care_flow_stages`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `doctor_profiles`
--
ALTER TABLE `doctor_profiles`
  ADD PRIMARY KEY (`user_id`);

--
-- Chỉ mục cho bảng `documents`
--
ALTER TABLE `documents`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_doc_patient` (`patient_id`),
  ADD KEY `fk_doc_appt` (`appointment_id`),
  ADD KEY `fk_doc_creator` (`created_by`);

--
-- Chỉ mục cho bảng `invoices`
--
ALTER TABLE `invoices`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `invoice_no` (`invoice_no`),
  ADD KEY `fk_invoice_patient` (`patient_id`),
  ADD KEY `fk_invoice_appt` (`appointment_id`);

--
-- Chỉ mục cho bảng `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_msg_pair_time` (`patient_id`,`doctor_id`,`sent_at`),
  ADD KEY `fk_msg_doctor` (`doctor_id`),
  ADD KEY `fk_msg_sender` (`sender_user_id`);

--
-- Chỉ mục cho bảng `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_notif_user_time` (`user_id`,`created_at`);

--
-- Chỉ mục cho bảng `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `token` (`token`),
  ADD KEY `fk_prt_user` (`user_id`);

--
-- Chỉ mục cho bảng `patient_profiles`
--
ALTER TABLE `patient_profiles`
  ADD PRIMARY KEY (`user_id`);

--
-- Chỉ mục cho bảng `process_logs`
--
ALTER TABLE `process_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_log_appointment` (`appointment_id`),
  ADD KEY `FKp6t6p8283f42h4di796uhaych` (`updated_by`);

--
-- Chỉ mục cho bảng `services`
--
ALTER TABLE `services`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`);

--
-- Chỉ mục cho bảng `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `announcements`
--
ALTER TABLE `announcements`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT cho bảng `appointments`
--
ALTER TABLE `appointments`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=41;

--
-- AUTO_INCREMENT cho bảng `care_flow_stages`
--
ALTER TABLE `care_flow_stages`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `documents`
--
ALTER TABLE `documents`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT cho bảng `invoices`
--
ALTER TABLE `invoices`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT cho bảng `messages`
--
ALTER TABLE `messages`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT cho bảng `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT cho bảng `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT cho bảng `process_logs`
--
ALTER TABLE `process_logs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT cho bảng `services`
--
ALTER TABLE `services`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

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

--
-- Các ràng buộc cho bảng `process_logs`
--
ALTER TABLE `process_logs`
  ADD CONSTRAINT `FKp6t6p8283f42h4di796uhaych` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `fk_log_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
