package org.example.aggrevateddata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class CurrentPossibleObjectsController {
    private final PossibleObjectMapper possibleObjectMapper;

    @Autowired
    public CurrentPossibleObjectsController(PossibleObjectMapper possibleObjectMapper) {
        this.possibleObjectMapper = possibleObjectMapper;
    }

    @GetMapping("/current")
    public String listCurrentPossibleObjects(Model model,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {

        List<PossibleObject> rootObjects = StreamSupport.stream(possibleObjectMapper.findAllRoots(size, page).spliterator(), false).collect(Collectors.toList());
        for(PossibleObject obj : rootObjects) {
            var objSize = obj.getSize();
            var childObjects = possibleObjectMapper.findChildren(obj.getId());
            for(PossibleObject child : childObjects) {
                objSize += child.getSize();
            }
            obj.setSize(objSize);
        }
        model.addAttribute("rootObjects", rootObjects);
        return "current_possible_objects";
    }

}
