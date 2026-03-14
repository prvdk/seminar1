package org.example.seminars;

import org.example.ConstellationRepository;
import org.example.SatelliteConstellation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Unit tests for ConstellationRepository")
class ConstellationRepositoryUnitTest {

    private static final String PRIMARY_CONSTELLATION_NAME = "Orbital-Alpha";
    private static final String SECONDARY_CONSTELLATION_NAME = "Orbital-Beta";
    private static final String UNKNOWN_CONSTELLATION_NAME = "Unknown-Orbit";
    private static final String MAX_LENGTH_CONSTELLATION_NAME = "X".repeat(255);

    private ConstellationRepository constellationRepository;

    @BeforeEach
    void setUp() {
        constellationRepository = new ConstellationRepository();
    }

    @Test
    @DisplayName("save should add new constellation to repository")
    void save_shouldAddNewConstellationToRepository() {
        SatelliteConstellation createdConstellation = new SatelliteConstellation(PRIMARY_CONSTELLATION_NAME);

        SatelliteConstellation savedConstellation = constellationRepository.save(createdConstellation);

        assertEquals(PRIMARY_CONSTELLATION_NAME, savedConstellation.getConstellationName());
        assertTrue(constellationRepository.findByName(PRIMARY_CONSTELLATION_NAME).isPresent());
    }

    @Test
    @DisplayName("save should overwrite constellation with same name")
    void save_shouldOverwriteConstellationWithSameName() {
        SatelliteConstellation firstVersion = new SatelliteConstellation(PRIMARY_CONSTELLATION_NAME);
        SatelliteConstellation secondVersion = new SatelliteConstellation(PRIMARY_CONSTELLATION_NAME);
        constellationRepository.save(firstVersion);

        constellationRepository.save(secondVersion);
        Map<String, SatelliteConstellation> allConstellations = constellationRepository.getAllConstellations();

        assertEquals(1, allConstellations.size());
        assertEquals(secondVersion, allConstellations.get(PRIMARY_CONSTELLATION_NAME));
    }

    @Test
    @DisplayName("save should support boundary case with max length name")
    void save_shouldSupportBoundaryCaseWithMaxLengthName() {
        SatelliteConstellation longNameConstellation = new SatelliteConstellation(MAX_LENGTH_CONSTELLATION_NAME);

        constellationRepository.save(longNameConstellation);

        assertTrue(constellationRepository.existsByName(MAX_LENGTH_CONSTELLATION_NAME));
    }

    @Test
    @DisplayName("save should throw exception for null constellation")
    void save_shouldThrowExceptionForNullConstellation() {
        assertThrows(NullPointerException.class, () -> constellationRepository.save(null));
    }

    @Test
    @DisplayName("findByName should return empty optional for unknown name")
    void findByName_shouldReturnEmptyOptionalForUnknownName() {
        constellationRepository.save(new SatelliteConstellation(PRIMARY_CONSTELLATION_NAME));

        assertTrue(constellationRepository.findByName(UNKNOWN_CONSTELLATION_NAME).isEmpty());
    }

    @Test
    @DisplayName("getAllConstellations should return all saved constellations")
    void getAllConstellations_shouldReturnAllSavedConstellations() {
        constellationRepository.save(new SatelliteConstellation(PRIMARY_CONSTELLATION_NAME));
        constellationRepository.save(new SatelliteConstellation(SECONDARY_CONSTELLATION_NAME));

        Map<String, SatelliteConstellation> allConstellations = constellationRepository.getAllConstellations();

        assertEquals(2, allConstellations.size());
        assertTrue(allConstellations.containsKey(PRIMARY_CONSTELLATION_NAME));
        assertTrue(allConstellations.containsKey(SECONDARY_CONSTELLATION_NAME));
    }

    @Test
    @DisplayName("existsByName should return false for absent constellation")
    void existsByName_shouldReturnFalseForAbsentConstellation() {
        constellationRepository.save(new SatelliteConstellation(PRIMARY_CONSTELLATION_NAME));

        boolean exists = constellationRepository.existsByName(UNKNOWN_CONSTELLATION_NAME);

        assertFalse(exists);
    }

    @Test
    @DisplayName("deleteByName should remove existing constellation")
    void deleteByName_shouldRemoveExistingConstellation() {
        constellationRepository.save(new SatelliteConstellation(PRIMARY_CONSTELLATION_NAME));

        constellationRepository.deleteByName(PRIMARY_CONSTELLATION_NAME);

        assertFalse(constellationRepository.existsByName(PRIMARY_CONSTELLATION_NAME));
    }

    @Test
    @DisplayName("deleteByName should not affect repository for unknown name")
    void deleteByName_shouldNotAffectRepositoryForUnknownName() {
        constellationRepository.save(new SatelliteConstellation(PRIMARY_CONSTELLATION_NAME));

        constellationRepository.deleteByName(UNKNOWN_CONSTELLATION_NAME);

        assertEquals(1, constellationRepository.getAllConstellations().size());
    }
}
