package org.example.tournamentapp.e2e;

import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.repository.PlayerRepository;
import org.example.tournamentapp.repository.TournamentRepository;
import org.example.tournamentapp.repository.PairingRepository;
import org.example.tournamentapp.testData.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TournamentEndToEndTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PairingRepository pairingRepository;
    private TestData testData;

    @BeforeEach
    void setUp() {
        testData = new TestData();
        pairingRepository.deleteAll();
        playerRepository.deleteAll();
        tournamentRepository.deleteAll();
    }

    @Test
    void autoSetupFlowTest() throws Exception {
        mockMvc.perform(get("/setup"))
                .andExpect(status().isOk())
                .andExpect(view().name("setupPage"))
                .andExpect(model().attributeExists("wrapper"));

        mockMvc.perform(post("/setup")
                        .param("tourCount", "3")
                        .param("tourFlag", "false")
                        .param("playerList[0].name", "Паша")
                        .param("playerList[0].faction", "faction1")
                        .param("playerList[1].name", "Витя")
                        .param("playerList[1].faction", "faction3")
                        .param("playerList[2].name", "Даня")
                        .param("playerList[2].faction", "faction2")
                        .param("playerList[3].name", "Настя")
                        .param("playerList[3].faction", "faction4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/nextTour?tournamentId=*"));

        List<Tournament> tournaments = tournamentRepository.findAll();
        assertThat(tournaments).hasSize(1);

        Tournament tournament = tournaments.get(0);
        assertThat(tournament.getTourCount()).isEqualTo(3);
        assertThat(tournament.getCurrentTour()).isGreaterThan(0);

        List<PlayerEntity> players = playerRepository.findAllByTournamentId(tournament.getId());
        assertThat(players).hasSize(4);
        assertThat(players).extracting(PlayerEntity::getName)
                .containsExactlyInAnyOrder("Паша", "Витя", "Даня", "Настя");
        
        assertThat(players).allMatch(p -> p.getVp() == 0);
        assertThat(players).allMatch(p -> p.getTp() == 0);
    }

    @Test
    void manualSetupFlowTest() throws Exception {
        mockMvc.perform(post("/setup")
                        .param("tourCount", "2")
                        .param("tourFlag", "true")
                        .param("playerList[0].name", "Михаил")
                        .param("playerList[0].faction", "faction2")
                        .param("playerList[1].name", "Тигран")
                        .param("playerList[1].faction", "faction3")
                        .param("playerList[2].name", "Иван")
                        .param("playerList[2].faction", "faction1")
                        .param("playerList[3].name", "Евгений")
                        .param("playerList[3].faction", "faction4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/hsetup?tournamentId=*"));

        List<Tournament> tournaments = tournamentRepository.findAll();
        assertThat(tournaments).hasSize(1);

        Tournament tournament = tournaments.get(0);
        List<PlayerEntity> players = playerRepository.findAllByTournamentId(tournament.getId());
        assertThat(players).hasSize(4);

        mockMvc.perform(get("/hsetup")
                        .param("tournamentId", tournament.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("handSetupPage"))
                .andExpect(model().attributeExists("pairsWrapper"))
                .andExpect(model().attributeExists("playerList"))
                .andExpect(model().attribute("tournamentId", tournament.getId()));

        Long player1Id = players.get(0).getId();
        Long player2Id = players.get(1).getId();
        Long player3Id = players.get(2).getId();
        Long player4Id = players.get(3).getId();

        mockMvc.perform(post("/hsetup")
                        .param("tournamentId", tournament.getId().toString())
                        .param("pairs[0].firstPlayer.id", player1Id.toString())
                        .param("pairs[0].secondPlayer.id", player2Id.toString())
                        .param("pairs[1].firstPlayer.id", player3Id.toString())
                        .param("pairs[1].secondPlayer.id", player4Id.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nextTour?tournamentId=" + tournament.getId()));

        Tournament updatedTournament = tournamentRepository.findById(tournament.getId()).orElseThrow();
        assertThat(updatedTournament.getCurrentTour()).isGreaterThan(0);
    }


    @Test
    void validationBlocksDataTest() throws Exception {
        mockMvc.perform(post("/setup")
                        .param("tourCount", "2")
                        .param("tourFlag", "false")
                        .param("playerList[0].name", "A")
                        .param("playerList[0].faction", "faction1"))
                .andExpect(status().isOk())
                .andExpect(view().name("setupPage"));

        assertThat(tournamentRepository.findAll()).isEmpty();
        assertThat(playerRepository.findAll()).isEmpty();
    }

    @Test
    void scoreValidationBlocksTest() throws Exception {
        mockMvc.perform(post("/setup")
                        .param("tourCount", "1")
                        .param("tourFlag", "false")
                        .param("playerList[0].name", "Даня")
                        .param("playerList[0].faction", "faction2")
                        .param("playerList[1].name", "Настя")
                        .param("playerList[1].faction", "faction4"));

        Tournament tournament = tournamentRepository.findAll().get(0);
        List<PlayerEntity> players = playerRepository.findAllByTournamentId(tournament.getId());

        mockMvc.perform(post("/nextTour")
                        .param("tournamentId", tournament.getId().toString())
                        .param("pairs[0].firstPlayer.id", players.get(0).getId().toString())
                        .param("pairs[0].firstPlayer.mp", "1000")
                        .param("pairs[0].firstPlayer.ap", "5")
                        .param("pairs[0].secondPlayer.id", players.get(1).getId().toString())
                        .param("pairs[0].secondPlayer.mp", "5")
                        .param("pairs[0].secondPlayer.ap", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("nextTourPage"));

        PlayerEntity player1 = playerRepository.findById(players.get(0).getId()).orElseThrow();
        assertThat(player1.getVp()).isEqualTo(0);
        assertThat(player1.getTp()).isEqualTo(0);
    }

    @Test
    void subTotalStandingsTest() throws Exception {
        mockMvc.perform(post("/setup")
                        .param("tourCount", "2")
                        .param("tourFlag", "false")
                        .param("playerList[0].name", "Иван")
                        .param("playerList[0].faction", "faction1")
                        .param("playerList[1].name", "Евгений")
                        .param("playerList[1].faction", "faction4"));

        Tournament tournament = tournamentRepository.findAll().get(0);
        List<PlayerEntity> players = playerRepository.findAllByTournamentId(tournament.getId());

        mockMvc.perform(post("/nextTour")
                        .param("tournamentId", tournament.getId().toString())
                        .param("pairs[0].firstPlayer.id", players.get(0).getId().toString())
                        .param("pairs[0].firstPlayer.mp", "8")
                        .param("pairs[0].firstPlayer.ap", "8")
                        .param("pairs[0].secondPlayer.id", players.get(1).getId().toString())
                        .param("pairs[0].secondPlayer.mp", "7")
                        .param("pairs[0].secondPlayer.ap", "7"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/subTotalResult")
                        .param("tournamentId", tournament.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("subTotalPage"))
                .andExpect(model().attributeExists("wrapper"))
                .andExpect(model().attributeExists("currentTour"))
                .andExpect(model().attribute("tournamentId", tournament.getId()));
    }

    @Test
    void historyResumeTest() throws Exception {
        mockMvc.perform(post("/setup")
                        .param("tourCount", "1")
                        .param("tourFlag", "false")
                        .param("playerList[0].name", "Тигран")
                        .param("playerList[0].faction", "faction3")
                        .param("playerList[1].name", "Михаил")
                        .param("playerList[1].faction", "faction2"));

        Tournament tournament = tournamentRepository.findAll().get(0);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("startingPage"))
                .andExpect(model().attributeExists("tournaments"));

        mockMvc.perform(post("/")
                        .param("tournamentId", tournament.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nextTour?tournamentId=" + tournament.getId()));
    }
}


