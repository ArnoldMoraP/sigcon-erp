--
-- PostgreSQL database dump
--


-- Dumped from database version 16.14 (3cbc516)
-- Dumped by pg_dump version 18.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: inventario; Type: TABLE DATA; Schema: almacen; Owner: -
--

INSERT INTO public.inventario VALUES (3, 'Tubo galvanizado 2"', 'Tubos', 200, 30, 45.00, 'unidad', '2026-05-25 21:14:39.605472');
INSERT INTO public.inventario VALUES (4, 'Varilla corrugada 1/2"', 'Varillas', 500, 50, 12.50, 'unidad', '2026-05-25 21:14:39.605472');
INSERT INTO public.inventario VALUES (6, 'Plancha antideslizante', 'Planchas', 5, 15, 380.00, 'unidad', '2026-05-25 21:14:39.605472');
INSERT INTO public.inventario VALUES (7, 'Tubo cuadrado 1"', 'Tubos', 120, 25, 35.00, 'unidad', '2026-05-25 21:14:39.605472');
INSERT INTO public.inventario VALUES (9, 'Cable de acero 1/2', 'Cables', 100, 50, 15.50, 'metro', '2026-05-25 21:44:29.723494');
INSERT INTO public.inventario VALUES (2, 'Plancha de acero 3mm', 'Planchas', 81, 15, 320.00, 'unidad', '2026-05-26 21:21:29.45467');
INSERT INTO public.inventario VALUES (5, 'Angulo de acero 2x2', 'Angulos', 7, 20, 28.00, 'unidad', '2026-05-25 21:14:39.605472');
INSERT INTO public.inventario VALUES (8, 'Soldadura 6011', 'Insumos', 299, 100, 8.50, 'kg', '2026-05-26 21:21:01.533829');
INSERT INTO public.inventario VALUES (10, 'Barras de acero 1/2"', 'General', 30, 10, 1500.00, 'unidad', '2026-06-21 13:57:41.919836');
INSERT INTO public.inventario VALUES (1, 'Acero estructural A36', 'Acero', 157, 20, 850.00, 'tonelada', '2026-07-14 02:53:31.333997');


--
-- Data for Name: proveedor; Type: TABLE DATA; Schema: compras; Owner: -
--

INSERT INTO public.proveedor VALUES (1, 'Aceros Lima SAC', 'Peru', '20111111111', 'Carlos Ruiz', '999111222', 'Acero', 4.8, 'Activo', '2026-06-18 17:01:34.600702', '2026-06-18 17:01:34.600702');
INSERT INTO public.proveedor VALUES (2, 'Metalurgica Andina SAC', 'Peru', '20222222222', 'Luis Torres', '999333444', 'Metales', 4.5, 'Activo', '2026-06-18 17:01:34.600702', '2026-06-18 17:01:34.600702');


--
-- Data for Name: orden_compra; Type: TABLE DATA; Schema: compras; Owner: -
--

INSERT INTO public.orden_compra VALUES (2, 'OC-0002', 2, 'Tubos Estructurales', '50', 18000.00, '2026-06-28', 'Pendiente', NULL, '2026-06-18 17:01:34.600702', '2026-06-18 22:17:41.41848');
INSERT INTO public.orden_compra VALUES (1, 'OC-0001', 1, 'Planchas de Acero A36', '100', 25000.00, '2026-06-25', 'Pendiente', NULL, '2026-06-18 17:01:34.600702', '2026-06-18 22:17:44.993328');
INSERT INTO public.orden_compra VALUES (3, 'OC-0003', 1, 'Barras de acero 1/2"', '30 TM', 45000.00, '2026-07-15', 'Aprobada', NULL, '2026-06-21 16:32:03.176647', '2026-06-21 13:14:01.669685');
INSERT INTO public.orden_compra VALUES (4, 'OC-0004', 1, 'Tubos Estructurales', '30 TM', 25.00, '2026-12-05', 'Pendiente', NULL, '2026-06-23 19:56:07.29401', '2026-06-23 19:56:40.046413');


--
-- Data for Name: compra; Type: TABLE DATA; Schema: compras; Owner: -
--

INSERT INTO public.compra VALUES (1, 1, 1, NULL, 'Planchas de Acero A36', 100, 250.00, 25000.00, '2026-06-18 17:01:34.600702', NULL);
INSERT INTO public.compra VALUES (2, 2, 2, NULL, 'Tubos Estructurales', 50, 360.00, 18000.00, '2026-06-18 17:01:34.600702', NULL);
INSERT INTO public.compra VALUES (3, 3, 1, 10, 'Barras de acero 1/2"', 30, 1500.00, 45000.00, '2026-06-21 18:57:43.078036', NULL);


--
-- Data for Name: factura_proveedor; Type: TABLE DATA; Schema: compras; Owner: -
--



--
-- Data for Name: pago_proveedor; Type: TABLE DATA; Schema: compras; Owner: -
--



--
-- Data for Name: presupuesto; Type: TABLE DATA; Schema: compras; Owner: -
--

INSERT INTO public.presupuesto VALUES (1, '2026-06', 3000000.00, 2954975.00, '2026-06-23 19:56:06.940746');


--
-- Name: inventario_id_seq; Type: SEQUENCE SET; Schema: almacen; Owner: -
--

SELECT pg_catalog.setval('public.inventario_id_seq', 10, true);


--
-- Name: compra_id_seq; Type: SEQUENCE SET; Schema: compras; Owner: -
--

SELECT pg_catalog.setval('public.compra_id_seq', 3, true);


--
-- Name: factura_proveedor_id_seq; Type: SEQUENCE SET; Schema: compras; Owner: -
--

SELECT pg_catalog.setval('public.factura_proveedor_id_seq', 1, false);


--
-- Name: orden_compra_id_seq; Type: SEQUENCE SET; Schema: compras; Owner: -
--

SELECT pg_catalog.setval('public.orden_compra_id_seq', 4, true);


--
-- Name: pago_proveedor_id_seq; Type: SEQUENCE SET; Schema: compras; Owner: -
--

SELECT pg_catalog.setval('public.pago_proveedor_id_seq', 1, false);


--
-- Name: presupuesto_id_seq; Type: SEQUENCE SET; Schema: compras; Owner: -
--

SELECT pg_catalog.setval('public.presupuesto_id_seq', 1, true);


--
-- Name: proveedor_id_seq; Type: SEQUENCE SET; Schema: compras; Owner: -
--

SELECT pg_catalog.setval('public.proveedor_id_seq', 3, true);


--
-- PostgreSQL database dump complete
--


