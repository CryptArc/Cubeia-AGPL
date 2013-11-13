--
-- Dumping data for table `blindsstructure`
--

LOCK TABLES `blindsstructure` WRITE;
/*!40000 ALTER TABLE `blindsstructure` DISABLE KEYS */;
INSERT INTO `blindsstructure` VALUES (1,'Quick');
/*!40000 ALTER TABLE `blindsstructure` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `level`
--

LOCK TABLES `level` WRITE;
/*!40000 ALTER TABLE `level` DISABLE KEYS */;
INSERT INTO `level` VALUES (1,NULL,20.00,2,0,10.00),(2,NULL,200.00,2,0,100.00),(3,NULL,2000.00,2,0,1000.00),(4,NULL,20000.00,2,0,10000.00),(5,NULL,200000.00,2,0,100000.00);
/*!40000 ALTER TABLE `level` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `blindsstructure_level`
--

LOCK TABLES `blindsstructure_level` WRITE;
/*!40000 ALTER TABLE `blindsstructure_level` DISABLE KEYS */;
INSERT INTO `blindsstructure_level` VALUES (1,1,0),(1,2,1),(1,3,2),(1,4,3),(1,5,4);
/*!40000 ALTER TABLE `blindsstructure_level` ENABLE KEYS */;
UNLOCK TABLES;

