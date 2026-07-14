--
-- PostgreSQL database dump
--

\restrict IkEPsGSJ9buxBI8gFo1hHhutRhiVDsulthjo7lxZ2ZryiG77j8hLRAY4n1x5ldm

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
-- Name: comercial; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA comercial;


SET default_table_access_method = heap;

--
-- Name: cotizacion; Type: TABLE; Schema: comercial; Owner: -
--

CREATE TABLE comercial.cotizacion (
    id integer NOT NULL,
    codigo character varying(50) NOT NULL,
    cliente character varying(200) NOT NULL,
    ruc character varying(20),
    producto character varying(200) NOT NULL,
    cantidad numeric(12,2) NOT NULL,
    precio_unitario numeric(12,2) NOT NULL,
    subtotal numeric(12,2) NOT NULL,
    descuento_porcentaje numeric(5,2) DEFAULT 0,
    descuento_monto numeric(12,2) DEFAULT 0,
    total numeric(12,2) NOT NULL,
    estado character varying(30) DEFAULT 'PENDIENTE'::character varying,
    usuario_id integer,
    fecha_registro timestamp without time zone DEFAULT now(),
    vendedor character varying(100)
);


--
-- Name: cotizacion_id_seq; Type: SEQUENCE; Schema: comercial; Owner: -
--

CREATE SEQUENCE comercial.cotizacion_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cotizacion_id_seq; Type: SEQUENCE OWNED BY; Schema: comercial; Owner: -
--

ALTER SEQUENCE comercial.cotizacion_id_seq OWNED BY comercial.cotizacion.id;


--
-- Name: despacho; Type: TABLE; Schema: comercial; Owner: -
--

CREATE TABLE comercial.despacho (
    id bigint NOT NULL,
    codigo character varying(20) NOT NULL,
    cliente character varying(200) NOT NULL,
    ruc character varying(20),
    producto character varying(200) NOT NULL,
    cantidad integer NOT NULL,
    direccion character varying(300),
    transportista character varying(100),
    peso character varying(50),
    estado character varying(20) DEFAULT 'PENDIENTE'::character varying NOT NULL,
    venta_id bigint,
    pedido_id bigint,
    usuario_id bigint,
    fecha_despacho timestamp without time zone DEFAULT now(),
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    comprobante character varying(300),
    comprobante_validado boolean DEFAULT false,
    fecha_validacion_comprobante timestamp without time zone,
    fecha_entrega timestamp without time zone
);


--
-- Name: despacho_id_seq; Type: SEQUENCE; Schema: comercial; Owner: -
--

CREATE SEQUENCE comercial.despacho_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: despacho_id_seq; Type: SEQUENCE OWNED BY; Schema: comercial; Owner: -
--

ALTER SEQUENCE comercial.despacho_id_seq OWNED BY comercial.despacho.id;


--
-- Name: pedido; Type: TABLE; Schema: comercial; Owner: -
--

CREATE TABLE comercial.pedido (
    id integer NOT NULL,
    codigo character varying(50) NOT NULL,
    cliente character varying(200) NOT NULL,
    ruc character varying(20),
    producto character varying(200) NOT NULL,
    cantidad numeric(12,2) NOT NULL,
    precio_unitario numeric(12,2) NOT NULL,
    subtotal numeric(12,2) NOT NULL,
    igv numeric(12,2) NOT NULL,
    total numeric(12,2) NOT NULL,
    estado character varying(30) DEFAULT 'PENDIENTE'::character varying NOT NULL,
    usuario_id integer,
    fecha_registro timestamp without time zone DEFAULT now()
);


--
-- Name: pedido_id_seq; Type: SEQUENCE; Schema: comercial; Owner: -
--

CREATE SEQUENCE comercial.pedido_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: pedido_id_seq; Type: SEQUENCE OWNED BY; Schema: comercial; Owner: -
--

ALTER SEQUENCE comercial.pedido_id_seq OWNED BY comercial.pedido.id;


