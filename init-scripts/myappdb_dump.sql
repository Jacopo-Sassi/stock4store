-- ==============================================================
-- PostgreSQL full dump – Gioielleria PW Backend
-- Con codfornitore articoli allineato agli id fornitori
-- ==============================================================

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

-- ==============================================================
-- DROP TABLES (ordine inverso rispetto alle FK)
-- ==============================================================
DROP TABLE IF EXISTS public.ordini_items   CASCADE;
DROP TABLE IF EXISTS public.ordini         CASCADE;
DROP TABLE IF EXISTS public.articolo_stock CASCADE;
DROP TABLE IF EXISTS public.sco_dettaglio_sto CASCADE;
DROP TABLE IF EXISTS public.articoli       CASCADE;
DROP TABLE IF EXISTS public.fornitori      CASCADE;
DROP TABLE IF EXISTS public.gerarchie      CASCADE;
DROP TABLE IF EXISTS public.attivita       CASCADE;
DROP TABLE IF EXISTS public.products       CASCADE;
DROP TABLE IF EXISTS public.utenti         CASCADE;

-- ==============================================================
-- DROP SEQUENCES
-- ==============================================================
DROP SEQUENCE IF EXISTS public.fornitori_id_seq;
DROP SEQUENCE IF EXISTS public.articolo_stock_id_seq;
DROP SEQUENCE IF EXISTS public.ordini_id_seq;
DROP SEQUENCE IF EXISTS public.ordini_items_id_seq;
DROP SEQUENCE IF EXISTS public.attivita_id_seq;
DROP SEQUENCE IF EXISTS public.sco_dettaglio_sto_id_seq;
DROP SEQUENCE IF EXISTS public.products_id_seq;
DROP SEQUENCE IF EXISTS public.utenti_id_seq;

