/* SQLEditor (MySQL (2))*/

CREATE TABLE HealthSystem.Administrator
(
ID INT NOT NULL AUTO_INCREMENT UNIQUE ,
login VARCHAR(45) UNIQUE ,
firstName VARCHAR(45) NOT NULL,
lastName VARCHAR(45) NOT NULL,
sex VARCHAR(6) NOT NULL DEFAULT 'Male',
additionalInfo VARCHAR(200) DEFAULT 'No additional information',
salt VARCHAR(200) NOT NULL,
password VARCHAR(200) NOT NULL,
available TINYINT(1) NOT NULL DEFAULT 1,
lastUsedDevice VARCHAR(200) CHARACTER SET 'utf8' NOT NULL DEFAULT 'Undefined',
primaryPassword VARCHAR(200) NOT NULL,
PRIMARY KEY (ID)
) ENGINE=InnoDB;

CREATE TABLE HealthSystem.Doctor
(
ID INT NOT NULL AUTO_INCREMENT UNIQUE ,
login VARCHAR(45) UNIQUE ,
title VARCHAR(45) NOT NULL DEFAULT 'Untitteled',
firstName VARCHAR(45) NOT NULL,
lastName VARCHAR(45) NOT NULL,
sex VARCHAR(6) NOT NULL DEFAULT 'Male',
license VARCHAR(1) NOT NULL,
additionalInfo VARCHAR(200) DEFAULT 'No additional information',
salt VARCHAR(200) NOT NULL,
password VARCHAR(200) NOT NULL,
available TINYINT(1) NOT NULL DEFAULT 1,
lastUsedDevice VARCHAR(200) NOT NULL DEFAULT 'Undefined',
primaryPassword VARCHAR(200) NOT NULL,
PRIMARY KEY (ID)
) ENGINE=InnoDB;

CREATE TABLE HealthSystem.Medicine
(
ID INT NOT NULL AUTO_INCREMENT UNIQUE ,
name VARCHAR(200) NOT NULL,
description VARCHAR(200) NOT NULL,
`usage` VARCHAR(200) NOT NULL,
license VARCHAR(1) NOT NULL,
sideEffect VARCHAR(200) NOT NULL DEFAULT 'Undefined',
type VARCHAR(200) NOT NULL,
available TINYINT(1) NOT NULL DEFAULT 1,
PRIMARY KEY (ID)
) ENGINE=InnoDB;

CREATE TABLE HealthSystem.Patient
(
ID INT NOT NULL AUTO_INCREMENT UNIQUE ,
login VARCHAR(45) UNIQUE ,
firstName VARCHAR(45) NOT NULL,
lastName VARCHAR(45) NOT NULL,
sex VARCHAR(6) NOT NULL DEFAULT 'Male',
additionalInfo VARCHAR(200) DEFAULT 'No additional information',
salt VARCHAR(200) NOT NULL,
password VARCHAR(200) NOT NULL,
available TINYINT(1) NOT NULL DEFAULT 1,
lastUsedDevice VARCHAR(200) NOT NULL DEFAULT 'Undefined',
primaryPassword VARCHAR(200) NOT NULL,
PRIMARY KEY (ID)
) ENGINE=InnoDB;

CREATE TABLE HealthSystem.Pharmacy
(
ID INT NOT NULL AUTO_INCREMENT UNIQUE ,
login VARCHAR(45) UNIQUE ,
name VARCHAR(200) NOT NULL,
address VARCHAR(200) NOT NULL DEFAULT 'Undefined',
additionalInfo VARCHAR(200) DEFAULT 'No additional information',
salt VARCHAR(200) NOT NULL,
password VARCHAR(200) NOT NULL,
available TINYINT(1) NOT NULL DEFAULT 1,
lastUsedDevice VARCHAR(200) NOT NULL DEFAULT 'Undefined',
primaryPassword VARCHAR(200) NOT NULL,
PRIMARY KEY (ID)
) ENGINE=InnoDB;

CREATE TABLE HealthSystem.Prescription
(
ID INT NOT NULL AUTO_INCREMENT UNIQUE ,
Medicine_ID INT NOT NULL,
Doctor_ID INT NOT NULL,
Patient_ID INT NOT NULL,
Pharmacy_ID INT DEFAULT NULL,
available TINYINT(1) NOT NULL DEFAULT 1,
specialNotes VARCHAR(2000) DEFAULT 'None',
PRIMARY KEY (ID,Medicine_ID,Doctor_ID,Patient_ID)
) ENGINE=InnoDB;

CREATE INDEX Medicine_ID_idx ON HealthSystem.Prescription (Medicine_ID);
ALTER TABLE HealthSystem.Prescription ADD CONSTRAINT `fk_Prescription_Medicine` FOREIGN KEY `fk_Prescription_Medicine` (Medicine_ID) REFERENCES HealthSystem.Medicine (ID) ON DELETE NO ACTION  ON UPDATE NO ACTION;

CREATE INDEX Doctor_ID_idx ON HealthSystem.Prescription (Doctor_ID);
ALTER TABLE HealthSystem.Prescription ADD CONSTRAINT `fk_Prescription_Doctor1` FOREIGN KEY `fk_Prescription_Doctor1` (Doctor_ID) REFERENCES HealthSystem.Doctor (ID) ON DELETE NO ACTION  ON UPDATE NO ACTION;

CREATE INDEX Patient_ID_idx ON HealthSystem.Prescription (Patient_ID);
ALTER TABLE HealthSystem.Prescription ADD CONSTRAINT `fk_Prescription_Patient1` FOREIGN KEY `fk_Prescription_Patient1` (Patient_ID) REFERENCES HealthSystem.Patient (ID) ON DELETE NO ACTION  ON UPDATE NO ACTION;

CREATE INDEX Pharmacy_ID_idx ON HealthSystem.Prescription (Pharmacy_ID);
ALTER TABLE HealthSystem.Prescription ADD CONSTRAINT `fk_Prescription_Pharmacy1` FOREIGN KEY `fk_Prescription_Pharmacy1` (Pharmacy_ID) REFERENCES HealthSystem.Pharmacy (ID) ON DELETE NO ACTION  ON UPDATE NO ACTION;
