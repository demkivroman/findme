create table findme_db.FINDER
(
    ID          INT AUTO_INCREMENT PRIMARY KEY,
    FULLNAME    varchar(100) not null,
    PHONE       varchar(20) UNIQUE ,
    EMAIL       varchar(100) UNIQUE ,
    INFORMATION text
);

create table findme_db.PERSON
(
    ID          INT AUTO_INCREMENT PRIMARY KEY,
    FINDER_ID INT,
    FULLNAME    varchar(100) not null,
    BIRTHDAY    date,
    DESCRIPTION text,
    FOREIGN KEY (FINDER_ID) REFERENCES FINDER (ID)
);

create table findme_db.person_status
(
    ID INT AUTO_INCREMENT PRIMARY KEY,
    person_id INT UNIQUE,
    isFound boolean,
    createdAt timestamp not null,
    removedAt timestamp,
    FOREIGN KEY (person_id) REFERENCES PERSON(id)
);

create table findme_db.person_payment
(
    ID INT AUTO_INCREMENT PRIMARY KEY,
    person_id INT UNIQUE,
    payedAt timestamp not null,
    days int not null,
    costPerDay float not null,
    FOREIGN KEY (person_id) REFERENCES PERSON(id)
);

create table findme_db.POSTS
(
    ID        INT AUTO_INCREMENT PRIMARY KEY,
    PERSON_ID INT not null ,
    POST      text not null,
    author    varchar(100),
    TIME      TIMESTAMP     not null,
    FOREIGN KEY (PERSON_ID) REFERENCES PERSON (ID)
);

create table findme_db.PHOTO
(
    ID        INT AUTO_INCREMENT PRIMARY KEY,
    PERSON_ID INT not null ,
    URL       varchar(200) UNIQUE not null,
    FOREIGN KEY (PERSON_ID) REFERENCES PERSON (ID)
);

create table findme_db.THUMBNAIL
(
    ID        INT AUTO_INCREMENT PRIMARY KEY,
    PERSON_ID INT not null ,
    URL       varchar(200) UNIQUE not null,
    FOREIGN KEY (PERSON_ID) REFERENCES PERSON (ID)
);
