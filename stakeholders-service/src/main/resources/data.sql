INSERT INTO users (username, password, email, role, blocked)
VALUES ('admin1', '$2a$12$hBwGbA96F8LgSpDuDIb1ieZ/sxARP399AexVao29gwohVQFpDpd5u', 'admin1@gmail.com', 'ADMIN', false)
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, email, role, blocked)
VALUES ('admin2', '$2a$12$hBwGbA96F8LgSpDuDIb1ieZ/sxARP399AexVao29gwohVQFpDpd5u', 'admin2@gmail.com', 'ADMIN', false)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, email, role, blocked)
VALUES ('ivana', '$2a$12$g5o1C.i0GWatn4eopDyFO.Ft5hC1/V2qJjJ47vxca55MCNUPEgPqK', 'ivana@gmail.com', 'GUIDE', false)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, email, role, blocked)
VALUES ('tijana', '$2a$12$I7F8gnxKeinpltVva1of9u57enoodLRvM8870OKgQMdJnh8z4gfo2', 'tijana@gmail.com', 'TOURIST', false)
    ON CONFLICT (username) DO NOTHING;