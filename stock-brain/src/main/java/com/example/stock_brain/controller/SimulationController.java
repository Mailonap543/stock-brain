package com.example.stock_brain.controller;

import com.example.stock_brain.dto.SimulationDtos.SimulationRequest;
import com.example.stock_brain.dto.SimulationDtos.SimulationResponse;
import com.example.stock_brain.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService simulationService;

    @PostMapping
    public ResponseEntity<SimulationResponse> simulate(@RequestBody SimulationRequest request) {
        return ResponseEntity.ok(simulationService.simulate(request));
    }
}