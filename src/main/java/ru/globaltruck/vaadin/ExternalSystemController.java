package ru.globaltruck.vaadin;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/v1/external-system")
public class ExternalSystemController {

    private final ExternalSystemRepository systemRepository;

    public ExternalSystemController(ExternalSystemRepository systemRepository) {
        this.systemRepository = systemRepository;
    }

    @GetMapping
    public void addEs(@RequestParam String name){
        ExternalSystem system = new ExternalSystem();
        system.setName(name);
        systemRepository.save(system);
    }
}
