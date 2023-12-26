create table findme_db.POSTS
(
    ID INT PRIMARY KEY,
    POST varchar(1000) not null,
    TIME TIMESTAMP not null
);

create table findme_db.FINDER
(
    ID INT PRIMARY KEY,
    FULLNAME varchar(100) not null,
    PHONE varchar(20),
    EMAIL varchar(20),
    INFORMATION varchar(200)
);

create table findme_db.PERSON
(
    ID INT PRIMARY KEY,
    FULLNAME varchar(100) not null,
    BIRTHDAY date,
    PHOTO_URL varchar(200) not null,
    DESCRIPTION varchar(1000),
    FINDER_ID INT UNIQUE FOREIGN KEY REFERENCES FINDER(ID),
    POSTS_ID INT FOREIGN KEY REFERENCES POSTS(ID)
);
