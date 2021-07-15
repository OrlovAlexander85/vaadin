package ru.globaltruck.vaadin;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/v1/external-system")
public class NodeController {

    private final NodeService nodeService;

    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping
    public void addExternalSystem(){
        nodeService.save();
    }
}
