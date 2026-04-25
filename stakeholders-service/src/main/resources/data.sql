INSERT INTO users (username, password, email, role, blocked)
VALUES ('admin1', '$2a$12$hBwGbA96F8LgSpDuDIb1ieZ/sxARP399AexVao29gwohVQFpDpd5u', 'admin1@gmail.com', 'ADMIN', false)
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, email, role, blocked)
VALUES ('admin2', '$2a$12$hBwGbA96F8LgSpDuDIb1ieZ/sxARP399AexVao29gwohVQFpDpd5u', 'admin2@gmail.com', 'ADMIN', false)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, email, role, blocked)
VALUES ('ivana', '$2a$12$g5o1C.i0GWatn4eopDyFO.Ft5hC1/V2qJjJ47vxca55MCNUPEgPqK', 'ivana1@gmail.com', 'GUIDE', false)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, email, role, blocked)
VALUES ('tijana', '$2a$12$I7F8gnxKeinpltVva1of9u57enoodLRvM8870OKgQMdJnh8z4gfo2', 'tijana@gmail.com', 'TOURIST', false)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, email, role, blocked)
VALUES ('srdjan', '$2a$12$Zva6o83Mzt7BjusANjsnmeqxSrcR/u/2B2i.u548zDBGRHRP4ewti', 'srdjan@gmail.com', 'TOURIST', false)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, email, role, blocked)
VALUES ('marko', '$2a$12$b6jSIijUfEOx7sFDHMBHYuS4mCXlv0mJAIgDXgdIin2qsWIYHROCS', 'marko@gmail.com', 'TOURIST', false)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, email, role, blocked)
VALUES ('ana', '$2a$12$QDayKihX40yR0lAcVLzwNuVmIU..AsGZW/XyrxZ5J5XE9JrYs5CUa', 'ana@gmail.com', 'GUIDE', false)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, email, role, blocked)
VALUES ('nikola', '$2a$12$kp46YD14BfUaei/dDHfMfu4H13gAiQ59WeMKsFfdqVsRFEUXmH/XK', 'nikola@gmail.com', 'TOURIST', false)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, email, role, blocked)
VALUES ('jelena', '$2a$12$cAJ6EpGSomwLyHPe1lOPb.xYLpOLZtaCMfjaS37k9d94Nc.42hWUq', 'jelena@gmail.com', 'GUIDE', false)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, email, role, blocked)
VALUES ('petar', '$2a$12$I.ymF3irUzX.9pwS5AzPBuP0DyKSyLCQo9X5FafFmwcz8UJEwUWmK', 'petar@gmail.com', 'TOURIST', false)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, email, role, blocked)
VALUES ('milica', '$2a$12$I.ymF3irUzX.9pwS5AzPBuP0DyKSyLCQo9X5FafFmwcz8UJEwUWmK', 'milica@gmail.com', 'GUIDE', false)
    ON CONFLICT (username) DO NOTHING;