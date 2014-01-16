CREATE DATABASE IF NOT EXISTS HAIBA;
USE HAIBA;

CREATE TABLE IF NOT EXISTS Header (
       HeaderId BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
       Cprnr varchar(10),
       Extid varchar(255),
       Refnr varchar(50),
       Labnr BIGINT(15),
       Lar BIGINT(15),
       Pname varchar(150),
       Indate datetime,
       Prdate datetime,
       Result varchar(50),
       Evaluation varchar(255),
       Usnr varchar(50),
       Alnr varchar(200),
       Stnr varchar(50),
       Avd varchar(50),
       Mgkod varchar(300),
       HAIBACaseDef char(4)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS Isolate (
       IsolateId BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
       Banr varchar(50),
       Quantity varchar(500),
       HeaderId BIGINT(15)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS Quantitative (
       QuantitativeId BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
       Analysis varchar(50),
       Comment varchar(1000),
       EvaluationText varchar(50),
       Qtnr varchar(50),
       Quantity varchar(50),
       HeaderId BIGINT(15)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- must be populated manually, 119="Bakteriæmi" etc.
CREATE TABLE IF NOT EXISTS CaseDef (
       Id BIGINT(15) NOT NULL PRIMARY KEY,
       Text varchar(1000)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS Tabmicroorganism (
	   TabmicroorganismId BIGINT(15) NOT NULL PRIMARY KEY,
       Banr varchar(50),
       Text varchar(300)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS TabLabSection (
	   TabLabSectionId BIGINT(15) NOT NULL PRIMARY KEY,
       Avd varchar(50),
       Text varchar(300)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS TabOrganization (
	   TabOrganizationId BIGINT(15) NOT NULL PRIMARY KEY,
       Mgkod varchar(300),
       Text varchar(300)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS TabAnalysis (
	   TabAnalysisId BIGINT(15) NOT NULL PRIMARY KEY,
       Qtnr varchar(300),
       Text varchar(300)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS TabInvestigation (
	   TabInvestigationId BIGINT(15) NOT NULL PRIMARY KEY,
       Usnr varchar(50),
       Text varchar(300)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS TabLocation (
	   TabLocationId BIGINT(15) NOT NULL PRIMARY KEY,
       Alnr varchar(200),
       Text varchar(300)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS EpimibaImporterStatus (
    Id BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    StartTime DATETIME NOT NULL,
    EndTime DATETIME,
    Outcome VARCHAR(20),
    ErrorMessage VARCHAR(200),

    INDEX (StartTime)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS EpimibaTransaction (
	   EpimibaTransactionId BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
       TransactionId BIGINT(15) NOT NULL,
       TransactionProcessed DATETIME NOT NULL,
       TransactionType BIGINT(15) NOT NULL
) ENGINE=InnoDB COLLATE=utf8_bin;

-- other database
CREATE TABLE IF NOT EXISTS Klass_microorganism (
    TabmicroorganismId BIGINT(15) NOT NULL PRIMARY KEY,
    Banr varchar(50) NOT NULL,
    Text varchar(300) NULL,
    H_BAKT_MICRO float NULL
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS Klass_Location (
    TabLocationId BIGINT(15) NOT NULL PRIMARY KEY,
    Alnr varchar(200) NOT NULL,
    Text varchar(300) NULL,
    H_SAAR_LOC float NULL
) ENGINE=InnoDB COLLATE=utf8_bin;