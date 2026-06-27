package com.dev.bike.controller;

import com.dev.bike.controller.dto.BikeDTO;
import com.dev.bike.service.BikeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/bikes")
public class BikeController {

    private final BikeService bikeService;
    private final SpringTemplateEngine templateEngine;

    public BikeController(BikeService bikeService, SpringTemplateEngine templateEngine) {
        this.bikeService = bikeService;
        this.templateEngine = templateEngine;
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

    /**
     * Streams the bikes as an HTML fragment, one bike at a time.
     * <p>
     * Emits the same wrapper as {@code bikes.html} (the {@code <main>}, search box and flex
     * container), then renders and flushes each bike box individually using the
     * {@code bike.html} Thymeleaf template, so consumers (e.g. the MFE shell) receive bikes
     * progressively as they stream.
     *
     * @param delay optional milliseconds to wait between bikes to make streaming observable
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> streamBikes(
            @RequestParam(name = "delay", defaultValue = "500") long delay) {

        List<BikeDTO> bikes = bikeService.getAllBikes().stream()
                .map(BikeDTO::new)
                .collect(Collectors.toList());

        StreamingResponseBody body = output -> {
            try (Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
                writeWrapperOpen(writer);
                writer.flush();
                for (BikeDTO bike : bikes) {
                    Context context = new Context();
                    context.setVariable("bike", bike);
                    templateEngine.process("bike", context, writer);
                    writer.flush();
                    if (delay > 0) {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
                writeWrapperClose(writer);
                writer.flush();
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .body(body);
    }

    private void writeWrapperOpen(Writer writer) throws java.io.IOException {
        writer.write("""
                <main style="padding: 20px;">
                
                    <div style="margin-bottom:10px">
                        <label for="search">Search</label>
                        <input id="search" type="text" placeholder="Search bikes..."/>
                    </div>

                    <div style="display: flex; gap: 20px; flex-wrap: wrap; justify-content: space-evenly;">
                    <template shadowrootmode="open">
                        <slot name="bike"></slot>
                    </template>
                """);
    }

    private void writeWrapperClose(Writer writer) throws java.io.IOException {
        writer.write("""
                    </div>
                </main>
                """);
    }

}
