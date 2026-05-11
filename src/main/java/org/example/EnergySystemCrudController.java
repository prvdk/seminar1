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
@RequestMapping("/api/db/energy-systems")
@RequiredArgsConstructor
public class EnergySystemCrudController {

    private final EnergySystemRepository energySystemRepository;

    @PostMapping
    public ResponseEntity<EnergySystem> create(@RequestBody EnergySystem energySystem) {
        return ResponseEntity.ok(energySystemRepository.save(energySystem));
    }

    @GetMapping
    public ResponseEntity<List<EnergySystem>> findAll() {
        return ResponseEntity.ok(energySystemRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnergySystem> findById(@PathVariable Long id) {
        return energySystemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnergySystem> update(@PathVariable Long id, @RequestBody EnergySystem energySystem) {
        if (!energySystemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        energySystem.setId(id);
        return ResponseEntity.ok(energySystemRepository.save(energySystem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!energySystemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        energySystemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
