INSERT INTO blogs (title, description, created_at, author_id,author_username)
VALUES
    (

        'Najlepše destinacije za leto',
        'Otkrijte najlepše destinacije za savršeno letovanje širom Evrope i sveta.',
        NOW(),
        3,'ivana'
    ),
    (

        'Kako se spakovati za put',
        'Saveti za efikasno pakovanje i organizaciju pre putovanja bez stresa.',
        NOW(),
        4,'tijana'
    ),
    (

        'Top 3 grada za vikend',
        'Ako planirate kratak odmor, ovo su idealne evropske destinacije za vikend.',
        NOW(),
        3,'ivana'
    ),
    (

        'Putovanje sa malim budžetom',
        'Kako da obiđete svet i uštedite novac uz pametno planiranje.',
        NOW(),
        3,'ivana'
    );

INSERT INTO blog_images (blog_id, image_url)
VALUES
    (1, 'zakintos.jpg'),
    (2, 'pakovanje.jpg'),
    (3, 'barsa.jpg'),
    (3, 'rim.jpg'),
    (3, 'pariz.jpg'),
    (4, 'jeftino.jpg');

INSERT INTO comment (author_id, author_username, text, blog_id, created_at)
VALUES
    (3, 'ivana', 'Ovo izgleda prelepo, moram posetiti!', 1, NOW() - INTERVAL '2 hours'),
    (4, 'tijana', 'Super preporuke, hvala!', 1, NOW() - INTERVAL '1 hour'),

    (4, 'tijana', 'Ovo mi je baš trebalo, uvek nosim previše stvari', 2, NOW() - INTERVAL '3 hours'),

    (3, 'ivana', 'Barselona je stvarno top izbor!', 3, NOW() - INTERVAL '1 day'),
    (4, 'tijana', 'Dodala bih i Lisabon na ovu listu.', 3, NOW() - INTERVAL '20 hours'),

    (3, 'ivana', 'Odlični saveti za štednju!', 4, NOW() - INTERVAL '30 minutes');

INSERT INTO blog_likes (blog_id, user_id)
VALUES
    (1, 3),
    (1, 4),

    (2, 4),

    (3, 3),
    (3, 4),

    (4, 3);