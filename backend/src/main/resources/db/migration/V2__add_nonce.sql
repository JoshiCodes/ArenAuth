ALTER TABLE oauth_authorization_codes
    ADD nonce VARCHAR(255);

ALTER TABLE oauth_requests
    ADD nonce VARCHAR(255);