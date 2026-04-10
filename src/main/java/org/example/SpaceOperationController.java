package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SpaceOperationController {

    private final SpaceOperationCenterService spaceOperationCenterService;

    @PostMapping("/add-satellites")
    public ResponseEntity<SatelliteConstellation> addSatellite(@RequestBody AddSatelliteRequest request) {
        SatelliteConstellation constellation = spaceOperationCenterService.addSatellite(request);
        return ResponseEntity.ok(constellation);
    }

    @PostMapping("/missions")
    public ResponseEntity<Void> executeMission(@RequestBody MissionRequest request) {
        spaceOperationCenterService.executeMission(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/overview")
    public ResponseEntity<String> getOverview() {
        return ResponseEntity.ok(spaceOperationCenterService.getOverview());
    }

    @DeleteMapping("/constellations/{constellationName}/satellites/{satelliteName}")
    public ResponseEntity<Void> decommissionSatellite(
            @PathVariable String constellationName,
            @PathVariable String satelliteName
    ) {
        spaceOperationCenterService.decommissionSatellite(constellationName, satelliteName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/constellations/{constellationName}/status")
    public ResponseEntity<SatelliteConstellation> showStatus(@PathVariable String constellationName) {
        return ResponseEntity.ok(spaceOperationCenterService.showConstellationStatus(constellationName));
    }

    @PostMapping("/constellations/{constellationName}/activate")
    public ResponseEntity<Void> activateConstellation(@PathVariable String constellationName) {
        spaceOperationCenterService.activateConstellation(constellationName);
        return ResponseEntity.ok().build();
    }
}
