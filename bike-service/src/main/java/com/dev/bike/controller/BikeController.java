package com.dev.bike.controller;

import com.dev.bike.controller.dto.BikeDTO;
import com.dev.bike.service.BikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/bikes")
public class BikeController {

    private final BikeService bikeService;

    public BikeController(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<BikeDTO>> getAllBikes() {
        List<BikeDTO> bikeDTOList = bikeService.getAllBikes().stream()
                .map(BikeDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bikeDTOList);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<BikeDTO> getBikeById(@PathVariable Long id) {
        var bike = bikeService.getBikeById(id);
        if (bike == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new BikeDTO(bike));
    }

    @GetMapping("/view")
    public String getBikesView(Model model) {
        List<BikeDTO> bikes = bikeService.getAllBikes().stream()
                .map(BikeDTO::new)
                .collect(Collectors.toList());
//        bikes.forEach(bike -> bike.imageSource = "images/" + bike.imageSource);
        model.addAttribute("bikes", bikes);
        return "bikes";
    }
}
