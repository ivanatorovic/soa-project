INSERT INTO blogs (id, title, description, created_at, author_id,author_username)
VALUES
    (
        1,
        'Najlepše destinacije za leto',
        'Otkrijte najlepše destinacije za savršeno letovanje širom Evrope i sveta.',
        NOW(),
        3,'ivana'
    ),
    (
        2,
        'Kako se spakovati za put',
        'Saveti za efikasno pakovanje i organizaciju pre putovanja bez stresa.',
        NOW(),
        4,'tijana'
    ),
    (
        3,
        'Top 5 gradova za vikend',
        'Ako planirate kratak odmor, ovo su idealne evropske destinacije za vikend.',
        NOW(),
        3,'ivana'
    ),
    (
        4,
        'Putovanje sa malim budžetom',
        'Kako da obiđete svet i uštedite novac uz pametno planiranje.',
        NOW(),
        3,'ivana'
    );

INSERT INTO blog_images (blog_id, image_url)
VALUES
    (1, '/assets/zakintos.jpg'),
    (2, '/assets/pakovanje.jpg'),
    (3, '/assets/barsa.jpg'),
    (4, '/assets/jeftino.jpg');
