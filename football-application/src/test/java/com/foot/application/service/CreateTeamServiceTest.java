package com.foot.application.service;

import Errors.DuplicateTeamException;
import entity.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import port.ITeamRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de CreateTeamService")
class CreateTeamServiceTest {

    @Mock
    private ITeamRepository teamRepository;

    @InjectMocks
    private CreateTeamService service;

    @Test
    @DisplayName("Doit creer une equipe quand nom et acronyme sont disponibles")
    void shouldCreateTeamWhenNameAndAcronymAreAvailable() {
        when(teamRepository.findByName("Arsenal")).thenReturn(Optional.empty());
        when(teamRepository.findByAcronym("ARS")).thenReturn(Optional.empty());
        when(teamRepository.save(any(Team.class))).thenReturn(42L);

        Long createdId = service.execute("Arsenal", "ARS", BigDecimal.valueOf(50000.0));

        assertEquals(42L, createdId);

        ArgumentCaptor<Team> teamCaptor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(teamCaptor.capture());

        Team savedTeam = teamCaptor.getValue();
        assertEquals("Arsenal", savedTeam.teamId().name());
        assertEquals("ARS", savedTeam.teamId().acronym());
        assertEquals(BigDecimal.valueOf(50000.0), savedTeam.teamStat().budget());
        assertEquals(0, savedTeam.teamStat().version());
    }

    @Test
    @DisplayName("Doit lever une erreur si le nom est deja utilise")
    void shouldThrowWhenNameAlreadyExists() {
        when(teamRepository.findByName("Arsenal")).thenReturn(Optional.of(mock(Team.class)));

        assertThrows(DuplicateTeamException.class,
            () -> service.execute("Arsenal", "ARS", BigDecimal.valueOf(50000.0)));
    }

    @Test
    @DisplayName("Doit lever une erreur si l'acronyme est deja utilise")
    void shouldThrowWhenAcronymAlreadyExists() {
        when(teamRepository.findByName("Arsenal")).thenReturn(Optional.empty());
        when(teamRepository.findByAcronym("ARS")).thenReturn(Optional.of(mock(Team.class)));

        assertThrows(DuplicateTeamException.class,
            () -> service.execute("Arsenal", "ARS", BigDecimal.valueOf(50000.0)));
    }
}

