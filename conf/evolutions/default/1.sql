# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table jpeg_image (
  id                        bigint not null,
  image_byte_array          bytea,
  created                   timestamp,
  constraint pk_jpeg_image primary key (id))
;

create table s3file (
  id                        bigint not null,
  bucket                    varchar(255),
  name                      varchar(255),
  constraint pk_s3file primary key (id))
;

create sequence jpeg_image_seq;

create sequence s3file_seq;




# --- !Downs

drop table if exists jpeg_image cascade;

drop table if exists s3file cascade;

drop sequence if exists jpeg_image_seq;

drop sequence if exists s3file_seq;

