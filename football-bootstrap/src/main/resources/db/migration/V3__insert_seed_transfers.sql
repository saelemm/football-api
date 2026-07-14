-- Historique de transferts seed execute au demarrage via Flyway

WITH transfer_seed (
    player_acronym,
    source_team_acronym,
    target_team_acronym,
    transfer_price,
    transfer_date
) AS (
    VALUES
        ('PSG12', 'LIV', 'PSG', 82000000.00, TIMESTAMP '2024-01-10 10:00:00'),
        ('PSG09', 'BARCA', 'PSG', 54000000.00, TIMESTAMP '2024-01-15 15:30:00'),

        ('BAY13', 'LIV', 'BAYERN', 100000000.00, TIMESTAMP '2024-02-02 12:15:00'),
        ('BAY04', 'LIV', 'BAYERN', 52000000.00, TIMESTAMP '2024-02-12 14:45:00'),

        ('REA09', 'BAYERN', 'REAL', 120000000.00, TIMESTAMP '2024-03-01 09:20:00'),
        ('REA07', 'PSG', 'REAL', 80000000.00, TIMESTAMP '2024-03-10 11:05:00'),

        ('BAR12', 'BAYERN', 'BARCA', 45000000.00, TIMESTAMP '2024-04-05 13:00:00'),
        ('BAR14', 'PSG', 'BARCA', 35000000.00, TIMESTAMP '2024-04-20 16:10:00'),

        ('LIV12', 'REAL', 'LIV', 75000000.00, TIMESTAMP '2024-05-03 10:40:00'),
        ('LIV06', 'BARCA', 'LIV', 38000000.00, TIMESTAMP '2024-05-18 17:25:00')
)
INSERT INTO player_transfers (
    player_id,
    source_team_id,
    target_team_id,
    transfer_price,
    transfer_date
)
SELECT
    p.id,
    src.id,
    tgt.id,
    ts.transfer_price,
    ts.transfer_date
FROM transfer_seed ts
JOIN players p ON p.acronym = ts.player_acronym
JOIN teams tgt ON tgt.acronym = ts.target_team_acronym
LEFT JOIN teams src ON src.acronym = ts.source_team_acronym
WHERE p.team_id = tgt.id
  AND NOT EXISTS (
      SELECT 1
      FROM player_transfers pt
      WHERE pt.player_id = p.id
        AND pt.source_team_id IS NOT DISTINCT FROM src.id
        AND pt.target_team_id = tgt.id
        AND pt.transfer_date = ts.transfer_date
  );

