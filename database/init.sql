-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema is241_mo_vat
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema is241_mo_vat
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `is241_mo_vat` DEFAULT CHARACTER SET utf8 ;
USE `is241_mo_vat` ;

-- -----------------------------------------------------
-- Table `is241_mo_vat`.`Site`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `is241_mo_vat`.`Site` ;

CREATE TABLE IF NOT EXISTS `is241_mo_vat`.`Site` (
  `id` INT NOT NULL,
  `location` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `is241_mo_vat`.`PatientInformation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `is241_mo_vat`.`PatientInformation` ;

CREATE TABLE IF NOT EXISTS `is241_mo_vat`.`PatientInformation` (
  `id` INT NOT NULL,
  `first_name` VARCHAR(45) NULL,
  `last_name` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `is241_mo_vat`.`VaccineInformation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `is241_mo_vat`.`VaccineInformation` ;

CREATE TABLE IF NOT EXISTS `is241_mo_vat`.`VaccineInformation` (
  `lot_num` INT NOT NULL,
  `provider_id` INT NULL,
  `patient_id` INT NULL,
  `administered_date` DATETIME NULL,
  PRIMARY KEY (`lot_num`),
  UNIQUE INDEX `LOT_NUM_UNIQUE` (`lot_num` ASC) VISIBLE,
  INDEX `provider_vaccine_idx` (`provider_id` ASC) VISIBLE,
  INDEX `patient_id_vaccine_idx` (`patient_id` ASC) VISIBLE,
  CONSTRAINT `provider_vaccine`
    FOREIGN KEY (`provider_id`)
    REFERENCES `is241_mo_vat`.`Site` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `patient_id_vaccine`
    FOREIGN KEY (`patient_id`)
    REFERENCES `is241_mo_vat`.`PatientInformation` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `is241_mo_vat`.`Permissions`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `is241_mo_vat`.`Permissions` ;

CREATE TABLE IF NOT EXISTS `is241_mo_vat`.`Permissions` (
  `permission_id` INT NOT NULL AUTO_INCREMENT,
  `read_patient` TINYINT NOT NULL,
  `reports` TINYINT NOT NULL,
  `add_user` TINYINT NOT NULL,
  `edit_user` TINYINT NOT NULL,
  `write_patient` TINYINT NOT NULL,
  `edit_patient` TINYINT NOT NULL,
  PRIMARY KEY (`permission_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `is241_mo_vat`.`User`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `is241_mo_vat`.`User` ;

CREATE TABLE IF NOT EXISTS `is241_mo_vat`.`User` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(36) NOT NULL,
  `password_hash` BLOB(64) NOT NULL,
  `salt` VARBINARY(32) NOT NULL,
  `2fa_secret` VARCHAR(64) NULL,
  `permissions_id` INT NULL,
  PRIMARY KEY (`user_id`),
  INDEX `permissions_user_idx` (`permissions_id` ASC) VISIBLE,
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
  CONSTRAINT `permissions_user`
    FOREIGN KEY (`permissions_id`)
    REFERENCES `is241_mo_vat`.`Permissions` (`permission_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `is241_mo_vat` ;

-- -----------------------------------------------------
-- procedure ADD_USER
-- -----------------------------------------------------

USE `is241_mo_vat`;
DROP procedure IF EXISTS `is241_mo_vat`.`ADD_USER`;

DELIMITER $$
USE `is241_mo_vat`$$
CREATE PROCEDURE `ADD_USER` (username_param char(36), password_hash_param blob(64), salt_param varbinary(32), 2fa_secret_param char(64), permissions_id_param INT)
BEGIN
	INSERT INTO `is241_mo_vat`.`User` (username, password_hash,salt, 2fa_secret, permissions_id) VALUES (username_param, password_hash_param, salt_param, 2fa_secret_param, permissions_id_param);
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure GET_USER
-- -----------------------------------------------------

USE `is241_mo_vat`;
DROP procedure IF EXISTS `is241_mo_vat`.`GET_USER`;

DELIMITER $$
USE `is241_mo_vat`$$
CREATE PROCEDURE `GET_USER` (username_param LONGTEXT)
BEGIN
	SELECT user_id, username, password_hash, salt FROM `is241_mo_vat`.`User` WHERE username = username_param;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure CHECK_USERNAME
-- -----------------------------------------------------

USE `is241_mo_vat`;
DROP procedure IF EXISTS `is241_mo_vat`.`CHECK_USERNAME`;

DELIMITER $$
USE `is241_mo_vat`$$
CREATE PROCEDURE `CHECK_USERNAME` (username_param longtext)
BEGIN
	SELECT username FROM `is241_mo_vat`.`User` WHERE username = username_param;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure CREATE_PERMISSIONS
-- -----------------------------------------------------

USE `is241_mo_vat`;
DROP procedure IF EXISTS `is241_mo_vat`.`CREATE_PERMISSIONS`;

DELIMITER $$
USE `is241_mo_vat`$$
CREATE PROCEDURE `CREATE_PERMISSIONS` (OUT permission_num int, read_patient_param boolean, reports_param boolean, add_users_param boolean, edit_users_param boolean, write_patient_param boolean, edit_patient_param boolean)
BEGIN
	INSERT INTO `is241_mo_vat`.`Permissions` (read_patient, reports, add_user, edit_user, write_patient, edit_patient) VALUES (read_patient_param, reports_param, add_users_param, edit_users_param, write_patient_param, edit_patient_param);
	SET permission_num = LAST_INSERT_ID();
END$$

DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
