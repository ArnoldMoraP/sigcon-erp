--
-- PostgreSQL database dump
--

\restrict ul2ZY1EONlVyjEoDAfHcxak9Xlrre5ouIsJgnzApTFjl0EHhXLfWojp4ge8p1DI

-- Dumped from database version 16.14 (3cbc516)
-- Dumped by pg_dump version 18.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: catalogos; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA catalogos;


--
-- Name: seguridad; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA seguridad;


SET default_table_access_method = heap;

--
-- Name: estado; Type: TABLE; Schema: catalogos; Owner: -
--

CREATE TABLE catalogos.estado (
    id integer NOT NULL,
    nombre character varying(50) NOT NULL
);


--
-- Name: estado_id_seq; Type: SEQUENCE; Schema: catalogos; Owner: -
--

CREATE SEQUENCE catalogos.estado_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: estado_id_seq; Type: SEQUENCE OWNED BY; Schema: catalogos; Owner: -
--

ALTER SEQUENCE catalogos.estado_id_seq OWNED BY catalogos.estado.id;


--
-- Name: rol; Type: TABLE; Schema: seguridad; Owner: -
--

CREATE TABLE seguridad.rol (
    id integer NOT NULL,
    nombre character varying(50) NOT NULL
);


--
-- Name: rol_id_seq; Type: SEQUENCE; Schema: seguridad; Owner: -
--

CREATE SEQUENCE seguridad.rol_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: rol_id_seq; Type: SEQUENCE OWNED BY; Schema: seguridad; Owner: -
--

ALTER SEQUENCE seguridad.rol_id_seq OWNED BY seguridad.rol.id;


--
-- Name: usuario; Type: TABLE; Schema: seguridad; Owner: -
--

CREATE TABLE seguridad.usuario (
    id integer NOT NULL,
    username character varying(100) NOT NULL,
    password character varying(200) NOT NULL,
    rol_id integer,
    estado_id integer
);


--
-- Name: usuario_id_seq; Type: SEQUENCE; Schema: seguridad; Owner: -
--

CREATE SEQUENCE seguridad.usuario_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: usuario_id_seq; Type: SEQUENCE OWNED BY; Schema: seguridad; Owner: -
--

ALTER SEQUENCE seguridad.usuario_id_seq OWNED BY seguridad.usuario.id;


--
-- Name: estado id; Type: DEFAULT; Schema: catalogos; Owner: -
--

ALTER TABLE ONLY catalogos.estado ALTER COLUMN id SET DEFAULT nextval('catalogos.estado_id_seq'::regclass);


--
-- Name: rol id; Type: DEFAULT; Schema: seguridad; Owner: -
--

ALTER TABLE ONLY seguridad.rol ALTER COLUMN id SET DEFAULT nextval('seguridad.rol_id_seq'::regclass);


--
-- Name: usuario id; Type: DEFAULT; Schema: seguridad; Owner: -
--

ALTER TABLE ONLY seguridad.usuario ALTER COLUMN id SET DEFAULT nextval('seguridad.usuario_id_seq'::regclass);


--
-- Data for Name: estado; Type: TABLE DATA; Schema: catalogos; Owner: -
--

COPY catalogos.estado (id, nombre) FROM stdin;
1	Activo
2	Inactivo
\.


--
-- Data for Name: rol; Type: TABLE DATA; Schema: seguridad; Owner: -
--

COPY seguridad.rol (id, nombre) FROM stdin;
1	ADMIN
2	VENTAS
3	ALMACEN
4	RRHH
5	CONSULTA
\.


--
-- Data for Name: usuario; Type: TABLE DATA; Schema: seguridad; Owner: -
--

COPY seguridad.usuario (id, username, password, rol_id, estado_id) FROM stdin;
1	admin	$2a$10$RkHYxmmZwbOipfFXRSBw/OpQfY.p.rj8uXkJ3j2GIhMM9CrM2ZxgW	1	1
2	ventas	$2a$10$RkHYxmmZwbOipfFXRSBw/OpQfY.p.rj8uXkJ3j2GIhMM9CrM2ZxgW	2	1
4	rrhh	$2a$10$RkHYxmmZwbOipfFXRSBw/OpQfY.p.rj8uXkJ3j2GIhMM9CrM2ZxgW	4	1
5	consulta	$2a$10$RkHYxmmZwbOipfFXRSBw/OpQfY.p.rj8uXkJ3j2GIhMM9CrM2ZxgW	5	1
3	almacen	$2a$10$RkHYxmmZwbOipfFXRSBw/OpQfY.p.rj8uXkJ3j2GIhMM9CrM2ZxgW	3	1
8	admin2	$2a$10$mgiygIbAut8Nij6sKReidehZAKaI3c5Dr1g0.hGNwhS7S0ZYZ4RJa	1	\N
\.


--
-- Name: estado_id_seq; Type: SEQUENCE SET; Schema: catalogos; Owner: -
--

SELECT pg_catalog.setval('catalogos.estado_id_seq', 2, true);


--
-- Name: rol_id_seq; Type: SEQUENCE SET; Schema: seguridad; Owner: -
--

SELECT pg_catalog.setval('seguridad.rol_id_seq', 5, true);


--
-- Name: usuario_id_seq; Type: SEQUENCE SET; Schema: seguridad; Owner: -
--

SELECT pg_catalog.setval('seguridad.usuario_id_seq', 8, true);


--
-- Name: estado estado_pkey; Type: CONSTRAINT; Schema: catalogos; Owner: -
--

ALTER TABLE ONLY catalogos.estado
    ADD CONSTRAINT estado_pkey PRIMARY KEY (id);


--
-- Name: rol rol_pkey; Type: CONSTRAINT; Schema: seguridad; Owner: -
--

ALTER TABLE ONLY seguridad.rol
    ADD CONSTRAINT rol_pkey PRIMARY KEY (id);


--
-- Name: usuario usuario_pkey; Type: CONSTRAINT; Schema: seguridad; Owner: -
--

ALTER TABLE ONLY seguridad.usuario
    ADD CONSTRAINT usuario_pkey PRIMARY KEY (id);


--
-- Name: usuario usuario_username_key; Type: CONSTRAINT; Schema: seguridad; Owner: -
--

ALTER TABLE ONLY seguridad.usuario
    ADD CONSTRAINT usuario_username_key UNIQUE (username);


--
-- Name: usuario usuario_estado_id_fkey; Type: FK CONSTRAINT; Schema: seguridad; Owner: -
--

ALTER TABLE ONLY seguridad.usuario
    ADD CONSTRAINT usuario_estado_id_fkey FOREIGN KEY (estado_id) REFERENCES catalogos.estado(id);


--
-- Name: usuario usuario_rol_id_fkey; Type: FK CONSTRAINT; Schema: seguridad; Owner: -
--

ALTER TABLE ONLY seguridad.usuario
    ADD CONSTRAINT usuario_rol_id_fkey FOREIGN KEY (rol_id) REFERENCES seguridad.rol(id);


--
-- PostgreSQL database dump complete
--

\unrestrict ul2ZY1EONlVyjEoDAfHcxak9Xlrre5ouIsJgnzApTFjl0EHhXLfWojp4ge8p1DI

