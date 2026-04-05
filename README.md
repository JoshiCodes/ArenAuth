# ArenAuth

A modern OAuth 2.0 and OpenID Connect server built with Quarkus and Svelte.

## Overview

ArenAuth is a self-hosted authentication and authorization platform. It provides OAuth 2.0 flows, JWT-based security, user management, and project/application management with avatar support.

## Features

- OAuth 2.0 authorization server
- OpenID Connect support (currently no PKCE)
- JWT-based authentication
- User and project management
- PostgreSQL backend
- Responsive web dashboard

## Tech Stack

> [!Note]
> This is my first real quarkus project, so the codebase is probably a mess.
> Feel free to improve some things if you want to.

**Backend:**
- Java 21 with Quarkus 3.32.4
- PostgreSQL database
- Hibernate ORM with Panache
- SmallRye JWT and Security
- Flyway migrations

**Frontend:**
- Svelte 5 with SvelteKit
- TypeScript
- Tailwind CSS
- Vite

## Install
> [!Note]
> Currently, the docker images are not published to any registry, so you need to build them yourself. The instructions for building the images are in the next section.

To install ArenAuth copy the `docker-compose.yml` file to your server.
Make sure to configure the environment variables in the `docker-compose.yml` file according to your needs (e.g., database credentials, JWT secret, etc.).

Then, run the following command to start the application:
```bash
docker-compose up -d
```

## Build

### Backend (docker / quarkus-native)
To start, you need to cd into the backend directory:
```bash
cd backend
```
First, run the following command to build the backend:
```bash
.\mvnw clean package -Dnative "-Dquarkus.native.container-build=true"
```
(Alternatively, if this fails and you have the code on a different drive, maybe try adding "-Dquarkus.native.remote-container-build=true".)
Then, build the docker image:
```bash
docker build -f ./src/main/docker/Dockerfile.native -t arenauth-backend:latest .
```

### Frontend (docker / sveltekit with node adapter)
To start, you need to cd into the frontend directory:
```bash
cd frontend
```

Then, build the docker image:
```bash
docker build -f ./Dockerfile -t arenauth-frontend:latest .
```
