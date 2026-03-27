CREATE TABLE oauth_consent_records
(
    id         UUID                        NOT NULL,
    owner_id   UUID                        NOT NULL,
    project_id UUID                        NOT NULL,
    createdAt  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expiresAt  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_oauth_consent_records PRIMARY KEY (id)
);

ALTER TABLE projects
    ALTER COLUMN client_secret SET NOT NULL;

ALTER TABLE oauth_consent_records
    ADD CONSTRAINT FK_OAUTH_CONSENT_RECORDS_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (id);

ALTER TABLE oauth_consent_records
    ADD CONSTRAINT FK_OAUTH_CONSENT_RECORDS_ON_PROJECT FOREIGN KEY (project_id) REFERENCES projects (id);