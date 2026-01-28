package org.example.aggrevateddata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class Page {
    private final int page;
    private final int size;
    private final Integer totalObjects;

    public Page(int page, int size, Integer totalObjects) {
        this.page = page;
        this.size = size;
        this.totalObjects = totalObjects;
    }

    public int getPage() { return page; }
    public int getSize() { return size; }
    public Integer getTotalObjects() { return totalObjects; }

    public boolean hasNext() { return page * size < totalObjects; }
    public boolean hasPrevious() { return page > 0; }

    public int totalPages() { return (int) Math.ceil(totalObjects / (double) size); }

}

@Controller
public class CurrentPossibleObjectsController {
    private final PossibleObjectMapper possibleObjectMapper;

    @Autowired
    public CurrentPossibleObjectsController(PossibleObjectMapper possibleObjectMapper) {
        this.possibleObjectMapper = possibleObjectMapper;
    }

    @GetMapping("/console/objects/")
    public String listCurrentPossibleObjects(Model model,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {

        Integer countCurrentRoots = possibleObjectMapper.countCurrentRoots();

        var pageObj = new Page(page, size, countCurrentRoots);

        List<PossibleObject> rootObjects = new ArrayList<>(possibleObjectMapper.findAllCurrentRoots(size, page * size));
        model.addAttribute("rootObjects", rootObjects);
        model.addAttribute("page", pageObj);
        return "current_possible_objects";
    }

    @GetMapping("/debug")
    public String debug(Model model) {
        var debugInfo = possibleObjectMapper.debugDb();
        var explicitSchemaTest = possibleObjectMapper.testExplicitSchema();
        System.err.println(explicitSchemaTest);

        var searchPath = possibleObjectMapper.debugSearchPath();
        System.err.println("SEARCH PATH: " + searchPath);

        model.addAttribute("debugInfo", debugInfo);
        System.err.println(debugInfo);
        return "debug";
    }

}
