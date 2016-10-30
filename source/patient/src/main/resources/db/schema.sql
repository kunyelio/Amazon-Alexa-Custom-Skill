drop table PATIENT if exists;

create table PATIENT (ID bigint identity primary key, NAME varchar(40) not null,
                        NUMBER int not null, PULSE int, TEMPERATURE int,
                        SYSP int, DIASP int, unique(NUMBER));
                        
alter table PATIENT ALTER COLUMN TEMPERATURE SET DEFAULT 98;

alter table PATIENT ALTER COLUMN PULSE SET DEFAULT 75;

alter table PATIENT ALTER COLUMN SYSP SET DEFAULT 110;

alter table PATIENT ALTER COLUMN DIASP SET DEFAULT 70;