--
-- Name: venta; Type: TABLE; Schema: comercial; Owner: -
--

CREATE TABLE comercial.venta (
    id bigint NOT NULL,
    codigo character varying(20) NOT NULL,
    cliente character varying(200) NOT NULL,
    ruc character varying(20),
    producto character varying(200) NOT NULL,
    cantidad integer NOT NULL,
    precio_unitario numeric(12,2) NOT NULL,
    subtotal numeric(14,2) NOT NULL,
    igv numeric(14,2) NOT NULL,
    total numeric(14,2) NOT NULL,
    vendedor character varying(100),
    estado character varying(20) DEFAULT 'PENDIENTE'::character varying NOT NULL,
    fecha_venta timestamp without time zone DEFAULT now(),
    pedido_id bigint,
    cotizacion_id bigint,
    usuario_id bigint,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: venta_id_seq; Type: SEQUENCE; Schema: comercial; Owner: -
--

CREATE SEQUENCE comercial.venta_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: venta_id_seq; Type: SEQUENCE OWNED BY; Schema: comercial; Owner: -
--

ALTER SEQUENCE comercial.venta_id_seq OWNED BY comercial.venta.id;


--
-- Name: cotizacion id; Type: DEFAULT; Schema: comercial; Owner: -
--

ALTER TABLE ONLY comercial.cotizacion ALTER COLUMN id SET DEFAULT nextval('comercial.cotizacion_id_seq'::regclass);


--
-- Name: despacho id; Type: DEFAULT; Schema: comercial; Owner: -
--

ALTER TABLE ONLY comercial.despacho ALTER COLUMN id SET DEFAULT nextval('comercial.despacho_id_seq'::regclass);


--
-- Name: pedido id; Type: DEFAULT; Schema: comercial; Owner: -
--

ALTER TABLE ONLY comercial.pedido ALTER COLUMN id SET DEFAULT nextval('comercial.pedido_id_seq'::regclass);


--
-- Name: venta id; Type: DEFAULT; Schema: comercial; Owner: -
--

ALTER TABLE ONLY comercial.venta ALTER COLUMN id SET DEFAULT nextval('comercial.venta_id_seq'::regclass);


--
-- Data for Name: cotizacion; Type: TABLE DATA; Schema: comercial; Owner: -
--

COPY comercial.cotizacion (id, codigo, cliente, ruc, producto, cantidad, precio_unitario, subtotal, descuento_porcentaje, descuento_monto, total, estado, usuario_id, fecha_registro, vendedor) FROM stdin;
9	COT-1779840219201	Minera Andina Corp	20512345679	Varilla corrugada 1/2	2.00	12.50	25.00	0.00	0.00	25.00	APROBADA	1	2026-05-26 19:03:39.203661	admin
10	COT-1779840454302	Minera Andina Corp	20512345679	Varilla corrugada 1/2	1.00	12.50	12.50	0.00	0.00	12.50	APROBADA	1	2026-05-26 19:07:34.313136	admin
2	COT-1779420488031	Constructora Lima SAC	20123456789	Plancha de acero	50.00	120.00	6000.00	7.00	420.00	5580.00	APROBADA	1	2026-05-21 22:28:08.031987	admin
11	COT-1779840873055	Minera Andina Corp	20512345679	Varilla corrugada 1/2"	1.00	12.50	12.50	0.00	0.00	12.50	CONVERTIDA	1	2026-05-26 19:14:33.057137	admin
18	COT-2026-0016	Constructora Lima S.A.	20512345671	Acero estructural A36	15.00	850.00	12750.00	5.00	637.50	12112.50	PENDIENTE	1	2026-06-29 02:04:22.994004	admin
19	COT-2026-0017	Minera Andina Corp	20512345679	Plancha de acero 3mm	25.00	320.00	8000.00	0.00	0.00	8000.00	APROBADA	1	2026-06-29 02:04:22.994004	admin
20	COT-2026-0018	Infraestructura Sur SAC	20198765432	Tubo galvanizado 2"	40.00	45.00	1800.00	10.00	180.00	1620.00	CONVERTIDA	1	2026-06-29 02:04:22.994004	admin
21	COT-2026-0019	Grupo Constructor Norte	20312345678	Varilla corrugada 1/2"	200.00	12.50	2500.00	0.00	0.00	2500.00	PENDIENTE	1	2026-06-29 02:04:22.994004	admin
22	COT-2026-0020	Aceros del Pacífico SAC	20412345679	Soldadura 6011	50.00	8.50	425.00	0.00	0.00	425.00	CONVERTIDA	1	2026-06-29 02:04:22.994004	admin
23	COT-1783713581188	Prueba Gateway SAC	20999999999	Plancha de acero 3mm	20.00	320.00	6400.00	5.00	320.00	6080.00	CONVERTIDA	1	2026-07-10 19:59:43.327621	admin
24	COT-1783997260085	Prueba AuthClient SAC	20111111111	Acero estructural A36	3.00	850.00	2550.00	0.00	0.00	2550.00	PENDIENTE	1	2026-07-14 02:47:40.706509	admin
\.


--
-- Data for Name: despacho; Type: TABLE DATA; Schema: comercial; Owner: -
--

COPY comercial.despacho (id, codigo, cliente, ruc, producto, cantidad, direccion, transportista, peso, estado, venta_id, pedido_id, usuario_id, fecha_despacho, created_at, updated_at, comprobante, comprobante_validado, fecha_validacion_comprobante, fecha_entrega) FROM stdin;
1	DES-0001	Minera Andina Corp	20512345679	Barras corrugadas 3/4	10	Av. Industrial 450, Lima	Transportes Rápidos SAC	2.5 TN	PENDIENTE	2	\N	1	2026-05-26 18:36:14.331138	2026-05-26 18:36:14.326793	2026-05-26 18:36:14.331138	\N	f	\N	\N
7	DES-0007	Constructora Lima S.A.	20512345671	Acero estructural A36	15	Av. Los Constructores 123, Lima	Transportes Rápidos SAC	8.5 TN	ENTREGADO	\N	\N	1	2026-06-29 02:11:47.085005	2026-06-29 02:11:47.085005	2026-06-29 02:11:47.085005	COMP-0007	t	\N	\N
8	DES-0008	Minera Andina Corp	20512345679	Plancha de acero 3mm	25	Carretera Central Km 45, Junín	Logística del Centro SAC	12.0 TN	ENTREGADO	\N	\N	1	2026-06-29 02:11:47.085005	2026-06-29 02:11:47.085005	2026-06-29 02:11:47.085005	COMP-0008	t	\N	\N
9	DES-0009	Infraestructura Sur SAC	20198765432	Tubo galvanizado 2"	40	Av. Metropolitana 890, Arequipa	Sur Cargo SAC	3.2 TN	PREPARADO	\N	\N	1	2026-06-29 02:11:47.085005	2026-06-29 02:11:47.085005	2026-06-29 02:11:47.085005	\N	f	\N	\N
10	DES-0010	Grupo Constructor Norte	20312345678	Varilla corrugada 1/2"	200	Av. Industrial 234, Trujillo	Norte Express SAC	5.0 TN	PENDIENTE	\N	\N	1	2026-06-29 02:11:47.085005	2026-06-29 02:11:47.085005	2026-06-29 02:11:47.085005	\N	f	\N	\N
\.


--
-- Data for Name: pedido; Type: TABLE DATA; Schema: comercial; Owner: -
--

COPY comercial.pedido (id, codigo, cliente, ruc, producto, cantidad, precio_unitario, subtotal, igv, total, estado, usuario_id, fecha_registro) FROM stdin;
15	PED-0001	 Constructora Lima SAC	20123456789	Plancha de acero 3mm	5.00	320.00	1600.00	288.00	1888.00	PENDIENTE	1	2026-05-26 19:01:04.741901
16	PED-0002	Minera Andina Corp	20512345679	Varilla corrugada 1/2"	1.00	12.50	12.50	2.25	14.75	PENDIENTE	1	2026-06-01 18:42:03.858987
25	PED-0025	Constructora Lima S.A.	20512345671	Acero estructural A36	15.00	850.00	12750.00	2295.00	15045.00	APROBADO	1	2026-06-29 02:04:33.130639
26	PED-0026	Minera Andina Corp	20512345679	Plancha de acero 3mm	25.00	320.00	8000.00	1440.00	9440.00	FACTURADO	1	2026-06-29 02:04:33.130639
27	PED-0027	Infraestructura Sur SAC	20198765432	Tubo galvanizado 2"	40.00	45.00	1800.00	324.00	2124.00	FACTURADO	1	2026-06-29 02:04:33.130639
28	PED-0028	Grupo Constructor Norte	20312345678	Varilla corrugada 1/2"	200.00	12.50	2500.00	450.00	2950.00	PENDIENTE	1	2026-06-29 02:04:33.130639
29	PED-0029	Aceros del Pacífico SAC	20412345679	Soldadura 6011	50.00	8.50	425.00	76.50	501.50	APROBADO	1	2026-06-29 02:04:33.130639
30	PED-0030	Aceros del Pacífico SAC	20412345679	Soldadura 6011	50.00	8.50	425.00	76.50	501.50	PENDIENTE	1	2026-07-10 19:43:15.136711
31	PED-0031	Prueba Gateway SAC	20999999999	Plancha de acero 3mm	20.00	320.00	6400.00	1152.00	7552.00	PENDIENTE	1	2026-07-10 20:00:04.909431
\.


--
-- Data for Name: venta; Type: TABLE DATA; Schema: comercial; Owner: -
--

COPY comercial.venta (id, codigo, cliente, ruc, producto, cantidad, precio_unitario, subtotal, igv, total, vendedor, estado, fecha_venta, pedido_id, cotizacion_id, usuario_id, created_at, updated_at) FROM stdin;
2	VEN-0002	Minera Andina Corp	20512345679	Barras corrugadas 3/4	10	500.00	5000.00	900.00	5900.00	\N	COMPLETADO	2026-05-26 18:36:00.382722	2	\N	1	2026-05-26 18:36:00.378335	2026-05-26 18:36:14.334138
10	VEN-0010	Constructora Lima S.A.	20512345671	Acero estructural A36	15	850.00	12750.00	2295.00	15045.00	Carlos Mendoza	COMPLETADO	2026-06-29 02:09:43.719894	\N	\N	1	2026-06-29 02:09:43.719894	2026-06-29 02:09:43.719894
11	VEN-0011	Minera Andina Corp	20512345679	Plancha de acero 3mm	25	320.00	8000.00	1440.00	9440.00	María Torres	COMPLETADO	2026-06-29 02:09:43.719894	\N	\N	1	2026-06-29 02:09:43.719894	2026-06-29 02:09:43.719894
12	VEN-0012	Infraestructura Sur SAC	20198765432	Tubo galvanizado 2"	40	45.00	1800.00	324.00	2124.00	Carlos Mendoza	APROBADO	2026-06-29 02:09:43.719894	\N	\N	1	2026-06-29 02:09:43.719894	2026-06-29 02:09:43.719894
13	VEN-0013	Grupo Constructor Norte	20312345678	Varilla corrugada 1/2"	200	12.50	2500.00	450.00	2950.00	María Torres	PENDIENTE	2026-06-29 02:09:43.719894	\N	\N	1	2026-06-29 02:09:43.719894	2026-06-29 02:09:43.719894
14	VEN-0014	Aceros del Pacífico SAC	20412345679	Soldadura 6011	50	8.50	425.00	76.50	501.50	Carlos Mendoza	COMPLETADO	2026-06-29 02:09:43.719894	\N	\N	1	2026-06-29 02:09:43.719894	2026-06-29 02:09:43.719894
15	VEN-0015	Constructora Lima S.A.	20512345671	Barras corrugadas 3/4	30	500.00	15000.00	2700.00	17700.00	María Torres	COMPLETADO	2026-06-29 02:09:43.719894	\N	\N	1	2026-06-29 02:09:43.719894	2026-06-29 02:09:43.719894
\.


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
-- Name: cotizacion cotizacion_codigo_key; Type: CONSTRAINT; Schema: comercial; Owner: -
--

ALTER TABLE ONLY comercial.cotizacion
    ADD CONSTRAINT cotizacion_codigo_key UNIQUE (codigo);


--
-- Name: cotizacion cotizacion_pkey; Type: CONSTRAINT; Schema: comercial; Owner: -
--

ALTER TABLE ONLY comercial.cotizacion
    ADD CONSTRAINT cotizacion_pkey PRIMARY KEY (id);


--
-- Name: despacho despacho_codigo_key; Type: CONSTRAINT; Schema: comercial; Owner: -
--

ALTER TABLE ONLY comercial.despacho
    ADD CONSTRAINT despacho_codigo_key UNIQUE (codigo);


--
-- Name: despacho despacho_pkey; Type: CONSTRAINT; Schema: comercial; Owner: -
--

ALTER TABLE ONLY comercial.despacho
    ADD CONSTRAINT despacho_pkey PRIMARY KEY (id);


--
-- Name: pedido pedido_codigo_key; Type: CONSTRAINT; Schema: comercial; Owner: -
--

ALTER TABLE ONLY comercial.pedido
    ADD CONSTRAINT pedido_codigo_key UNIQUE (codigo);


--
-- Name: pedido pedido_pkey; Type: CONSTRAINT; Schema: comercial; Owner: -
--

ALTER TABLE ONLY comercial.pedido
    ADD CONSTRAINT pedido_pkey PRIMARY KEY (id);


--
-- Name: venta venta_codigo_key; Type: CONSTRAINT; Schema: comercial; Owner: -
--

ALTER TABLE ONLY comercial.venta
    ADD CONSTRAINT venta_codigo_key UNIQUE (codigo);


--
-- Name: venta venta_pkey; Type: CONSTRAINT; Schema: comercial; Owner: -
--

ALTER TABLE ONLY comercial.venta
    ADD CONSTRAINT venta_pkey PRIMARY KEY (id);


--
-- Name: idx_despacho_estado; Type: INDEX; Schema: comercial; Owner: -
--

CREATE INDEX idx_despacho_estado ON comercial.despacho USING btree (estado);


--
-- Name: idx_despacho_venta_id; Type: INDEX; Schema: comercial; Owner: -
--

CREATE INDEX idx_despacho_venta_id ON comercial.despacho USING btree (venta_id);


--
-- Name: idx_venta_estado; Type: INDEX; Schema: comercial; Owner: -
--

CREATE INDEX idx_venta_estado ON comercial.venta USING btree (estado);


--
-- Name: idx_venta_pedido_id; Type: INDEX; Schema: comercial; Owner: -
--

CREATE INDEX idx_venta_pedido_id ON comercial.venta USING btree (pedido_id);


--
-- Name: cotizacion cotizacion_usuario_id_fkey; Type: FK CONSTRAINT; Schema: comercial; Owner: -
--

ALTER TABLE ONLY comercial.cotizacion
    ADD CONSTRAINT cotizacion_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES seguridad.usuario(id);


--
-- Name: pedido pedido_usuario_id_fkey; Type: FK CONSTRAINT; Schema: comercial; Owner: -
--

ALTER TABLE ONLY comercial.pedido
    ADD CONSTRAINT pedido_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES seguridad.usuario(id);


--
-- PostgreSQL database dump complete
--

\unrestrict IkEPsGSJ9buxBI8gFo1hHhutRhiVDsulthjo7lxZ2ZryiG77j8hLRAY4n1x5ldm

