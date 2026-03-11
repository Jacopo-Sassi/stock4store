-- ==============================================================
-- PostgreSQL full dump – Gioielleria PW Backend (corretto)
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
-- DROP TABLES
-- ==============================================================

DROP TABLE IF EXISTS public.ordini_items CASCADE;
DROP TABLE IF EXISTS public.ordini CASCADE;
DROP TABLE IF EXISTS public.articolo_stock CASCADE;
DROP TABLE IF EXISTS public.sco_dettaglio_sto CASCADE;
DROP TABLE IF EXISTS public.articoli CASCADE;
DROP TABLE IF EXISTS public.fornitori CASCADE;
DROP TABLE IF EXISTS public.gerarchie CASCADE;
DROP TABLE IF EXISTS public.attivita CASCADE;
DROP TABLE IF EXISTS public.products CASCADE;
DROP TABLE IF EXISTS public.utenti CASCADE;

-- ==============================================================
-- TABELLA: fornitori
-- ==============================================================

CREATE TABLE public.fornitori (
                                  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                  nome varchar(255) NOT NULL,
                                  partita_iva varchar(20) NOT NULL UNIQUE,
                                  email varchar(255) NOT NULL UNIQUE,
                                  telefono varchar(50),
                                  indirizzo varchar(500),
                                  citta varchar(100),
                                  cap varchar(10),
                                  paese varchar(100),
                                  created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                                  updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================
-- TABELLA: articoli
-- ==============================================================

CREATE TABLE public.articoli (
                                 codice varchar(50) PRIMARY KEY,
                                 descrizione varchar(100) NOT NULL,
                                 ean varchar(13) NOT NULL,
                                 prezzodilistino numeric(10,2) NOT NULL,
                                 gruppo varchar(15) NOT NULL,
                                 stato varchar(2) NOT NULL,
                                 codfornitore varchar(45) NOT NULL,
                                 scortaminima integer DEFAULT 0 NOT NULL,
                                 dataultimamodifica timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                 online_relevant smallint DEFAULT 0 NOT NULL
);

CREATE INDEX idx_articoli_codfornitore ON public.articoli(codfornitore);
CREATE INDEX idx_articoli_descrizione ON public.articoli(descrizione);
CREATE INDEX idx_articoli_ean ON public.articoli(ean);

-- ==============================================================
-- TABELLA: articolo_stock
-- ==============================================================

CREATE TABLE public.articolo_stock (
                                       id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                       codice_articolo varchar(50) NOT NULL,
                                       quantita_stock integer DEFAULT 0 NOT NULL,
                                       data_aggiornamento timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                       FOREIGN KEY (codice_articolo) REFERENCES public.articoli(codice)
);

CREATE INDEX idx_articolo_stock_codice ON public.articolo_stock(codice_articolo);

-- ==============================================================
-- TABELLA: ordini
-- ==============================================================

CREATE TABLE public.ordini (
                               id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               contatore bigint GENERATED ALWAYS AS IDENTITY,
                               numero_ordine varchar(50) GENERATED ALWAYS AS (
                                   'ORD-' || EXTRACT(YEAR FROM CURRENT_DATE)::text || '-' || LPAD(contatore::text,3,'0')
                                   ) STORED UNIQUE,
                               stato varchar(50) NOT NULL,
                               totale numeric(10,2) NOT NULL,
                               data_ordine timestamp DEFAULT CURRENT_TIMESTAMP,
                               fornitore_id bigint NOT NULL,
                               FOREIGN KEY (fornitore_id) REFERENCES public.fornitori(id)
);

CREATE INDEX idx_ordini_data ON public.ordini(data_ordine);
CREATE INDEX idx_ordini_fornitore ON public.ordini(fornitore_id);
CREATE INDEX idx_ordini_stato ON public.ordini(stato);

-- ==============================================================
-- TABELLA: ordini_items
-- ==============================================================

CREATE TABLE public.ordini_items (
                                     id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     ordine_id bigint NOT NULL,
                                     codice_articolo varchar(50) NOT NULL,
                                     quantita integer NOT NULL,
                                     prezzo_unitario numeric(10,2) NOT NULL,
                                     subtotale numeric(10,2) NOT NULL,
                                     FOREIGN KEY (ordine_id) REFERENCES public.ordini(id) ON DELETE CASCADE,
                                     FOREIGN KEY (codice_articolo) REFERENCES public.articoli(codice)
);

CREATE INDEX idx_ordini_items_articolo ON public.ordini_items(codice_articolo);
CREATE INDEX idx_ordini_items_ordine ON public.ordini_items(ordine_id);

-- ==============================================================
-- TABELLA: attivita
-- ==============================================================

CREATE TABLE public.attivita (
                                 id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                 tipo varchar(50) NOT NULL,
                                 descrizione text,
                                 "timestamp" timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_attivita_timestamp ON public.attivita("timestamp");

-- ==============================================================
-- TABELLA: gerarchie
-- ==============================================================

CREATE TABLE public.gerarchie (
                                  lp1 char(3) DEFAULT '' NOT NULL,
                                  lp2 char(3) DEFAULT '' NOT NULL,
                                  lp3 char(3) DEFAULT '' NOT NULL,
                                  lp4 char(3) DEFAULT '' NOT NULL,
                                  lp5 char(3) DEFAULT '' NOT NULL,
                                  descri1 varchar(100) DEFAULT '' NOT NULL,
                                  descri2 varchar(250) DEFAULT '' NOT NULL
);

-- ==============================================================
-- TABELLA: products
-- ==============================================================

CREATE TABLE public.products (
                                 id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                 name varchar(255) NOT NULL,
                                 sku varchar(50) UNIQUE,
                                 price numeric(10,2),
                                 current_stock integer DEFAULT 0,
                                 avg_daily_sales numeric(10,2) DEFAULT 0,
                                 total_revenue numeric(12,2) DEFAULT 0,
                                 days_of_stock integer,
                                 category varchar(100),
                                 first_sale_date date,
                                 last_sale_date date,
                                 created_at timestamp DEFAULT now()
);

CREATE INDEX idx_revenue ON public.products(total_revenue);
CREATE INDEX idx_stock_days ON public.products(days_of_stock);

-- ==============================================================
-- TABELLA: utenti
-- ==============================================================

CREATE TABLE public.utenti (
                               id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               nome_utente varchar(50) NOT NULL UNIQUE,
                               password varchar(64) NOT NULL,
                               email varchar(100) NOT NULL UNIQUE,
                               ruolo varchar(20) NOT NULL,
                               attivo boolean DEFAULT true NOT NULL,
                               data_creazione timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                               ultimo_accesso timestamp
);

-- ==============================================================
-- DATA: fornitori
-- ==============================================================

INSERT INTO public.fornitori (nome, partita_iva, email, telefono, indirizzo, citta, cap, paese) VALUES
                                                                                                    ('Swarovski Italia SRL','12345678901','swarovski@gioielleria.it','+39 02 8845123','Via Montenapoleone 8','Milano','20121','Italia'),
                                                                                                    ('Gold & Silver Wholesale SpA','98765432109','goldsilver@gioielleria.it','+39 055 7743210','Ponte Vecchio 12','Firenze','50125','Italia'),
                                                                                                    ('Diamanti Preziosi Import','11223344556','diamanti@gioielleria.it','+39 081 5529876','Via Caracciolo 45','Napoli','80122','Italia');

-- ==============================================================
-- DATA: articoli
-- ==============================================================

INSERT INTO public.articoli
(codice,descrizione,ean,prezzodilistino,gruppo,stato,codfornitore,scortaminima,online_relevant)
VALUES
    ('ANL001','Anello Solitario Diamante 0.50ct','8001234567890',2599.00,'ANELLI','AT','3',2,1),
    ('COL002','Collana Swarovski Angelic Square','8001234567891',129.00,'COLLANE','AT','1',5,1),
    ('ORE003','Orecchini Punto Luce Oro Giallo','8001234567892',890.00,'ORECCHINI','AT','2',3,1),
    ('BRA004','Bracciale Swarovski Tennis Deluxe','8001234567893',289.00,'BRACCIALI','AT','1',4,1),
    ('FED005','Fede Nuziale Oro Bianco Classica','8001234567894',320.00,'FEDI','AT','2',10,1);

-- ==============================================================
-- DATA: stock
-- ==============================================================

INSERT INTO public.articolo_stock (codice_articolo,quantita_stock) VALUES
                                                                       ('ANL001',5),
                                                                       ('COL002',12),
                                                                       ('ORE003',8),
                                                                       ('BRA004',15),
                                                                       ('FED005',25);

-- ==============================================================
-- DATA: ordini (numero_ordine generato automaticamente)
-- ==============================================================

INSERT INTO public.ordini (stato, totale, data_ordine, fornitore_id) VALUES
                                                                         ('CONSEGNATO',5198.00,'2026-02-18 11:38:19',3),
                                                                         ('IN_LAVORAZIONE',836.00,'2026-02-18 11:38:19',1),
                                                                         ('CREATO',1530.00,'2026-02-18 11:38:19',2);

-- ==============================================================
-- DATA: ordini_items
-- ==============================================================

INSERT INTO public.ordini_items
(ordine_id,codice_articolo,quantita,prezzo_unitario,subtotale)
VALUES
    (1,'ANL001',2,2599.00,5198.00),
    (2,'COL002',5,129.00,645.00),
    (2,'BRA004',1,289.00,289.00),
    (3,'ORE003',1,890.00,890.00),
    (3,'FED005',2,320.00,640.00);

-- ==============================================================
-- DATA: utenti
-- ==============================================================

INSERT INTO public.utenti (nome_utente,password,email,ruolo)
VALUES
    ('admin','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','admin@gioielleria.it','ADMIN'),
    ('staff','10176e7b7b24d317acfcf8d2064cfd2f24e154f7b5a96603077d5ef813d6a6b6','staff@gioielleria.it','STAFF');