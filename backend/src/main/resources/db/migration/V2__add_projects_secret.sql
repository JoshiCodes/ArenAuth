ALTER TABLE projects
    ADD client_secret VARCHAR(255);

ALTER TABLE projects
    ALTER COLUMN client_secret SET NOT NULL;