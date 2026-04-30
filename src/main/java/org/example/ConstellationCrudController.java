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
@RequestMapping("/api/db/constellations")
@RequiredArgsConstructor
public class ConstellationCrudController {

    private final ConstellationRepository constellationRepository;

    @PostMapping
    public ResponseEntity<SatelliteConstellation> create(@RequestBody SatelliteConstellation constellation) {
        return ResponseEntity.ok(constellationRepository.save(constellation));
    }

    @GetMapping
    public ResponseEntity<List<SatelliteConstellation>> findAll() {
        return ResponseEntity.ok(constellationRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SatelliteConstellation> findById(@PathVariable Long id) {
        return constellationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SatelliteConstellation> update(
            @PathVariable Long id,
            @RequestBody SatelliteConstellation constellation
    ) {
        if (!constellationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        constellation.setId(id);
        return ResponseEntity.ok(constellationRepository.save(constellation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!constellationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        constellationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
