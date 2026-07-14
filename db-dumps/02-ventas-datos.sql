--
-- PostgreSQL database dump
--

\restrict OSs6wNt5mC7S8yZPcsQckpuVcrw3q7bs6w6Ps1nl8wUexdeGxqOF847g2V5Um32

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
-- Data for Name: cotizacion; Type: TABLE DATA; Schema: comercial; Owner: -
--

INSERT INTO comercial.cotizacion VALUES (9, 'COT-1779840219201', 'Minera Andina Corp', '20512345679', 'Varilla corrugada 1/2', 2.00, 12.50, 25.00, 0.00, 0.00, 25.00, 'APROBADA', 1, '2026-05-26 19:03:39.203661', 'admin');
INSERT INTO comercial.cotizacion VALUES (10, 'COT-1779840454302', 'Minera Andina Corp', '20512345679', 'Varilla corrugada 1/2', 1.00, 12.50, 12.50, 0.00, 0.00, 12.50, 'APROBADA', 1, '2026-05-26 19:07:34.313136', 'admin');
INSERT INTO comercial.cotizacion VALUES (2, 'COT-1779420488031', 'Constructora Lima SAC', '20123456789', 'Plancha de acero', 50.00, 120.00, 6000.00, 7.00, 420.00, 5580.00, 'APROBADA', 1, '2026-05-21 22:28:08.031987', 'admin');
INSERT INTO comercial.cotizacion VALUES (11, 'COT-1779840873055', 'Minera Andina Corp', '20512345679', 'Varilla corrugada 1/2"', 1.00, 12.50, 12.50, 0.00, 0.00, 12.50, 'CONVERTIDA', 1, '2026-05-26 19:14:33.057137', 'admin');
INSERT INTO comercial.cotizacion VALUES (18, 'COT-2026-0016', 'Constructora Lima S.A.', '20512345671', 'Acero estructural A36', 15.00, 850.00, 12750.00, 5.00, 637.50, 12112.50, 'PENDIENTE', 1, '2026-06-29 02:04:22.994004', 'admin');
INSERT INTO comercial.cotizacion VALUES (19, 'COT-2026-0017', 'Minera Andina Corp', '20512345679', 'Plancha de acero 3mm', 25.00, 320.00, 8000.00, 0.00, 0.00, 8000.00, 'APROBADA', 1, '2026-06-29 02:04:22.994004', 'admin');
INSERT INTO comercial.cotizacion VALUES (20, 'COT-2026-0018', 'Infraestructura Sur SAC', '20198765432', 'Tubo galvanizado 2"', 40.00, 45.00, 1800.00, 10.00, 180.00, 1620.00, 'CONVERTIDA', 1, '2026-06-29 02:04:22.994004', 'admin');
INSERT INTO comercial.cotizacion VALUES (21, 'COT-2026-0019', 'Grupo Constructor Norte', '20312345678', 'Varilla corrugada 1/2"', 200.00, 12.50, 2500.00, 0.00, 0.00, 2500.00, 'PENDIENTE', 1, '2026-06-29 02:04:22.994004', 'admin');
INSERT INTO comercial.cotizacion VALUES (22, 'COT-2026-0020', 'Aceros del Pacífico SAC', '20412345679', 'Soldadura 6011', 50.00, 8.50, 425.00, 0.00, 0.00, 425.00, 'CONVERTIDA', 1, '2026-06-29 02:04:22.994004', 'admin');
INSERT INTO comercial.cotizacion VALUES (23, 'COT-1783713581188', 'Prueba Gateway SAC', '20999999999', 'Plancha de acero 3mm', 20.00, 320.00, 6400.00, 5.00, 320.00, 6080.00, 'CONVERTIDA', 1, '2026-07-10 19:59:43.327621', 'admin');
INSERT INTO comercial.cotizacion VALUES (24, 'COT-1783997260085', 'Prueba AuthClient SAC', '20111111111', 'Acero estructural A36', 3.00, 850.00, 2550.00, 0.00, 0.00, 2550.00, 'PENDIENTE', 1, '2026-07-14 02:47:40.706509', 'admin');


--
-- Data for Name: despacho; Type: TABLE DATA; Schema: comercial; Owner: -
--

