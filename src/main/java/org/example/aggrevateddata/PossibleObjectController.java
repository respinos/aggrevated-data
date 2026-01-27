package org.example.aggrevateddata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class PossibleObjectController {
    private final PossibleObjectMapper possibleObjectMapper;

    @Autowired
    public PossibleObjectController(PossibleObjectMapper possibleObjectMapper) {
        this.possibleObjectMapper = possibleObjectMapper;
    }

    // TODO - add version parameter
    @GetMapping("/console/objects/{id}/")
    public String viewPossibleObject(Model model,
                                     @PathVariable Long id) {


        var possibleObject = possibleObjectMapper.findByIdWithObjectFiles(id);
//        if (possibleObject == null) {
//            return "redirect:/console/objects/";
//        }
        model.addAttribute("possibleObject", possibleObject);
        return "possible_object";
    }


    }
