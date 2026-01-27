package org.example.aggrevateddata;

import org.apache.ibatis.annotations.*;
import org.example.aggrevateddata.PossibleObject;

import java.util.List;

@Mapper
public interface PossibleObjectMapper {

    @Select("SELECT possible_objects.* FROM possible_objects WHERE id = #{id}")
    PossibleObject findById(Long id);

    @Select("SELECT * FROM possible_objects WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id", id = true),
            @Result(property = "objectFiles", column = "id",
                    many = @Many(select = "com.example.aggregavateddata.ObjectFileMapper.findByPossibleObjectsKey"))
    })
    PossibleObject findByIdWithObjectFiles(int id);

    @Select("SELECT possible_objects.*, ( SELECT SUM(size) FROM object_files o WHERE o.possible_objects_key = possible_objects.id ) AS size FROM possible_objects LIMIT #{limit} OFFSET #{offset}")
    List<PossibleObject> findAll(Integer limit, Integer offset);

    @Select("SELECT possible_objects.*, ( SELECT SUM(size) FROM object_files o WHERE o.possible_objects_key = possible_objects.id ) AS size FROM possible_objects WHERE parent_id IS NULL LIMIT #{limit} OFFSET #{offset}")
    List<PossibleObject> findAllRoots(Integer limit, Integer offset);

    @Select("SELECT possible_objects.*, ( SELECT SUM(size) FROM object_files o WHERE o.possible_objects_key = possible_objects.id ) AS size FROM possible_objects WHERE parent_id = #{id}")
    List<PossibleObject> findChildren(int id);

    @Insert("INSERT INTO possible_objects (parent_id, identifier, type, version_number, bin_identifier) " +
            "VALUES (#{parentId}, #{identifier}, #{type}, #{versionNumber}, #{binIdentifier})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PossibleObject possibleObject);

    @Update("UPDATE possible_objects SET parent_id = #{parentId}, identifier = #{identifier}, " +
            "type = #{type}, version_number = #{versionNumber}, bin_identifier = #{binIdentifier} WHERE id = #{id}")
    int update(PossibleObject possibleObject);

    @Delete("DELETE FROM possible_objects WHERE id = #{id}")
    int delete(Long id);
}
