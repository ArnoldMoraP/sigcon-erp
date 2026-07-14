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
-- Name: almacen; Type: SCHEMA; Schema: -; Owner: -
--



--
-- Name: compras; Type: SCHEMA; Schema: -; Owner: -
--



SET default_table_access_method = heap;

--
-- Name: inventario; Type: TABLE; Schema: almacen; Owner: -
--

CREATE TABLE public.inventario (
    id integer NOT NULL,
    producto character varying(200) NOT NULL,
    categoria character varying(100),
    stock integer DEFAULT 0 NOT NULL,
    stock_minimo integer DEFAULT 10 NOT NULL,
    precio_unitario numeric(10,2),
    unidad character varying(50) DEFAULT 'unidad'::character varying,
    fecha_actualizacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: inventario_id_seq; Type: SEQUENCE; Schema: almacen; Owner: -
--

CREATE SEQUENCE public.inventario_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: inventario_id_seq; Type: SEQUENCE OWNED BY; Schema: almacen; Owner: -
--

ALTER SEQUENCE public.inventario_id_seq OWNED BY public.inventario.id;


--
-- Name: compra; Type: TABLE; Schema: compras; Owner: -
--

CREATE TABLE public.compra (
    id bigint NOT NULL,
    orden_compra_id bigint,
    proveedor_id bigint NOT NULL,
    inventario_id integer,
    producto character varying(200) NOT NULL,
    cantidad integer NOT NULL,
    precio_unitario numeric(14,2) NOT NULL,
    total numeric(14,2) NOT NULL,
    fecha_compra timestamp without time zone DEFAULT now(),
    usuario_id integer
);


--
-- Name: compra_id_seq; Type: SEQUENCE; Schema: compras; Owner: -
--

CREATE SEQUENCE public.compra_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: compra_id_seq; Type: SEQUENCE OWNED BY; Schema: compras; Owner: -
--

ALTER SEQUENCE public.compra_id_seq OWNED BY public.compra.id;


--
-- Name: factura_proveedor; Type: TABLE; Schema: compras; Owner: -
--

CREATE TABLE public.factura_proveedor (
    id bigint NOT NULL,
    proveedor_id bigint NOT NULL,
    compra_id bigint,
    numero_factura character varying(50) NOT NULL,
    monto numeric(14,2) NOT NULL,
    fecha_emision date NOT NULL,
    fecha_vencimiento date,
    estado character varying(20) DEFAULT 'Pendiente'::character varying NOT NULL,
    usuario_id integer,
    created_at timestamp without time zone DEFAULT now()
);


--
-- Name: factura_proveedor_id_seq; Type: SEQUENCE; Schema: compras; Owner: -
--

CREATE SEQUENCE public.factura_proveedor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: factura_proveedor_id_seq; Type: SEQUENCE OWNED BY; Schema: compras; Owner: -
--

ALTER SEQUENCE public.factura_proveedor_id_seq OWNED BY public.factura_proveedor.id;


--
-- Name: orden_compra; Type: TABLE; Schema: compras; Owner: -
--

CREATE TABLE public.orden_compra (
    id bigint NOT NULL,
    codigo character varying(20) NOT NULL,
    proveedor_id bigint NOT NULL,
    producto character varying(200) NOT NULL,
    cantidad character varying(50),
    total numeric(14,2) NOT NULL,
    fecha_entrega date,
    estado character varying(20) DEFAULT 'Pendiente'::character varying NOT NULL,
    usuario_id integer,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: orden_compra_id_seq; Type: SEQUENCE; Schema: compras; Owner: -
--

CREATE SEQUENCE public.orden_compra_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: orden_compra_id_seq; Type: SEQUENCE OWNED BY; Schema: compras; Owner: -
--

ALTER SEQUENCE public.orden_compra_id_seq OWNED BY public.orden_compra.id;


--
-- Name: pago_proveedor; Type: TABLE; Schema: compras; Owner: -
--

CREATE TABLE public.pago_proveedor (
    id bigint NOT NULL,
    factura_id bigint NOT NULL,
    proveedor_id bigint NOT NULL,
    monto numeric(14,2) NOT NULL,
    metodo_pago character varying(50),
    fecha_pago timestamp without time zone DEFAULT now(),
    usuario_id integer
);


--
-- Name: pago_proveedor_id_seq; Type: SEQUENCE; Schema: compras; Owner: -
--

CREATE SEQUENCE public.pago_proveedor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: pago_proveedor_id_seq; Type: SEQUENCE OWNED BY; Schema: compras; Owner: -
--

ALTER SEQUENCE public.pago_proveedor_id_seq OWNED BY public.pago_proveedor.id;


--
-- Name: presupuesto; Type: TABLE; Schema: compras; Owner: -
--

CREATE TABLE public.presupuesto (
    id bigint NOT NULL,
    periodo character varying(20) NOT NULL,
    monto_total numeric(14,2) NOT NULL,
    monto_disponible numeric(14,2) NOT NULL,
    fecha_actualizacion timestamp without time zone DEFAULT now()
);


--
-- Name: presupuesto_id_seq; Type: SEQUENCE; Schema: compras; Owner: -
--

CREATE SEQUENCE public.presupuesto_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: presupuesto_id_seq; Type: SEQUENCE OWNED BY; Schema: compras; Owner: -
--

ALTER SEQUENCE public.presupuesto_id_seq OWNED BY public.presupuesto.id;


--
-- Name: proveedor; Type: TABLE; Schema: compras; Owner: -
--

CREATE TABLE public.proveedor (
    id bigint NOT NULL,
    nombre character varying(200) NOT NULL,
    origen character varying(150),
    ruc character varying(20) NOT NULL,
    contacto character varying(150),
    telefono character varying(30),
    categoria character varying(100),
    calificacion numeric(2,1) DEFAULT 4.0,
    estado character varying(20) DEFAULT 'Activo'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: proveedor_id_seq; Type: SEQUENCE; Schema: compras; Owner: -
--

CREATE SEQUENCE public.proveedor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: proveedor_id_seq; Type: SEQUENCE OWNED BY; Schema: compras; Owner: -
--

ALTER SEQUENCE public.proveedor_id_seq OWNED BY public.proveedor.id;


--
-- Name: inventario id; Type: DEFAULT; Schema: almacen; Owner: -
--

ALTER TABLE ONLY public.inventario ALTER COLUMN id SET DEFAULT nextval('public.inventario_id_seq'::regclass);


--
-- Name: compra id; Type: DEFAULT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.compra ALTER COLUMN id SET DEFAULT nextval('public.compra_id_seq'::regclass);


--
-- Name: factura_proveedor id; Type: DEFAULT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.factura_proveedor ALTER COLUMN id SET DEFAULT nextval('public.factura_proveedor_id_seq'::regclass);


--
-- Name: orden_compra id; Type: DEFAULT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.orden_compra ALTER COLUMN id SET DEFAULT nextval('public.orden_compra_id_seq'::regclass);


--
-- Name: pago_proveedor id; Type: DEFAULT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.pago_proveedor ALTER COLUMN id SET DEFAULT nextval('public.pago_proveedor_id_seq'::regclass);


--
-- Name: presupuesto id; Type: DEFAULT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.presupuesto ALTER COLUMN id SET DEFAULT nextval('public.presupuesto_id_seq'::regclass);


--
-- Name: proveedor id; Type: DEFAULT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.proveedor ALTER COLUMN id SET DEFAULT nextval('public.proveedor_id_seq'::regclass);


--
-- Data for Name: inventario; Type: TABLE DATA; Schema: almacen; Owner: -
--

COPY public.inventario (id, producto, categoria, stock, stock_minimo, precio_unitario, unidad, fecha_actualizacion) FROM stdin;
3	Tubo galvanizado 2"	Tubos	200	30	45.00	unidad	2026-05-25 21:14:39.605472
4	Varilla corrugada 1/2"	Varillas	500	50	12.50	unidad	2026-05-25 21:14:39.605472
6	Plancha antideslizante	Planchas	5	15	380.00	unidad	2026-05-25 21:14:39.605472
7	Tubo cuadrado 1"	Tubos	120	25	35.00	unidad	2026-05-25 21:14:39.605472
9	Cable de acero 1/2	Cables	100	50	15.50	metro	2026-05-25 21:44:29.723494
2	Plancha de acero 3mm	Planchas	81	15	320.00	unidad	2026-05-26 21:21:29.45467
5	Angulo de acero 2x2	Angulos	7	20	28.00	unidad	2026-05-25 21:14:39.605472
8	Soldadura 6011	Insumos	299	100	8.50	kg	2026-05-26 21:21:01.533829
10	Barras de acero 1/2"	General	30	10	1500.00	unidad	2026-06-21 13:57:41.919836
1	Acero estructural A36	Acero	157	20	850.00	tonelada	2026-07-14 02:53:31.333997


--
-- Data for Name: compra; Type: TABLE DATA; Schema: compras; Owner: -
--

COPY public.compra (id, orden_compra_id, proveedor_id, inventario_id, producto, cantidad, precio_unitario, total, fecha_compra, usuario_id) FROM stdin;
1	1	1	\N	Planchas de Acero A36	100	250.00	25000.00	2026-06-18 17:01:34.600702	\N
2	2	2	\N	Tubos Estructurales	50	360.00	18000.00	2026-06-18 17:01:34.600702	\N
3	3	1	10	Barras de acero 1/2"	30	1500.00	45000.00	2026-06-21 18:57:43.078036	\N


--
-- Data for Name: factura_proveedor; Type: TABLE DATA; Schema: compras; Owner: -
--

COPY public.factura_proveedor (id, proveedor_id, compra_id, numero_factura, monto, fecha_emision, fecha_vencimiento, estado, usuario_id, created_at) FROM stdin;


--
-- Data for Name: orden_compra; Type: TABLE DATA; Schema: compras; Owner: -
--

COPY public.orden_compra (id, codigo, proveedor_id, producto, cantidad, total, fecha_entrega, estado, usuario_id, created_at, updated_at) FROM stdin;
2	OC-0002	2	Tubos Estructurales	50	18000.00	2026-06-28	Pendiente	\N	2026-06-18 17:01:34.600702	2026-06-18 22:17:41.41848
1	OC-0001	1	Planchas de Acero A36	100	25000.00	2026-06-25	Pendiente	\N	2026-06-18 17:01:34.600702	2026-06-18 22:17:44.993328
3	OC-0003	1	Barras de acero 1/2"	30 TM	45000.00	2026-07-15	Aprobada	\N	2026-06-21 16:32:03.176647	2026-06-21 13:14:01.669685
4	OC-0004	1	Tubos Estructurales	30 TM	25.00	2026-12-05	Pendiente	\N	2026-06-23 19:56:07.29401	2026-06-23 19:56:40.046413


--
-- Data for Name: pago_proveedor; Type: TABLE DATA; Schema: compras; Owner: -
--

COPY public.pago_proveedor (id, factura_id, proveedor_id, monto, metodo_pago, fecha_pago, usuario_id) FROM stdin;


--
-- Data for Name: presupuesto; Type: TABLE DATA; Schema: compras; Owner: -
--

COPY public.presupuesto (id, periodo, monto_total, monto_disponible, fecha_actualizacion) FROM stdin;
1	2026-06	3000000.00	2954975.00	2026-06-23 19:56:06.940746


--
-- Data for Name: proveedor; Type: TABLE DATA; Schema: compras; Owner: -
--

COPY public.proveedor (id, nombre, origen, ruc, contacto, telefono, categoria, calificacion, estado, created_at, updated_at) FROM stdin;
1	Aceros Lima SAC	Peru	20111111111	Carlos Ruiz	999111222	Acero	4.8	Activo	2026-06-18 17:01:34.600702	2026-06-18 17:01:34.600702
2	Metalurgica Andina SAC	Peru	20222222222	Luis Torres	999333444	Metales	4.5	Activo	2026-06-18 17:01:34.600702	2026-06-18 17:01:34.600702


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
-- Name: inventario inventario_pkey; Type: CONSTRAINT; Schema: almacen; Owner: -
--

ALTER TABLE ONLY public.inventario
    ADD CONSTRAINT inventario_pkey PRIMARY KEY (id);


--
-- Name: compra compra_pkey; Type: CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.compra
    ADD CONSTRAINT compra_pkey PRIMARY KEY (id);


--
-- Name: factura_proveedor factura_proveedor_pkey; Type: CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.factura_proveedor
    ADD CONSTRAINT factura_proveedor_pkey PRIMARY KEY (id);


--
-- Name: orden_compra orden_compra_codigo_key; Type: CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.orden_compra
    ADD CONSTRAINT orden_compra_codigo_key UNIQUE (codigo);


--
-- Name: orden_compra orden_compra_pkey; Type: CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.orden_compra
    ADD CONSTRAINT orden_compra_pkey PRIMARY KEY (id);


--
-- Name: pago_proveedor pago_proveedor_pkey; Type: CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.pago_proveedor
    ADD CONSTRAINT pago_proveedor_pkey PRIMARY KEY (id);


--
-- Name: presupuesto presupuesto_periodo_key; Type: CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.presupuesto
    ADD CONSTRAINT presupuesto_periodo_key UNIQUE (periodo);


--
-- Name: presupuesto presupuesto_pkey; Type: CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.presupuesto
    ADD CONSTRAINT presupuesto_pkey PRIMARY KEY (id);


--
-- Name: proveedor proveedor_pkey; Type: CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.proveedor
    ADD CONSTRAINT proveedor_pkey PRIMARY KEY (id);


--
-- Name: proveedor proveedor_ruc_key; Type: CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.proveedor
    ADD CONSTRAINT proveedor_ruc_key UNIQUE (ruc);


--
-- Name: compra compra_inventario_id_fkey; Type: FK CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.compra
    ADD CONSTRAINT compra_inventario_id_fkey FOREIGN KEY (inventario_id) REFERENCES public.inventario(id);


--
-- Name: compra compra_orden_compra_id_fkey; Type: FK CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.compra
    ADD CONSTRAINT compra_orden_compra_id_fkey FOREIGN KEY (orden_compra_id) REFERENCES public.orden_compra(id);


--
-- Name: compra compra_proveedor_id_fkey; Type: FK CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.compra
    ADD CONSTRAINT compra_proveedor_id_fkey FOREIGN KEY (proveedor_id) REFERENCES public.proveedor(id);


--
-- Name: compra compra_usuario_id_fkey; Type: FK CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.compra
    ADD CONSTRAINT compra_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES seguridad.usuario(id);


--
-- Name: factura_proveedor factura_proveedor_compra_id_fkey; Type: FK CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.factura_proveedor
    ADD CONSTRAINT factura_proveedor_compra_id_fkey FOREIGN KEY (compra_id) REFERENCES public.compra(id);


--
-- Name: factura_proveedor factura_proveedor_proveedor_id_fkey; Type: FK CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.factura_proveedor
    ADD CONSTRAINT factura_proveedor_proveedor_id_fkey FOREIGN KEY (proveedor_id) REFERENCES public.proveedor(id);


--
-- Name: factura_proveedor factura_proveedor_usuario_id_fkey; Type: FK CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.factura_proveedor
    ADD CONSTRAINT factura_proveedor_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES seguridad.usuario(id);


--
-- Name: orden_compra orden_compra_proveedor_id_fkey; Type: FK CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.orden_compra
    ADD CONSTRAINT orden_compra_proveedor_id_fkey FOREIGN KEY (proveedor_id) REFERENCES public.proveedor(id);


--
-- Name: orden_compra orden_compra_usuario_id_fkey; Type: FK CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.orden_compra
    ADD CONSTRAINT orden_compra_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES seguridad.usuario(id);


--
-- Name: pago_proveedor pago_proveedor_factura_id_fkey; Type: FK CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.pago_proveedor
    ADD CONSTRAINT pago_proveedor_factura_id_fkey FOREIGN KEY (factura_id) REFERENCES public.factura_proveedor(id);


--
-- Name: pago_proveedor pago_proveedor_proveedor_id_fkey; Type: FK CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.pago_proveedor
    ADD CONSTRAINT pago_proveedor_proveedor_id_fkey FOREIGN KEY (proveedor_id) REFERENCES public.proveedor(id);


--
-- Name: pago_proveedor pago_proveedor_usuario_id_fkey; Type: FK CONSTRAINT; Schema: compras; Owner: -
--

ALTER TABLE ONLY public.pago_proveedor
    ADD CONSTRAINT pago_proveedor_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES seguridad.usuario(id);


--
-- PostgreSQL database dump complete
--


