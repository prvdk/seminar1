package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/db/satellite-states")
@RequiredArgsConstructor
public class SatelliteStateCrudController {

    private final SatelliteStateRepository satelliteStateRepository;

    @PostMapping
    public ResponseEntity<SatelliteState> create(@RequestBody SatelliteState state) {
        return ResponseEntity.ok(satelliteStateRepository.save(state));
    }

    @GetMapping
    public ResponseEntity<List<SatelliteState>> findAll() {
        return ResponseEntity.ok(satelliteStateRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SatelliteState> findById(@PathVariable Long id) {
        return satelliteStateRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SatelliteState> update(@PathVariable Long id, @RequestBody SatelliteState state) {
        if (!satelliteStateRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        state.setId(id);
        return ResponseEntity.ok(satelliteStateRepository.save(state));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!satelliteStateRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        satelliteStateRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
