-- MySQL dump 10.13  Distrib 5.7.31, for Linux (x86_64)
--
-- Host: localhost    Database: springboot673i609q
-- ------------------------------------------------------
-- Server version	5.7.31

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
-- Current Database: `springboot673i609q`
--

/*!40000 DROP DATABASE IF EXISTS `springboot673i609q`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `springboot673i609q` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `springboot673i609q`;

--
-- Table structure for table `cheweixinxi`
--

DROP TABLE IF EXISTS `cheweixinxi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cheweixinxi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tingchechangmingcheng` varchar(200) DEFAULT NULL COMMENT '停车场名称',
  `quyu` varchar(200) DEFAULT NULL COMMENT '区域',
  `cheweitupian` longtext COMMENT '车位图片',
  `cheweishuliang` int(11) DEFAULT NULL COMMENT '车位数量',
  `xiaoshidanjia` int(11) DEFAULT NULL COMMENT '小时单价',
  `cheweixiangqing` longtext COMMENT '车位详情',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 COMMENT='车位信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cheweixinxi`
--

LOCK TABLES `cheweixinxi` WRITE;
/*!40000 ALTER TABLE `cheweixinxi` DISABLE KEYS */;
INSERT INTO `cheweixinxi` VALUES (21,'2026-04-23 15:13:06','停车场名称1','A区','upload/cheweixinxi_cheweitupian1.jpg,upload/cheweixinxi_cheweitupian2.jpg,upload/cheweixinxi_cheweitupian3.jpg',1,1,'车位详情1'),(22,'2026-04-23 15:13:06','停车场名称2','A区','upload/cheweixinxi_cheweitupian2.jpg,upload/cheweixinxi_cheweitupian3.jpg,upload/cheweixinxi_cheweitupian4.jpg',2,2,'车位详情2'),(23,'2026-04-23 15:13:06','停车场名称3','A区','upload/cheweixinxi_cheweitupian3.jpg,upload/cheweixinxi_cheweitupian4.jpg,upload/cheweixinxi_cheweitupian5.jpg',3,3,'车位详情3'),(24,'2026-04-23 15:13:06','停车场名称4','A区','upload/cheweixinxi_cheweitupian4.jpg,upload/cheweixinxi_cheweitupian5.jpg,upload/cheweixinxi_cheweitupian6.jpg',4,4,'车位详情4'),(25,'2026-04-23 15:13:06','停车场名称5','A区','upload/cheweixinxi_cheweitupian5.jpg,upload/cheweixinxi_cheweitupian6.jpg,upload/cheweixinxi_cheweitupian7.jpg',5,5,'车位详情5'),(26,'2026-04-23 15:13:06','停车场名称6','A区','upload/cheweixinxi_cheweitupian6.jpg,upload/cheweixinxi_cheweitupian7.jpg,upload/cheweixinxi_cheweitupian8.jpg',6,6,'车位详情6'),(27,'2026-04-23 15:13:06','停车场名称7','A区','upload/cheweixinxi_cheweitupian7.jpg,upload/cheweixinxi_cheweitupian8.jpg,upload/cheweixinxi_cheweitupian9.jpg',7,7,'车位详情7'),(28,'2026-04-23 15:13:06','停车场名称8','A区','upload/cheweixinxi_cheweitupian8.jpg,upload/cheweixinxi_cheweitupian9.jpg,upload/cheweixinxi_cheweitupian10.jpg',8,8,'车位详情8');
/*!40000 ALTER TABLE `cheweixinxi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chezijinchang`
--

DROP TABLE IF EXISTS `chezijinchang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chezijinchang` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tingchechangmingcheng` varchar(200) DEFAULT NULL COMMENT '停车场名称',
  `quyu` varchar(200) DEFAULT NULL COMMENT '区域',
  `cheweishuliang` int(11) NOT NULL COMMENT '车位数量',
  `xiaoshidanjia` int(11) DEFAULT NULL COMMENT '小时单价',
  `yonghuzhanghao` varchar(200) DEFAULT NULL COMMENT '用户账号',
  `xingming` varchar(200) DEFAULT NULL COMMENT '姓名',
  `shouji` varchar(200) DEFAULT NULL COMMENT '手机',
  `chepaihao` varchar(200) DEFAULT NULL COMMENT '车牌号',
  `cheliangtupian` longtext COMMENT '车辆图片',
  `jinchangshijian` datetime DEFAULT NULL COMMENT '进场时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8 COMMENT='车子进场';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chezijinchang`
--

LOCK TABLES `chezijinchang` WRITE;
/*!40000 ALTER TABLE `chezijinchang` DISABLE KEYS */;
INSERT INTO `chezijinchang` VALUES (31,'2026-04-23 15:13:06','停车场名称1','区域1',1,1,'用户账号1','姓名1','手机1','车牌号1','upload/chezijinchang_cheliangtupian1.jpg,upload/chezijinchang_cheliangtupian2.jpg,upload/chezijinchang_cheliangtupian3.jpg','2026-04-23 23:13:06'),(32,'2026-04-23 15:13:06','停车场名称2','区域2',1,2,'用户账号2','姓名2','手机2','车牌号2','upload/chezijinchang_cheliangtupian2.jpg,upload/chezijinchang_cheliangtupian3.jpg,upload/chezijinchang_cheliangtupian4.jpg','2026-04-23 23:13:06'),(33,'2026-04-23 15:13:06','停车场名称3','区域3',1,3,'用户账号3','姓名3','手机3','车牌号3','upload/chezijinchang_cheliangtupian3.jpg,upload/chezijinchang_cheliangtupian4.jpg,upload/chezijinchang_cheliangtupian5.jpg','2026-04-23 23:13:06'),(34,'2026-04-23 15:13:06','停车场名称4','区域4',1,4,'用户账号4','姓名4','手机4','车牌号4','upload/chezijinchang_cheliangtupian4.jpg,upload/chezijinchang_cheliangtupian5.jpg,upload/chezijinchang_cheliangtupian6.jpg','2026-04-23 23:13:06'),(35,'2026-04-23 15:13:06','停车场名称5','区域5',1,5,'用户账号5','姓名5','手机5','车牌号5','upload/chezijinchang_cheliangtupian5.jpg,upload/chezijinchang_cheliangtupian6.jpg,upload/chezijinchang_cheliangtupian7.jpg','2026-04-23 23:13:06'),(36,'2026-04-23 15:13:06','停车场名称6','区域6',1,6,'用户账号6','姓名6','手机6','车牌号6','upload/chezijinchang_cheliangtupian6.jpg,upload/chezijinchang_cheliangtupian7.jpg,upload/chezijinchang_cheliangtupian8.jpg','2026-04-23 23:13:06'),(37,'2026-04-23 15:13:06','停车场名称7','区域7',1,7,'用户账号7','姓名7','手机7','车牌号7','upload/chezijinchang_cheliangtupian7.jpg,upload/chezijinchang_cheliangtupian8.jpg,upload/chezijinchang_cheliangtupian9.jpg','2026-04-23 23:13:06'),(38,'2026-04-23 15:13:06','停车场名称8','区域8',1,8,'用户账号8','姓名8','手机8','车牌号8','upload/chezijinchang_cheliangtupian8.jpg,upload/chezijinchang_cheliangtupian9.jpg,upload/chezijinchang_cheliangtupian10.jpg','2026-04-23 23:13:06');
/*!40000 ALTER TABLE `chezijinchang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config`
--

DROP TABLE IF EXISTS `config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '配置参数名称',
  `value` varchar(100) DEFAULT NULL COMMENT '配置参数值',
  `url` varchar(500) DEFAULT NULL COMMENT 'url',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='配置文件';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config`
--

LOCK TABLES `config` WRITE;
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
INSERT INTO `config` VALUES (1,'picture1','upload/picture1.jpg',NULL),(2,'picture2','upload/picture2.jpg',NULL),(3,'picture3','upload/picture3.jpg',NULL);
/*!40000 ALTER TABLE `config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `messages` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `userid` bigint(20) NOT NULL COMMENT '留言人id',
  `username` varchar(200) DEFAULT NULL COMMENT '用户名',
  `avatarurl` longtext COMMENT '头像',
  `content` longtext NOT NULL COMMENT '留言内容',
  `cpicture` longtext COMMENT '留言图片',
  `reply` longtext COMMENT '回复内容',
  `rpicture` longtext COMMENT '回复图片',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8 COMMENT='用户反馈';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
INSERT INTO `messages` VALUES (51,'2026-04-23 15:13:06',1,'用户名1','upload/messages_avatarurl1.jpg','留言内容1','upload/messages_cpicture1.jpg','回复内容1','upload/messages_rpicture1.jpg'),(52,'2026-04-23 15:13:06',2,'用户名2','upload/messages_avatarurl2.jpg','留言内容2','upload/messages_cpicture2.jpg','回复内容2','upload/messages_rpicture2.jpg'),(53,'2026-04-23 15:13:06',3,'用户名3','upload/messages_avatarurl3.jpg','留言内容3','upload/messages_cpicture3.jpg','回复内容3','upload/messages_rpicture3.jpg'),(54,'2026-04-23 15:13:06',4,'用户名4','upload/messages_avatarurl4.jpg','留言内容4','upload/messages_cpicture4.jpg','回复内容4','upload/messages_rpicture4.jpg'),(55,'2026-04-23 15:13:06',5,'用户名5','upload/messages_avatarurl5.jpg','留言内容5','upload/messages_cpicture5.jpg','回复内容5','upload/messages_rpicture5.jpg'),(56,'2026-04-23 15:13:06',6,'用户名6','upload/messages_avatarurl6.jpg','留言内容6','upload/messages_cpicture6.jpg','回复内容6','upload/messages_rpicture6.jpg'),(57,'2026-04-23 15:13:06',7,'用户名7','upload/messages_avatarurl7.jpg','留言内容7','upload/messages_cpicture7.jpg','回复内容7','upload/messages_rpicture7.jpg'),(58,'2026-04-23 15:13:06',8,'用户名8','upload/messages_avatarurl8.jpg','留言内容8','upload/messages_cpicture8.jpg','回复内容8','upload/messages_rpicture8.jpg');
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tingchejiaofei`
--

DROP TABLE IF EXISTS `tingchejiaofei`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tingchejiaofei` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dingdanhao` varchar(200) DEFAULT NULL COMMENT '订单号',
  `tingchechangmingcheng` varchar(200) DEFAULT NULL COMMENT '停车场名称',
  `quyu` varchar(200) DEFAULT NULL COMMENT '区域',
  `xiaoshidanjia` int(11) DEFAULT NULL COMMENT '小时单价',
  `yonghuzhanghao` varchar(200) DEFAULT NULL COMMENT '用户账号',
  `xingming` varchar(200) DEFAULT NULL COMMENT '姓名',
  `shouji` varchar(200) DEFAULT NULL COMMENT '手机',
  `chepaihao` varchar(200) DEFAULT NULL COMMENT '车牌号',
  `cheliangtupian` longtext COMMENT '车辆图片',
  `jinchangshijian` datetime DEFAULT NULL COMMENT '进场时间',
  `lichangshijian` datetime DEFAULT NULL COMMENT '离场时间',
  `bencitingcheshizhang` double DEFAULT NULL COMMENT '本次停车时长',
  `bencitingchefeiyong` double DEFAULT NULL COMMENT '停车费用',
  `crossuserid` bigint(20) DEFAULT NULL COMMENT '跨表用户id',
  `crossrefid` bigint(20) DEFAULT NULL COMMENT '跨表主键id',
  `ispay` varchar(200) DEFAULT '未支付' COMMENT '是否支付',
  PRIMARY KEY (`id`),
  UNIQUE KEY `dingdanhao` (`dingdanhao`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8 COMMENT='停车缴费';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tingchejiaofei`
--

LOCK TABLES `tingchejiaofei` WRITE;
/*!40000 ALTER TABLE `tingchejiaofei` DISABLE KEYS */;
INSERT INTO `tingchejiaofei` VALUES (41,'2026-04-23 15:13:06','1111111111','停车场名称1','区域1',1,'用户账号1','姓名1','手机1','车牌号1','upload/tingchejiaofei_cheliangtupian1.jpg,upload/tingchejiaofei_cheliangtupian2.jpg,upload/tingchejiaofei_cheliangtupian3.jpg','2026-04-23 23:13:06','2026-04-23 23:13:06',1,1,1,1,'未支付'),(42,'2026-04-23 15:13:06','2222222222','停车场名称2','区域2',2,'用户账号2','姓名2','手机2','车牌号2','upload/tingchejiaofei_cheliangtupian2.jpg,upload/tingchejiaofei_cheliangtupian3.jpg,upload/tingchejiaofei_cheliangtupian4.jpg','2026-04-23 23:13:06','2026-04-23 23:13:06',2,2,2,2,'未支付'),(43,'2026-04-23 15:13:06','3333333333','停车场名称3','区域3',3,'用户账号3','姓名3','手机3','车牌号3','upload/tingchejiaofei_cheliangtupian3.jpg,upload/tingchejiaofei_cheliangtupian4.jpg,upload/tingchejiaofei_cheliangtupian5.jpg','2026-04-23 23:13:06','2026-04-23 23:13:06',3,3,3,3,'未支付'),(44,'2026-04-23 15:13:06','4444444444','停车场名称4','区域4',4,'用户账号4','姓名4','手机4','车牌号4','upload/tingchejiaofei_cheliangtupian4.jpg,upload/tingchejiaofei_cheliangtupian5.jpg,upload/tingchejiaofei_cheliangtupian6.jpg','2026-04-23 23:13:06','2026-04-23 23:13:06',4,4,4,4,'未支付'),(45,'2026-04-23 15:13:06','5555555555','停车场名称5','区域5',5,'用户账号5','姓名5','手机5','车牌号5','upload/tingchejiaofei_cheliangtupian5.jpg,upload/tingchejiaofei_cheliangtupian6.jpg,upload/tingchejiaofei_cheliangtupian7.jpg','2026-04-23 23:13:06','2026-04-23 23:13:06',5,5,5,5,'未支付'),(46,'2026-04-23 15:13:06','6666666666','停车场名称6','区域6',6,'用户账号6','姓名6','手机6','车牌号6','upload/tingchejiaofei_cheliangtupian6.jpg,upload/tingchejiaofei_cheliangtupian7.jpg,upload/tingchejiaofei_cheliangtupian8.jpg','2026-04-23 23:13:06','2026-04-23 23:13:06',6,6,6,6,'未支付'),(47,'2026-04-23 15:13:06','7777777777','停车场名称7','区域7',7,'用户账号7','姓名7','手机7','车牌号7','upload/tingchejiaofei_cheliangtupian7.jpg,upload/tingchejiaofei_cheliangtupian8.jpg,upload/tingchejiaofei_cheliangtupian9.jpg','2026-04-23 23:13:06','2026-04-23 23:13:06',7,7,7,7,'未支付'),(48,'2026-04-23 15:13:06','8888888888','停车场名称8','区域8',8,'用户账号8','姓名8','手机8','车牌号8','upload/tingchejiaofei_cheliangtupian8.jpg,upload/tingchejiaofei_cheliangtupian9.jpg,upload/tingchejiaofei_cheliangtupian10.jpg','2026-04-23 23:13:06','2026-04-23 23:13:06',8,8,8,8,'未支付');
/*!40000 ALTER TABLE `tingchejiaofei` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `token`
--

DROP TABLE IF EXISTS `token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userid` bigint(20) NOT NULL COMMENT '用户id',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `tablename` varchar(100) DEFAULT NULL COMMENT '表名',
  `role` varchar(100) DEFAULT NULL COMMENT '角色',
  `token` varchar(200) NOT NULL COMMENT '密码',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `expiratedtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='token表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token`
--

LOCK TABLES `token` WRITE;
/*!40000 ALTER TABLE `token` DISABLE KEYS */;
INSERT INTO `token` VALUES (1,1,'admin','users','管理员','3bu8kc72316o4nue6bbp2aocewuf53bl','2026-04-23 15:15:56','2026-04-23 16:15:57');
/*!40000 ALTER TABLE `token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `image` varchar(200) DEFAULT NULL COMMENT '头像',
  `role` varchar(100) DEFAULT '管理员' COMMENT '角色',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin','upload/image1.jpg','管理员','2026-04-23 15:13:06');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `yonghu`
--

DROP TABLE IF EXISTS `yonghu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yonghu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yonghuzhanghao` varchar(200) NOT NULL COMMENT '用户账号',
  `xingming` varchar(200) NOT NULL COMMENT '姓名',
  `mima` varchar(200) NOT NULL COMMENT '密码',
  `xingbie` varchar(200) DEFAULT NULL COMMENT '性别',
  `shouji` varchar(200) DEFAULT NULL COMMENT '手机',
  `chepaihao` varchar(200) DEFAULT NULL COMMENT '车牌号',
  `touxiang` longtext COMMENT '头像',
  PRIMARY KEY (`id`),
  UNIQUE KEY `yonghuzhanghao` (`yonghuzhanghao`),
  UNIQUE KEY `chepaihao` (`chepaihao`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COMMENT='用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `yonghu`
--

LOCK TABLES `yonghu` WRITE;
/*!40000 ALTER TABLE `yonghu` DISABLE KEYS */;
INSERT INTO `yonghu` VALUES (11,'2026-04-23 15:13:06','用户账号1','姓名1','123456','男','13823888881','车牌号1','upload/yonghu_touxiang1.jpg'),(12,'2026-04-23 15:13:06','用户账号2','姓名2','123456','男','13823888882','车牌号2','upload/yonghu_touxiang2.jpg'),(13,'2026-04-23 15:13:06','用户账号3','姓名3','123456','男','13823888883','车牌号3','upload/yonghu_touxiang3.jpg'),(14,'2026-04-23 15:13:06','用户账号4','姓名4','123456','男','13823888884','车牌号4','upload/yonghu_touxiang4.jpg'),(15,'2026-04-23 15:13:06','用户账号5','姓名5','123456','男','13823888885','车牌号5','upload/yonghu_touxiang5.jpg'),(16,'2026-04-23 15:13:06','用户账号6','姓名6','123456','男','13823888886','车牌号6','upload/yonghu_touxiang6.jpg'),(17,'2026-04-23 15:13:06','用户账号7','姓名7','123456','男','13823888887','车牌号7','upload/yonghu_touxiang7.jpg'),(18,'2026-04-23 15:13:06','用户账号8','姓名8','123456','男','13823888888','车牌号8','upload/yonghu_touxiang8.jpg');
/*!40000 ALTER TABLE `yonghu` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-04-24 15:27:08
