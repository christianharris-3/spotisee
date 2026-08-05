
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS UserRole;
DROP TABLE IF EXISTS Upload;
DROP TABLE IF EXISTS UploadItem;
DROP VIEW IF EXISTS SongView;
DROP TABLE IF EXISTS Selection;
DROP TABLE IF EXISTS SelectionItem;


CREATE TABLE Users(
    userId BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    passwordHash VARCHAR(255) NOT NULL
);

CREATE TABLE UserRole(
    userId BIGINT,
    role VARCHAR(255),
    PRIMARY KEY (userId, role)

);

CREATE TABLE Upload(
    uploadId BIGINT PRIMARY KEY AUTO_INCREMENT,
    uploadName VARCHAR(255),
    userId INT NOT NULL
);

CREATE TABLE UploadItem(
    uploadItemId INT PRIMARY KEY AUTO_INCREMENT,
    uploadId BIGINT NOT NULL,

    timestamp DATETIME NOT NULL,
    platform VARCHAR(100),
    msPlayed INT UNSIGNED NOT NULL,
    country CHAR(2),
    ipAddress VARCHAR(45),
    trackName VARCHAR(500),
    albumName VARCHAR(500),
    artistName VARCHAR(500),
    spotifyTrackUri VARCHAR(255),
    episodeName VARCHAR(500),
    episodeShowName VARCHAR(500),
    spotifyEpisodeUri VARCHAR(255),
    audiobookTitle VARCHAR(500),
    audiobookUri VARCHAR(255),
    audiobookChapterUri VARCHAR(255),
    audiobookChapterTitle VARCHAR(500),
    reasonEnd VARCHAR(50),
    reasonStart VARCHAR(50),
    shuffle BOOLEAN,
    skipped BOOLEAN,
    offline BOOLEAN,
    offlineTimestamp DATETIME,
    incognitoMode BOOLEAN
);

CREATE VIEW SongView AS
    SELECT uploadId, timestamp as endTime, msPlayed, trackName, albumName, artistName, skipped, spotifyTrackUri
        FROM UploadItem WHERE
        (timestamp IS NOT NULL) AND
        (msPlayed IS NOT NULL) AND
        (trackName IS NOT NULL) AND
        (albumName IS NOT NULL) AND
        (artistName IS NOT NULL) AND
        (spotifyTrackUri IS NOT NULL);