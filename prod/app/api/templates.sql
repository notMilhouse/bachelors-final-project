-- Kotlin API Query Templates (use these as string templates in your Kotlin code):

-- Template: Create new user
/*
INSERT INTO app_user (name, email, password_hash, profile_picture_path) 
VALUES (?, ?, ?, ?)
*/

-- Template: Get user by email (authentication)
/*
SELECT id, name, email, password_hash, profile_picture_path, is_active, created_at
FROM app_user 
WHERE email = ? AND is_active = TRUE
*/

-- Template: Get user by ID
/*
SELECT id, name, email, profile_picture_path, is_active, created_at, updated_at
FROM app_user 
WHERE id = ? AND is_active = TRUE
*/

-- Template: Update user profile
/*
UPDATE app_user 
SET name = ?, profile_picture_path = ?, updated_at = CURRENT_TIMESTAMP 
WHERE id = ?
*/

-- Template: Update user password
/*
UPDATE app_user 
SET password_hash = ?, updated_at = CURRENT_TIMESTAMP 
WHERE id = ?
*/

-- Template: Soft delete user
/*
UPDATE app_user 
SET is_active = FALSE, updated_at = CURRENT_TIMESTAMP 
WHERE id = ?
*/

-- Template: Add weight metric
/*
INSERT INTO app_user_metrics (user_id, weight_value, unit, measured_at, notes) 
VALUES (?, ?, ?, ?, ?)
*/

-- Template: Get user with latest weight
/*
SELECT 
    u.id,
    u.name,
    u.email,
    u.profile_picture_path,
    m.weight_value,
    m.unit,
    m.measured_at
FROM app_user u
LEFT JOIN LATERAL (
    SELECT weight_value, unit, measured_at
    FROM app_user_metrics 
    WHERE user_id = u.id 
    ORDER BY measured_at DESC 
    LIMIT 1
) m ON TRUE
WHERE u.id = ? AND u.is_active = TRUE
*/

-- Template: Get weight history for user (paginated)
/*
SELECT 
    id,
    weight_value,
    unit,
    measured_at,
    notes
FROM app_user_metrics 
WHERE user_id = ? 
ORDER BY measured_at DESC
LIMIT ? OFFSET ?
*/

-- Template: Get weight history for date range
/*
SELECT 
    id,
    weight_value,
    unit,
    measured_at,
    notes
FROM app_user_metrics 
WHERE user_id = ? 
  AND measured_at BETWEEN ? AND ?
ORDER BY measured_at DESC
*/

-- Template: Get weight statistics for user
/*
SELECT 
    COUNT(*) as total_measurements,
    ROUND(AVG(weight_value), 2) as avg_weight,
    MIN(weight_value) as min_weight,
    MAX(weight_value) as max_weight,
    unit
FROM app_user_metrics 
WHERE user_id = ?
GROUP BY unit
*/

-- Template: Update weight metric
/*
UPDATE app_user_metrics 
SET weight_value = ?, unit = ?, notes = ? 
WHERE id = ? AND user_id = ?
*/

-- Template: Delete weight metric
/*
DELETE FROM app_user_metrics 
WHERE id = ? AND user_id = ?
*/

-- Template: Get latest N weight entries
/*
SELECT 
    id,
    weight_value,
    unit,
    measured_at,
    notes
FROM app_user_metrics 
WHERE user_id = ? 
ORDER BY measured_at DESC 
LIMIT ?
*/

-- Template: Check if email exists (for registration validation)
/*
SELECT COUNT(*) as email_count 
FROM app_user 
WHERE email = ?
*/
