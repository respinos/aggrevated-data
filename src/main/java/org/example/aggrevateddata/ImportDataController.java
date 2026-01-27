package org.example.aggrevateddata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class ImportDataController {
    private final PossibleObjectMapper possibleObjectMapper;
    private final ObjectFileMapper objectFileMapper;

    @Autowired
    public ImportDataController(PossibleObjectMapper possibleObjectMapper, ObjectFileMapper objectFileMapper) {
        this.possibleObjectMapper = possibleObjectMapper;
        this.objectFileMapper = objectFileMapper;
    }

    @GetMapping("/import")
    public String importData(@RequestParam Map<String, String> params, Model model) throws IOException {
        String filePath = "tmp/output.tsv";
        Map<String, PossibleObject> identifierToObject = new HashMap<>();
        Map<String, String> identifierToParentIdentifier = new HashMap<>();
        BufferedWriter writer = new BufferedWriter(new FileWriter("tmp/import.txt"));
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String header = reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\t", -1);
                // if (fields.length < 5) continue;
                if ( fields.length < 5 ) {
                    System.err.println("!! " + line);
                    continue;
                }
                writer.write(line + "\n");
                String binIdentifier = fields[0];
                String identifier = fields[1];
                String parentIdentifiers = fields[2];
                String type = fields[3];
                String revisionNumberStr = fields[4];
                Integer versionNumber = 1;
                try {
                    versionNumber = Integer.parseInt(revisionNumberStr);
                } catch (NumberFormatException ignored) {}
                Integer parentId = null;
                // Do NOT set parentId yet, as parent may not have id assigned
                PossibleObject obj = new PossibleObject(null, null, identifier, type, versionNumber, binIdentifier);
                identifierToObject.put(identifier, obj);
                if ( parentIdentifiers != null && !parentIdentifiers.isBlank() ) {
                    identifierToParentIdentifier.put(identifier, parentIdentifiers);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading TSV file: " + e.getMessage());
        }

        // do we not have to save the root objects first
        for(PossibleObject obj : identifierToObject.values()) {
            var identifier = obj.getIdentifier();
            if ( !identifierToParentIdentifier.containsKey(identifier) ) {
                possibleObjectMapper.insert(obj);
                writer.write(":: " + obj.getId() + " -> " + identifier + "\n");;
            }
        }

        for(PossibleObject obj : identifierToObject.values()) {
            var identifier = obj.getIdentifier();
            if ( identifierToParentIdentifier.containsKey(identifier) ) {
                var parentIdentifier = identifierToParentIdentifier.get(identifier);
                var parentObj = identifierToObject.get(parentIdentifier);
                obj.setParentId(parentObj.getId());
                possibleObjectMapper.insert(obj);
                writer.write(":: " + obj.getId() + " -> " + identifier + " <- " + parentObj.getId() + "\n");;

            }
        }

        filePath = "tmp/files.tsv";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String header = reader.readLine(); // skip header
            String line;
            Map<Integer, Integer> objectFileIndex = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\t", -1);
                if (fields.length < 8) continue;
                String objectIdentifier = fields[0];
                String identifier = fields[1];
                String fileFormat = fields[2];
                String fileFunction = fields[3];
                String sizeStr = fields[4];
                Integer size = 0;
                try {
                    size = Integer.parseInt(sizeStr);
                } catch (NumberFormatException ignored) {}
                String digestHex = fields[5];
                byte[] digest = null;
                if (digestHex != null && !digestHex.isBlank()) {
                    int len = digestHex.length();
                    digest = new byte[len / 2];
                    for (int i = 0; i < len; i += 2) {
                        digest[i / 2] = (byte) ((Character.digit(digestHex.charAt(i), 16) << 4)
                                + Character.digit(digestHex.charAt(i+1), 16));
                    }
                }
                String versionNumberStr = fields[6];
                Integer versionNumber = 1;
                try {
                    versionNumber = Integer.parseInt(versionNumberStr);
                } catch (NumberFormatException ignored) {}
                String lastFixityCheckStr = fields[7];
                LocalDateTime lastFixityCheck = null;
                if (lastFixityCheckStr != null && !lastFixityCheckStr.isBlank()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
                    lastFixityCheck = LocalDateTime.parse(lastFixityCheckStr, formatter);
                }
                PossibleObject obj = identifierToObject.get(objectIdentifier);
                if (obj != null) {
                    var index = 0;
                    if ( objectFileIndex.containsKey(obj.getId()) ) {
                        objectFileIndex.put(obj.getId(), objectFileIndex.get(obj.getId()) + 1);
                        index = objectFileIndex.get(obj.getId());
                    } else {
                        objectFileIndex.put(obj.getId(), index);
                    }
                    ObjectFile objFile = new ObjectFile(null, identifier, fileFormat, fileFunction, size, digest, versionNumber, lastFixityCheck, obj.getId(), index);
                    objectFileMapper.insert(objFile);
                    System.err.println(":: " + objectIdentifier + " -> " + identifier);
                    writer.write(":: " + objectIdentifier + " -> " + identifier + "\n");
                } else {
                    System.err.println("Object not found: " + objectIdentifier);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading TSV file: " + e.getMessage());
        }

        var possibleObjects = StreamSupport.stream(possibleObjectMapper.findAll(10, 0).spliterator(), false).collect(Collectors.toList());
        model.addAttribute("possibleObjects", possibleObjects);
        writer.write("# objects: " + possibleObjects.size() + "\n");;

        Map<Integer, List<ObjectFile>> objectFileMap = new HashMap<>();
        for(PossibleObject obj : possibleObjects) {
            writer.write("$$ " + obj.getId() + " - " + obj.getIdentifier() + " - " + obj.getBinIdentifier() + "\n");;
            objectFileMap.put(obj.getId(), objectFileMapper.findByPossibleObjectsKey(obj.getId()));
        }

        writer.close();

        model.addAttribute("message", "Message received, La Jolla.");
        return "import";
    }
}
