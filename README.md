🚀 Overview

Stock4Store è una piattaforma full-stack per la gestione di inventario, ordini e fornitori nel settore retail (es. gioielleria), potenziata da analytics avanzati e modelli di intelligenza artificiale in locale.

L’obiettivo non è solo gestire dati, ma:

prendere decisioni migliori sugli acquisti.

🧠 Core Features
🔐 Autenticazione JWT (login, logout, verifica token)
📦 Gestione inventario articoli (CRUD + stock)
🛒 Gestione ordini con workflow stati
🏢 Gestione fornitori
📊 Dashboard e analytics vendite

🤖 AI Analytics per:
previsioni di vendita
suggerimenti acquisto
analisi stock
📈 Analisi scontrini e performance

🧩 Architettura a microservizi (Java + Python AI)

🏗️ Architettura

Frontend
Vue.js
UI navigabile per:
dashboard
gestione articoli
ordini
analytics

👉 Scelta sensata: Vue è veloce da sviluppare e meno verboso di React.

Backend principale
Java + Spring Boot
REST API strutturata (OpenAPI 3.0)
Gestione:
autenticazione (JWT)
business logic
integrazione database

Build & dependency:

Maven
AI Service
Python
Modello:
Mistral 7B (local)

Funzioni:

analisi vendite
previsione domanda
suggerimenti di riordino

👉 Scelta intelligente: AI locale = niente costi cloud + controllo dati.

Database & Migration
Database relazionale
Liquibase per versionamento schema

👉 Tradotto: niente “funziona sul mio PC”.

Infrastructure
🐳 Docker → containerizzazione completa
🖥️ Fedora Linux → ambiente host
🌐 Tailscale VPN → accesso sicuro + HTTPS
📦 Portainer → gestione container via UI
🔄 CI/CD via GitHub (build immagini Docker)
🔌 API Overview

Base URL:

http://localhost:8080

🔐 Authentication
POST /api/auth/login
POST /api/auth/logout
POST /api/auth/verify
GET /api/auth/me
PUT /api/auth/change-password

📦 Articoli
GET /api/articoli
POST /api/articoli
GET /api/articoli/{codice}
PUT /api/articoli/{codice}
DELETE /api/articoli/{codice}
GET /api/articoli/{codice}/stock
POST /api/articoli/search
GET /api/articoli/ean/{ean}

🛒 Ordini
GET /api/ordini
POST /api/ordini
GET /api/ordini/{id}
DELETE /api/ordini/{id}
PUT /api/ordini/{id}/avanza

Workflow stati:

CREATO → IN_LAVORAZIONE → SPEDITO → CONSEGNATO
                     ↘ ANNULLATO

🏢 Fornitori
CRUD completo + ricerca

📊 Analytics
GET /api/analytics/articoli
GET /api/ai-analytics
🤖 AI Analytics – Il vero differenziatore

Il sistema utilizza un modello LLM locale per:

📉 Analizzare trend di vendita
📦 Identificare prodotti critici
💡 Suggerire quantità di riordino
💰 Stimare ROI e margini

Output:

raccomandazioni operative
previsioni economiche
insight testuali

⚙️ Setup
🐳 Docker (consigliato)
docker-compose up -d

Gestione via:

Portainer UI

🖥️ Backend Java
mvn clean install
mvn spring-boot:run

🤖 AI Service (Python)
pip install -r requirements.txt
python app.py

🌐 Frontend
npm install
npm run serve


⚠️ Sfide e Limitazioni


❗ AI locale → richiede hardware adeguato
❗ qualità previsioni dipende dai dati storici
❗ molte integrazioni → rischio punti di rottura
❗ complessità architetturale elevata

💡 Miglioramenti Futuri
📊 Modelli predittivi più avanzati (time-series)
🧠 Fine-tuning modello AI su dati reali
☁️ Opzione cloud ibrida
📱 Mobile app per gestione inventario
🔔 Alert automatici su stock critico