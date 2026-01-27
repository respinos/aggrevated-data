package org.example.aggrevateddata;

import java.util.List;

public class PossibleObject {
    private Integer id;
    private Integer parentId;

    private String identifier;
    private String type;
    private Integer versionNumber = 1;
    private String binIdentifier;
    private Long size = 0L;

    // Triggered by getChildCount()
    private Long childCount = 0L;

    // Triggered by getChildObjects()
    private List<PossibleObject> childObjects;

    private List<ObjectFile> objectFiles;

    public PossibleObject() {}

    public PossibleObject(Integer id, Integer parentId, String identifier, String type, Integer versionNumber, String binIdentifier) {
        this.id = id;
        this.parentId = parentId;
        this.identifier = identifier;
        this.type = type;
        this.versionNumber = versionNumber;
        this.binIdentifier = binIdentifier;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }

    public String getBinIdentifier() { return binIdentifier; }
    public void setBinIdentifier(String binIdentifier) { this.binIdentifier = binIdentifier; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public Long getChildCount() { return childCount; }
    public void setChildCount(Long childCount) { this.childCount = childCount; }

    public List<PossibleObject> getChildObjects() { return childObjects; }
    public void setChildObjects(List<PossibleObject> childObjects) { this.childObjects = childObjects; }

    public List<ObjectFile> getObjectFiles() { return objectFiles; }
    public void setObjectFiles(List<ObjectFile> objectFiles) { this.objectFiles = objectFiles; }

    public Long totalSize() {
        var totalSize = size;
        if (childObjects != null) {
            for (PossibleObject child : childObjects) {
                totalSize += child.totalSize();
            }
        }
        return totalSize;
    }
}
