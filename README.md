<div align="center">

<img src="https://img.shields.io/badge/QSmartPark-00b894?style=for-the-badge&logoColor=white" alt="QSmartPark"/>

# 🅿️ QSmartPark

**An intelligent smart parking management system**

[![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=flat-square&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](LICENSE)

[Overview](#overview) · [Features](#features) · [Architecture](#architecture) · [Getting Started](#getting-started) · [Contributing](#contributing)

</div>

---

## Overview

QSmartPark is a full-stack smart parking management system that enables real-time monitoring of parking slot availability, automated slot booking, and admin management — all from a clean, modern web interface.

> 🔗 **Live Demo:** [Coming Soon](#)

## Features

- 🟢 **Real-time Slot Availability** — Live view of occupied vs. free parking slots
- 📅 **Slot Reservation** — Users can book parking slots in advance
- 👤 **User & Admin Portals** — Separate dashboards for users and administrators
- 📊 **Analytics Dashboard** — Visualize parking usage trends
- 🔔 **Notifications** — Alerts for booking confirmations and slot changes
- 📱 **Responsive UI** — Works seamlessly on all devices

## Architecture

QSmartPark is split into two repositories:

| Repo | Stack | Description |
|------|-------|-------------|
| [qsmartpark-frontend](https://github.com/akalankawijesing/qsmartpark-frontend) | TypeScript | Web interface |
| [qsmartpark-backend](https://github.com/akalankawijesing/qsmartpark-backend) | Java / Spring Boot | REST API & business logic |

```
┌─────────────────────┐        ┌──────────────────────┐
│   Frontend (TS)     │ ──────▶│   Backend (Java)      │
│   User Interface    │  REST  │   Business Logic      │
│   Admin Dashboard   │        │   Database Layer      │
└─────────────────────┘        └──────────────────────┘
```

## Tech Stack

### Frontend
- TypeScript
- React / Angular / Vue *(update with actual framework)*
- CSS / Tailwind

### Backend
- Java 17+
- Spring Boot
- Spring Security (JWT)
- MySQL / PostgreSQL

## Getting Started

### Backend

```bash
# Clone backend
git clone https://github.com/akalankawijesing/qsmartpark-backend.git
cd qsmartpark-backend

# Configure database in application.properties
# Run with Maven
./mvnw spring-boot:run
```

### Frontend

```bash
# Clone frontend
git clone https://github.com/akalankawijesing/qsmartpark-frontend.git
cd qsmartpark-frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

### Environment Variables (Frontend)

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## Project Structure

```
Backend (Java)
├── controller/     # REST controllers
├── service/        # Business logic
├── repository/     # Data access layer
├── model/          # Entity classes
└── config/         # Security & app config

Frontend (TypeScript)
├── components/     # UI components
├── pages/          # Route pages
├── services/       # API integrations
└── types/          # TypeScript types
```

## Contributing

Pull requests are welcome! Please open an issue to discuss what you'd like to change.

---

<div align="center">
  Made with ❤️ by <a href="https://github.com/akalankawijesing">Akalanka Wijesing</a>
</div>
