-- Add latitude and longitude columns to agency table
-- These columns are used by MetroWithCoordinatesDTO for mapping metropolitan areas

ALTER TABLE agency 
ADD COLUMN latitude DECIMAL(10, 8) NULL,
ADD COLUMN longitude DECIMAL(11, 8) NULL;

-- Add index for better query performance when filtering by coordinates
CREATE INDEX idx_agency_coordinates ON agency(latitude, longitude);

