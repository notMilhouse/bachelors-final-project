-- Create database (uncomment and modify as needed)
-- CREATE DATABASE profile_measurements;
-- USE profile_measurements;

-- Table: profile
-- Stores user account information
CREATE TABLE profile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL, -- Store hashed passwords, never plain text
    profile_picture_path VARCHAR(500), -- File path to profile image
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT checker_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT checker_name_length CHECK (LENGTH(TRIM(name)) >= 2)
);

-- Table: profile_measurement
-- Stores weight measurements for users
CREATE TABLE profile_measurement (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    profile_id UUID NOT NULL,
    weight_value DECIMAL(5,2) NOT NULL, 
    measured_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, -- comes from esp32
    recorded_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, -- delta between recorded and measured represent delay between two timepoints
    notes TEXT, -- Optional notes about the measurement
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key relationship
    CONSTRAINT fk_profile_measurement_user_id
        FOREIGN KEY (user_id) REFERENCES profile(id)
        ON DELETE CASCADE,
    
    -- Constraints
    CONSTRAINT checker_weight_reasonable CHECK (weight_value BETWEEN 1.0 AND 1000.0)
);

-- Indexes for better query performance
CREATE INDEX idx_profile_email ON profile(email);
CREATE INDEX idx_profile_measurement_user_id ON profile_measurement(user_id);
CREATE INDEX idx_profile_measurement_measured_at ON profile_measurement(measured_at);
CREATE INDEX idx_profile_measurement_user_date ON profile_measurement(user_id, measured_at DESC);

-- Trigger to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_profile_updated_at
    BEFORE UPDATE ON profile
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