INSERT INTO comercial.despacho VALUES (1, 'DES-0001', 'Minera Andina Corp', '20512345679', 'Barras corrugadas 3/4', 10, 'Av. Industrial 450, Lima', 'Transportes Rápidos SAC', '2.5 TN', 'PENDIENTE', 2, NULL, 1, '2026-05-26 18:36:14.331138', '2026-05-26 18:36:14.326793', '2026-05-26 18:36:14.331138', NULL, false, NULL, NULL);
INSERT INTO comercial.despacho VALUES (7, 'DES-0007', 'Constructora Lima S.A.', '20512345671', 'Acero estructural A36', 15, 'Av. Los Constructores 123, Lima', 'Transportes Rápidos SAC', '8.5 TN', 'ENTREGADO', NULL, NULL, 1, '2026-06-29 02:11:47.085005', '2026-06-29 02:11:47.085005', '2026-06-29 02:11:47.085005', 'COMP-0007', true, NULL, NULL);
INSERT INTO comercial.despacho VALUES (8, 'DES-0008', 'Minera Andina Corp', '20512345679', 'Plancha de acero 3mm', 25, 'Carretera Central Km 45, Junín', 'Logística del Centro SAC', '12.0 TN', 'ENTREGADO', NULL, NULL, 1, '2026-06-29 02:11:47.085005', '2026-06-29 02:11:47.085005', '2026-06-29 02:11:47.085005', 'COMP-0008', true, NULL, NULL);
INSERT INTO comercial.despacho VALUES (9, 'DES-0009', 'Infraestructura Sur SAC', '20198765432', 'Tubo galvanizado 2"', 40, 'Av. Metropolitana 890, Arequipa', 'Sur Cargo SAC', '3.2 TN', 'PREPARADO', NULL, NULL, 1, '2026-06-29 02:11:47.085005', '2026-06-29 02:11:47.085005', '2026-06-29 02:11:47.085005', NULL, false, NULL, NULL);
INSERT INTO comercial.despacho VALUES (10, 'DES-0010', 'Grupo Constructor Norte', '20312345678', 'Varilla corrugada 1/2"', 200, 'Av. Industrial 234, Trujillo', 'Norte Express SAC', '5.0 TN', 'PENDIENTE', NULL, NULL, 1, '2026-06-29 02:11:47.085005', '2026-06-29 02:11:47.085005', '2026-06-29 02:11:47.085005', NULL, false, NULL, NULL);


--
-- Data for Name: pedido; Type: TABLE DATA; Schema: comercial; Owner: -
--

INSERT INTO comercial.pedido VALUES (15, 'PED-0001', ' Constructora Lima SAC', '20123456789', 'Plancha de acero 3mm', 5.00, 320.00, 1600.00, 288.00, 1888.00, 'PENDIENTE', 1, '2026-05-26 19:01:04.741901');
INSERT INTO comercial.pedido VALUES (16, 'PED-0002', 'Minera Andina Corp', '20512345679', 'Varilla corrugada 1/2"', 1.00, 12.50, 12.50, 2.25, 14.75, 'PENDIENTE', 1, '2026-06-01 18:42:03.858987');
INSERT INTO comercial.pedido VALUES (25, 'PED-0025', 'Constructora Lima S.A.', '20512345671', 'Acero estructural A36', 15.00, 850.00, 12750.00, 2295.00, 15045.00, 'APROBADO', 1, '2026-06-29 02:04:33.130639');
INSERT INTO comercial.pedido VALUES (26, 'PED-0026', 'Minera Andina Corp', '20512345679', 'Plancha de acero 3mm', 25.00, 320.00, 8000.00, 1440.00, 9440.00, 'FACTURADO', 1, '2026-06-29 02:04:33.130639');
INSERT INTO comercial.pedido VALUES (27, 'PED-0027', 'Infraestructura Sur SAC', '20198765432', 'Tubo galvanizado 2"', 40.00, 45.00, 1800.00, 324.00, 2124.00, 'FACTURADO', 1, '2026-06-29 02:04:33.130639');
INSERT INTO comercial.pedido VALUES (28, 'PED-0028', 'Grupo Constructor Norte', '20312345678', 'Varilla corrugada 1/2"', 200.00, 12.50, 2500.00, 450.00, 2950.00, 'PENDIENTE', 1, '2026-06-29 02:04:33.130639');
INSERT INTO comercial.pedido VALUES (29, 'PED-0029', 'Aceros del Pacífico SAC', '20412345679', 'Soldadura 6011', 50.00, 8.50, 425.00, 76.50, 501.50, 'APROBADO', 1, '2026-06-29 02:04:33.130639');
INSERT INTO comercial.pedido VALUES (30, 'PED-0030', 'Aceros del Pacífico SAC', '20412345679', 'Soldadura 6011', 50.00, 8.50, 425.00, 76.50, 501.50, 'PENDIENTE', 1, '2026-07-10 19:43:15.136711');
INSERT INTO comercial.pedido VALUES (31, 'PED-0031', 'Prueba Gateway SAC', '20999999999', 'Plancha de acero 3mm', 20.00, 320.00, 6400.00, 1152.00, 7552.00, 'PENDIENTE', 1, '2026-07-10 20:00:04.909431');


