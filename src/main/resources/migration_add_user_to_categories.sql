-- Step 1: Add the user_id column to the categories table
ALTER TABLE categories ADD COLUMN user_id BIGINT;

-- Step 2: Add a foreign key constraint to ensure user_id references users table
ALTER TABLE categories
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Step 3: Update existing categories with appropriate user_id values
-- Assign user_id based on your current logic. Example below assumes id=6 exists.

UPDATE categories SET user_id = 1 WHERE id IN (1, 2); -- Assigns Arts and Sports to user 1
UPDATE categories SET user_id = 2 WHERE id IN (3, 4); -- Assigns Science and History to user 2
UPDATE categories SET user_id = 1 WHERE id = 5;       -- Assigns Entertainment to user 1
UPDATE categories SET user_id = 2 WHERE id = 6;       -- Assigns the sixth category to user 2

-- Step 4: Ensure all existing rows in the categories table have a valid user_id
-- If any rows are left unassigned, decide on a default user_id or assign manually
-- Example: Assign to a default user (e.g., admin user with id = 1)
UPDATE categories SET user_id = 1 WHERE user_id IS NULL;

-- Step 5: Make user_id column NOT NULL to enforce data integrity
ALTER TABLE categories MODIFY user_id BIGINT NOT NULL;
