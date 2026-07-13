package com.foot.application.service;

import Errors.TeamNotFoundException;
import entity.Note;
import entity.Player;
import entity.PlayerId;
import entity.PlayerIdentifier;
import entity.PlayerStat;
import entity.PlayerVersion;
import entity.PositionEnum;
import entity.Price;
import entity.Team;
import entity.TeamId;
import entity.TeamIdentifier;
import entity.TeamStat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import port.IPlayerRepository;
import port.ITeamRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de GetTeamDetailsService")
class GetTeamDetailsServiceTest {

    @Mock
    private ITeamRepository teamRepository;

    @Mock
    private IPlayerRepository playerRepository;

    @InjectMocks
    private GetTeamDetailsService service;

    @Test
    @DisplayName("Doit retourner toutes les equipes")
    void shouldReturnAllTeams() {
        List<Team> teams = List.of(team(1L, "PSG", "PSG"), team(2L, "OM", "OM"));
        when(teamRepository.findAll()).thenReturn(teams);

        List<Team> result = service.findAllTeams();

        assertEquals(2, result.size());
        assertEquals("PSG", result.get(0).teamId().name());
    }

    @Test
    @DisplayName("Doit retourner une equipe par id")
    void shouldReturnTeamById() {
        TeamId teamId = new TeamId(1L);
        Team team = team(1L, "PSG", "PSG");
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        Team result = service.execute(teamId);

        assertEquals(team, result);
    }

    @Test
    @DisplayName("Doit lever une erreur si l'equipe n'existe pas")
    void shouldThrowWhenTeamNotFound() {
        TeamId teamId = new TeamId(404L);
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        TeamNotFoundException exception = assertThrows(TeamNotFoundException.class, () -> service.execute(teamId));

        assertEquals("Équipe introuvable : 404", exception.getMessage());
    }

    @Test
    @DisplayName("Doit retourner les joueurs actuels de l'equipe")
    void shouldReturnCurrentPlayers() {
        TeamId teamId = new TeamId(1L);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team(1L, "PSG", "PSG")));
        when(playerRepository.findByTeamId(teamId)).thenReturn(List.of(
            player(10L, teamId, true),
            player(11L, teamId, false)
        ));

        List<Player> result = service.findCurrentPlayers(teamId);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Doit retourner uniquement les titulaires")
    void shouldReturnOnlyStarters() {
        TeamId teamId = new TeamId(1L);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team(1L, "PSG", "PSG")));
        when(playerRepository.findByTeamId(teamId)).thenReturn(List.of(
            player(10L, teamId, true),
            player(11L, teamId, false),
            player(12L, teamId, true)
        ));

        List<Player> result = service.findTitulaires(teamId);

        assertEquals(2, result.size());
        assertTrue(result.get(0).stats().isTitulaire());
        assertTrue(result.get(1).stats().isTitulaire());
    }

    @Test
    @DisplayName("Doit retourner uniquement les remplacants")
    void shouldReturnOnlyBenchPlayers() {
        TeamId teamId = new TeamId(1L);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team(1L, "PSG", "PSG")));
        when(playerRepository.findByTeamId(teamId)).thenReturn(List.of(
            player(10L, teamId, true),
            player(11L, teamId, false),
            player(12L, teamId, false)
        ));

        List<Player> result = service.findRemplacants(teamId);

        assertEquals(2, result.size());
        assertFalse(result.get(0).stats().isTitulaire());
        assertFalse(result.get(1).stats().isTitulaire());
    }

    private Team team(Long id, String name, String acronym) {
        Date now = new Date();
        return new Team(
            new TeamIdentifier(new TeamId(id), name, acronym),
            new TeamStat(BigDecimal.valueOf(50000.0), now, now),
            List.of(),
            List.of()
        );
    }

    private Player player(Long playerId, TeamId teamId, boolean titulaire) {
        Date now = new Date();
        return new Player(
            new PlayerIdentifier(new PlayerId(playerId), "Player" + playerId, "Test", "P" + playerId, teamId),
            new PlayerStat(PositionEnum.CM, new Note(7.5f), new Price(BigDecimal.valueOf(2500.0)), titulaire),
            new PlayerVersion(0, now, now)
        );
    }
}

