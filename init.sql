-- -- Création de la base de données bibliothèqu
-- -- Table des livres
-- CREATE TABLE IF NOT EXISTS livres (
--     id SERIAL PRIMARY KEY,
--     titre VARCHAR(255) NOT NULL,
--     auteur VARCHAR(255) NOT NULL,
--     disponible BOOLEAN DEFAULT TRUE,
--     date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- -- Table des utilisateurs
-- CREATE TABLE IF NOT EXISTS utilisateurs (
--     id SERIAL PRIMARY KEY,
--     nom VARCHAR(255) NOT NULL,
--     email VARCHAR(255) UNIQUE NOT NULL,
--     date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- -- Table des emprunts
-- CREATE TABLE IF NOT EXISTS emprunts (
--     id SERIAL PRIMARY KEY,
--     utilisateur_id INTEGER REFERENCES utilisateurs(id) ON DELETE CASCADE,
--     livre_id INTEGER REFERENCES livres(id) ON DELETE CASCADE,
--     date_emprunt DATE NOT NULL DEFAULT CURRENT_DATE,
--     date_retour DATE,
--     UNIQUE(livre_id, date_retour) -- Un livre ne peut être emprunté qu'une fois à la fois (si date_retour est NULL)
-- );

-- -- Insertion des données de test pour les livres
-- INSERT INTO livres (titre, auteur, disponible) VALUES
-- ('Le Petit Prince', 'Antoine de Saint-Exupéry', TRUE),
-- ('1984', 'George Orwell', TRUE),
-- ('L''Alchimiste', 'Paulo Coelho', TRUE),
-- ('Les Misérables', 'Victor Hugo', TRUE),
-- ('Harry Potter à l''école des sorciers', 'J.K. Rowling', TRUE),
-- ('Le Seigneur des Anneaux', 'J.R.R. Tolkien', TRUE),
-- ('Pride and Prejudice', 'Jane Austen', TRUE),
-- ('To Kill a Mockingbird', 'Harper Lee', TRUE);

-- -- Insertion des données de test pour les utilisateurs
-- INSERT INTO utilisateurs (nom, email) VALUES
-- ('Alice Dupont', 'alice@example.com'),
-- ('Bob Martin', 'bob@example.com'),
-- ('Charlie Lambert', 'charlie@example.com'),
-- ('David Morel', 'david@example.com'),
-- ('Emma Leroy', 'emma@example.com');

-- -- Insertion des données de test pour les emprunts
-- INSERT INTO emprunts (utilisateur_id, livre_id, date_emprunt, date_retour) VALUES
-- (1, 2, '2024-02-01', NULL), -- Alice emprunte "1984" et ne l'a pas encore rendu
-- (2, 3, '2024-01-20', '2024-02-02'), -- Bob a emprunté "L'Alchimiste" et l'a rendu
-- (3, 1, '2024-01-25', NULL), -- Charlie emprunte "Le Petit Prince"
-- (4, 5, '2024-01-30', NULL), -- David emprunte "Harry Potter à l'école des sorciers"
-- (5, 4, '2024-02-03', '2024-02-15'); -- Emma a emprunté "Les Misérables" et l'a rendu

-- -- Mise à jour de la disponibilité des livres empruntés
-- UPDATE livres SET disponible = FALSE WHERE id IN (
--     SELECT livre_id FROM emprunts WHERE date_retour IS NULL
-- );

-- -- Vues utiles pour les requêtes
-- CREATE VIEW emprunts_actifs AS
-- SELECT 
--     e.id,
--     u.nom as nom_utilisateur,
--     u.email,
--     l.titre,
--     l.auteur,
--     e.date_emprunt
-- FROM emprunts e
-- JOIN utilisateurs u ON e.utilisateur_id = u.id
-- JOIN livres l ON e.livre_id = l.id
-- WHERE e.date_retour IS NULL;

-- CREATE VIEW historique_emprunts AS
-- SELECT 
--     e.id,
--     u.nom as nom_utilisateur,
--     u.email,
--     l.titre,
--     l.auteur,
--     e.date_emprunt,
--     e.date_retour
-- FROM emprunts e
-- JOIN utilisateurs u ON e.utilisateur_id = u.id
-- JOIN livres l ON e.livre_id = l.id
-- ORDER BY e.date_emprunt DESC;

-- La base de données 'bibliotheque' est déjà créée via POSTGRES_DB
-- Pas besoin de CREATE DATABASE dans PostgreSQL avec docker-entrypoint-initdb.d

-- Table des livres
CREATE TABLE IF NOT EXISTS livres (
    id SERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    auteur VARCHAR(255) NOT NULL,
    disponible BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS utilisateurs (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des emprunts
CREATE TABLE IF NOT EXISTS emprunts (
    id SERIAL PRIMARY KEY,
    utilisateur_id INTEGER REFERENCES utilisateurs(id) ON DELETE CASCADE,
    livre_id INTEGER REFERENCES livres(id) ON DELETE CASCADE,
    date_emprunt DATE NOT NULL DEFAULT CURRENT_DATE,
    date_retour DATE,
    UNIQUE(livre_id, date_retour) -- Un livre ne peut être emprunté qu'une fois à la fois (si date_retour est NULL)
);

-- Insertion des données de test pour les livres
INSERT INTO livres (titre, auteur, disponible) VALUES
('Le Petit Prince', 'Antoine de Saint-Exupéry', TRUE),
('1984', 'George Orwell', TRUE),
('L''Alchimiste', 'Paulo Coelho', TRUE),
('Les Misérables', 'Victor Hugo', TRUE),
('Harry Potter à l''école des sorciers', 'J.K. Rowling', TRUE),
('Le Seigneur des Anneaux', 'J.R.R. Tolkien', TRUE),
('Pride and Prejudice', 'Jane Austen', TRUE),
('To Kill a Mockingbird', 'Harper Lee', TRUE);

-- Insertion des données de test pour les utilisateurs
INSERT INTO utilisateurs (nom, email) VALUES
('Alice Dupont', 'alice@example.com'),
('Bob Martin', 'bob@example.com'),
('Charlie Lambert', 'charlie@example.com'),
('David Morel', 'david@example.com'),
('Emma Leroy', 'emma@example.com');

-- Insertion des données de test pour les emprunts
INSERT INTO emprunts (utilisateur_id, livre_id, date_emprunt, date_retour) VALUES
(1, 2, '2024-02-01', NULL), -- Alice emprunte "1984" et ne l'a pas encore rendu
(2, 3, '2024-01-20', '2024-02-02'), -- Bob a emprunté "L'Alchimiste" et l'a rendu
(3, 1, '2024-01-25', NULL), -- Charlie emprunte "Le Petit Prince"
(4, 5, '2024-01-30', NULL), -- David emprunte "Harry Potter à l'école des sorciers"
(5, 4, '2024-02-03', '2024-02-15'); -- Emma a emprunté "Les Misérables" et l'a rendu

-- Mise à jour de la disponibilité des livres empruntés
UPDATE livres SET disponible = FALSE WHERE id IN (
    SELECT livre_id FROM emprunts WHERE date_retour IS NULL
);

-- Vues utiles pour les requêtes
CREATE VIEW emprunts_actifs AS
SELECT 
    e.id,
    u.nom as nom_utilisateur,
    u.email,
    l.titre,
    l.auteur,
    e.date_emprunt
FROM emprunts e
JOIN utilisateurs u ON e.utilisateur_id = u.id
JOIN livres l ON e.livre_id = l.id
WHERE e.date_retour IS NULL;

CREATE VIEW historique_emprunts AS
SELECT 
    e.id,
    u.nom as nom_utilisateur,
    u.email,
    l.titre,
    l.auteur,
    e.date_emprunt,
    e.date_retour
FROM emprunts e
JOIN utilisateurs u ON e.utilisateur_id = u.id
JOIN livres l ON e.livre_id = l.id
ORDER BY e.date_emprunt DESC;