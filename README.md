<div align="center">

![Logo](https://raw.githubusercontent.com/JoshiCodes/ArenAuth/refs/heads/master/frontend/src/lib/assets/favicon.svg)
# ArenAuth

A modern OAuth 2.0 and OpenID Connect server built with Quarkus and Svelte.
</div>

## Overview

ArenAuth is a self-hosted authentication and authorization platform. It provides OAuth 2.0 flows, JWT-based security, user management, and project/application management with avatar support.

> [!Warning]
> This project is still in early development and should not be used in production environments. Use at your own risk.
> Some important features, like PCKE support, an admin page (settings) and more are still missing.
> If you want to contribute, feel free to open a PR or an issue. I would love to see this project grow and improve.

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
> Currently, the docker images are only published for this repository.
> You may need to implement a custom registry to use github (ghcr.io) images.
> See [this guide](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry) for more information.

To install ArenAuth, copy the [docker-compose.yaml](https://github.com/JoshiCodes/ArenAuth/blob/master/docker/docker-compose.yaml)
and the [.env.docker](https://github.com/JoshiCodes/ArenAuth/blob/master/docker/.env.docker) files to your server (or set the environment variables manually).
Make sure to choose the corrent architecture for the docker images (amd64 or arm64) in the docker-compose.yaml file. Default is arm64. <br>
Then, make sure to update the environment variables in the .env.docker file (or manually) to fit your needs.
I do recommend running both the backend and frontend on the same domain, using a reverse proxy (like nginx or caddy).
The most important variables to change are:
- `FRONTEND_URL`: The frontend url (with https:// or http://) | This is the general frontend url.
- `BACKEND_URL`: The backend url (with https:// or http://) | This is the general backend url.
- `INTERNAL_BACKEND_URL`: The backend url used internally by the backend (with http://) | I do recommend setting this to the same as BACKEND_URL or something like 'backend:8080' (if using docker).
- `COOKIE_DOMAIN`: Your domain (without https:// or http://) | This is used for setting the cookie domain, so make sure to set this correctly.
- `USE_HTTPS`: Whether or not you plan on using https. | This is used for setting the cookie.
Have a look at the .env.docker file to see all the available environment variables.

I do recommend setting the FRONTEND_URL to something like `https://auth.yourdomain.tld` and BACKEND_URL and INTERNAL_BACKEND_URL to the same. 
In this case, you will need to set the `COOKIE_DOMAIN` to `auth.yourdomain.tld` and `USE_HTTPS` to `true`.
Then, use a reverse proxy to route the traffic to the corresponding services.
You will need to serve / to the frontend container (default port 3000).
Then, route /api, /oauth2 and /.well-known to the backend container (with the path) (default port 8080). *This may get changed in the future.* (e.g. auth.yourdomain.tld/api → (local backend url):8080/api)
Of course, if you change any of the ports, you have to update your proxy configuration and environment variables accordingly.

To start your application, run the following command:
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
*(Alternatively, if this fails and you have the code on a different drive, maybe try adding "-Dquarkus.native.remote-container-build=true".)*

<br>Then, build the docker image:
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
