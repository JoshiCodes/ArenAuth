CREATE TABLE oauth_requests
(
    id           UUID                        NOT NULL,
    client_id    VARCHAR(255)                NOT NULL,
    redirect_uri VARCHAR(255)                NOT NULL,
    scope        VARCHAR(255)                NOT NULL,
    state        VARCHAR(255),
    project_id   UUID                        NOT NULL,
    createdAt    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expiresAt    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_oauth_requests PRIMARY KEY (id)
);

ALTER TABLE oauth_requests
    ADD CONSTRAINT FK_OAUTH_REQUESTS_ON_PROJECT FOREIGN KEY (project_id) REFERENCES projects (id);