--
-- Data for Name: venta; Type: TABLE DATA; Schema: comercial; Owner: -
--

INSERT INTO comercial.venta VALUES (2, 'VEN-0002', 'Minera Andina Corp', '20512345679', 'Barras corrugadas 3/4', 10, 500.00, 5000.00, 900.00, 5900.00, NULL, 'COMPLETADO', '2026-05-26 18:36:00.382722', 2, NULL, 1, '2026-05-26 18:36:00.378335', '2026-05-26 18:36:14.334138');
INSERT INTO comercial.venta VALUES (10, 'VEN-0010', 'Constructora Lima S.A.', '20512345671', 'Acero estructural A36', 15, 850.00, 12750.00, 2295.00, 15045.00, 'Carlos Mendoza', 'COMPLETADO', '2026-06-29 02:09:43.719894', NULL, NULL, 1, '2026-06-29 02:09:43.719894', '2026-06-29 02:09:43.719894');
INSERT INTO comercial.venta VALUES (11, 'VEN-0011', 'Minera Andina Corp', '20512345679', 'Plancha de acero 3mm', 25, 320.00, 8000.00, 1440.00, 9440.00, 'María Torres', 'COMPLETADO', '2026-06-29 02:09:43.719894', NULL, NULL, 1, '2026-06-29 02:09:43.719894', '2026-06-29 02:09:43.719894');
INSERT INTO comercial.venta VALUES (12, 'VEN-0012', 'Infraestructura Sur SAC', '20198765432', 'Tubo galvanizado 2"', 40, 45.00, 1800.00, 324.00, 2124.00, 'Carlos Mendoza', 'APROBADO', '2026-06-29 02:09:43.719894', NULL, NULL, 1, '2026-06-29 02:09:43.719894', '2026-06-29 02:09:43.719894');
INSERT INTO comercial.venta VALUES (13, 'VEN-0013', 'Grupo Constructor Norte', '20312345678', 'Varilla corrugada 1/2"', 200, 12.50, 2500.00, 450.00, 2950.00, 'María Torres', 'PENDIENTE', '2026-06-29 02:09:43.719894', NULL, NULL, 1, '2026-06-29 02:09:43.719894', '2026-06-29 02:09:43.719894');
INSERT INTO comercial.venta VALUES (14, 'VEN-0014', 'Aceros del Pacífico SAC', '20412345679', 'Soldadura 6011', 50, 8.50, 425.00, 76.50, 501.50, 'Carlos Mendoza', 'COMPLETADO', '2026-06-29 02:09:43.719894', NULL, NULL, 1, '2026-06-29 02:09:43.719894', '2026-06-29 02:09:43.719894');
INSERT INTO comercial.venta VALUES (15, 'VEN-0015', 'Constructora Lima S.A.', '20512345671', 'Barras corrugadas 3/4', 30, 500.00, 15000.00, 2700.00, 17700.00, 'María Torres', 'COMPLETADO', '2026-06-29 02:09:43.719894', NULL, NULL, 1, '2026-06-29 02:09:43.719894', '2026-06-29 02:09:43.719894');


--
-- Name: cotizacion_id_seq; Type: SEQUENCE SET; Schema: comercial; Owner: -
--

SELECT pg_catalog.setval('comercial.cotizacion_id_seq', 24, true);


--
-- Name: despacho_id_seq; Type: SEQUENCE SET; Schema: comercial; Owner: -
--

SELECT pg_catalog.setval('comercial.despacho_id_seq', 10, true);


--
-- Name: pedido_id_seq; Type: SEQUENCE SET; Schema: comercial; Owner: -
--

SELECT pg_catalog.setval('comercial.pedido_id_seq', 31, true);


--
-- Name: venta_id_seq; Type: SEQUENCE SET; Schema: comercial; Owner: -
--

SELECT pg_catalog.setval('comercial.venta_id_seq', 15, true);


--
-- PostgreSQL database dump complete
--

\unrestrict OSs6wNt5mC7S8yZPcsQckpuVcrw3q7bs6w6Ps1nl8wUexdeGxqOF847g2V5Um32

