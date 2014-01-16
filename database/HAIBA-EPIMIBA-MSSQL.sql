CREATE TABLE Header (
	   HeaderId BIGINT NOT NULL PRIMARY KEY,
       Cprnr varchar(10),
       Extid varchar(255),
       Refnr varchar(50),
       Labnr BIGINT,
       Lar BIGINT,
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
       HAIBACaseDef char(4),
);

CREATE TABLE Isolate (
       IsolateId BIGINT NOT NULL PRIMARY KEY,
       Banr varchar(50),
       Quantity varchar(500),
       HeaderId BIGINT
);

CREATE TABLE Quantitative (
       QuantitativeId BIGINT NOT NULL PRIMARY KEY,
       Analysis varchar(50),
       Comment varchar(1000),
       EvaluationText varchar(50),
       Qtnr varchar(50),
       Quantity varchar(50),
       HeaderId BIGINT
);

-- must be populated manually, 119="Bakteriæmi" etc.
CREATE TABLE CaseDef (
       Id BIGINT NOT NULL PRIMARY KEY,
       Text varchar(1000)
);

CREATE TABLE Tabmicroorganism (
	   TabmicroorganismId BIGINT NOT NULL PRIMARY KEY,
       Banr varchar(50),
       Text varchar(300)
);

CREATE TABLE TabLabSection (
	   TabLabSectionId BIGINT NOT NULL PRIMARY KEY,
       Avd varchar(50),
       Text varchar(300)
);

CREATE TABLE TabOrganization (
	   TabOrganizationId BIGINT NOT NULL PRIMARY KEY,
       Mgkod varchar(300),
       Text varchar(300)
);

CREATE TABLE TabAnalysis (
	   TabAnalysisId BIGINT NOT NULL PRIMARY KEY,
       Qtnr varchar(300),
       Text varchar(300)
);

CREATE TABLE TabInvestigation (
	   TabInvestigationId BIGINT NOT NULL PRIMARY KEY,
       Usnr varchar(50),
       Text varchar(300)
);

CREATE TABLE TabLocation (
	   TabLocationId BIGINT NOT NULL PRIMARY KEY,
       Alnr varchar(200),
       Text varchar(300)
);

CREATE TABLE EpimibaImporterStatus (
    Id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    StartTime DATETIME NOT NULL,
    EndTime DATETIME,
    Outcome VARCHAR(20),
    ErrorMessage VARCHAR(200),

    INDEX (StartTime)
);

CREATE TABLE EpimibaTransaction (
	   EpimibaTransactionId BIGINT IDENTITY NOT NULL PRIMARY KEY,
       TransactionId BIGINT NOT NULL,
       TransactionProcessed DATETIME NOT NULL,
       TransactionType BIGINT NOT NULL
);

-- other database
CREATE TABLE Klass_microorganism (
    TabmicroorganismId BIGINT(15) NOT NULL PRIMARY KEY,
    Banr varchar(50) NOT NULL,
    Text varchar(300) NULL,
    H_BAKT_MICRO float NULL
);

CREATE TABLE Klass_Location (
    TabLocationId BIGINT(15) NOT NULL PRIMARY KEY,
    Alnr varchar(50) NOT NULL,
    Text varchar(300) NULL,
    H_SAAR_LOC float NULL
);