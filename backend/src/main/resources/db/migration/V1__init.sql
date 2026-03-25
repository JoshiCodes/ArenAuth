CREATE TABLE project_redirect_uris
(
    project_id   UUID         NOT NULL,
    redirect_uri VARCHAR(255) NOT NULL
);

CREATE TABLE projects
(
    id          UUID NOT NULL,
    name        VARCHAR(255),
    description VARCHAR(255),
    imageBlob   VARCHAR(255),
    owner_id    UUID NOT NULL,
    CONSTRAINT pk_projects PRIMARY KEY (id)
);

CREATE TABLE users
(
    id       UUID         NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE users_roles
(
    user_id UUID NOT NULL,
    role    VARCHAR(255)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE projects
    ADD CONSTRAINT FK_PROJECTS_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (id);

ALTER TABLE project_redirect_uris
    ADD CONSTRAINT fk_project_redirect_uris_on_project FOREIGN KEY (project_id) REFERENCES projects (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_users_roles_on_user FOREIGN KEY (user_id) REFERENCES users (id);