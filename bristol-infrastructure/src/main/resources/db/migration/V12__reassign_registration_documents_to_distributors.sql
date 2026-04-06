ALTER TABLE registration_documents
    ADD COLUMN IF NOT EXISTS distributor_id UUID;

ALTER TABLE registration_documents
    ALTER COLUMN registration_request_id DROP NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_registration_documents_distributor'
          AND table_name = 'registration_documents'
    ) THEN
        ALTER TABLE registration_documents
            ADD CONSTRAINT fk_registration_documents_distributor
                FOREIGN KEY (distributor_id) REFERENCES distributors (id) ON DELETE CASCADE;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_registration_documents_distributor
    ON registration_documents (distributor_id);
