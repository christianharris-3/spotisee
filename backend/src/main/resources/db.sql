
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Uploads;
DROP TABLE IF EXISTS Songs;


CREATE TABLE Users(
    userId INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255)
);

CREATE TABLE Uploads(
    uploadId INT PRIMARY KEY AUTO_INCREMENT,
    userId INT
);

CREATE TABLE Songs(
    songId INT PRIMARY KEY AUTO_INCREMENT,
    uploadId INT,

    timestamp DATETIME NOT NULL,
    username VARCHAR(255),
    platform VARCHAR(100),
    msPlayed INT UNSIGNED,
    country CHAR(50),
    ipAddress VARCHAR(45),
    trackName VARCHAR(500),
    albumName VARCHAR(500),
    artistName VARCHAR(500),
    spotifyTrackUri VARCHAR(255),
    episodeName VARCHAR(500),
    episodeShowName VARCHAR(500),
    spotifyEpisodeUri VARCHAR(255),
    reasonEnd VARCHAR(50),
    reasonStart VARCHAR(50),
    shuffle BOOLEAN,
    skipped BOOLEAN,
    offline BOOLEAN,
    offlineTimestamp DATETIME,
    incognito_mode BOOLEAN
);