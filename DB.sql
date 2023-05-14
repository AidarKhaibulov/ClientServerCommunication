--
-- PostgreSQL database dump
--

-- Dumped from database version 15.2 (Debian 15.2-1.pgdg110+1)
-- Dumped by pg_dump version 15.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE IF EXISTS postgres;
--
-- Name: postgres; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE postgres WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.utf8';


ALTER DATABASE postgres OWNER TO postgres;

\connect postgres

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: DATABASE postgres; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON DATABASE postgres IS 'default administrative connection database';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: table1; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.table1 (
    id integer NOT NULL,
    name character varying NOT NULL,
    value integer NOT NULL
);


ALTER TABLE public.table1 OWNER TO postgres;

--
-- Name: table1_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.table1_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.table1_id_seq OWNER TO postgres;

--
-- Name: table1_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.table1_id_seq OWNED BY public.table1.id;


--
-- Name: table2; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.table2 (
    id integer NOT NULL,
    name character varying NOT NULL,
    value integer NOT NULL
);


ALTER TABLE public.table2 OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id integer NOT NULL,
    login character varying NOT NULL,
    password character varying NOT NULL,
    role character varying,
    dac character varying,
    mac character varying
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: table1 id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.table1 ALTER COLUMN id SET DEFAULT nextval('public.table1_id_seq'::regclass);


--
-- Data for Name: table1; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.table1 (id, name, value) FROM stdin;
3	Река	400
4	Элоквенция	200
5	Манускрипт	3
6	Выпуклый	100
1	Ежик	700
2	Змея	234
\.


--
-- Data for Name: table2; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.table2 (id, name, value) FROM stdin;
1	Анатолий	2
2	Ипполит	3
3	Аглая	4
4	Тихон	5
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, login, password, role, dac, mac) FROM stdin;
2	alice	123	R2	1000,2100	secret
3	dima	333	D1	1111,2110	confidential
1	bob	111	R1	1100,2000	NA
5	ivan	322	ADMIN	1000,2000	admin
4	liza	321	R1	1000,2000	top_secret
6   agasfer 666 D2 1111,2111 NA
7   esfir 122 ADMIN 1000,2111 NA
\.


--
-- Name: table1_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.table1_id_seq', 6, true);


--
-- Name: table1 table1_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.table1
    ADD CONSTRAINT table1_pk PRIMARY KEY (id);


--
-- Name: table2 table2_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.table2
    ADD CONSTRAINT table2_pk PRIMARY KEY (id);


--
-- Name: users users_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pk PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

