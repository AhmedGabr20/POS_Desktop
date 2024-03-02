CREATE SCHEMA `elnoor_v2``;
USE `elnoor_v2`;

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index2` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ;

CREATE TABLE `store` (
  `id` int NOT NULL AUTO_INCREMENT,
  `storeName` varchar(20) NOT NULL,
  `storeItems` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `companes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `phone` varchar(100) NOT NULL,
  `email` varchar(40) NOT NULL,
  `address` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `companysales` (
  `id` int NOT NULL AUTO_INCREMENT,
  `item` varchar(400) NOT NULL,
  `total` double NOT NULL,
  `paid` double NOT NULL,
  `remain` double NOT NULL,
  `date` varchar(50) NOT NULL,
  `cus_id` int NOT NULL,
  `link` varchar(400) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `company_id_idx` (`cus_id`),
  KEY `user_id_fk_idx` (`user_id`),
  CONSTRAINT `company_id` FOREIGN KEY (`cus_id`) REFERENCES `companes` (`id`),
  CONSTRAINT `user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `customers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `phone` varchar(100) NOT NULL,
  `email` varchar(40) NOT NULL,
  `address` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `code` varchar(50) NOT NULL,
  `price` double NOT NULL,
  `priceforcustomer` double NOT NULL,
  `amount` int NOT NULL,
  `storeCode` int DEFAULT NULL,
  PRIMARY KEY (`id`,`code`),
  UNIQUE KEY `code_UNIQUE` (`code`),
  KEY `storeCode_idx` (`storeCode`),
  CONSTRAINT `storeCode` FOREIGN KEY (`storeCode`) REFERENCES `store` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `sales` (
  `id` int NOT NULL AUTO_INCREMENT,
  `date` varchar(10) NOT NULL,
  `price` double NOT NULL,
  `paid` double NOT NULL,
  `remain` double NOT NULL,
  `cus_id` int NOT NULL,
  `user_id` int NOT NULL,
  `profit` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `customerId_idx` (`cus_id`),
  CONSTRAINT `customerId` FOREIGN KEY (`cus_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;







