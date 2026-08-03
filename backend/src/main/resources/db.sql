
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Upload;
DROP TABLE IF EXISTS UploadItem;
DROP VIEW IF EXISTS SongView;


CREATE TABLE Users(
    userId INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255)
);

CREATE TABLE Upload(
    uploadId INT PRIMARY KEY AUTO_INCREMENT,
    userId INT
);

CREATE TABLE UploadItem(
    uploadItemId INT PRIMARY KEY AUTO_INCREMENT,
    uploadId INT NOT NULL,

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