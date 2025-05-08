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
-- Table structure for table `user_actions`
--

DROP TABLE IF EXISTS `user_actions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_actions` (
  `action_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `action_type` enum('view','like','dislike','create','edit','delete','report','share','follow','block','system_generated') NOT NULL,
  `action_details` text,
  `action_timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `target_id` int DEFAULT NULL COMMENT 'ID of the target (joke_id, user_id, etc.) related to this action',
  `target_type` varchar(50) DEFAULT NULL COMMENT 'Type of target (joke, user, category, etc.)',
  `duration` int DEFAULT NULL COMMENT 'Duration of action in seconds (for views)',
  `platform` varchar(50) DEFAULT NULL COMMENT 'Platform or device where action was taken',
  PRIMARY KEY (`action_id`),
  KEY `user_id` (`user_id`),
  KEY `idx_user_actions_action_type` (`action_type`),
  KEY `idx_user_actions_target` (`target_id`,`target_type`),
  CONSTRAINT `user_actions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_actions`
--

LOCK TABLES `user_actions` WRITE;
/*!40000 ALTER TABLE `user_actions` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_actions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-08  9:22:59
