CREATE DATABASE  IF NOT EXISTS `yidingbao` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `yidingbao`;
-- MySQL dump 10.13  Distrib 5.6.13, for Win32 (x86)
--
-- Host: 127.0.0.1    Database: yidingbao
-- ------------------------------------------------------
-- Server version	5.6.15

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `all_orders`
--

DROP TABLE IF EXISTS `all_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `all_orders` (
  `Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `CustomerId` int(11) DEFAULT NULL COMMENT '买家编号',
  `ProductId` int(11) NOT NULL COMMENT '货品编号',
  `ProductType` varchar(10) NOT NULL COMMENT '货品款别 取值有：必定款，推广款，普通款',
  `ProductSerial` varchar(10) NOT NULL COMMENT '货品系列 取值有：办公，家具，装饰，收藏',
  `ProductName` varchar(20) NOT NULL COMMENT '货品名称',
  `ProductPrice` int(11) NOT NULL COMMENT '货品单价',
  `ProductNumber` int(11) NOT NULL COMMENT '订货的件数',
  `OrderAmount` int(11) NOT NULL COMMENT '订货金额',
  `OrderDetails` varchar(50) NOT NULL COMMENT '订货详情 取值有：花梨色+直径80：30，花梨色+直径90:60',
  PRIMARY KEY (`Id`),
  KEY `IDX_all_orders_ProductType` (`ProductType`),
  KEY `IDX_all_orders_ProductSerial` (`ProductSerial`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='所有买家的订单 店仓提交订单时，由服务器后台执行程序把所有customerid_orders表里数据合并到本表，如果性能不佳，可考虑由二进制文件存储';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `all_orders`
--

LOCK TABLES `all_orders` WRITE;
/*!40000 ALTER TABLE `all_orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `all_orders` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-01-22 12:15:48
