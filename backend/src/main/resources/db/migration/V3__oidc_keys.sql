CREATE TABLE oidc_keys
(
    kid           VARCHAR(50)                 NOT NULL,
    publicKeyPem  OID                         NOT NULL,
    privateKeyPem OID                         NOT NULL,
    createdAt     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    active        BOOLEAN                     NOT NULL,
    CONSTRAINT pk_oidc_keys PRIMARY KEY (kid)
);