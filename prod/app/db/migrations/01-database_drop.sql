-- Drop Migration - Rollback DDL
-- Execute in reverse order of creation to avoid foreign key constraint issues

-- Drop indexes
DROP INDEX IF EXISTS idx_profile_embedding_cosine_ops;
DROP INDEX IF EXISTS idx_profile_measurement_profile_id;
DROP INDEX IF EXISTS idx_profile_email;

-- Drop tables (child table first due to foreign key constraints)
DROP TABLE IF EXISTS profile_measurement;
DROP TABLE IF EXISTS profile_embedding;
DROP TABLE IF EXISTS profile;