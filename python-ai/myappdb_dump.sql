--
-- PostgreSQL database dump
--

\restrict RLAx9qs6EvN27PK0pRK4dSZkEwqygs6bkUG3YKIrpsECZSHBQiseMqRezlRHHyC

-- Dumped from database version 15.16 (Debian 15.16-1.pgdg13+1)
-- Dumped by pg_dump version 16.11 (Ubuntu 16.11-0ubuntu0.24.04.1)

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: articoli; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.articoli (
    codice character varying(50) NOT NULL,
    descrizione character varying(100) DEFAULT ''::character varying NOT NULL,
    ean character varying(13) DEFAULT ''::character varying NOT NULL,
    prezzodilistino numeric(10,2) NOT NULL,
    gruppo character varying(15) NOT NULL,
    stato character varying(2) NOT NULL,
    lp1 character(3),
    lp2 character(3),
    lp3 character(3),
    lp4 character(3),
    lp5 character(3) DEFAULT ''::bpchar,
    lineaprod character varying(100) DEFAULT ''::character varying NOT NULL,
    stagione character varying(50) NOT NULL,
    linkimmagine character varying(200) NOT NULL,
    bidone character varying(15) NOT NULL,
    scodescri character varying(15) NOT NULL,
    tipo character(2) NOT NULL,
    iva character(4) NOT NULL,
    codfornitore character varying(45) NOT NULL,
    peso character varying(10) NOT NULL,
    note character varying(250) NOT NULL,
    ubicazione character varying(45) NOT NULL,
    datainserimento timestamp without time zone,
    dataritiro timestamp without time zone,
    scortaminima integer DEFAULT 0 NOT NULL,
    codpadre character varying(50),
    qtafiglio integer DEFAULT 0 NOT NULL,
    codaccessori character varying(45) NOT NULL,
    grcassa character varying(10) NOT NULL,
    ordinabile character(1) DEFAULT ''::bpchar NOT NULL,
    datascad timestamp without time zone,
    cod_iniziale character varying(30) DEFAULT ''::character varying NOT NULL,
    var1 character varying(10) DEFAULT ''::character varying NOT NULL,
    var2 character varying(10) DEFAULT ''::character varying NOT NULL,
    var3 character varying(10) DEFAULT ''::character varying NOT NULL,
    var4 character varying(10) DEFAULT ''::character varying NOT NULL,
    cursoremod character(1) DEFAULT ''::bpchar NOT NULL,
    gestgiacenza character(1) DEFAULT ''::bpchar NOT NULL,
    codice2 character varying(50) DEFAULT ''::character varying NOT NULL,
    confezione character(1) DEFAULT ''::bpchar NOT NULL,
    g_cliente character varying(20) DEFAULT ''::character varying NOT NULL,
    progre integer NOT NULL,
    dataultimamodifica timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    online_relevant smallint DEFAULT 0 NOT NULL
);


ALTER TABLE public.articoli OWNER TO admin;

--
-- Name: COLUMN articoli.descrizione; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.articoli.descrizione IS 'descrizione completa';


--
-- Name: COLUMN articoli.scodescri; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.articoli.scodescri IS 'descrizione per scontrino';


--
-- Name: COLUMN articoli.tipo; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.articoli.tipo IS 'tipo di gestione: fiscale, no giacenza, solo quantità, ecc';


--
-- Name: COLUMN articoli.ubicazione; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.articoli.ubicazione IS 'ubicazione fisica articolo: scaffale, magazzino, codifiche archiviazione';


--
-- Name: COLUMN articoli.qtafiglio; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.articoli.qtafiglio IS 'quantità di questo articolo che corrisponde a un padre';


--
-- Name: COLUMN articoli.codaccessori; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.articoli.codaccessori IS 'chiave con tabella accessori per collegare un articolo con i relativi accessori';


--
-- Name: COLUMN articoli.grcassa; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.articoli.grcassa IS 'reparto misuratore fiscale';


