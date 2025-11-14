-- Drop Migration - Rollback DDL
-- Execute in reverse order of creation to avoid foreign key constraint issues

-- Drop triggers first
DROP TRIGGER IF EXISTS update_profile_updated_at ON profile;

-- Drop the trigger function
DROP FUNCTION IF EXISTS update_updated_at_column();

-- Drop indexes
DROP INDEX IF EXISTS idx_profile_measurement_user_date;
DROP INDEX IF EXISTS idx_profile_measurement_measured_at;
DROP INDEX IF EXISTS idx_profile_measurement_user_id;
DROP INDEX IF EXISTS idx_profile_active;
DROP INDEX IF EXISTS idx_profile_email;

-- Drop tables (child table first due to foreign key constraints)
DROP TABLE IF EXISTS profile_measurement;
DROP TABLE IF EXISTS profile;