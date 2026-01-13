-- Insert sample data into Digital Marketplace database

-- Insert roles
INSERT INTO roles (name) VALUES
('ROLE_BUYER'),
('ROLE_SELLER'),
('ROLE_ADMIN');

-- Insert categories
INSERT INTO categories (name, description, allowed_file_types) VALUES
('3D Models', 'Three-dimensional digital models', '. blend,.fbx,.obj,.glb'),
('Textures', 'High-quality texture maps and materials', '.png,.jpg,.exr,.hdr'),
('Graphics', 'Logos, icons, UI kits, vectors', '.svg,.ai,.psd,.png,. pdf'),
('Documents', 'E-books, guides, templates', '.pdf,.epub,. docx');

-- Insert admin user (password: admin123)
INSERT INTO users (username, email, password, full_name, enabled) VALUES 
('admin', 'admin@marketplace.com', '$2a$10$N. zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8MDKCE8mLSZKSdY2dO', 'System Administrator', TRUE);

-- Assign ADMIN role
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';