--
-- Name: articolo_stock; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.articolo_stock (
    id bigint NOT NULL,
    codice_articolo character varying(50) NOT NULL,
    quantita_stock integer DEFAULT 0 NOT NULL,
    data_aggiornamento timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.articolo_stock OWNER TO admin;

--
-- Name: articolo_stock_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.articolo_stock_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.articolo_stock_id_seq OWNER TO admin;

--
-- Name: articolo_stock_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.articolo_stock_id_seq OWNED BY public.articolo_stock.id;


--
-- Name: attivita; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.attivita (
    id bigint NOT NULL,
    tipo character varying(50) NOT NULL,
    descrizione text,
    "timestamp" timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.attivita OWNER TO admin;

--
-- Name: attivita_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.attivita_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.attivita_id_seq OWNER TO admin;

--
-- Name: attivita_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.attivita_id_seq OWNED BY public.attivita.id;


--
-- Name: fornitori; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.fornitori (
    id bigint NOT NULL,
    nome character varying(255) NOT NULL,
    partita_iva character varying(20) NOT NULL,
    email character varying(255) NOT NULL,
    telefono character varying(50),
    indirizzo character varying(500),
    citta character varying(100),
    cap character varying(10),
    paese character varying(100),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.fornitori OWNER TO admin;

--
-- Name: fornitori_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.fornitori_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.fornitori_id_seq OWNER TO admin;

--
-- Name: fornitori_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.fornitori_id_seq OWNED BY public.fornitori.id;


--
-- Name: gerarchie; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.gerarchie (
    lp1 character(3) DEFAULT ''::bpchar NOT NULL,
    lp2 character(3) DEFAULT ''::bpchar NOT NULL,
    lp3 character(3) DEFAULT ''::bpchar NOT NULL,
    lp4 character(3) DEFAULT ''::bpchar NOT NULL,
    lp5 character(3) DEFAULT ''::bpchar NOT NULL,
    descri1 character varying(100) DEFAULT ''::character varying NOT NULL,
    descri2 character varying(250) DEFAULT ''::character varying NOT NULL,
    g_cliente character varying(20) DEFAULT ''::character varying NOT NULL,
    progre integer DEFAULT 0 NOT NULL,
    dataultimamodifica timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.gerarchie OWNER TO admin;

--
-- Name: ordini; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.ordini (
    id bigint NOT NULL,
    numero_ordine character varying(50) NOT NULL,
    stato character varying(50) NOT NULL,
    totale numeric(10,2) NOT NULL,
    data_ordine timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    fornitore_id bigint NOT NULL
);


ALTER TABLE public.ordini OWNER TO admin;

--
-- Name: ordini_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.ordini_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ordini_id_seq OWNER TO admin;

--
-- Name: ordini_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.ordini_id_seq OWNED BY public.ordini.id;


--
-- Name: ordini_items; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.ordini_items (
    id bigint NOT NULL,
    ordine_id bigint NOT NULL,
    codice_articolo character varying(50) NOT NULL,
    quantita integer NOT NULL,
    prezzo_unitario numeric(10,2) NOT NULL,
    subtotale numeric(10,2) NOT NULL
);


ALTER TABLE public.ordini_items OWNER TO admin;

--
-- Name: ordini_items_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.ordini_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ordini_items_id_seq OWNER TO admin;

--
-- Name: ordini_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.ordini_items_id_seq OWNED BY public.ordini_items.id;


--
-- Name: products; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.products (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    sku character varying(50),
    price numeric(10,2),
    current_stock integer DEFAULT 0,
    avg_daily_sales numeric(10,2) DEFAULT 0,
    total_revenue numeric(12,2) DEFAULT 0,
    days_of_stock integer,
    category character varying(100),
    first_sale_date date,
    last_sale_date date,
    created_at timestamp without time zone DEFAULT now()
);


ALTER TABLE public.products OWNER TO admin;

--
-- Name: products_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.products_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.products_id_seq OWNER TO admin;

--
-- Name: products_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.products_id_seq OWNED BY public.products.id;


--
-- Name: sco_dettaglio_sto; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.sco_dettaglio_sto (
    id bigint NOT NULL,
    contatore integer DEFAULT 0 NOT NULL,
    numero integer DEFAULT 0 NOT NULL,
    dataora timestamp without time zone NOT NULL,
    codnegozio character varying(10) NOT NULL,
    codpostazione character varying(5) DEFAULT ''::character varying NOT NULL,
    riga integer DEFAULT 0 NOT NULL,
    codarticolo character varying(50),
    ean character varying(20) DEFAULT ''::character varying NOT NULL,
    descri_articolo character varying(100) DEFAULT ''::character varying NOT NULL,
    quantita character varying(10) DEFAULT ''::character varying NOT NULL,
    scontoperc character varying(15) DEFAULT '0'::character varying NOT NULL,
    scontoimp character varying(15) DEFAULT '0,00'::character varying NOT NULL,
    prezzolistino character varying(15) DEFAULT '0,00'::character varying NOT NULL,
    prezzomodificato character varying(15) DEFAULT '0,00'::character varying NOT NULL,
    importo character varying(15) DEFAULT '0,00'::character varying NOT NULL,
    iva character varying(10) DEFAULT ''::character varying NOT NULL,
    tipo_mov character(1) DEFAULT ''::bpchar NOT NULL,
    scontoperctestata character varying(15) DEFAULT '0,00'::character varying NOT NULL,
    scontoimptestata character varying(15) DEFAULT '0,00'::character varying NOT NULL,
    scontocalc character varying(15) DEFAULT '0,00'::character varying NOT NULL,
    imposta character varying(15) DEFAULT '0,00'::character varying NOT NULL,
    imponibile character varying(15) DEFAULT '0,00'::character varying NOT NULL,
    codvenditore character varying(45) DEFAULT ''::character varying NOT NULL,
    old_codarticolo character varying(50),
    old_descri_art character varying(100) DEFAULT ''::character varying NOT NULL,
    modificato character(1) DEFAULT ''::bpchar NOT NULL,
    annullato character(1) DEFAULT ''::bpchar NOT NULL,
    contatoreordcli integer DEFAULT 0 NOT NULL,
    voucher character varying(45) DEFAULT ''::character varying NOT NULL,
    inviatoalserverone character(1) NOT NULL,
    dataultimamodifica timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.sco_dettaglio_sto OWNER TO admin;

--
-- Name: COLUMN sco_dettaglio_sto.contatore; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.contatore IS 'chiave con testata';


--
-- Name: COLUMN sco_dettaglio_sto.numero; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.numero IS 'attribuito da programma';


--
-- Name: COLUMN sco_dettaglio_sto.dataora; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.dataora IS 'attribuita da programma';


--
-- Name: COLUMN sco_dettaglio_sto.riga; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.riga IS 'numero riga di scontrino, attribuito da programma';


--
-- Name: COLUMN sco_dettaglio_sto.scontoperc; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.scontoperc IS 'sconto in percentuale sulla riga';


--
-- Name: COLUMN sco_dettaglio_sto.scontoimp; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.scontoimp IS 'sconto in importo sulla riga';


--
-- Name: COLUMN sco_dettaglio_sto.prezzolistino; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.prezzolistino IS 'prezzo da listino - dato riportato da tabella articoli';


--
-- Name: COLUMN sco_dettaglio_sto.prezzomodificato; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.prezzomodificato IS 'prezzo modificato da operatore';


--
-- Name: COLUMN sco_dettaglio_sto.importo; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.importo IS '((prezzomodif) * quantita) - scontiriga: è il totale di riga che si vede in griglia scontrino';


--
-- Name: COLUMN sco_dettaglio_sto.tipo_mov; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.tipo_mov IS 'venduto (-) o reso (+)';


--
-- Name: COLUMN sco_dettaglio_sto.scontoperctestata; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.scontoperctestata IS 'sconto percentuale derivante da quello su totale - dato riportato da testata';


--
-- Name: COLUMN sco_dettaglio_sto.scontoimptestata; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.scontoimptestata IS 'sconto in importo derivante da quello su totale - dato calcolato';


--
-- Name: COLUMN sco_dettaglio_sto.scontocalc; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.scontocalc IS 'somma in importo di tutti gli sconti che competono a questa riga';


--
-- Name: COLUMN sco_dettaglio_sto.imposta; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.imposta IS 'dato calcolato - serve per fatture e pno';


--
-- Name: COLUMN sco_dettaglio_sto.imponibile; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.imponibile IS 'dato calcolato - serve per fatture e pno';


--
-- Name: COLUMN sco_dettaglio_sto.codvenditore; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.codvenditore IS 'persona che si occupa delle vendite, può essere diverso dal cassiere';


--
-- Name: COLUMN sco_dettaglio_sto.old_descri_art; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.sco_dettaglio_sto.old_descri_art IS 'descrizione articolo prima di modifica';


--
-- Name: sco_dettaglio_sto_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.sco_dettaglio_sto_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sco_dettaglio_sto_id_seq OWNER TO admin;

--
-- Name: sco_dettaglio_sto_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.sco_dettaglio_sto_id_seq OWNED BY public.sco_dettaglio_sto.id;


--
-- Name: utenti; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.utenti (
    id bigint NOT NULL,
    nome_utente character varying(50) NOT NULL,
    password character varying(64) NOT NULL,
    email character varying(100) NOT NULL,
    ruolo character varying(20) NOT NULL,
    attivo boolean DEFAULT true NOT NULL,
    data_creazione timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ultimo_accesso timestamp without time zone
);


ALTER TABLE public.utenti OWNER TO admin;

--
-- Name: COLUMN utenti.password; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.utenti.password IS 'Password hashata con SHA-256 (64 caratteri hex)';


--
-- Name: COLUMN utenti.ruolo; Type: COMMENT; Schema: public; Owner: admin
--

COMMENT ON COLUMN public.utenti.ruolo IS 'Ruoli: ADMIN, STAFF';


--
-- Name: utenti_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.utenti_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.utenti_id_seq OWNER TO admin;

--
-- Name: utenti_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.utenti_id_seq OWNED BY public.utenti.id;


--
-- Name: articolo_stock id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.articolo_stock ALTER COLUMN id SET DEFAULT nextval('public.articolo_stock_id_seq'::regclass);


--
-- Name: attivita id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.attivita ALTER COLUMN id SET DEFAULT nextval('public.attivita_id_seq'::regclass);


--
-- Name: fornitori id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.fornitori ALTER COLUMN id SET DEFAULT nextval('public.fornitori_id_seq'::regclass);


--
-- Name: ordini id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.ordini ALTER COLUMN id SET DEFAULT nextval('public.ordini_id_seq'::regclass);


--
-- Name: ordini_items id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.ordini_items ALTER COLUMN id SET DEFAULT nextval('public.ordini_items_id_seq'::regclass);


--
-- Name: products id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.products ALTER COLUMN id SET DEFAULT nextval('public.products_id_seq'::regclass);


--
-- Name: sco_dettaglio_sto id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.sco_dettaglio_sto ALTER COLUMN id SET DEFAULT nextval('public.sco_dettaglio_sto_id_seq'::regclass);


--
-- Name: utenti id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.utenti ALTER COLUMN id SET DEFAULT nextval('public.utenti_id_seq'::regclass);


--
-- Data for Name: articoli; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.articoli (codice, descrizione, ean, prezzodilistino, gruppo, stato, lp1, lp2, lp3, lp4, lp5, lineaprod, stagione, linkimmagine, bidone, scodescri, tipo, iva, codfornitore, peso, note, ubicazione, datainserimento, dataritiro, scortaminima, codpadre, qtafiglio, codaccessori, grcassa, ordinabile, datascad, cod_iniziale, var1, var2, var3, var4, cursoremod, gestgiacenza, codice2, confezione, g_cliente, progre, dataultimamodifica, online_relevant) FROM stdin;
ANL001	Anello Solitario Diamante 0.50ct	8001234567890	2599.00	ANELLI	AT	GIO	ANL	DIA	\N	   	Anelli Diamanti	2026 S/S	https://example.com/img/anl001.jpg		Anello Solitr	ST	22  	3	3.5	Oro bianco 18kt certificato	SCAFF-A12	\N	\N	2	\N	0		REP01	S	\N	ANL001					 	 		 		1	2026-02-18 11:38:19.74386	1
COL002	Collana Swarovski Angelic Square	8001234567891	129.00	COLLANE	AT	GIO	COL	SWA	\N	   	Swarovski Collection	2026 S/S	https://example.com/img/col002.jpg		Coll Swarovs	ST	22  	1	2.1	Cristallo Swarovski azzurro	SCAFF-B05	\N	\N	5	\N	0		REP01	S	\N	COL002					 	 		 		2	2026-02-18 11:38:19.74386	1
ORE003	Orecchini Punto Luce Oro Giallo	8001234567892	890.00	ORECCHINI	AT	GIO	ORE	ORO	\N	   	Orecchini Classici	2026 S/S	https://example.com/img/ore003.jpg		Orecc PuntoL	ST	22  	2	1.8	Oro giallo 18kt con diamanti	CASSAF-C01	\N	\N	3	\N	0		REP01	S	\N	ORE003					 	 		 		3	2026-02-18 11:38:19.74386	1
BRA004	Bracciale Swarovski Tennis Deluxe	8001234567893	289.00	BRACCIALI	AT	GIO	BRA	SWA	\N	   	Swarovski Collection	2026 S/S	https://example.com/img/bra004.jpg		Bracc Tennis	ST	22  	1	12.5	Bracciale tennis con cristalli	SCAFF-B08	\N	\N	4	\N	0		REP01	S	\N	BRA004					 	 		 		4	2026-02-18 11:38:19.74386	1
FED005	Fede Nuziale Oro Bianco Classica	8001234567894	320.00	FEDI	AT	GIO	FED	ORO	\N	   	Fedi Nuziali	PERMANENTE	https://example.com/img/fed005.jpg		Fede Nuziale	ST	22  	2	4.2	Oro bianco 18kt 4mm	CASSAF-F01	\N	\N	10	\N	0		REP01	S	\N	FED005					 	 		 		5	2026-02-18 11:38:19.74386	1
\.


--
-- Data for Name: articolo_stock; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.articolo_stock (id, codice_articolo, quantita_stock, data_aggiornamento) FROM stdin;
1	ANL001	5	2026-02-18 11:38:19.749998
2	COL002	12	2026-02-18 11:38:19.749998
3	ORE003	8	2026-02-18 11:38:19.749998
4	BRA004	15	2026-02-18 11:38:19.749998
5	FED005	25	2026-02-18 11:38:19.749998
\.


--
-- Data for Name: attivita; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.attivita (id, tipo, descrizione, "timestamp") FROM stdin;
1	NUOVO_ORDINE	Creato nuovo ordine ORD-2026-003 per orecchini e fedi	2026-02-18 11:38:19.756243
2	NUOVO_ARTICOLO	Aggiunto articolo ANL001 - Anello Solitario Diamante all'inventario	2026-02-18 11:38:19.756243
3	ORDINE_CONSEGNATO	Ordine ORD-2026-001 consegnato - 2 anelli solitario	2026-02-18 11:38:19.756243
\.


--
-- Data for Name: fornitori; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.fornitori (id, nome, partita_iva, email, telefono, indirizzo, citta, cap, paese, created_at, updated_at) FROM stdin;
1	Swarovski Italia SRL	12345678901	mail1@mail.com	+39 02 8845123	Via Montenapoleone 8	Milano	20121	Italia	2026-02-18 11:38:19.741032	2026-02-18 11:38:19.741032
2	Gold & Silver Wholesale SpA	98765432109	mail2@mail.com	+39 055 7743210	Ponte Vecchio 12	Firenze	50125	Italia	2026-02-18 11:38:19.741032	2026-02-18 11:38:19.741032
3	Diamanti Preziosi Import	11223344556	mail3@mail.com	+39 081 5529876	Via Caracciolo 45	Napoli	80122	Italia	2026-02-18 11:38:19.741032	2026-02-18 11:38:19.741032
\.


--
-- Data for Name: gerarchie; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.gerarchie (lp1, lp2, lp3, lp4, lp5, descri1, descri2, g_cliente, progre, dataultimamodifica) FROM stdin;
\.


--
-- Data for Name: ordini; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.ordini (id, numero_ordine, stato, totale, data_ordine, fornitore_id) FROM stdin;
1	ORD-2026-001	CONSEGNATO	5198.00	2026-02-18 11:38:19.746916	3
2	ORD-2026-002	IN_LAVORAZIONE	836.00	2026-02-18 11:38:19.746916	1
3	ORD-2026-003	CREATO	1530.00	2026-02-18 11:38:19.746916	2
\.


--
-- Data for Name: ordini_items; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.ordini_items (id, ordine_id, codice_articolo, quantita, prezzo_unitario, subtotale) FROM stdin;
1	1	ANL001	2	2599.00	5198.00
2	2	COL002	5	129.00	645.00
3	2	BRA004	1	289.00	289.00
4	3	ORE003	1	890.00	890.00
5	3	FED005	2	320.00	640.00
\.


--
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.products (id, name, sku, price, current_stock, avg_daily_sales, total_revenue, days_of_stock, category, first_sale_date, last_sale_date, created_at) FROM stdin;
1	Laptop Premium	LAP001	1200.00	2	1.50	15000.00	1	Elettronica	2025-12-01	2026-02-18	2026-02-18 11:57:15.45739
2	Top Urgenza	URG001	899.00	0	5.00	8500.00	0	Urgenze	2025-12-01	2026-02-18	2026-02-18 11:57:15.45739
3	Slow Mover	SLOW001	25.00	150	0.20	500.00	750	Magazzino	2025-12-01	2026-02-18	2026-02-18 11:57:15.45739
4	Best Seller	BS001	299.00	5	8.00	25000.00	1	Top	2025-12-01	2026-02-18	2026-02-18 11:57:15.45739
5	Premium Alto	PREM001	2500.00	3	0.50	18000.00	6	Luxury	2025-12-01	2026-02-18	2026-02-18 11:57:15.45739
6	Stock OK	STK001	150.00	50	2.00	4500.00	25	Standard	2025-12-01	2026-02-18	2026-02-18 11:57:15.45739
7	Eccesso	EXC001	80.00	200	0.10	2000.00	2000	Eccesso	2025-12-01	2026-02-18	2026-02-18 11:57:15.45739
8	Low Revenue	LOW001	10.00	100	1.00	800.00	100	Base	2025-12-01	2026-02-18	2026-02-18 11:57:15.45739
9	Daily Alto	DAY001	400.00	10	12.00	12000.00	1	Alta Rotazione	2025-12-01	2026-02-18	2026-02-18 11:57:15.45739
10	Critico Revenue	CRIT001	750.00	1	3.00	6000.00	0	Critico	2025-12-01	2026-02-18	2026-02-18 11:57:15.45739
\.


--
-- Data for Name: sco_dettaglio_sto; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.sco_dettaglio_sto (id, contatore, numero, dataora, codnegozio, codpostazione, riga, codarticolo, ean, descri_articolo, quantita, scontoperc, scontoimp, prezzolistino, prezzomodificato, importo, iva, tipo_mov, scontoperctestata, scontoimptestata, scontocalc, imposta, imponibile, codvenditore, old_codarticolo, old_descri_art, modificato, annullato, contatoreordcli, voucher, inviatoalserverone, dataultimamodifica) FROM stdin;
1	1	1001	2026-02-13 11:53:41.3325	NEG01	POS1	1	COL002	8001234567891	Coll Swarovs	1	0	0.00	129.00	129.00	129.00	22	-	0.00	0.00	0.00	23.28	105.72	VEND01	\N		 	 	0		S	2026-02-18 11:53:41.3325
2	1	1001	2026-02-13 11:53:41.3325	NEG01	POS1	2	BRA004	8001234567893	Bracc Tennis	1	0	0.00	289.00	289.00	289.00	22	-	0.00	0.00	0.00	52.13	236.87	VEND01	\N		 	 	0		S	2026-02-18 11:53:41.3325
3	2	1002	2026-02-14 11:53:54.436464	NEG01	POS1	1	ANL001	8001234567890	Anello Solitr	1	0	0.00	2599.00	2599.00	2599.00	22	-	0.00	0.00	0.00	468.67	2130.33	VEND02	\N		 	 	0		S	2026-02-18 11:53:54.436464
4	3	1003	2026-02-15 11:54:13.052851	NEG01	POS2	1	FED005	8001234567894	Fede Nuziale	2	0	0.00	320.00	320.00	640.00	22	-	0.00	0.00	0.00	115.41	524.59	VEND01	\N		 	 	0		S	2026-02-18 11:54:13.052851
5	4	1004	2026-02-16 11:54:25.282114	NEG01	POS1	1	ORE003	8001234567892	Orecc PuntoL	1	10	89.00	890.00	801.00	801.00	22	-	0.00	0.00	89.00	144.46	656.54	VEND03	\N		 	 	0		S	2026-02-18 11:54:25.282114
6	5	1005	2026-02-17 11:54:51.104794	NEG01	POS2	1	COL002	8001234567891	Coll Swarovs	2	0	0.00	129.00	129.00	258.00	22	-	0.00	0.00	0.00	46.56	211.44	VEND02	\N		 	 	0		S	2026-02-18 11:54:51.104794
7	5	1005	2026-02-17 11:54:51.104794	NEG01	POS2	2	FED005	8001234567894	Fede Nuziale	1	0	0.00	320.00	320.00	320.00	22	-	0.00	0.00	0.00	57.70	262.30	VEND02	\N		 	 	0		S	2026-02-18 11:54:51.104794
\.


--
-- Data for Name: utenti; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.utenti (id, nome_utente, password, email, ruolo, attivo, data_creazione, ultimo_accesso) FROM stdin;
1	admin	240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9	admin@gioielleria.it	ADMIN	t	2026-02-18 11:37:49.38584	\N
2	staff	10176e7b7b24d317acfcf8d2064cfd2f24e154f7b5a96603077d5ef813d6a6b6	staff@gioielleria.it	STAFF	t	2026-02-18 11:37:49.38584	\N
\.


--
-- Name: articolo_stock_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.articolo_stock_id_seq', 5, true);


--
-- Name: attivita_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.attivita_id_seq', 3, true);


--
-- Name: fornitori_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.fornitori_id_seq', 3, true);


--
-- Name: ordini_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.ordini_id_seq', 3, true);


--
-- Name: ordini_items_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.ordini_items_id_seq', 5, true);


--
-- Name: products_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.products_id_seq', 10, true);


--
-- Name: sco_dettaglio_sto_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.sco_dettaglio_sto_id_seq', 7, true);


--
-- Name: utenti_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.utenti_id_seq', 2, true);


--
-- Name: articoli articoli_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.articoli
    ADD CONSTRAINT articoli_pkey PRIMARY KEY (codice);


--
-- Name: articolo_stock articolo_stock_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.articolo_stock
    ADD CONSTRAINT articolo_stock_pkey PRIMARY KEY (id);


--
-- Name: attivita attivita_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.attivita
    ADD CONSTRAINT attivita_pkey PRIMARY KEY (id);


--
-- Name: fornitori fornitori_email_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.fornitori
    ADD CONSTRAINT fornitori_email_key UNIQUE (email);


--
-- Name: fornitori fornitori_partita_iva_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.fornitori
    ADD CONSTRAINT fornitori_partita_iva_key UNIQUE (partita_iva);


--
-- Name: fornitori fornitori_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.fornitori
    ADD CONSTRAINT fornitori_pkey PRIMARY KEY (id);


--
-- Name: ordini_items ordini_items_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.ordini_items
    ADD CONSTRAINT ordini_items_pkey PRIMARY KEY (id);


--
-- Name: ordini ordini_numero_ordine_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.ordini
    ADD CONSTRAINT ordini_numero_ordine_key UNIQUE (numero_ordine);


--
-- Name: ordini ordini_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.ordini
    ADD CONSTRAINT ordini_pkey PRIMARY KEY (id);


--
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


--
-- Name: products products_sku_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_sku_key UNIQUE (sku);


--
-- Name: sco_dettaglio_sto sco_dettaglio_sto_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.sco_dettaglio_sto
    ADD CONSTRAINT sco_dettaglio_sto_pkey PRIMARY KEY (id);


--
-- Name: utenti utenti_email_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.utenti
    ADD CONSTRAINT utenti_email_key UNIQUE (email);


--
-- Name: utenti utenti_nome_utente_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.utenti
    ADD CONSTRAINT utenti_nome_utente_key UNIQUE (nome_utente);


--
-- Name: utenti utenti_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.utenti
    ADD CONSTRAINT utenti_pkey PRIMARY KEY (id);


--
-- Name: idx_articoli_codfornitore; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_articoli_codfornitore ON public.articoli USING btree (codfornitore);


--
-- Name: idx_articoli_descrizione; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_articoli_descrizione ON public.articoli USING btree (descrizione);


--
-- Name: idx_articoli_ean; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_articoli_ean ON public.articoli USING btree (ean);


--
-- Name: idx_articoli_gruppo; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_articoli_gruppo ON public.articoli USING btree (gruppo);


--
-- Name: idx_articoli_stagione; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_articoli_stagione ON public.articoli USING btree (stagione);


--
-- Name: idx_articoli_stato; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_articoli_stato ON public.articoli USING btree (stato);


--
-- Name: idx_articolo_stock_codice; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_articolo_stock_codice ON public.articolo_stock USING btree (codice_articolo);


--
-- Name: idx_attivita_timestamp; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_attivita_timestamp ON public.attivita USING btree ("timestamp");


--
-- Name: idx_ordini_data; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_ordini_data ON public.ordini USING btree (data_ordine);


--
-- Name: idx_ordini_fornitore; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_ordini_fornitore ON public.ordini USING btree (fornitore_id);


--
-- Name: idx_ordini_items_articolo; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_ordini_items_articolo ON public.ordini_items USING btree (codice_articolo);


--
-- Name: idx_ordini_items_ordine; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_ordini_items_ordine ON public.ordini_items USING btree (ordine_id);


--
-- Name: idx_ordini_stato; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_ordini_stato ON public.ordini USING btree (stato);


--
-- Name: idx_revenue; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_revenue ON public.products USING btree (total_revenue);


--
-- Name: idx_sco_codarticolo; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_sco_codarticolo ON public.sco_dettaglio_sto USING btree (codarticolo);


--
-- Name: idx_sco_contatore; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_sco_contatore ON public.sco_dettaglio_sto USING btree (contatore);


--
-- Name: idx_sco_dataora; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_sco_dataora ON public.sco_dettaglio_sto USING btree (dataora);


--
-- Name: idx_sco_dettaglio_contatore; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_sco_dettaglio_contatore ON public.sco_dettaglio_sto USING btree (contatore);


--
-- Name: idx_sco_dettaglio_dataora; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_sco_dettaglio_dataora ON public.sco_dettaglio_sto USING btree (dataora);


--
-- Name: idx_stock_days; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_stock_days ON public.products USING btree (days_of_stock);


--
-- Name: articolo_stock fk_articolo_stock_articolo; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.articolo_stock
    ADD CONSTRAINT fk_articolo_stock_articolo FOREIGN KEY (codice_articolo) REFERENCES public.articoli(codice);


--
-- Name: ordini fk_ordini_fornitore; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.ordini
    ADD CONSTRAINT fk_ordini_fornitore FOREIGN KEY (fornitore_id) REFERENCES public.fornitori(id);


--
-- Name: ordini_items fk_ordini_items_articolo; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.ordini_items
    ADD CONSTRAINT fk_ordini_items_articolo FOREIGN KEY (codice_articolo) REFERENCES public.articoli(codice);


--
-- Name: ordini_items fk_ordini_items_ordine; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.ordini_items
    ADD CONSTRAINT fk_ordini_items_ordine FOREIGN KEY (ordine_id) REFERENCES public.ordini(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict RLAx9qs6EvN27PK0pRK4dSZkEwqygs6bkUG3YKIrpsECZSHBQiseMqRezlRHHyC

