INSERT INTO manufacturers (id, name, nationality) VALUES (1, 'Moritz', 'Spanish');
INSERT INTO manufacturers (id, name, nationality) VALUES (2, 'Amstel', 'Spanish');
INSERT INTO beers (id, name, graduation, type, description, manufacturers_id, deleted) VALUES (1, 'Moritz', 'strong', 'ALE', 'strong beer', 2, 'false');
INSERT INTO beers (id, name, graduation, type, description, manufacturers_id, deleted) VALUES (2, 'Heineken', 'medium', 'IPA', 'medium beer', 1, 'true');
INSERT INTO beers (id, name, graduation, type, description, manufacturers_id, deleted) VALUES (3, 'Alice Witbier', 'strong', 'Dark Mild ', 'strong beer', 1, 'true');
UPDATE manufacturers set deleted = 'true' where id = '1';