-- ==============================================================
-- TABELLA: fornitori
-- ==============================================================
CREATE TABLE public.fornitori (
                                  id          bigint                      NOT NULL,
                                  nome        character varying(255)      NOT NULL,
                                  partita_iva character varying(20)       NOT NULL,
                                  email       character varying(255)      NOT NULL,
                                  telefono    character varying(50),
                                  indirizzo   character varying(500),
                                  citta       character varying(100),
                                  cap         character varying(10),
                                  paese       character varying(100),
                                  created_at  timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                                  updated_at  timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE public.fornitori_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

ALTER SEQUENCE public.fornitori_id_seq OWNED BY public.fornitori.id;
ALTER TABLE ONLY public.fornitori ALTER COLUMN id SET DEFAULT nextval('public.fornitori_id_seq'::regclass);

ALTER TABLE ONLY public.fornitori
    ADD CONSTRAINT fornitori_pkey         PRIMARY KEY (id);
ALTER TABLE ONLY public.fornitori
    ADD CONSTRAINT fornitori_email_key    UNIQUE (email);
ALTER TABLE ONLY public.fornitori
    ADD CONSTRAINT fornitori_partita_iva_key UNIQUE (partita_iva);

ALTER TABLE public.fornitori OWNER TO admin;

-- ==============================================================
-- TABELLA: articoli
-- ==============================================================
CREATE TABLE public.articoli (
                                 codice              character varying(50)   DEFAULT '' NOT NULL UNIQUE,
                                 descrizione         character varying(100)  DEFAULT ''  NOT NULL,
                                 ean                 character varying(13)   DEFAULT ''  NOT NULL,
                                 prezzodilistino     numeric(10,2)           DEFAULT 0 NOT NULL,
                                 gruppo              character varying(15)   DEFAULT '' NOT NULL,
                                 stato               character varying(2)    DEFAULT 'ST',
                                 lp1                 character(3),
                                 lp2                 character(3),
                                 lp3                 character(3),
                                 lp4                 character(3),
                                 lp5                 character(3)            DEFAULT '',
                                 lineaprod           character varying(100)  DEFAULT ''  NOT NULL,
                                 stagione            character varying(50)   DEFAULT '' NOT NULL,
                                 linkimmagine        character varying(200)  DEFAULT '' NOT NULL,
                                 bidone              character varying(15)   DEFAULT '' NOT NULL,
                                 scodescri           character varying(15)   DEFAULT '' NOT NULL,
                                 tipo                character(2)            DEFAULT '' NOT NULL,
                                 iva                 character(4)            DEFAULT '' NOT NULL,
                                 codfornitore        character varying(45)   DEFAULT '' NOT NULL,
                                 peso                character varying(10)   DEFAULT '' NOT NULL,
                                 note                character varying(250)  DEFAULT '' NOT NULL,
                                 ubicazione          character varying(45)   DEFAULT '' NOT NULL,
                                 datainserimento     timestamp without time zone,
                                 dataritiro          timestamp without time zone,
                                 scortaminima        integer                 DEFAULT 0   NOT NULL,
                                 codpadre            character varying(50),
                                 qtafiglio           integer                 DEFAULT 0   NOT NULL,
                                 codaccessori        character varying(45)   DEFAULT '' NOT NULL,
                                 grcassa             character varying(10)   DEFAULT '' NOT NULL,
                                 ordinabile          character(1)            DEFAULT ''  NOT NULL,
                                 datascad            timestamp without time zone,
                                 cod_iniziale        character varying(30)   DEFAULT ''  NOT NULL,
                                 var1                character varying(10)   DEFAULT ''  NOT NULL,
                                 var2                character varying(10)   DEFAULT ''  NOT NULL,
                                 var3                character varying(10)   DEFAULT ''  NOT NULL,
                                 var4                character varying(10)   DEFAULT ''  NOT NULL,
                                 cursoremod          character(1)            DEFAULT ''  NOT NULL,
                                 gestgiacenza        character(1)            DEFAULT ''  NOT NULL,
                                 codice2             character varying(50)   DEFAULT ''  NOT NULL,
                                 confezione          character(1)            DEFAULT ''  NOT NULL,
                                 g_cliente           character varying(20)   DEFAULT ''  NOT NULL,
                                 progre              integer                 DEFAULT 0,
                                 dataultimamodifica  timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                                 online_relevant     smallint                DEFAULT 0
);

ALTER TABLE ONLY public.articoli
    ADD CONSTRAINT articoli_pkey PRIMARY KEY (codice);

CREATE INDEX idx_articoli_codfornitore ON public.articoli USING btree (codfornitore);
CREATE INDEX idx_articoli_descrizione  ON public.articoli USING btree (descrizione);
CREATE INDEX idx_articoli_ean          ON public.articoli USING btree (ean);
CREATE INDEX idx_articoli_gruppo       ON public.articoli USING btree (gruppo);
CREATE INDEX idx_articoli_stagione     ON public.articoli USING btree (stagione);
CREATE INDEX idx_articoli_stato        ON public.articoli USING btree (stato);

ALTER TABLE public.articoli OWNER TO admin;

-- ==============================================================
-- TABELLA: articolo_stock
-- ==============================================================
CREATE TABLE public.articolo_stock (
                                       id                  bigint                      NOT NULL,
                                       codice_articolo     character varying(50)       NOT NULL,
                                       quantita_stock      integer DEFAULT 0           NOT NULL,
                                       data_aggiornamento  timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE SEQUENCE public.articolo_stock_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

ALTER SEQUENCE public.articolo_stock_id_seq OWNED BY public.articolo_stock.id;
ALTER TABLE ONLY public.articolo_stock ALTER COLUMN id SET DEFAULT nextval('public.articolo_stock_id_seq'::regclass);

ALTER TABLE ONLY public.articolo_stock
    ADD CONSTRAINT articolo_stock_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.articolo_stock
    ADD CONSTRAINT fk_articolo_stock_articolo FOREIGN KEY (codice_articolo)
        REFERENCES public.articoli(codice);

CREATE INDEX idx_articolo_stock_codice ON public.articolo_stock USING btree (codice_articolo);

ALTER TABLE public.articolo_stock OWNER TO admin;

-- ==============================================================
-- TABELLA: ordini
-- ==============================================================
CREATE TABLE public.ordini (
                               id              bigint                      NOT NULL,
                               numero_ordine   character varying(50)       NOT NULL,
                               stato           character varying(50)       NOT NULL,
                               totale          numeric(10,2)               NOT NULL,
                               data_ordine     timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                               fornitore_id    bigint                      NOT NULL
);

CREATE SEQUENCE public.ordini_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

ALTER SEQUENCE public.ordini_id_seq OWNED BY public.ordini.id;
ALTER TABLE ONLY public.ordini ALTER COLUMN id SET DEFAULT nextval('public.ordini_id_seq'::regclass);

ALTER TABLE ONLY public.ordini
    ADD CONSTRAINT ordini_pkey             PRIMARY KEY (id);
ALTER TABLE ONLY public.ordini
    ADD CONSTRAINT ordini_numero_ordine_key UNIQUE (numero_ordine);
ALTER TABLE ONLY public.ordini
    ADD CONSTRAINT fk_ordini_fornitore FOREIGN KEY (fornitore_id)
        REFERENCES public.fornitori(id);

CREATE INDEX idx_ordini_data      ON public.ordini USING btree (data_ordine);
CREATE INDEX idx_ordini_fornitore ON public.ordini USING btree (fornitore_id);
CREATE INDEX idx_ordini_stato     ON public.ordini USING btree (stato);

ALTER TABLE public.ordini OWNER TO admin;

-- ==============================================================
-- TABELLA: ordini_items
-- ==============================================================
CREATE TABLE public.ordini_items (
                                     id                  bigint          NOT NULL,
                                     ordine_id           bigint          NOT NULL,
                                     codice_articolo     character varying(50) NOT NULL,
                                     quantita            integer         NOT NULL,
                                     prezzo_unitario     numeric(10,2)   NOT NULL,
                                     subtotale           numeric(10,2)   NOT NULL
);

CREATE SEQUENCE public.ordini_items_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

ALTER SEQUENCE public.ordini_items_id_seq OWNED BY public.ordini_items.id;
ALTER TABLE ONLY public.ordini_items ALTER COLUMN id SET DEFAULT nextval('public.ordini_items_id_seq'::regclass);

ALTER TABLE ONLY public.ordini_items
    ADD CONSTRAINT ordini_items_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.ordini_items
    ADD CONSTRAINT fk_ordini_items_ordine   FOREIGN KEY (ordine_id)
        REFERENCES public.ordini(id) ON DELETE CASCADE;
ALTER TABLE ONLY public.ordini_items
    ADD CONSTRAINT fk_ordini_items_articolo FOREIGN KEY (codice_articolo)
        REFERENCES public.articoli(codice);

CREATE INDEX idx_ordini_items_articolo ON public.ordini_items USING btree (codice_articolo);
CREATE INDEX idx_ordini_items_ordine   ON public.ordini_items USING btree (ordine_id);

ALTER TABLE public.ordini_items OWNER TO admin;

-- ==============================================================
-- TABELLA: attivita
-- ==============================================================
CREATE TABLE public.attivita (
                                 id          bigint                      NOT NULL,
                                 tipo        character varying(50)       NOT NULL,
                                 descrizione text,
                                 "timestamp" timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE public.attivita_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

ALTER SEQUENCE public.attivita_id_seq OWNED BY public.attivita.id;
ALTER TABLE ONLY public.attivita ALTER COLUMN id SET DEFAULT nextval('public.attivita_id_seq'::regclass);

ALTER TABLE ONLY public.attivita
    ADD CONSTRAINT attivita_pkey PRIMARY KEY (id);

CREATE INDEX idx_attivita_timestamp ON public.attivita USING btree ("timestamp");

ALTER TABLE public.attivita OWNER TO admin;

-- ==============================================================
-- TABELLA: gerarchie
-- ==============================================================
CREATE TABLE public.gerarchie (
                                  lp1                 character(3)            DEFAULT '' NOT NULL,
                                  lp2                 character(3)            DEFAULT '' NOT NULL,
                                  lp3                 character(3)            DEFAULT '' NOT NULL,
                                  lp4                 character(3)            DEFAULT '' NOT NULL,
                                  lp5                 character(3)            DEFAULT '' NOT NULL,
                                  descri1             character varying(100)  DEFAULT '' NOT NULL,
                                  descri2             character varying(250)  DEFAULT '' NOT NULL,
                                  g_cliente           character varying(20)   DEFAULT '' NOT NULL,
                                  progre              integer                 DEFAULT 0  NOT NULL,
                                  dataultimamodifica  timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);

ALTER TABLE public.gerarchie OWNER TO admin;

-- ==============================================================
-- TABELLA: sco_dettaglio_sto
-- ==============================================================
CREATE TABLE public.sco_dettaglio_sto (
                                          id                  bigint                      NOT NULL,
                                          contatore           integer     DEFAULT 0       NOT NULL,
                                          numero              integer     DEFAULT 0       NOT NULL,
                                          dataora             timestamp without time zone NOT NULL,
                                          codnegozio          character varying(10)       NOT NULL,
                                          codpostazione       character varying(5)        DEFAULT '' NOT NULL,
                                          riga                integer     DEFAULT 0       NOT NULL,
                                          codarticolo         character varying(50),
                                          ean                 character varying(20)       DEFAULT '' NOT NULL,
                                          descri_articolo     character varying(100)      DEFAULT '' NOT NULL,
                                          quantita            character varying(10)       DEFAULT '' NOT NULL,
                                          scontoperc          character varying(15)       DEFAULT '0' NOT NULL,
                                          scontoimp           character varying(15)       DEFAULT '0,00' NOT NULL,
                                          prezzolistino       character varying(15)       DEFAULT '0,00' NOT NULL,
                                          prezzomodificato    character varying(15)       DEFAULT '0,00' NOT NULL,
                                          importo             character varying(15)       DEFAULT '0,00' NOT NULL,
                                          iva                 character varying(10)       DEFAULT '' NOT NULL,
                                          tipo_mov            character(1)                DEFAULT '' NOT NULL,
                                          scontoperctestata   character varying(15)       DEFAULT '0,00' NOT NULL,
                                          scontoimptestata    character varying(15)       DEFAULT '0,00' NOT NULL,
                                          scontocalc          character varying(15)       DEFAULT '0,00' NOT NULL,
                                          imposta             character varying(15)       DEFAULT '0,00' NOT NULL,
                                          imponibile          character varying(15)       DEFAULT '0,00' NOT NULL,
                                          codvenditore        character varying(45)       DEFAULT '' NOT NULL,
                                          old_codarticolo     character varying(50),
                                          old_descri_art      character varying(100)      DEFAULT '' NOT NULL,
                                          modificato          character(1)                DEFAULT '' NOT NULL,
                                          annullato           character(1)                DEFAULT '' NOT NULL,
                                          contatoreordcli     integer     DEFAULT 0       NOT NULL,
                                          voucher             character varying(45)       DEFAULT '' NOT NULL,
                                          inviatoalserverone  character(1)                NOT NULL,
                                          dataultimamodifica  timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE SEQUENCE public.sco_dettaglio_sto_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

ALTER SEQUENCE public.sco_dettaglio_sto_id_seq OWNED BY public.sco_dettaglio_sto.id;
ALTER TABLE ONLY public.sco_dettaglio_sto ALTER COLUMN id SET DEFAULT nextval('public.sco_dettaglio_sto_id_seq'::regclass);

ALTER TABLE ONLY public.sco_dettaglio_sto
    ADD CONSTRAINT sco_dettaglio_sto_pkey PRIMARY KEY (id);

CREATE INDEX idx_sco_codarticolo         ON public.sco_dettaglio_sto USING btree (codarticolo);
CREATE INDEX idx_sco_contatore           ON public.sco_dettaglio_sto USING btree (contatore);
CREATE INDEX idx_sco_dataora             ON public.sco_dettaglio_sto USING btree (dataora);
CREATE INDEX idx_sco_dettaglio_contatore ON public.sco_dettaglio_sto USING btree (contatore);
CREATE INDEX idx_sco_dettaglio_dataora   ON public.sco_dettaglio_sto USING btree (dataora);

ALTER TABLE public.sco_dettaglio_sto OWNER TO admin;

-- ==============================================================
-- TABELLA: products
-- ==============================================================
CREATE TABLE public.products (
                                 id              integer                     NOT NULL,
                                 name            character varying(255)      NOT NULL,
                                 sku             character varying(50),
                                 price           numeric(10,2),
                                 current_stock   integer         DEFAULT 0,
                                 avg_daily_sales numeric(10,2)   DEFAULT 0,
                                 total_revenue   numeric(12,2)   DEFAULT 0,
                                 days_of_stock   integer,
                                 category        character varying(100),
                                 first_sale_date date,
                                 last_sale_date  date,
                                 created_at      timestamp without time zone DEFAULT now()
);

CREATE SEQUENCE public.products_id_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

ALTER SEQUENCE public.products_id_seq OWNED BY public.products.id;
ALTER TABLE ONLY public.products ALTER COLUMN id SET DEFAULT nextval('public.products_id_seq'::regclass);

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey    PRIMARY KEY (id);
ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_sku_key UNIQUE (sku);

CREATE INDEX idx_revenue    ON public.products USING btree (total_revenue);
CREATE INDEX idx_stock_days ON public.products USING btree (days_of_stock);

ALTER TABLE public.products OWNER TO admin;

-- ==============================================================
-- TABELLA: utenti
-- ==============================================================
CREATE TABLE public.utenti (
                               id              bigint                      NOT NULL,
                               nome_utente     character varying(50)       NOT NULL,
                               password        character varying(64)       NOT NULL,
                               email           character varying(100)      NOT NULL,
                               ruolo           character varying(20)       NOT NULL,
                               attivo          boolean     DEFAULT true    NOT NULL,
                               data_creazione  timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
                               ultimo_accesso  timestamp without time zone
);

CREATE SEQUENCE public.utenti_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

ALTER SEQUENCE public.utenti_id_seq OWNED BY public.utenti.id;
ALTER TABLE ONLY public.utenti ALTER COLUMN id SET DEFAULT nextval('public.utenti_id_seq'::regclass);

ALTER TABLE ONLY public.utenti
    ADD CONSTRAINT utenti_pkey           PRIMARY KEY (id);
ALTER TABLE ONLY public.utenti
    ADD CONSTRAINT utenti_email_key      UNIQUE (email);
ALTER TABLE ONLY public.utenti
    ADD CONSTRAINT utenti_nome_utente_key UNIQUE (nome_utente);

ALTER TABLE public.utenti OWNER TO admin;

-- ==============================================================
-- DATA: fornitori
-- 1 → Swarovski  (COL002, BRA004)
-- 2 → Gold&Silver (ORE003, FED005)
-- 3 → Diamanti    (ANL001)
-- ==============================================================
INSERT INTO public.fornitori (id, nome, partita_iva, email, telefono, indirizzo, citta, cap, paese, created_at, updated_at) VALUES
                                                                                                                                (1, 'Swarovski Italia SRL',        '12345678901', 'swarovski@gioielleria.it',  '+39 02 8845123',  'Via Montenapoleone 8', 'Milano',  '20121', 'Italia', '2026-02-18 11:38:19.741032', '2026-02-18 11:38:19.741032'),
                                                                                                                                (2, 'Gold & Silver Wholesale SpA', '98765432109', 'goldsilver@gioielleria.it', '+39 055 7743210', 'Ponte Vecchio 12',     'Firenze', '50125', 'Italia', '2026-02-18 11:38:19.741032', '2026-02-18 11:38:19.741032'),
                                                                                                                                (3, 'Diamanti Preziosi Import',    '11223344556', 'diamanti@gioielleria.it',   '+39 081 5529876', 'Via Caracciolo 45',    'Napoli',  '80122', 'Italia', '2026-02-18 11:38:19.741032', '2026-02-18 11:38:19.741032');

SELECT pg_catalog.setval('public.fornitori_id_seq', 3, true);

-- ==============================================================
-- DATA: articoli
-- codfornitore: '1'=Swarovski '2'=Gold&Silver '3'=Diamanti
-- ==============================================================
INSERT INTO public.articoli (
    codice, descrizione, ean, prezzodilistino,
    gruppo, stato, lp1, lp2, lp3, lp4, lp5,
    lineaprod, stagione, linkimmagine,
    bidone, scodescri, tipo, iva,
    codfornitore, peso, note, ubicazione,
    datainserimento, dataritiro,
    scortaminima, codpadre, qtafiglio,
    codaccessori, grcassa, ordinabile,
    datascad, cod_iniziale,
    var1, var2, var3, var4,
    cursoremod, gestgiacenza,
    codice2, confezione, g_cliente,
    progre, dataultimamodifica, online_relevant
) VALUES
-- ANL001 → Diamanti Preziosi Import (id=3)
('ANL001', 'Anello Solitario Diamante 0.50ct',  '8001234567890', 2599.00,
 'ANELLI',    'AT', 'GIO', 'ANL', 'DIA', NULL, '',
 'Anelli Diamanti',    '2026 S/S',   'https://example.com/img/anl001.jpg',
 '', 'Anello Solitr', 'ST', '22  ',
 '3', '3.5',  'Oro bianco 18kt certificato',   'SCAFF-A12',
 NULL, NULL, 2, NULL, 0, '', 'REP01', 'S',
 NULL, 'ANL001', '', '', '', '', '', '',  '', '', '',
 1, '2026-02-18 11:38:19.743860', 1),

-- COL002 → Swarovski Italia SRL (id=1)
('COL002', 'Collana Swarovski Angelic Square',  '8001234567891',  129.00,
 'COLLANE',   'AT', 'GIO', 'COL', 'SWA', NULL, '',
 'Swarovski Collection','2026 S/S',  'https://example.com/img/col002.jpg',
 '', 'Coll Swarovs',  'ST', '22  ',
 '1', '2.1',  'Cristallo Swarovski azzurro',    'SCAFF-B05',
 NULL, NULL, 5, NULL, 0, '', 'REP01', 'S',
 NULL, 'COL002', '', '', '', '', '', '', '', '', '',
 2, '2026-02-18 11:38:19.743860', 1),

-- ORE003 → Gold & Silver Wholesale SpA (id=2)
('ORE003', 'Orecchini Punto Luce Oro Giallo',   '8001234567892',  890.00,
 'ORECCHINI', 'AT', 'GIO', 'ORE', 'ORO', NULL, '',
 'Orecchini Classici', '2026 S/S',   'https://example.com/img/ore003.jpg',
 '', 'Orecc PuntoL',  'ST', '22  ',
 '2', '1.8',  'Oro giallo 18kt con diamanti',   'CASSAF-C01',
 NULL, NULL, 3, NULL, 0, '', 'REP01', 'S',
 NULL, 'ORE003', '', '', '', '', '', '', '', '', '',
 3, '2026-02-18 11:38:19.743860', 1),

-- BRA004 → Swarovski Italia SRL (id=1)
('BRA004', 'Bracciale Swarovski Tennis Deluxe', '8001234567893',  289.00,
 'BRACCIALI', 'AT', 'GIO', 'BRA', 'SWA', NULL, '',
 'Swarovski Collection','2026 S/S',  'https://example.com/img/bra004.jpg',
 '', 'Bracc Tennis',  'ST', '22  ',
 '1', '12.5', 'Bracciale tennis con cristalli',  'SCAFF-B08',
 NULL, NULL, 4, NULL, 0, '', 'REP01', 'S',
 NULL, 'BRA004', '', '', '', '', '', '', '', '', '',
 4, '2026-02-18 11:38:19.743860', 1),

-- FED005 → Gold & Silver Wholesale SpA (id=2)
('FED005', 'Fede Nuziale Oro Bianco Classica',  '8001234567894',  320.00,
 'FEDI',      'AT', 'GIO', 'FED', 'ORO', NULL, '',
 'Fedi Nuziali',       'PERMANENTE', 'https://example.com/img/fed005.jpg',
 '', 'Fede Nuziale',  'ST', '22  ',
 '2', '4.2',  'Oro bianco 18kt 4mm',            'CASSAF-F01',
 NULL, NULL, 10, NULL, 0, '', 'REP01', 'S',
 NULL, 'FED005', '', '', '', '', '', '', '', '', '',
 5, '2026-02-18 11:38:19.743860', 1);

-- ==============================================================
-- DATA: articolo_stock
-- ==============================================================
INSERT INTO public.articolo_stock (id, codice_articolo, quantita_stock, data_aggiornamento) VALUES
                                                                                                (1, 'ANL001',  5, '2026-02-18 11:38:19.749998'),
                                                                                                (2, 'COL002', 12, '2026-02-18 11:38:19.749998'),
                                                                                                (3, 'ORE003',  8, '2026-02-18 11:38:19.749998'),
                                                                                                (4, 'BRA004', 15, '2026-02-18 11:38:19.749998'),
                                                                                                (5, 'FED005', 25, '2026-02-18 11:38:19.749998');

SELECT pg_catalog.setval('public.articolo_stock_id_seq', 5, true);

-- ==============================================================
-- DATA: attivita
-- ==============================================================
INSERT INTO public.attivita (id, tipo, descrizione, "timestamp") VALUES
                                                                     (1, 'NUOVO_ORDINE',      'Creato nuovo ordine ORD-2026-003 per orecchini e fedi',          '2026-02-18 11:38:19.756243'),
                                                                     (2, 'NUOVO_ARTICOLO',    'Aggiunto articolo ANL001 - Anello Solitario Diamante all''inventario', '2026-02-18 11:38:19.756243'),
                                                                     (3, 'ORDINE_CONSEGNATO', 'Ordine ORD-2026-001 consegnato - 2 anelli solitario',            '2026-02-18 11:38:19.756243');

SELECT pg_catalog.setval('public.attivita_id_seq', 3, true);

-- ==============================================================
-- DATA: ordini
-- fornitore_id: 1=Swarovski 2=Gold&Silver 3=Diamanti
-- ==============================================================
INSERT INTO public.ordini (id, numero_ordine, stato, totale, data_ordine, fornitore_id) VALUES
                                                                                            (1, 'ORD-2026-001', 'CONSEGNATO',    5198.00, '2026-02-18 11:38:19.746916', 3),
                                                                                            (2, 'ORD-2026-002', 'IN_LAVORAZIONE', 836.00, '2026-02-18 11:38:19.746916', 1),
                                                                                            (3, 'ORD-2026-003', 'CREATO',        1530.00, '2026-02-18 11:38:19.746916', 2);

SELECT pg_catalog.setval('public.ordini_id_seq', 3, true);

-- ==============================================================
-- DATA: ordini_items
-- ==============================================================
INSERT INTO public.ordini_items (id, ordine_id, codice_articolo, quantita, prezzo_unitario, subtotale) VALUES
                                                                                                           (1, 1, 'ANL001', 2, 2599.00, 5198.00),
                                                                                                           (2, 2, 'COL002', 5,  129.00,  645.00),
                                                                                                           (3, 2, 'BRA004', 1,  289.00,  289.00),
                                                                                                           (4, 3, 'ORE003', 1,  890.00,  890.00),
                                                                                                           (5, 3, 'FED005', 2,  320.00,  640.00);

SELECT pg_catalog.setval('public.ordini_items_id_seq', 5, true);

-- ==============================================================
-- DATA: gerarchie (vuota)
-- ==============================================================
-- nessun record

-- ==============================================================
-- DATA: sco_dettaglio_sto
-- ==============================================================
INSERT INTO public.sco_dettaglio_sto (
    id, contatore, numero, dataora,
    codnegozio, codpostazione, riga,
    codarticolo, ean, descri_articolo,
    quantita, scontoperc, scontoimp,
    prezzolistino, prezzomodificato, importo,
    iva, tipo_mov,
    scontoperctestata, scontoimptestata, scontocalc,
    imposta, imponibile,
    codvenditore, old_codarticolo, old_descri_art,
    modificato, annullato, contatoreordcli, voucher,
    inviatoalserverone, dataultimamodifica
) VALUES
      (1, 1, 1001, '2026-02-13 11:53:41.332500', 'NEG01', 'POS1', 1,
       'COL002', '8001234567891', 'Coll Swarovs',
       '1', '0', '0,00', '129,00', '129,00', '129,00',
       '22', '-', '0,00', '0,00', '0,00', '23,28', '105,72',
       'VEND01', NULL, '', '', '', 0, '', 'S', '2026-02-18 11:53:41.332500'),

      (2, 1, 1001, '2026-02-13 11:53:41.332500', 'NEG01', 'POS1', 2,
       'BRA004', '8001234567893', 'Bracc Tennis',
       '1', '0', '0,00', '289,00', '289,00', '289,00',
       '22', '-', '0,00', '0,00', '0,00', '52,13', '236,87',
       'VEND01', NULL, '', '', '', 0, '', 'S', '2026-02-18 11:53:41.332500'),

      (3, 2, 1002, '2026-02-14 11:53:54.436464', 'NEG01', 'POS1', 1,
       'ANL001', '8001234567890', 'Anello Solitr',
       '1', '0', '0,00', '2599,00', '2599,00', '2599,00',
       '22', '-', '0,00', '0,00', '0,00', '468,67', '2130,33',
       'VEND02', NULL, '', '', '', 0, '', 'S', '2026-02-18 11:53:54.436464'),

      (4, 3, 1003, '2026-02-15 11:54:13.052851', 'NEG01', 'POS2', 1,
       'FED005', '8001234567894', 'Fede Nuziale',
       '2', '0', '0,00', '320,00', '320,00', '640,00',
       '22', '-', '0,00', '0,00', '0,00', '115,41', '524,59',
       'VEND01', NULL, '', '', '', 0, '', 'S', '2026-02-18 11:54:13.052851'),

      (5, 4, 1004, '2026-02-16 11:54:25.282114', 'NEG01', 'POS1', 1,
       'ORE003', '8001234567892', 'Orecc PuntoL',
       '1', '10', '89,00', '890,00', '801,00', '801,00',
       '22', '-', '0,00', '0,00', '89,00', '144,46', '656,54',
       'VEND03', NULL, '', '', '', 0, '', 'S', '2026-02-18 11:54:25.282114'),

      (6, 5, 1005, '2026-02-17 11:54:51.104794', 'NEG01', 'POS2', 1,
       'COL002', '8001234567891', 'Coll Swarovs',
       '2', '0', '0,00', '129,00', '129,00', '258,00',
       '22', '-', '0,00', '0,00', '0,00', '46,56', '211,44',
       'VEND02', NULL, '', '', '', 0, '', 'S', '2026-02-18 11:54:51.104794'),

      (7, 5, 1005, '2026-02-17 11:54:51.104794', 'NEG01', 'POS2', 2,
       'FED005', '8001234567894', 'Fede Nuziale',
       '1', '0', '0,00', '320,00', '320,00', '320,00',
       '22', '-', '0,00', '0,00', '0,00', '57,70', '262,30',
       'VEND02', NULL, '', '', '', 0, '', 'S', '2026-02-18 11:54:51.104794');

SELECT pg_catalog.setval('public.sco_dettaglio_sto_id_seq', 7, true);

-- ==============================================================
-- DATA: products
-- ==============================================================
INSERT INTO public.products (id, name, sku, price, current_stock, avg_daily_sales, total_revenue, days_of_stock, category, first_sale_date, last_sale_date, created_at) VALUES
                                                                                                                                                                            (1,  'Laptop Premium', 'LAP001',  1200.00,   2,  1.50, 15000.00,    1, 'Elettronica',    '2025-12-01', '2026-02-18', '2026-02-18 11:57:15.457390'),
                                                                                                                                                                            (2,  'Top Urgenza',    'URG001',   899.00,   0,  5.00,  8500.00,    0, 'Urgenze',        '2025-12-01', '2026-02-18', '2026-02-18 11:57:15.457390'),
                                                                                                                                                                            (3,  'Slow Mover',     'SLOW001',   25.00, 150,  0.20,   500.00,  750, 'Magazzino',      '2025-12-01', '2026-02-18', '2026-02-18 11:57:15.457390'),
                                                                                                                                                                            (4,  'Best Seller',    'BS001',    299.00,   5,  8.00, 25000.00,    1, 'Top',            '2025-12-01', '2026-02-18', '2026-02-18 11:57:15.457390'),
                                                                                                                                                                            (5,  'Premium Alto',   'PREM001', 2500.00,   3,  0.50, 18000.00,    6, 'Luxury',         '2025-12-01', '2026-02-18', '2026-02-18 11:57:15.457390'),
                                                                                                                                                                            (6,  'Stock OK',       'STK001',   150.00,  50,  2.00,  4500.00,   25, 'Standard',       '2025-12-01', '2026-02-18', '2026-02-18 11:57:15.457390'),
                                                                                                                                                                            (7,  'Eccesso',        'EXC001',    80.00, 200,  0.10,  2000.00, 2000, 'Eccesso',        '2025-12-01', '2026-02-18', '2026-02-18 11:57:15.457390'),
                                                                                                                                                                            (8,  'Low Revenue',    'LOW001',    10.00, 100,  1.00,   800.00,  100, 'Base',           '2025-12-01', '2026-02-18', '2026-02-18 11:57:15.457390'),
                                                                                                                                                                            (9,  'Daily Alto',     'DAY001',   400.00,  10, 12.00, 12000.00,    1, 'Alta Rotazione', '2025-12-01', '2026-02-18', '2026-02-18 11:57:15.457390'),
                                                                                                                                                                            (10, 'Critico Revenue','CRIT001',  750.00,   1,  3.00,  6000.00,    0, 'Critico',        '2025-12-01', '2026-02-18', '2026-02-18 11:57:15.457390');

SELECT pg_catalog.setval('public.products_id_seq', 10, true);

-- ==============================================================
-- DATA: utenti
-- password SHA-256: admin='admin123' staff='staff123'
-- ==============================================================
INSERT INTO public.utenti (id, nome_utente, password, email, ruolo, attivo, data_creazione, ultimo_accesso) VALUES
                                                                                                                (1, 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin@gioielleria.it', 'ADMIN', true, '2026-02-18 11:37:49.385840', NULL),
                                                                                                                (2, 'staff', '10176e7b7b24d317acfcf8d2064cfd2f24e154f7b5a96603077d5ef813d6a6b6', 'staff@gioielleria.it', 'STAFF', true, '2026-02-18 11:37:49.385840', NULL);

SELECT pg_catalog.setval('public.utenti_id_seq', 2, true);

-- ==============================================================
-- Fine dump
-- ==============================================================
