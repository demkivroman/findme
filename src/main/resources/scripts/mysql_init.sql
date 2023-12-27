create table findme_db.FINDER
(
    ID          INT AUTO_INCREMENT PRIMARY KEY,
    FULLNAME    varchar(100) not null,
    PHONE       varchar(20),
    EMAIL       varchar(20),
    INFORMATION varchar(200)
);

create table findme_db.PERSON
(
    ID          INT AUTO_INCREMENT PRIMARY KEY,
    FULLNAME    varchar(100) not null,
    BIRTHDAY    date,
    DESCRIPTION text,
    FINDER_ID   INT UNIQUE,
    FOREIGN KEY (FINDER_ID) REFERENCES FINDER (ID)
);

create table findme_db.POSTS
(
    ID        INT AUTO_INCREMENT PRIMARY KEY,
    POST      text not null,
    TIME      TIMESTAMP     not null,
    PERSON_ID INT,
    FOREIGN KEY (PERSON_ID) REFERENCES PERSON (ID)
);

create table findme_db.PHOTO
(
    ID        INT AUTO_INCREMENT PRIMARY KEY,
    URL       varchar(200) not null,
    PERSON_ID INT,
    FOREIGN KEY (PERSON_ID) REFERENCES PERSON (ID)
);