-- TAG
INSERT INTO tag (id, name) VALUES (1, 'Priroda');
INSERT INTO tag (id, name) VALUES (2, 'Avantura');
INSERT INTO tag (id, name) VALUES (3, 'Kultura');
INSERT INTO tag (id, name) VALUES (4, 'Istorija');
INSERT INTO tag (id, name) VALUES (5, 'Planinarenje');

-- TOUR
INSERT INTO tour (id, name, description, difficulty, price, status, author_id)
VALUES
    (1, 'Planinarenje na Tari', 'Tura kroz Nacionalni park Tara sa obilaskom vidikovaca i prirodnih lepota.', 'MEDIUM', 0, 'DRAFT', 3),
    (2, 'Obilazak Beograda', 'Kulturno-istorijska tura kroz najpoznatije delove Beograda.', 'EASY', 0, 'DRAFT', 3),
    (3, 'Avantura na Kopaoniku', 'Planinska avanturistička tura namenjena ljubiteljima prirode i aktivnosti.', 'HARD', 0, 'DRAFT', 3),
    (4, 'Fruškogorska vinska šetnja', 'Lagana tura kroz Frušku goru, vidikovce i manastire.', 'EASY', 0, 'DRAFT', 3);

-- TOUR_TAG
INSERT INTO tour_tag (tour_id, tag_id) VALUES (1, 1);
INSERT INTO tour_tag (tour_id, tag_id) VALUES (1, 2);
INSERT INTO tour_tag (tour_id, tag_id) VALUES (1, 5);

INSERT INTO tour_tag (tour_id, tag_id) VALUES (2, 3);
INSERT INTO tour_tag (tour_id, tag_id) VALUES (2, 4);

INSERT INTO tour_tag (tour_id, tag_id) VALUES (3, 1);
INSERT INTO tour_tag (tour_id, tag_id) VALUES (3, 2);
INSERT INTO tour_tag (tour_id, tag_id) VALUES (3, 5);

-- KEY POINT
INSERT INTO key_point (id, name, description, latitude, longitude, image_url, tour_id)
VALUES
    (1, 'Vidikovac Banjska stena', 'Jedan od najpoznatijih vidikovaca na Tari.', 43.9536, 19.3972, '/uploads/keypoints/tara.jpg', 1),
    (2, 'Jezero Perućac', 'Prelepo jezero pogodno za odmor i fotografisanje.', 43.9491, 19.4078, '/uploads/keypoints/perucac.jpg', 1),
    (3, 'Kalemegdan', 'Istorijska tvrđava i park u centru Beograda.', 44.8230, 20.4500, '/uploads/keypoints/kalemegdan.jpg', 2),
    (4, 'Knez Mihailova', 'Najpoznatija pešačka zona u Beogradu.', 44.8176, 20.4569, '/uploads/keypoints/bg2.jpg', 2),
    (5, 'Pančićev vrh', 'Najviši vrh Kopaonika.', 43.2850, 20.8225, '/uploads/keypoints/kop.jpg', 3);

-- REVIEW
INSERT INTO review (
    id, rating, comment, tourist_id, tourist_username, visited_at, created_at, tour_id
)
VALUES
    (1, 5, 'Tura je bila odlično organizovana, priroda je prelepa.', 4, 'tijana', '2026-04-10', '2026-04-11 14:30:00', 1),
    (2, 4, 'Lepa tura, vodič je bio veoma ljubazan.', 5, 'srdjan', '2026-04-12', '2026-04-13 10:15:00', 1),
    (3, 5, 'Odličan obilazak Beograda, mnogo zanimljivih informacija.', 6, 'marko', '2026-03-20', '2026-03-21 09:00:00', 2),
    (4, 4, 'Tura je bila zanimljiva, ali malo naporna.', 8, 'nikola', '2026-04-02', '2026-04-03 18:45:00', 3),
    (5, 5, 'Prelepa šetnja i odličan vodič.', 10, 'petar', '2026-05-01', '2026-05-02 12:20:00', 4);

-- REVIEW_IMAGES
INSERT INTO review_images (review_id, image_url)
VALUES
    (1, '/uploads/keypoints/tara.jpg'),
    (1, '/uploads/keypoints/perucac.jpg'),
    (2, '/uploads/keypoints/tara.jpg'),
    (3, '/uploads/keypoints/bg2.jpg');


SELECT setval('tag_id_seq', (SELECT MAX(id) FROM tag));
SELECT setval('tour_id_seq', (SELECT MAX(id) FROM tour));
SELECT setval('key_point_id_seq', (SELECT MAX(id) FROM key_point));
SELECT setval('review_id_seq', (SELECT MAX(id) FROM review));