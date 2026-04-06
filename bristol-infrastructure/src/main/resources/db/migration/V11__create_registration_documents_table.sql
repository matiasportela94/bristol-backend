CREATE TABLE IF NOT EXISTS registration_documents
(
    id                      UUID PRIMARY KEY,
    registration_request_id UUID         NOT NULL,
    file_name               VARCHAR(255) NOT NULL,
    content_type            VARCHAR(100) NOT NULL,
    document_type           VARCHAR(50)  NOT NULL,
    file_size               BIGINT       NOT NULL,
    file_data               BYTEA        NOT NULL,
    created_at              TIMESTAMP    NOT NULL,
    CONSTRAINT fk_registration_documents_request
        FOREIGN KEY (registration_request_id) REFERENCES distributor_registration_requests (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_registration_documents_request
    ON registration_documents (registration_request_id);

CREATE INDEX IF NOT EXISTS idx_registration_documents_type
    ON registration_documents (document_type);
