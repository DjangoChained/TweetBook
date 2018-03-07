SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;
COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';

CREATE TYPE activityvisibility AS ENUM (
    'authoronly',
    'friends',
    'all'
);

CREATE TYPE reaction AS ENUM (
    'like',
    'dislike'
);

SET default_tablespace = '';
SET default_with_oids = false;

CREATE TABLE activity (
    id integer NOT NULL,
    date timestamp without time zone NOT NULL,
    id_human integer NOT NULL
);
CREATE SEQUENCE activity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE activity_id_seq OWNED BY activity.id;

CREATE TABLE friendshipactivity (
    id integer DEFAULT nextval('activity_id_seq'::regclass) NOT NULL,
    id_second_human integer NOT NULL
);

CREATE TABLE human (
    id integer NOT NULL,
    firstname text NOT NULL,
    lastname text NOT NULL,
    birthdate timestamp without time zone,
    email text NOT NULL,
    username text NOT NULL,
    password text,
    activityvisibility activityvisibility DEFAULT 'friends'::activityvisibility NOT NULL
);
CREATE SEQUENCE human_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE human_id_seq OWNED BY human.id;

CREATE TABLE linkpost (
    id integer DEFAULT nextval('activity_id_seq'::regclass) NOT NULL,
    url text NOT NULL,
    title text NOT NULL
);

CREATE TABLE photopost (
    id integer DEFAULT nextval('activity_id_seq'::regclass) NOT NULL,
    photopath text NOT NULL
);

CREATE TABLE post (
    id integer DEFAULT nextval('activity_id_seq'::regclass) NOT NULL,
    content text NOT NULL
);

CREATE TABLE reactionactivity (
    id integer DEFAULT nextval('activity_id_seq'::regclass) NOT NULL,
    reaction reaction NOT NULL,
    id_post integer NOT NULL
);

CREATE TABLE textpost (
    id integer DEFAULT nextval('activity_id_seq'::regclass) NOT NULL
);

ALTER TABLE ONLY activity ALTER COLUMN id SET DEFAULT nextval('activity_id_seq'::regclass);
ALTER TABLE ONLY human ALTER COLUMN id SET DEFAULT nextval('human_id_seq'::regclass);
ALTER TABLE ONLY activity
    ADD CONSTRAINT activity_pkey PRIMARY KEY (id);
ALTER TABLE ONLY human
    ADD CONSTRAINT human_pkey PRIMARY KEY (id);
ALTER TABLE ONLY post
    ADD CONSTRAINT post_id_key UNIQUE (id);
ALTER TABLE ONLY activity
    ADD CONSTRAINT fk_first_human FOREIGN KEY (id_human) REFERENCES human(id);
ALTER TABLE ONLY friendshipactivity
    ADD CONSTRAINT fk_second_human FOREIGN KEY (id_second_human) REFERENCES human(id);
ALTER TABLE ONLY reactionactivity
    ADD CONSTRAINT reactionactivity_id_post_fkey FOREIGN KEY (id_post) REFERENCES post(id);
