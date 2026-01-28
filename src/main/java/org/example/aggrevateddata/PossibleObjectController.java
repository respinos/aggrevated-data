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

    @GetMapping("/console/objects/{id}/")
    public String viewPossibleObject(Model model,
                                     @PathVariable Integer id) {


        var possibleObject = possibleObjectMapper.findByIdWithObjectFiles(id);
//        if (possibleObject == null) {
//            return "redirect:/console/objects/";
//        }

        var objectFiles = possibleObject.getObjectFiles();
        if ( objectFiles != null) {
            System.err.println("?? " + possibleObject.getObjectFiles().size());
        } else {
            System.err.println("!! objectFiles IS NULL");
        }
        model.addAttribute("possibleObject", possibleObject);
        return "possible_object";
    }

    @GetMapping("/console/objects/{id}/versions/")
    public String viewPossibleObjectVersions(Model model,
                                     @PathVariable Integer id) {

        var possibleObject = possibleObjectMapper.findByIdWithObjectFiles(id);
        var possibleObjectVersions = possibleObjectMapper.findVersionsByIdentifier(possibleObject.getIdentifier());
        model.addAttribute("possibleObject", possibleObject);
        model.addAttribute("possibleObjectVersions", possibleObjectVersions);
        return "possible_object_versions";

    }


}
