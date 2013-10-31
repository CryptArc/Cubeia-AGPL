
--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,NULL,'2013-10-15 04:29:04','EUR',2,NULL,NULL,1,0,'SYSTEM_ACCOUNT',-1000,NULL),(2,NULL,'2013-10-15 04:29:22','EUR',2,NULL,NULL,1,0,'SYSTEM_ACCOUNT',-2000,NULL),(3,NULL,'2013-10-15 04:29:36','EUR',2,NULL,NULL,1,0,'SYSTEM_ACCOUNT',-3000,NULL);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `accountattribute`
--

LOCK TABLES `accountattribute` WRITE;
/*!40000 ALTER TABLE `accountattribute` DISABLE KEYS */;
INSERT INTO `accountattribute` VALUES (1,'objectId',NULL,1),(2,'gameId',NULL,1),(3,'gameName','Rake',1),(4,'objectId',NULL,2),(5,'gameId',NULL,2),(6,'gameName','Promotions',2),(7,'objectId',NULL,3),(8,'gameId',NULL,3),(9,'gameName','General',3);
/*!40000 ALTER TABLE `accountattribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `supportedcurrency`
--

LOCK TABLES `supportedcurrency` WRITE;
/*!40000 ALTER TABLE `supportedcurrency` DISABLE KEYS */;
INSERT INTO `supportedcurrency` VALUES ('EUR',2,0);
/*!40000 ALTER TABLE `supportedcurrency` ENABLE KEYS */;
UNLOCK TABLES;
