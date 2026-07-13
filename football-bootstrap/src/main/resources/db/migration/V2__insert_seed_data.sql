-- Seed de base execute au demarrage via Flyway
-- 5 equipes x 15 joueurs (75 joueurs au total)

INSERT INTO teams (name, acronym, budget)
VALUES
    ('Paris Saint-Germain', 'PSG', 800000000.00),
    ('Bayern Munich', 'BAYERN', 900000000.00),
    ('Real Madrid', 'REAL', 1000000000.00),
    ('FC Barcelone', 'BARCA', 850000000.00),
    ('Liverpool FC', 'LIV', 780000000.00)
ON CONFLICT (acronym) DO NOTHING;

WITH player_seed (
    first_name,
    last_name,
    acronym,
    position,
    performance_note,
    market_price,
    is_titulaire,
    team_acronym
) AS (
    VALUES
        ('Gianluigi', 'Donnarumma', 'PSG01', 'GK', 8.60, 65000000.00, true, 'PSG'),
        ('Achraf', 'Hakimi', 'PSG02', 'RB', 8.10, 70000000.00, true, 'PSG'),
        ('Marquinhos', 'Silva', 'PSG03', 'CB', 8.00, 60000000.00, true, 'PSG'),
        ('Milan', 'Skriniar', 'PSG04', 'CB', 7.70, 45000000.00, true, 'PSG'),
        ('Nuno', 'Mendes', 'PSG05', 'LB', 7.90, 55000000.00, true, 'PSG'),
        ('Manuel', 'Ugarte', 'PSG06', 'DM', 7.60, 50000000.00, true, 'PSG'),
        ('Vitinha', 'Ferreira', 'PSG07', 'CM', 7.80, 60000000.00, true, 'PSG'),
        ('Warren', 'Zaire-Emery', 'PSG08', 'CM', 8.20, 70000000.00, true, 'PSG'),
        ('Ousmane', 'Dembele', 'PSG09', 'RW', 7.50, 55000000.00, true, 'PSG'),
        ('Bradley', 'Barcola', 'PSG10', 'LW', 7.40, 45000000.00, true, 'PSG'),
        ('Marco', 'Asensio', 'PSG11', 'AM', 7.20, 32000000.00, false, 'PSG'),
        ('Randal', 'Kolo-Muani', 'PSG12', 'ST', 7.50, 65000000.00, true, 'PSG'),
        ('Goncalo', 'Ramos', 'PSG13', 'CF', 7.10, 42000000.00, false, 'PSG'),
        ('Lee', 'Kang-In', 'PSG14', 'RM', 7.30, 30000000.00, false, 'PSG'),
        ('Fabian', 'Ruiz', 'PSG15', 'CM', 7.00, 28000000.00, false, 'PSG'),

        ('Manuel', 'Neuer', 'BAY01', 'GK', 7.90, 12000000.00, true, 'BAYERN'),
        ('Joshua', 'Kimmich', 'BAY02', 'DM', 8.40, 75000000.00, true, 'BAYERN'),
        ('Dayot', 'Upamecano', 'BAY03', 'CB', 7.70, 50000000.00, true, 'BAYERN'),
        ('Min-Jae', 'Kim', 'BAY04', 'CB', 7.60, 55000000.00, true, 'BAYERN'),
        ('Alphonso', 'Davies', 'BAY05', 'LB', 8.00, 70000000.00, true, 'BAYERN'),
        ('Noussair', 'Mazraoui', 'BAY06', 'RB', 7.10, 30000000.00, false, 'BAYERN'),
        ('Leon', 'Goretzka', 'BAY07', 'CM', 7.50, 40000000.00, true, 'BAYERN'),
        ('Jamal', 'Musiala', 'BAY08', 'AM', 8.80, 110000000.00, true, 'BAYERN'),
        ('Leroy', 'Sane', 'BAY09', 'RW', 8.00, 65000000.00, true, 'BAYERN'),
        ('Kingsley', 'Coman', 'BAY10', 'LW', 7.60, 50000000.00, true, 'BAYERN'),
        ('Serge', 'Gnabry', 'BAY11', 'RF', 7.30, 45000000.00, false, 'BAYERN'),
        ('Thomas', 'Muller', 'BAY12', 'CF', 7.40, 15000000.00, true, 'BAYERN'),
        ('Harry', 'Kane', 'BAY13', 'ST', 9.00, 120000000.00, true, 'BAYERN'),
        ('Konrad', 'Laimer', 'BAY14', 'CM', 7.00, 22000000.00, false, 'BAYERN'),
        ('Matthijs', 'De-Ligt', 'BAY15', 'CB', 7.80, 60000000.00, false, 'BAYERN'),

        ('Thibaut', 'Courtois', 'REA01', 'GK', 8.70, 45000000.00, true, 'REAL'),
        ('Dani', 'Carvajal', 'REA02', 'RB', 7.60, 18000000.00, true, 'REAL'),
        ('Eder', 'Militao', 'REA03', 'CB', 8.10, 70000000.00, true, 'REAL'),
        ('Antonio', 'Rudiger', 'REA04', 'CB', 8.00, 35000000.00, true, 'REAL'),
        ('Ferland', 'Mendy', 'REA05', 'LB', 7.30, 25000000.00, true, 'REAL'),
        ('Eduardo', 'Camavinga', 'REA06', 'CM', 8.20, 90000000.00, true, 'REAL'),
        ('Aurelien', 'Tchouameni', 'REA07', 'DM', 8.10, 85000000.00, true, 'REAL'),
        ('Federico', 'Valverde', 'REA08', 'RM', 8.50, 100000000.00, true, 'REAL'),
        ('Jude', 'Bellingham', 'REA09', 'AM', 9.20, 180000000.00, true, 'REAL'),
        ('Vinicius', 'Junior', 'REA10', 'LW', 9.10, 170000000.00, true, 'REAL'),
        ('Rodrygo', 'Goes', 'REA11', 'RW', 8.40, 100000000.00, true, 'REAL'),
        ('Joselu', 'Mato', 'REA12', 'ST', 7.00, 8000000.00, false, 'REAL'),
        ('Brahim', 'Diaz', 'REA13', 'RF', 7.40, 30000000.00, false, 'REAL'),
        ('Luka', 'Modric', 'REA14', 'CM', 7.80, 9000000.00, false, 'REAL'),
        ('Toni', 'Kroos', 'REA15', 'CM', 8.00, 12000000.00, false, 'REAL'),

        ('Marc-Andre', 'Ter-Stegen', 'BAR01', 'GK', 8.50, 45000000.00, true, 'BARCA'),
        ('Jules', 'Kounde', 'BAR02', 'RB', 8.00, 65000000.00, true, 'BARCA'),
        ('Ronald', 'Araujo', 'BAR03', 'CB', 8.30, 90000000.00, true, 'BARCA'),
        ('Andreas', 'Christensen', 'BAR04', 'CB', 7.60, 40000000.00, true, 'BARCA'),
        ('Alejandro', 'Balde', 'BAR05', 'LB', 7.80, 50000000.00, true, 'BARCA'),
        ('Frenkie', 'De-Jong', 'BAR06', 'CM', 8.40, 85000000.00, true, 'BARCA'),
        ('Pedro', 'Pedri', 'BAR07', 'AM', 8.60, 100000000.00, true, 'BARCA'),
        ('Ilkay', 'Gundogan', 'BAR08', 'CM', 7.90, 25000000.00, true, 'BARCA'),
        ('Pablo', 'Gavi', 'BAR09', 'CM', 8.10, 90000000.00, true, 'BARCA'),
        ('Raphinha', 'Bellucci', 'BAR10', 'RW', 7.70, 55000000.00, true, 'BARCA'),
        ('Lamine', 'Yamal', 'BAR11', 'LW', 8.10, 85000000.00, true, 'BARCA'),
        ('Robert', 'Lewandowski', 'BAR12', 'ST', 8.20, 30000000.00, true, 'BARCA'),
        ('Ferran', 'Torres', 'BAR13', 'RF', 7.20, 35000000.00, false, 'BARCA'),
        ('Joao', 'Felix', 'BAR14', 'CF', 7.50, 40000000.00, false, 'BARCA'),
        ('Oriol', 'Romeu', 'BAR15', 'DM', 6.80, 12000000.00, false, 'BARCA'),

        ('Alisson', 'Becker', 'LIV01', 'GK', 8.60, 50000000.00, true, 'LIV'),
        ('Trent', 'Alexander-Arnold', 'LIV02', 'RB', 8.40, 85000000.00, true, 'LIV'),
        ('Virgil', 'Van-Dijk', 'LIV03', 'CB', 8.30, 35000000.00, true, 'LIV'),
        ('Ibrahima', 'Konate', 'LIV04', 'CB', 7.80, 50000000.00, true, 'LIV'),
        ('Andrew', 'Robertson', 'LIV05', 'LB', 7.90, 30000000.00, true, 'LIV'),
        ('Alexis', 'Mac-Allister', 'LIV06', 'CM', 8.00, 70000000.00, true, 'LIV'),
        ('Dominik', 'Szoboszlai', 'LIV07', 'AM', 7.90, 65000000.00, true, 'LIV'),
        ('Wataru', 'Endo', 'LIV08', 'DM', 7.10, 18000000.00, true, 'LIV'),
        ('Mohamed', 'Salah', 'LIV09', 'RW', 8.90, 70000000.00, true, 'LIV'),
        ('Luis', 'Diaz', 'LIV10', 'LW', 8.00, 75000000.00, true, 'LIV'),
        ('Diogo', 'Jota', 'LIV11', 'CF', 7.80, 55000000.00, true, 'LIV'),
        ('Darwin', 'Nunez', 'LIV12', 'ST', 7.60, 70000000.00, true, 'LIV'),
        ('Cody', 'Gakpo', 'LIV13', 'LF', 7.70, 60000000.00, false, 'LIV'),
        ('Harvey', 'Elliott', 'LIV14', 'RM', 7.20, 35000000.00, false, 'LIV'),
        ('Curtis', 'Jones', 'LIV15', 'CM', 7.30, 30000000.00, false, 'LIV')
)
INSERT INTO players (
    first_name,
    last_name,
    acronym,
    position,
    performance_note,
    market_price,
    is_titulaire,
    team_id
)
SELECT
    ps.first_name,
    ps.last_name,
    ps.acronym,
    ps.position::player_position,
    ps.performance_note,
    ps.market_price,
    ps.is_titulaire,
    t.id
FROM player_seed ps
JOIN teams t ON t.acronym = ps.team_acronym
WHERE NOT EXISTS (
    SELECT 1
    FROM players p
    WHERE p.acronym = ps.acronym
);

