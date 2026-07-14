--
-- PostgreSQL database dump
--

\restrict ejVZ1DTayjPfCR7hwToLk0MZzlVMcXyUgJqbzbNROgBnNMQTBKxtkPkZyoNmAnZ

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
-- Data for Name: estado; Type: TABLE DATA; Schema: catalogos; Owner: -
--

INSERT INTO catalogos.estado VALUES (1, 'Activo');
INSERT INTO catalogos.estado VALUES (2, 'Inactivo');


--
-- Data for Name: rol; Type: TABLE DATA; Schema: seguridad; Owner: -
--

INSERT INTO seguridad.rol VALUES (1, 'ADMIN');
INSERT INTO seguridad.rol VALUES (2, 'VENTAS');
INSERT INTO seguridad.rol VALUES (3, 'ALMACEN');
INSERT INTO seguridad.rol VALUES (4, 'RRHH');
INSERT INTO seguridad.rol VALUES (5, 'CONSULTA');


--
-- Data for Name: usuario; Type: TABLE DATA; Schema: seguridad; Owner: -
--

INSERT INTO seguridad.usuario VALUES (1, 'admin', '$2a$10$RkHYxmmZwbOipfFXRSBw/OpQfY.p.rj8uXkJ3j2GIhMM9CrM2ZxgW', 1, 1);
INSERT INTO seguridad.usuario VALUES (2, 'ventas', '$2a$10$RkHYxmmZwbOipfFXRSBw/OpQfY.p.rj8uXkJ3j2GIhMM9CrM2ZxgW', 2, 1);
INSERT INTO seguridad.usuario VALUES (4, 'rrhh', '$2a$10$RkHYxmmZwbOipfFXRSBw/OpQfY.p.rj8uXkJ3j2GIhMM9CrM2ZxgW', 4, 1);
INSERT INTO seguridad.usuario VALUES (5, 'consulta', '$2a$10$RkHYxmmZwbOipfFXRSBw/OpQfY.p.rj8uXkJ3j2GIhMM9CrM2ZxgW', 5, 1);
INSERT INTO seguridad.usuario VALUES (3, 'almacen', '$2a$10$RkHYxmmZwbOipfFXRSBw/OpQfY.p.rj8uXkJ3j2GIhMM9CrM2ZxgW', 3, 1);
INSERT INTO seguridad.usuario VALUES (8, 'admin2', '$2a$10$mgiygIbAut8Nij6sKReidehZAKaI3c5Dr1g0.hGNwhS7S0ZYZ4RJa', 1, NULL);


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
-- PostgreSQL database dump complete
--

\unrestrict ejVZ1DTayjPfCR7hwToLk0MZzlVMcXyUgJqbzbNROgBnNMQTBKxtkPkZyoNmAnZ

