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
  `site_id` INT NOT NULL AUTO_INCREMENT,
  `location` VARCHAR(95) NULL,
  `county` VARCHAR(32) NULL,
  `phone_number` VARCHAR(15) NULL,
  `fips` INT(9) NULL,
  `zip_code` INT(9) NULL,
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`site_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `is241_mo_vat`.`PatientInformation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `is241_mo_vat`.`PatientInformation` ;

CREATE TABLE IF NOT EXISTS `is241_mo_vat`.`PatientInformation` (
  `patient_id` INT NOT NULL,
  `first_name` VARCHAR(45) NOT NULL,
  `middle_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `last_ss_num` INT(4) NOT NULL,
  `dob` DATETIME NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`patient_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `is241_mo_vat`.`UserType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `is241_mo_vat`.`UserType` ;

CREATE TABLE IF NOT EXISTS `is241_mo_vat`.`UserType` (
  `user_type_id` INT NOT NULL AUTO_INCREMENT,
  `type_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`user_type_id`))
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
  `2fa_secret` VARCHAR(64) NOT NULL,
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `site_id` INT NOT NULL,
  `user_type` INT NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
  INDEX `site_user_idx` (`site_id` ASC) VISIBLE,
  INDEX `usertype_user_idx` (`user_type` ASC) VISIBLE,
  CONSTRAINT `site_user`
    FOREIGN KEY (`site_id`)
    REFERENCES `is241_mo_vat`.`Site` (`site_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `usertype_user`
    FOREIGN KEY (`user_type`)
    REFERENCES `is241_mo_vat`.`UserType` (`user_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `is241_mo_vat`.`Vaccine`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `is241_mo_vat`.`Vaccine` ;

CREATE TABLE IF NOT EXISTS `is241_mo_vat`.`Vaccine` (
  `lot_num` INT NOT NULL,
  `site_id` INT NULL,
  `patient_id` INT NULL,
  `administered_date` DATETIME NULL,
  `manufacturer` VARCHAR(45) NULL,
  `dose` INT NULL,
  `administrated_by` INT NULL,
  PRIMARY KEY (`lot_num`),
  UNIQUE INDEX `LOT_NUM_UNIQUE` (`lot_num` ASC) VISIBLE,
  INDEX `provider_vaccine_idx` (`site_id` ASC) VISIBLE,
  INDEX `patient_id_vaccine_idx` (`patient_id` ASC) VISIBLE,
  INDEX `user_vaccine_idx` (`administrated_by` ASC) VISIBLE,
  CONSTRAINT `provider_vaccine`
    FOREIGN KEY (`site_id`)
    REFERENCES `is241_mo_vat`.`Site` (`site_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `patient_id_vaccine`
    FOREIGN KEY (`patient_id`)
    REFERENCES `is241_mo_vat`.`PatientInformation` (`patient_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `user_vaccine`
    FOREIGN KEY (`administrated_by`)
    REFERENCES `is241_mo_vat`.`User` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `is241_mo_vat`.`Permissions`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `is241_mo_vat`.`Permissions` ;

CREATE TABLE IF NOT EXISTS `is241_mo_vat`.`Permissions` (
  `permission_id` INT NOT NULL AUTO_INCREMENT,
  `user_type` INT NOT NULL,
  `read_patient` TINYINT NOT NULL,
  `reports` TINYINT NOT NULL,
  `add_user` TINYINT NOT NULL,
  `edit_user` TINYINT NOT NULL,
  `write_patient` TINYINT NOT NULL,
  `edit_patient` TINYINT NOT NULL,
  `add_site` TINYINT NOT NULL,
  `request_records` TINYINT NOT NULL,
  PRIMARY KEY (`permission_id`, `user_type`),
  INDEX `usertype_permissions_idx` (`user_type` ASC) VISIBLE,
  CONSTRAINT `usertype_permissions`
    FOREIGN KEY (`user_type`)
    REFERENCES `is241_mo_vat`.`UserType` (`user_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `is241_mo_vat`.`PatientContact`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `is241_mo_vat`.`PatientContact` ;

CREATE TABLE IF NOT EXISTS `is241_mo_vat`.`PatientContact` (
  `id` INT NOT NULL,
  `patient_id` INT NULL,
  `address` VARCHAR(95) NULL,
  `phone_num` VARCHAR(15) NULL,
  `phone_type` VARCHAR(4) NULL,
  PRIMARY KEY (`id`),
  INDEX `patient_contact_idx` (`patient_id` ASC) VISIBLE,
  CONSTRAINT `patient_contact`
    FOREIGN KEY (`patient_id`)
    REFERENCES `is241_mo_vat`.`PatientInformation` (`patient_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `is241_mo_vat`.`Insurance`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `is241_mo_vat`.`Insurance` ;

CREATE TABLE IF NOT EXISTS `is241_mo_vat`.`Insurance` (
  `insurance_id` INT NOT NULL AUTO_INCREMENT,
  `patient_id` INT NULL,
  `provider` VARCHAR(45) NOT NULL,
  `group_number` VARCHAR(45) NOT NULL,
  `policy_number` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`insurance_id`),
  INDEX `insurance_patient_idx` (`patient_id` ASC) VISIBLE,
  CONSTRAINT `insurance_patient`
    FOREIGN KEY (`patient_id`)
    REFERENCES `is241_mo_vat`.`PatientInformation` (`patient_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `is241_mo_vat`.`DataRequestHistory`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `is241_mo_vat`.`DataRequestHistory` ;

CREATE TABLE IF NOT EXISTS `is241_mo_vat`.`DataRequestHistory` (
  `request_id` INT NOT NULL AUTO_INCREMENT,
  `patient_id` INT NOT NULL,
  `first_name` VARCHAR(50) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `requestor_agency` VARCHAR(255) NOT NULL,
  `requestor_address` VARCHAR(95) NOT NULL,
  `requestor_phone` VARCHAR(15) NOT NULL,
  `requestor_email` VARCHAR(255) NOT NULL,
  `requestor_fax` VARCHAR(15) NOT NULL,
  `date_requested` DATETIME NOT NULL,
  PRIMARY KEY (`request_id`),
  INDEX `datarequest_patient_idx` (`patient_id` ASC) VISIBLE,
  CONSTRAINT `datarequest_patient`
    FOREIGN KEY (`patient_id`)
    REFERENCES `is241_mo_vat`.`PatientInformation` (`patient_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `is241_mo_vat`.`InvalidatedTokens`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `is241_mo_vat`.`InvalidatedTokens` ;

CREATE TABLE IF NOT EXISTS `is241_mo_vat`.`InvalidatedTokens` (
  `token` INT NOT NULL,
  `expires_at` DATETIME NULL,
  PRIMARY KEY (`token`))
ENGINE = InnoDB;

USE `is241_mo_vat` ;

-- -----------------------------------------------------
-- procedure ADD_USER
-- -----------------------------------------------------

USE `is241_mo_vat`;
DROP procedure IF EXISTS `is241_mo_vat`.`ADD_USER`;

DELIMITER $$
USE `is241_mo_vat`$$
CREATE PROCEDURE `ADD_USER` (username_param char(36), password_hash_param blob(64), salt_param varbinary(32), 2fa_secret_param char(64), firstname_param VARCHAR(45), lastname_param VARCHAR(45), site_id_param INT, user_type_param INT, email_param varchar(255))
BEGIN
	INSERT INTO User (username, password_hash, salt, 2fa_secret, first_name, last_name, site_id, user_type, email) VALUES (username_param, password_hash_param, salt_param, 2fa_secret_param, firstname_param, lastname_param, site_id_param, user_type_param, email_param);
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
CREATE PROCEDURE `CREATE_PERMISSIONS` (user_type_param int, read_patient_param boolean, edit_patient_param boolean, write_patient_param boolean, reports_param boolean, add_users_param boolean, edit_users_param boolean, create_site_param boolean, request_records_param boolean)
BEGIN
	INSERT INTO `is241_mo_vat`.`Permissions` (user_type, read_patient, reports, add_user, edit_user, write_patient, edit_patient, add_site, request_records) VALUES (user_type_param, read_patient_param, reports_param, add_users_param, edit_users_param, write_patient_param, edit_patient_param, create_site_param, request_records_param);
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure ADD_SITE
-- -----------------------------------------------------

USE `is241_mo_vat`;
DROP procedure IF EXISTS `is241_mo_vat`.`ADD_SITE`;

DELIMITER $$
USE `is241_mo_vat`$$
CREATE PROCEDURE `ADD_SITE` (location_param varchar(95), county_param varchar(32), phone_number_param int(15), fips_param INT(9), zip_code_param INT(9))
BEGIN
	INSERT INTO `is241_mo_vat`.`Site`(location, county, phone_number, fips, zip_code) VALUES (location_param, county_param, phone_number_param, fips_param, zip_code_param);
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure GET_PERMISSIONS
-- -----------------------------------------------------

USE `is241_mo_vat`;
DROP procedure IF EXISTS `is241_mo_vat`.`GET_PERMISSIONS`;

DELIMITER $$
USE `is241_mo_vat`$$
CREATE PROCEDURE `GET_PERMISSIONS` (username varchar(45))
BEGIN
	SELECT u.user_type, t.user_type_id, perms.read_patient, perms.reports, perms.add_user, perms.edit_user, perms.write_patient, perms.edit_patient, perms.add_site, perms.request_records FROM User u, UserType t, Permissions perms WHERE u.username = username AND u.user_type = t.user_type_id AND perms.user_type = t.user_type_id;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure ADD_PATIENT
-- -----------------------------------------------------

USE `is241_mo_vat`;
DROP procedure IF EXISTS `is241_mo_vat`.`ADD_PATIENT`;

DELIMITER $$
USE `is241_mo_vat`$$
CREATE PROCEDURE `ADD_PATIENT` (first_name_param varchar(45), middle_name_param varchar(45), last_name_param varchar(45), last_ss_num_param int, dob_param datetime, email_param varchar(255))
BEGIN
	INSERT INTO PatientInformation (first_name, middle_name, last_name, last_ss_num, dob, email) VALUES (first_name_param, middle_name_param, last_name_param, last_ss_num_param, dob_param, email_param);
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure ADD_VACCINE
-- -----------------------------------------------------

USE `is241_mo_vat`;
DROP procedure IF EXISTS `is241_mo_vat`.`ADD_VACCINE`;

DELIMITER $$
USE `is241_mo_vat`$$
CREATE PROCEDURE `ADD_VACCINE` (site_id_param int, patient_id_param int, administered_date_param datetime, manufacturer_param varchar(45), dose_param int, administrated_by_param int)
BEGIN
	INSERT INTO Vaccine (site_id, patient_id, administered_date, manufacturer, dose, administrated_by) VALUES (site_id_param, patient_id_param, administered_date_param, manufacturer_param, dose_param, administrated_by_param);
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure ADD_INSURANCE
-- -----------------------------------------------------

USE `is241_mo_vat`;
DROP procedure IF EXISTS `is241_mo_vat`.`ADD_INSURANCE`;

DELIMITER $$
USE `is241_mo_vat`$$
CREATE PROCEDURE `ADD_INSURANCE` (patient_id_param int, provider_id_param varchar(45), group_number_param varchar(45), policy_number_param varchar(45))
BEGIN
	INSERT INTO `is241_mo_vat`.`Insurance` (`patient_id`, `provider`, `group_number`, `policy_number`) VALUES (patient_id_param, provider_id_param, group_number_param, policy_number_param);
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure ADD_PATIENT_CONTACT
-- -----------------------------------------------------

USE `is241_mo_vat`;
DROP procedure IF EXISTS `is241_mo_vat`.`ADD_PATIENT_CONTACT`;

DELIMITER $$
USE `is241_mo_vat`$$
CREATE PROCEDURE `ADD_PATIENT_CONTACT` (patient_id_param int, address_param varchar(95), phone_num_param varchar(15), phone_type_param varchar(4))
BEGIN
	INSERT INTO `PatientContact` (patient_id, address, phone_num, phone_type) VALUES (patient_id_param, address_param, phone_num_param, phone_type_param);
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure ADD_PATIENT_INFO
-- -----------------------------------------------------

USE `is241_mo_vat`;
DROP procedure IF EXISTS `is241_mo_vat`.`ADD_PATIENT_INFO`;

DELIMITER $$
USE `is241_mo_vat`$$
CREATE PROCEDURE `ADD_PATIENT_INFO` (first_name_param varchar(45), middle_name_param varchar(45), last_name_param varchar(45), last_ss_num_param int, dob_param datetime, email_param varchar(255), address_param varchar(95), phone_num_param varchar(15), phone_type_param varchar(4), provider_id_param varchar(45), group_number_param varchar(45), policy_number_param varchar(45), administered_date_param datetime, manufacturer_param varchar(45), dose_param int, adminstrated_by_param int,  site_id_param int)
BEGIN
	INSERT INTO PatientInformation (first_name, middle_name, last_name, last_ss_num, dob, email) VALUES (first_name_param, middle_name_param, last_name_param, last_ss_num_param, dob_param, email_param);
    SET @patient_id_param = LAST_INSERT_ID();
	INSERT INTO PatientContact (patient_id, address, phone_num, phone_type) VALUES (@patient_id_param, address_param, phone_num_param, phone_type_param);
    INSERT INTO Insurance (`patient_id`, `provider`, `group_number`, `policy_number`) VALUES (@patient_id_param, provider_id_param, group_number_param, policy_number_param);
	INSERT INTO Vaccine (lot_num, site_id, patient_id, administered_date, manufacturer, dose, administrated_by) VALUES (lot_num_param, site_id_param, @patient_id_param, administered_date_param, manufacturer_param, dose_param, administrated_by_param);   
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure GET_SITES
-- -----------------------------------------------------

USE `is241_mo_vat`;
DROP procedure IF EXISTS `is241_mo_vat`.`GET_SITES`;

DELIMITER $$
USE `is241_mo_vat`$$
CREATE PROCEDURE `GET_SITES` ()
BEGIN
	SELECT site_id, name FROM Site;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure GET_TYPES
-- -----------------------------------------------------

USE `is241_mo_vat`;
DROP procedure IF EXISTS `is241_mo_vat`.`GET_TYPES`;

DELIMITER $$
USE `is241_mo_vat`$$
CREATE PROCEDURE `GET_TYPES` ()
BEGIN
	SELECT type_name, user_type_id FROM UserType;
END$$

DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
-- begin attached script 'script'
INSERT INTO UserType(type_name) VALUES ("System Admin");
# User Type ID, Read patient, Edit patient, Write patient, Reports, add user, edit user
CALL CREATE_PERMISSIONS(LAST_INSERT_ID(), TRUE,TRUE,TRUE,TRUE,TRUE,TRUE, TRUE, TRUE);
INSERT INTO UserType(type_name) VALUES ("IIS Admin");
CALL CREATE_PERMISSIONS(LAST_INSERT_ID(), FALSE,TRUE,TRUE,TRUE,FALSE,FALSE, TRUE, TRUE);
INSERT INTO UserType(type_name) VALUES ("Clinic");
CALL CREATE_PERMISSIONS(LAST_INSERT_ID(), TRUE,TRUE,TRUE,FALSE,FALSE,FALSE, FALSE, FALSE);
INSERT INTO UserType(type_name) VALUES ("CDC Staff");
CALL CREATE_PERMISSIONS(LAST_INSERT_ID(), FALSE,FALSE,FALSE,TRUE,FALSE,FALSE, FALSE, FALSE);
INSERT INTO UserType(type_name) VALUES ("IIS Staff");
CALL CREATE_PERMISSIONS(LAST_INSERT_ID(), TRUE,FALSE,FALSE,TRUE,FALSE,FALSE, FALSE, FALSE);
INSERT INTO Site(location, phone_number, `name`) VALUES ("912 Wildwood, Jefferson City, MO 65102-0570", "5737516400", "IIS");
-- end attached script 'script'
