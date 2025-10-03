create table findmeDB.FINDER
(
    ID          INT AUTO_INCREMENT PRIMARY KEY,
    FULLNAME    varchar(100) not null,
    PHONE       varchar(20) UNIQUE ,
    EMAIL       varchar(100) UNIQUE ,
    INFORMATION text
);

create table findmeDB.PERSON
(
    ID          INT AUTO_INCREMENT PRIMARY KEY,
    FINDER_ID INT,
    FULLNAME    varchar(100) not null,
    BIRTHDAY    date,
    DESCRIPTION text,
    FOREIGN KEY (FINDER_ID) REFERENCES FINDER (ID)
);

create table findmeDB.person_status
(
    ID INT AUTO_INCREMENT PRIMARY KEY,
    person_id INT UNIQUE,
    isFound boolean,
    createdAt timestamp not null,
    removedAt timestamp,
    FOREIGN KEY (person_id) REFERENCES PERSON(id)
);

create table findmeDB.person_payment
(
    ID INT AUTO_INCREMENT PRIMARY KEY,
    person_id INT UNIQUE,
    payedAt timestamp not null,
    days int not null,
    costPerDay float not null,
    FOREIGN KEY (person_id) REFERENCES PERSON(id)
);

create table findmeDB.POSTS
(
    ID        INT AUTO_INCREMENT PRIMARY KEY,
    PERSON_ID INT not null ,
    POST      text not null,
    author    varchar(100),
    TIME      TIMESTAMP     not null,
    FOREIGN KEY (PERSON_ID) REFERENCES PERSON (ID)
);

create table findmeDB.PHOTO
(
    ID        INT AUTO_INCREMENT PRIMARY KEY,
    PERSON_ID INT not null ,
    URL       varchar(200) UNIQUE not null,
    FOREIGN KEY (PERSON_ID) REFERENCES PERSON (ID)
);

CREATE TABLE subscriptions (
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   email VARCHAR(255) NOT NULL,
   token VARCHAR(255) UNIQUE,
   status ENUM('PENDING', 'CONFIRMED', 'COMPLAINED', 'UNSUBSCRIBED', 'INVALID') DEFAULT 'UNSUBSCRIBED',
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

