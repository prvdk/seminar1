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
@RequestMapping("/api/db/satellites")
@RequiredArgsConstructor
public class SatelliteCrudController {

    private final SatelliteRepository satelliteRepository;

    @PostMapping
    public ResponseEntity<Satellite> create(@RequestBody Satellite satellite) {
        return ResponseEntity.ok(satelliteRepository.save(satellite));
    }

    @GetMapping
    public ResponseEntity<List<Satellite>> findAll() {
        return ResponseEntity.ok(satelliteRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Satellite> findById(@PathVariable Long id) {
        return satelliteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Satellite> update(@PathVariable Long id, @RequestBody Satellite satellite) {
        if (!satelliteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        satellite.setId(id);
        return ResponseEntity.ok(satelliteRepository.save(satellite));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!satelliteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        satelliteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
