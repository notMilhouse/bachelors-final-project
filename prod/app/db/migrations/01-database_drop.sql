-- Drop Migration - Rollback DDL
-- Execute in reverse order of creation to avoid foreign key constraint issues

-- Drop triggers first
DROP TRIGGER IF EXISTS update_app_user_updated_at ON app_user;

-- Drop the trigger function
DROP FUNCTION IF EXISTS update_updated_at_column();

-- Drop indexes
DROP INDEX IF EXISTS idx_user_metrics_user_date;
DROP INDEX IF EXISTS idx_user_metrics_measured_at;
DROP INDEX IF EXISTS idx_user_metrics_user_id;
DROP INDEX IF EXISTS idx_app_user_active;
DROP INDEX IF EXISTS idx_app_user_email;

-- Drop tables (child table first due to foreign key constraints)
DROP TABLE IF EXISTS app_user_metrics;
DROP TABLE IF EXISTS app_user;

-- Optional: Drop database (uncomment if needed)
-- DROP DATABASE IF EXISTS user_metrics_app;
