-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: joke_server
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `system_jokes`
--

DROP TABLE IF EXISTS `system_jokes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_jokes` (
  `system_joke_id` int NOT NULL AUTO_INCREMENT,
  `joke_text` text NOT NULL,
  `generation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `source` varchar(100) NOT NULL DEFAULT 'system' COMMENT 'Source of the joke (system, AI, etc.)',
  `status` enum('pending','approved','rejected') NOT NULL DEFAULT 'pending',
  `approved_by` int DEFAULT NULL,
  `approval_date` datetime DEFAULT NULL,
  `joke_id` int DEFAULT NULL COMMENT 'If approved, references the jokes table',
  PRIMARY KEY (`system_joke_id`),
  KEY `idx_system_jokes_status` (`status`),
  KEY `idx_system_jokes_joke` (`joke_id`),
  KEY `idx_approved_by` (`approved_by`),
  CONSTRAINT `fk_system_jokes_approved_by` FOREIGN KEY (`approved_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `fk_system_jokes_joke` FOREIGN KEY (`joke_id`) REFERENCES `jokes` (`joke_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_jokes`
--

LOCK TABLES `system_jokes` WRITE;
/*!40000 ALTER TABLE `system_jokes` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_jokes` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-08  9:23:01
