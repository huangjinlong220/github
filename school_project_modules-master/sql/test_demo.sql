/*
 Navicat Premium Dump SQL

 Source Server         : 13306
 Source Server Type    : MySQL
 Source Server Version : 90300 (9.3.0)
 Source Host           : localhost:13306
 Source Schema         : test_demo

 Target Server Type    : MySQL
 Target Server Version : 90300 (9.3.0)
 File Encoding         : 65001

 Date: 17/04/2026 11:35:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

create database if not exists `test_demo`;

use `test_demo`;

-- ----------------------------
-- Table structure for QRTZ_BLOB_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
CREATE TABLE `QRTZ_BLOB_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `BLOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `SCHED_NAME`(`SCHED_NAME` ASC, `TRIGGER_NAME` ASC, `TRIGGER_GROUP` ASC) USING BTREE,
  CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of QRTZ_BLOB_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_CALENDARS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
CREATE TABLE `QRTZ_CALENDARS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `CALENDAR_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `CALENDAR_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of QRTZ_CALENDARS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_CRON_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
CREATE TABLE `QRTZ_CRON_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `CRON_EXPRESSION` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TIME_ZONE_ID` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of QRTZ_CRON_TRIGGERS
-- ----------------------------
INSERT INTO `QRTZ_CRON_TRIGGERS` VALUES ('quartzScheduler', 'imgTrigger', 'imgGroup', '0 0 1 * * ?', 'Asia/Shanghai');

-- ----------------------------
-- Table structure for QRTZ_FIRED_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
CREATE TABLE `QRTZ_FIRED_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `ENTRY_ID` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `INSTANCE_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `FIRED_TIME` bigint NOT NULL,
  `SCHED_TIME` bigint NOT NULL,
  `PRIORITY` int NOT NULL,
  `STATE` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `ENTRY_ID`) USING BTREE,
  INDEX `IDX_QRTZ_FT_TRIG_INST_NAME`(`SCHED_NAME` ASC, `INSTANCE_NAME` ASC) USING BTREE,
  INDEX `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY`(`SCHED_NAME` ASC, `INSTANCE_NAME` ASC, `REQUESTS_RECOVERY` ASC) USING BTREE,
  INDEX `IDX_QRTZ_FT_J_G`(`SCHED_NAME` ASC, `JOB_NAME` ASC, `JOB_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_FT_JG`(`SCHED_NAME` ASC, `JOB_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_FT_T_G`(`SCHED_NAME` ASC, `TRIGGER_NAME` ASC, `TRIGGER_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_FT_TG`(`SCHED_NAME` ASC, `TRIGGER_GROUP` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of QRTZ_FIRED_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_JOB_DETAILS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
CREATE TABLE `QRTZ_JOB_DETAILS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `DESCRIPTION` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `IS_DURABLE` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `IS_UPDATE_DATA` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `JOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_J_REQ_RECOVERY`(`SCHED_NAME` ASC, `REQUESTS_RECOVERY` ASC) USING BTREE,
  INDEX `IDX_QRTZ_J_GRP`(`SCHED_NAME` ASC, `JOB_GROUP` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of QRTZ_JOB_DETAILS
-- ----------------------------
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('quartzScheduler', 'imgJob', 'imgGroup', NULL, 'com.example.quartz.ImgQuartzJob', '1', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787000737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F40000000000010770800000010000000007800);

-- ----------------------------
-- Table structure for QRTZ_LOCKS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_LOCKS`;
CREATE TABLE `QRTZ_LOCKS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `LOCK_NAME` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `LOCK_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of QRTZ_LOCKS
-- ----------------------------
INSERT INTO `QRTZ_LOCKS` VALUES ('quartzScheduler', 'TRIGGER_ACCESS');

-- ----------------------------
-- Table structure for QRTZ_PAUSED_TRIGGER_GRPS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of QRTZ_PAUSED_TRIGGER_GRPS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_SCHEDULER_STATE
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
CREATE TABLE `QRTZ_SCHEDULER_STATE`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `INSTANCE_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `LAST_CHECKIN_TIME` bigint NOT NULL,
  `CHECKIN_INTERVAL` bigint NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `INSTANCE_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of QRTZ_SCHEDULER_STATE
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_SIMPLE_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `REPEAT_COUNT` bigint NOT NULL,
  `REPEAT_INTERVAL` bigint NOT NULL,
  `TIMES_TRIGGERED` bigint NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of QRTZ_SIMPLE_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_SIMPROP_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPROP_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `STR_PROP_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `STR_PROP_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `STR_PROP_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `INT_PROP_1` int NULL DEFAULT NULL,
  `INT_PROP_2` int NULL DEFAULT NULL,
  `LONG_PROP_1` bigint NULL DEFAULT NULL,
  `LONG_PROP_2` bigint NULL DEFAULT NULL,
  `DEC_PROP_1` decimal(13, 4) NULL DEFAULT NULL,
  `DEC_PROP_2` decimal(13, 4) NULL DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `QRTZ_SIMPROP_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of QRTZ_SIMPROP_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
CREATE TABLE `QRTZ_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `DESCRIPTION` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint NULL DEFAULT NULL,
  `PREV_FIRE_TIME` bigint NULL DEFAULT NULL,
  `PRIORITY` int NULL DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_TYPE` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `START_TIME` bigint NOT NULL,
  `END_TIME` bigint NULL DEFAULT NULL,
  `CALENDAR_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `MISFIRE_INSTR` smallint NULL DEFAULT NULL,
  `JOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_T_J`(`SCHED_NAME` ASC, `JOB_NAME` ASC, `JOB_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_JG`(`SCHED_NAME` ASC, `JOB_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_C`(`SCHED_NAME` ASC, `CALENDAR_NAME` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_G`(`SCHED_NAME` ASC, `TRIGGER_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_STATE`(`SCHED_NAME` ASC, `TRIGGER_STATE` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_N_STATE`(`SCHED_NAME` ASC, `TRIGGER_NAME` ASC, `TRIGGER_GROUP` ASC, `TRIGGER_STATE` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_N_G_STATE`(`SCHED_NAME` ASC, `TRIGGER_GROUP` ASC, `TRIGGER_STATE` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_NEXT_FIRE_TIME`(`SCHED_NAME` ASC, `NEXT_FIRE_TIME` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_ST`(`SCHED_NAME` ASC, `TRIGGER_STATE` ASC, `NEXT_FIRE_TIME` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_MISFIRE`(`SCHED_NAME` ASC, `MISFIRE_INSTR` ASC, `NEXT_FIRE_TIME` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_ST_MISFIRE`(`SCHED_NAME` ASC, `MISFIRE_INSTR` ASC, `NEXT_FIRE_TIME` ASC, `TRIGGER_STATE` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP`(`SCHED_NAME` ASC, `MISFIRE_INSTR` ASC, `NEXT_FIRE_TIME` ASC, `TRIGGER_GROUP` ASC, `TRIGGER_STATE` ASC) USING BTREE,
  CONSTRAINT `QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `QRTZ_JOB_DETAILS` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of QRTZ_TRIGGERS
-- ----------------------------
INSERT INTO `QRTZ_TRIGGERS` VALUES ('quartzScheduler', 'imgTrigger', 'imgGroup', 'imgJob', 'imgGroup', NULL, 1776445200000, -1, 5, 'WAITING', 'CRON', 1776394355000, 0, NULL, 0, '');

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book`  (
  `id` bigint NOT NULL COMMENT '主键',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_croatian_ci NULL DEFAULT NULL COMMENT '标题',
  `author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_croatian_ci NULL DEFAULT NULL COMMENT '作者',
  `introduction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_croatian_ci NULL DEFAULT NULL COMMENT '简介',
  `date` timestamp NULL DEFAULT NULL COMMENT '日期',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '分类',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_croatian_ci NOT NULL COMMENT '封面图片',
  `submit_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '保存时间',
  `is_banner` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否轮播图（0:否 1:是）',
  `is_recommend` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否推荐（0:否 1:是）',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序权重（数字越大越靠前）',
  `submit_user` bigint NULL DEFAULT NULL COMMENT '上传人ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_is_banner`(`is_banner` ASC) USING BTREE,
  INDEX `idx_is_recommend`(`is_recommend` ASC) USING BTREE,
  INDEX `idx_view_count`(`view_count` ASC) USING BTREE,
  INDEX `idx_submit_time`(`submit_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_croatian_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of book
-- ----------------------------
INSERT INTO `book` VALUES (2037426379865337858, 'Java程序设计教程', '丁新民主编', '21世纪计算机应用技术系列规划教材:本书结合软件设计语言的发展趋势，就Java语言的应用与面向对象的程序设计，详尽介绍传统结构化语言与面向对象语言的区别，由浅入深地叙述C、C++语言的基本结构，从而引出Java面向对象程序设计思想。', '2006-02-10 00:00:00', 1, '23dcf50a-4429-46b7-b84b-47a525e3b755_CoverNew (9).jpg', '2026-03-27 15:07:57', 1, 1, 1, 0, NULL);
INSERT INTO `book` VALUES (2037426938026536962, 'Java语言实用教程', '常亮', '高等院校规划教材 计算机基础教育系列:本书共12章，详细介绍了Java开发环境、语法知识、数组、类、对象、继承、接口等面向对象程序设计和开发的知识及应用，同时包括异常处理、输入输出方法、图形图像、多媒体、Applet等方面的内容。', '2007-07-12 00:00:00', 1, 'c1895575-c50a-4118-8c4a-e97e90f4195a_CoverNew.jpg', '2026-03-27 15:10:10', 0, 0, 0, 0, NULL);
INSERT INTO `book` VALUES (2037427310765944834, 'JAVA程序设计指导', '汪学明', '21世纪高等学校本科系列教材 计算机科学与技术专业(37):本书介绍JAVA程序设计的基础知识，JAVA高级应用程序设计方法与技巧，还提供了两个典型的综合应用程序设计实例。', '2001-09-01 00:00:00', 1, '74f36963-2c7d-498e-b9b6-0717a407bcbf_CoverNew (1).jpg', '2026-03-27 15:11:39', 1, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037427818104762369, 'C语言程序设计', '顾治华', '本书的写作融入了作者多年的教学经验，充分考虑到初学者的能力、认知水平、知识结构等因素，遵照循序渐进、由浅入深的原则，较系统地介绍了C语言程序设计知识。内容涵盖算法及算法设计、数据描述与基本操作、选择结构程序设计、循环结构程序设计、数组、指针、函数与模块化程…… ...', '2012-05-05 00:00:00', 1, '61e4b433-e48e-4a5f-9075-d8fc4a5f4c17_CoverNew (2).jpg', '2026-03-27 15:13:40', 1, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037428326940946433, '网页设计与制作', '李锐主编', 'Z高等学校教材:本书内容包括：现代科技信息检索的基本知识、原理和方法，国内外常用检索工具和参考工具书的编排结构和使用方法，联机信息检索、光盘信息检索和网络信息检索系统及其方法和技巧。原则。', '2004-06-10 00:00:00', 1, 'b2741356-28a0-4883-9cd3-fa8de8bd2edf_CoverNew (3).jpg', '2026-03-27 15:15:41', 1, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037428647108947969, '多媒体技术原理及应用', '沈吉锋', 'Z高等学校教材:本书内容包括：现代科技信息检索的基本知识、原理和方法，国内外常用检索工具和参考工具书的编排结构和使用方法，联机信息检索、光盘信息检索和网络信息检索系统及其方法和技巧。原则。', '2020-05-11 00:00:00', 1, 'd1248a34-3270-49f5-a371-4e84ceaba69b_CoverNew (4).jpg', '2026-03-27 15:16:57', 1, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037428976328257537, '动态网页设计教程', '陈立山', '本书详细讲述了使用ASP进行网络程序设计的应用设计。是网络程序设计初学者的入门指南，适合大中专院校网络程序设计的课程作为教材使用。', '2007-06-23 00:00:00', 1, 'c80d4ee1-ba01-43e5-b2fd-303a442906b9_CoverNew (5).jpg', '2026-03-27 15:18:16', 1, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037429844406579201, '可编程ASIC设计及应用', '李广军', '本书从系统级设计和系统集成芯片(SOC)设计技术的角度介绍可编程专用集成电路(ASIC)器件的结构和可编程资源等。', '2000-10-01 00:00:00', 1, '816c50a2-53ed-4274-8820-b1580e500695_CoverNew (6).jpg', '2026-03-27 15:21:43', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037430179858624513, '计算机硬件技术基础教程', '李阳明编著', '燕山大学课程建设基金资助项目:本书讲述了微型计算机的组成和多媒体计算机的组装，指令系统与汇编语言，输入与输出系统，中断控制，常见接口电路的应用等内容。', '2003-02-10 00:00:00', 1, '51766f74-1409-400e-ad89-8e4a96d17e10_CoverNew (7).jpg', '2026-03-27 15:23:03', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037430560638513153, 'Flash MX 程序员手册', '左仁贵', '本书主要针对Flash的编程应用如游戏、特效等方面进行细致的实例讲解，将常用的编程技术及关键语句的运用融入各个实例之中，让读者进行实例练习的同时在编程技术上可以得到较大的提高。同时提供了Flash与数据库交互，相信对于同为Flash和Asp爱好者的读者会是一个惊喜。最后附最新Flash MX...', '2002-08-26 00:00:00', 1, '65794c5b-9be5-4a0b-840c-fd01aa5b6733_CoverNew (8).jpg', '2026-03-27 15:24:33', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037431045722353666, '论思想力', '孙长松', '本书分“积极思维”、“多思敢想”、“学会思考”、“自信心能移山”、“突破界限”等61个专题，全面剖析了思想力这个论题。', '2004-12-01 00:00:00', 2, 'b797c616-6fcd-4c9f-9776-0e8977d67ff0_CoverNew.jpg', '2026-03-27 15:26:29', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037431307987988482, '父亲的影响力', '张良科编著', '本书以国内外30位名人的父亲对其成长的影响为题材，通过其教子理念及方法，展示了许多成功者的父亲是如何教育下一代的。旨在唤起父亲在家庭教育中的责任和意识。', '2004-10-07 00:00:00', 2, '3c8983c7-8e0a-4052-9271-de33bab7be1d_CoverNew (10).jpg', '2026-03-27 15:27:32', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037431531225624577, '巴黎，巴黎', '[爱尔兰]摩尔著', '本书是乔治·摩尔的一部逼视内心和社会的自传性作品，主要是以他本人在巴黎和爱尔兰的学艺经历为线索，回忆了其本人与巴黎各类性格迥异、艺术观千姿百态的艺术家的交往，并涉及巴黎艺术界的风风雨雨，逸事典故。', '2010-12-09 00:00:00', 2, 'bcbdd0c1-b535-4d03-a07c-762df1a8a97e_CoverNew (9).jpg', '2026-03-27 15:28:25', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037431785140400129, '明清小说名著导读 （修订本）', '陈文新', '本书是关于《三国演义》、《水浒传》、《西游记》、《金瓶梅》、《三言》、《二拍》、《聊斋志异》、《隋唐演义》、《儒林外史》、《红楼梦》、《阅微草堂笔记》等11部明清小说名著的导读。作者学识丰富，眼光敏锐，其导读不仅触及学术界长期关注的重要问题，而且以其清新的文笔，富有个...', '2008-10-28 00:00:00', 2, '529c8b2f-28a3-4423-9adb-e643dc5f4fea_CoverNew (8).jpg', '2026-03-27 15:29:25', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037432049025036289, '知识与道德的纠葛', '符杰祥著', '全书以鲁迅与五四启蒙精神为基本出发点，按照历史时序选择不同时期的思潮运动与文学现象，通过考察现代中国的文学运动来反思知识分子的道路选择与命运问题，通过考察鲁迅在当代中国的境遇来反思我们时代的思想状况与知识界的精神演变。', '2009-03-10 00:00:00', 2, 'fe5f1846-c50f-4098-84ab-b4ff82b34ff4_CoverNew (7).jpg', '2026-03-27 15:30:28', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037432230487404546, '经验及其模式', '（英）迈克尔·奥克肖特著', '本书系统阐述了历史、科学和实践三种经验模式的基本特征，三种经验模式的相互关系以及他们各自与哲学的关系。', '2005-03-10 00:00:00', 2, 'ef577e56-be63-4e15-9515-0304429cff9e_CoverNew (6).jpg', '2026-03-27 15:31:12', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037432411366764546, '知识创新 新媒体时代的视角', '姜进章著', '姜进章编著的《知识创新(新媒体时代的视角)》对新媒体时代的知识创新进行分析研究，建立了新媒体时代知识创新的概念、原理和模型，对管理史、媒体环境、社会创新网络、知识发展过程提出了一些新的见解，对企业、教育和城市等多个领域的知识创新提供了案例分析和创新管理对策，对知识创新...', '2011-01-08 00:00:00', 2, '174f0d3c-bd7e-455b-8c67-70d8f08bea45_CoverNew (5).jpg', '2026-03-27 15:31:55', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037432685896544258, '法国文化遗产', '王长明主编', '本书是对法兰西人文诸多方面的进一步挖掘。我们对丛书内容的选择是有针对性的，因为我们的立足点是教学，所以针对性和实用性比较强，对喜欢法国人文知识的人士当然也有一定的参考价值，但不一定能满足其夙求；此外，由于时间仓促，主要还是因解燃眉之急，编写前后未对文章体例确定统一的...', '2010-11-09 00:00:00', 2, '18001168-34f3-4395-a5e5-f7893481c902_CoverNew (4).jpg', '2026-03-27 15:33:00', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037433151074217986, '知识视角的组织文化', '富立友著', '本书主要内容包括：导论、组织文化相关理论研究、组织文化的知识性质、组织文化——知识共享的关键因素、学习与创新——改变组织文化的基础、基于知识共享的组织文化建设等七章。', '2010-11-09 00:00:00', 2, '3138bd80-1d85-4f21-a171-180a87cc9368_11.jpg', '2026-03-27 15:34:51', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037433461217832961, '文学返乡之路', '李丹梦著', '本书是文学批评论文集。收录了作者3年来最新的文学理论探讨和文学时评。', '2011-11-09 00:00:00', 2, '5e212042-5413-4d19-9187-7bd26c96f38a_CoverNew (12).jpg', '2026-03-27 15:36:05', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037433903293280258, '甘肃科技发展战略研究（2014～2018）', '甘肃省科技发展战略研究院编', '甘肃省科技发展战略研究院编', '2011-11-09 00:00:00', 3, 'ce4a56d7-e2ef-471d-b8c4-bd27e45b674a_CoverNew (1).jpg', '2026-03-27 15:37:50', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037434231996690434, '中医科技史研究', '严世芸', '中医科技史研究', '2023-08-19 00:00:00', 3, '9fb8b143-d897-4724-be0a-1cae5d64678f_CoverNew.jpg', '2026-03-27 15:39:09', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037434500541198338, '新时代高等工程科技人才培养', '马新成著', '新时代高等工程科技人才培养', '2021-12-19 00:00:00', 3, 'f1dbbe12-21bd-44be-a68d-a2bb5e841fbf_CoverNew (2).jpg', '2026-03-27 15:40:13', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037434748873355265, '科技期刊编辑探索', '郑秀娟著', '科技期刊编辑探索', '2013-06-19 00:00:00', 3, 'ed49d5be-24f7-4b74-9b75-6a83e5db1218_CoverNew (3).jpg', '2026-03-27 15:41:12', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037435111181529089, '生物史话与生命奥秘', '杨光著', '生物史话与生命奥秘', '2006-03-12 00:00:00', 3, '6de627a8-8d7f-4303-a1e1-6fc42cbceec2_CoverNew (4).jpg', '2026-03-27 15:42:38', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037435673381842945, '实验动物科学与应用', '肖杭', '实验动物科学是医药生物学乃至整个生命科学的重要基础，实验动物是生命科学研究的重要支撑条件。许多生物医药学研究课题需要实验动物，特别是实验性研究，离开实验动物几乎寸步难行．动物试验结果的比较和交流，应在实验动物质量优良的前提下进行，方有可比性、重复性和科学性，实验研究...', '2008-01-21 00:00:00', 3, 'f39589b3-2ac9-4256-90cb-6613ef644941_CoverNew (5).jpg', '2026-03-27 15:44:52', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037436006279557121, '化学与生命：活起来的分子和原子', '杨建明', '本书从生命的起源及发生发展、生命的遗传变异演化、生命的物质和能量代谢及信息传输3个方面，以专题的形式叙述了化学与生命不可分的联系。', '2000-10-21 00:00:00', 3, 'ab9f1a22-8c8f-4967-b9c7-f5e5ad6832d7_CoverNew (6).jpg', '2026-03-27 15:46:12', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037436241504514050, '生物化学学习指导与习题详解 高等教育第3版', '杨建雄主编', '本教材分14章，包括糖类、脂类与生物膜、蛋白质化学、酶学、核酸化学、激素和维生素、生物氧化等内容，每章中包括习题解析、习题、参考答案三部分。', '2005-10-12 00:00:00', 3, '33558e8b-84d1-454a-b1d7-86bf97e3eb1a_CoverNew (7).jpg', '2026-03-27 15:47:08', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037436708758368258, '星云湖流域生态环境保护治理研究', '谭志卫主编', '星云湖流域生态环境保护治理研究', '2023-05-11 00:00:00', 3, 'b81e3951-d0db-443b-9a79-0af10965a179_CoverNew (8).jpg', '2026-03-27 15:48:59', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037436970650710017, '废水生物处理的运行与管理 （第二版）', '徐亚同', '作者结合长期废水处理的研究和实践，对废水生物处理的基本原理和工艺进行了阐述，重点介绍了各类废水生物处理系统的观察评价和运行管理方法，并新推出运行管理的专家决策系统，为广火读者提供在废水处理运行和管理方面实用的几常运行管理经验和异常问题防治对策。书中有大量的图表供阅读...', '2009-01-10 00:00:00', 3, 'b5cf8dcf-e3bd-4fa7-b68a-d1d5d18021a6_CoverNew (9).jpg', '2026-03-27 15:50:02', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037437538454614017, '《马克思恩格斯选集》历史词典', '孔经纬', '收集了《马恩选集》中的历史人物、历史事件、历史名词、党派学派、组织等词目1750多条,以原著中的论述为主要依据。', '1992-09-10 00:00:00', 4, '51014181-886d-481c-b58b-9b0e7bb86228_CoverNew (10).jpg', '2026-03-27 15:52:17', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037437757036572674, '超越乌托邦：《社会主义从空想到科学的发展》义释', '刘伟', '超越乌托邦：《社会主义从空想到科学的发展》义释', '2023-07-09 00:00:00', 4, '412fb653-e312-49f8-abd4-1b457755d48d_CoverNew.jpg', '2026-03-27 15:53:09', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037438076629954561, '马克思主义的立场是什么   ', '徐彬著', '马克思主义的立场是什么   ', '2022-04-12 00:00:00', 4, 'f75d1c75-4a20-46b2-9789-90362d0e41c3_CoverNew (2).jpg', '2026-03-27 15:54:25', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037438298688991233, '马克思历史观视域中的权利正义论', '孟桢著', '马克思历史观视域中的权利正义论 ', '2021-12-11 00:00:00', 4, '12fe08e4-898e-4970-8e14-4d6337652fee_CoverNew (3).jpg', '2026-03-27 15:55:18', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037439607525748737, '一代巨人毛泽东', '侯树栋', '一代巨人毛泽东', '1993-07-09 00:00:00', 4, 'aefd66aa-6ee7-4369-91e4-00892c77a5df_CoverNew (4).jpg', '2026-03-27 16:00:30', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037440026058567682, '毛泽东与他的师长学友', '宋三旦', '本书选择大致覆盖毛泽东在师长学友领域人际关系的100人，力图通过再现毛泽东与他们交往的过程、情景与影响等内容，展现伟人成就事业的轨迹。', '2003-11-01 00:00:00', 4, '586f5d9b-b89b-42d9-b86e-32ac7b9d113c_CoverNew (5).jpg', '2026-03-27 16:02:10', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037440287447592962, '恩格斯传', '刘凤舞', '本书描述了恩格斯的生平、理论创作、革命活动、家庭生活以及他的优秀品质;力求把十九世纪三十年代到九十年代国际共产主义运动史和恩格斯的生平活动融为一体加以叙述。', '1989-08-18 00:00:00', 4, '18238a99-49cd-4d6a-acef-43d6570d4398_CoverNew (6).jpg', '2026-03-27 16:03:13', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037440586379833346, '历史法学', '吕世伦主编', '本书主要阐述了历史法学概述、历史法学的理论基础、历史法学的演变与发展、萨维尼的历史法学、梅因的历史法学、梅特兰的历史法学、穗积陈重的历史法学、历史法学的影响和评价等内容。', '2005-10-03 00:00:00', 4, 'a1e673db-5080-42bf-a217-f08ac0740167_CoverNew (7).jpg', '2026-03-27 16:04:24', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037440934473510913, '中国民主党派的历史道路', '吴智棠', '中国民主党派的历史道路', '1989-06-06 00:00:00', 4, 'aec07fa9-d369-456d-9cb9-bfc7c3142da6_CoverNew (8).jpg', '2026-03-27 16:05:47', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037441243434332161, '党的先进性历史溯源与现实要求', '申振东', '本书是对中国共产党思想建设的研究，对于全面认识党的先进性、马列主义和党的三代领导人关于党的先进性的理论与实践、坚持党的先进性的核心“三个代表”重要思想等进行了论述。', '2004-06-10 00:00:00', 4, '25308a52-e3f4-4dd9-ae39-5aa846dc7fef_CoverNew (9).jpg', '2026-03-27 16:07:00', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037441580102725633, '诗歌与经验：中国古典诗歌论稿', '张三夕著', '本书是作者长期研究中国古典诗歌的论文结集。包括《漫谈古典诗歌的艺术感受问题》、《宋诗宋注管窥》、《元明清诗词演变述论》等20篇文章。', '2008-08-09 00:00:00', 5, '33d862c0-4c59-4118-a40e-9186e906d5ec_CoverNew (11).jpg', '2026-03-27 16:08:21', 0, 1, 0, 0, NULL);
INSERT INTO `book` VALUES (2037441820650254337, '诗经', '邓启铜注释', '本书选择《诗经》的一些版本，配以拼音和注释，让儿童从小开始诵读，陶冶情趣，解惑释疑，寓学于听。', '2004-05-09 00:00:00', 5, 'b1ae560e-d790-4c82-af14-06e559375019_CoverNew.jpg', '2026-03-27 16:09:18', 0, 1, 0, 0, NULL);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT ' id',
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_croatian_ci NOT NULL COMMENT '公司名称/机构名称',
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_croatian_ci NOT NULL COMMENT '联系电话',
  `password` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_croatian_ci NOT NULL COMMENT '密码',
  `remark` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_croatian_ci NULL DEFAULT NULL COMMENT '简介',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态  1:正常 2:不正常',
  `submit_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_croatian_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id` DESC) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2034809046518235178 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_croatian_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (2034809046518235177, 'pingping', '15255525656', '$2a$10$RON2Rgj4HUdCP3aKGzOxi.yhcSVRqCmEY7F9RJBy0PHyjUaTynSjG', NULL, 1, '2026-04-03 01:18:03', '2456585858@qq.com');
INSERT INTO `sys_user` VALUES (2034809046518235139, 'dshajd', '12324144', '1342', NULL, 1, '2024-08-29 11:48:01', NULL);
INSERT INTO `sys_user` VALUES (2034809046518235138, '你是小学生', '15208413626', '25f9e794323b453885f5181f1b624d0b', '备注信息', 1, '2026-03-20 09:47:36', '572690488@qq.com');
INSERT INTO `sys_user` VALUES (1834855734120124418, 'hhhh', '23232323231', 'b1a13166cd3890f217a37a9e466e0149f06d547148234543', '11', 1, '2024-09-14 15:24:31', NULL);
INSERT INTO `sys_user` VALUES (1834853929231421442, '和', '12345678901', '392d0a684994a6f65202b701692a5267467d02499dd6cf42', 'vfdf', 1, '2024-09-14 15:17:21', NULL);
INSERT INTO `sys_user` VALUES (1834847589482205186, '张', '18280601426', 'a7a45f17ba4211765a94dc27740780f3a679d2121378e528', '备注', 1, '2024-09-14 14:52:09', NULL);
INSERT INTO `sys_user` VALUES (1834844685279006722, 'ahua', '12345678910', 'c1ec38700996d2da3d36329de7214e30f25095c094631c59', '哈哈哈', 1, '2024-09-14 14:40:37', NULL);
INSERT INTO `sys_user` VALUES (1834841094648598530, '你好', '15196111994', 'c37443212465a4233d07983208845ad47726b33c27b70e6d', 'wu', 1, '2024-09-14 14:26:21', NULL);
INSERT INTO `sys_user` VALUES (1834840829321121794, '张', '18280601422', '617b56b5ed7c05300bc06c5a91936d19b981a57d5d90410f', '按时', 1, '2024-09-14 14:25:18', NULL);
INSERT INTO `sys_user` VALUES (1834839882062729217, '张', '18280601425', 'a55b5a614f76e22f64777f2f748d0a480718257f91f0461b', '帮助怒', 1, '2024-09-14 14:21:32', NULL);
INSERT INTO `sys_user` VALUES (1834836840001146881, '是否', '18283938239', '23ce8b644383865d57c8e79983992fa7132fa11607c7460f', '为', 1, '2024-09-14 14:09:26', NULL);
INSERT INTO `sys_user` VALUES (1834836120518627329, '吴露可逃yyds', '14785294176', '77364759471370da0f40f804890239509e4c53ea54b7ae6d', 'yyds', 1, '2024-09-14 14:06:35', NULL);
INSERT INTO `sys_user` VALUES (1833062697878515714, 'cccc', '18339494949', '35364841715f239395255062c4307e60909890236d504c75', '12312321', 1, '2024-09-09 16:39:38', NULL);
INSERT INTO `sys_user` VALUES (1833054827472580610, 'cccc', '18349111112', '46eb84772484a7984154074975795923874e56d30166922b', 'hello', 1, '2024-09-09 16:08:22', NULL);
INSERT INTO `sys_user` VALUES (1833046712815546369, '吴露可逃', '14785236975', '37909a162d15b5dd15e82a81532a0a73b23df4ad5c87075a', '是真的', 1, '2024-09-09 15:36:07', NULL);
INSERT INTO `sys_user` VALUES (1833040394293702657, 'joy', '18349111111', 'd8bd61889c6e687a53008c8e83db71221173214162b17589', 'hello', 1, '2024-09-09 15:11:00', NULL);
INSERT INTO `sys_user` VALUES (1833036008851075073, '小花', '17723892735', '629c94539402206672f0a31fd9893c21187f857045e7190d', '简介，备注', 1, '2024-09-09 14:53:35', NULL);
INSERT INTO `sys_user` VALUES (1833035916337311746, '小花', '17723892734', 'd11c84755c7a97d163513483d7b295936280b0e03ec8023f', '简介，备注', 1, '2024-09-09 14:53:13', NULL);
INSERT INTO `sys_user` VALUES (1833023316069281793, '小花', '17723892733', '39a07eb7ae0949cb93b5626ab13547d08d2b52a08fa6b317', '简介，备注', 1, '2024-09-09 14:03:09', NULL);
INSERT INTO `sys_user` VALUES (1832984443339501569, 'cococ', '18349110454', '516398207b36d1f08207d49de1b560550d98b48c0020480e', '12346', 1, '2024-09-09 11:28:41', NULL);
INSERT INTO `sys_user` VALUES (1832983465269751810, 'cococo', '18349111033', '99513469d02c14734729a25ad5ee0b04594555d92ea48044', '121', 1, '2024-09-09 11:24:47', NULL);
INSERT INTO `sys_user` VALUES (1832981112915324930, '小花', '17723892708', '79d424444268f04044250188083c28d94d49707b2a68af20', '简介，备注', 1, '2024-09-09 11:15:27', NULL);
INSERT INTO `sys_user` VALUES (1831542099041947649, 'xiaoming', '123213213', '46f854c75a23d89e95a63b4b02c592173a84204d7ff97144', NULL, 1, '2024-09-05 11:57:19', NULL);
INSERT INTO `sys_user` VALUES (1830446784897413122, 'ninn', '18349131321', '64fa85737b1a052b0e34ef3001dd3782ad93078f9982e206', '12133`', 1, '2024-09-02 11:24:56', NULL);
INSERT INTO `sys_user` VALUES (1830442633379508225, 'ninin', '18312312312', 'f38290f93364c4f670033b4b83c83201824aa93292577e82', '12313', 1, '2024-09-02 11:08:26', NULL);
INSERT INTO `sys_user` VALUES (1830441777024266241, '小花', '17723892707', 'e1975b075a50f90f1024e40492f180d77b57b5b234c4156f', '简介，备注', 1, '2024-09-02 11:05:02', NULL);
INSERT INTO `sys_user` VALUES (1830440960368111618, 'nini', '18341912231', 'f2926c84a45d20af6f425c65a6e46f119e6ac60415319f14', 'heelo', 1, '2024-09-02 11:01:47', NULL);
INSERT INTO `sys_user` VALUES (1830439663036387330, '小花', '17723892706', '836f7f294459770d4632e705249c0fe06301679707b4b329', '简介，备注', 1, '2024-09-02 10:56:38', NULL);
INSERT INTO `sys_user` VALUES (1830438202336432129, '小花', '17723892705', '58f47639ca6c534e97367406f83095c1bd4bb1860f844172', '简介，备注', 1, '2024-09-02 10:50:50', NULL);
INSERT INTO `sys_user` VALUES (1830438171646709761, '小花', '17723892704', '26ea1404019448053466fe2b382593f1318c50083749e146', '简介，备注', 1, '2024-09-02 10:50:42', NULL);
INSERT INTO `sys_user` VALUES (1830437081194692609, '小花', '17723892703', '41220639150619245471206ca42b1535041fb22651d6c462', '简介，备注', 1, '2024-09-02 10:46:22', NULL);
INSERT INTO `sys_user` VALUES (1830436906967498753, '小花', '17723892702', '96d734a99686e5553bb0621b97301a614d83000e7df5a166', '简介，备注', 1, '2024-09-02 10:45:41', NULL);
INSERT INTO `sys_user` VALUES (1830436204203569153, '小花', '17723892701', 'c84f95315a8470a44506a79c410b1276214a01c327a9d931', '简介，备注', 1, '2024-09-02 10:42:53', NULL);
INSERT INTO `sys_user` VALUES (1830434097794732033, '测试数据', '17711002179', '423814d70815d77f2013581c35787ba95b8221a83e56856f', NULL, 1, '2024-09-02 10:34:31', NULL);
INSERT INTO `sys_user` VALUES (1827964469655752706, '小花', '17723892700', 'e1a22856d28f81692389ac7cd46a00b13268b53c85b51580', '简介，备注', 1, '2024-08-26 15:01:06', NULL);
INSERT INTO `sys_user` VALUES (1827961370346053634, '小花', '17723892743', 'b8886d55ca31a54d4688f59ac9b48aa66764048054d6c00b', '简介，备注', 1, '2024-08-26 14:48:47', NULL);
INSERT INTO `sys_user` VALUES (1748257990735794178, '千仓云贷', '17700002179', '18d70f70ec24630699212e6ea9a54152a449988981845796', NULL, 1, '2024-01-19 16:16:00', NULL);
INSERT INTO `sys_user` VALUES (414213, '小明', '12321421', '123', 'dsahdsbajkdbsahj', 1, '2024-08-29 11:32:06', NULL);
INSERT INTO `sys_user` VALUES (123214, 'dshajd', '123241454', '1342', NULL, 1, '2024-08-29 11:51:15', NULL);

SET FOREIGN_KEY_CHECKS = 1;
