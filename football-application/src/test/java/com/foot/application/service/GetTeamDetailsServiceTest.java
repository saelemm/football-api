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
import pagination.PagedResult;
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
        when(teamRepository.findAll(0, 20, "name", "asc"))
            .thenReturn(new PagedResult<>(teams, 0, 20, 2, 1, true, true, "name", "asc"));

        PagedResult<Team> result = service.findAllTeams(0, 20, "name", "asc");

        assertEquals(2, result.content().size());
        assertEquals("PSG", result.content().stream().findFirst().get().teamId().name());
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
        when(playerRepository.findByTeamId(teamId, 0, 20, "name", "asc"))
            .thenReturn(new PagedResult<>(
                List.of(player(10L, teamId, true), player(11L, teamId, false)),
                0, 20, 2, 1, true, true, "name", "asc"
            ));

        PagedResult<Player> result = service.findCurrentPlayers(teamId, 0, 20, "name", "asc");

        assertEquals(2, result.content().size());
    }

    @Test
    @DisplayName("Doit retourner uniquement les titulaires")
    void shouldReturnOnlyStarters() {
        TeamId teamId = new TeamId(1L);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team(1L, "PSG", "PSG")));
        when(playerRepository.findByTeamIdAndTitulaire(teamId, true, 0, 20, "name", "asc"))
            .thenReturn(new PagedResult<>(
                List.of(player(10L, teamId, true), player(12L, teamId, true)),
                0, 20, 2, 1, true, true, "name", "asc"
            ));

        PagedResult<Player> result = service.findTitulaires(teamId, 0, 20, "name", "asc");

        assertEquals(2, result.content().size());
        assertTrue(result.content().get(0).stats().isTitulaire());
        assertTrue(result.content().get(1).stats().isTitulaire());
    }

    @Test
    @DisplayName("Doit retourner uniquement les remplacants")
    void shouldReturnOnlyBenchPlayers() {
        TeamId teamId = new TeamId(1L);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team(1L, "PSG", "PSG")));
        when(playerRepository.findByTeamIdAndTitulaire(teamId, false, 0, 20, "name", "asc"))
            .thenReturn(new PagedResult<>(
                List.of(player(11L, teamId, false), player(12L, teamId, false)),
                0, 20, 2, 1, true, true, "name", "asc"
            ));

        PagedResult<Player> result = service.findRemplacants(teamId, 0, 20, "name", "asc");

        assertEquals(2, result.content().size());
        assertFalse(result.content().get(0).stats().isTitulaire());
        assertFalse(result.content().get(1).stats().isTitulaire());
    }

    @Test
    @DisplayName("Doit deleguer la pagination et le tri des equipes")
    void shouldDelegateTeamsPaginationAndSorting() {
        List<Team> teams = List.of(team(2L, "OM", "OM"));
        when(teamRepository.findAll(1, 5, "budget", "desc"))
            .thenReturn(new PagedResult<>(teams, 1, 5, 11, 3, false, false, "budget", "desc"));

        PagedResult<Team> result = service.findAllTeams(1, 5, "budget", "desc");

        assertEquals(1, result.content().size());
        assertEquals("OM", result.content().get(0).teamId().name());
        assertEquals(11, result.totalElements());
    }

    @Test
    @DisplayName("Doit deleguer la pagination et le tri des joueurs d'une equipe")
    void shouldDelegatePlayersPaginationAndSorting() {
        TeamId teamId = new TeamId(1L);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team(1L, "PSG", "PSG")));
        when(playerRepository.findByTeamId(teamId, 2, 10, "marketPrice", "desc"))
            .thenReturn(new PagedResult<>(
                List.of(player(12L, teamId, false)),
                2, 10, 21, 3, false, false, "marketPrice", "desc"
            ));

        PagedResult<Player> result = service.findCurrentPlayers(teamId, 2, 10, "marketPrice", "desc");

        assertEquals(1, result.content().size());
        assertEquals(12L, result.content().get(0).identifier().id().value());
        assertEquals(21, result.totalElements());
    }

    @Test
    @DisplayName("Doit déléguer la pagination et le tri des titulaires")
    void shouldDelegateStartersPaginationAndSorting() {
        TeamId teamId = new TeamId(1L);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team(1L, "PSG", "PSG")));
        when(playerRepository.findByTeamIdAndTitulaire(teamId, true, 1, 4, "acronym", "desc"))
            .thenReturn(new PagedResult<>(
                List.of(player(21L, teamId, true)),
                1, 4, 9, 3, false, false, "acronym", "desc"
            ));

        PagedResult<Player> result = service.findTitulaires(teamId, 1, 4, "acronym", "desc");

        assertEquals(1, result.content().size());
        assertTrue(result.content().get(0).stats().isTitulaire());
        assertEquals(9, result.totalElements());
    }

    @Test
    @DisplayName("Doit déléguer la pagination et le tri des remplaçants")
    void shouldDelegateSubstitutesPaginationAndSorting() {
        TeamId teamId = new TeamId(1L);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team(1L, "PSG", "PSG")));
        when(playerRepository.findByTeamIdAndTitulaire(teamId, false, 2, 3, "marketPrice", "desc"))
            .thenReturn(new PagedResult<>(
                List.of(player(31L, teamId, false)),
                2, 3, 7, 3, false, true, "marketPrice", "desc"
            ));

        PagedResult<Player> result = service.findRemplacants(teamId, 2, 3, "marketPrice", "desc");

        assertEquals(1, result.content().size());
        assertFalse(result.content().get(0).stats().isTitulaire());
        assertEquals(7, result.totalElements());
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

