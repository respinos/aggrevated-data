package org.example.aggrevateddata;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Mapper
public interface PossibleObjectMapper {

    @Select("SELECT possible_objects.* FROM possible_objects WHERE id = #{id}")
    PossibleObject findById(Long id);

    @Select("SELECT * FROM possible_objects WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id", id = true),
            // Lazy load the SIZE (Count)
            @Result(property = "childCount", column = "id",
                    one = @One(select = "countChildrenByParentId", fetchType = FetchType.LAZY)),

            // Lazy load the ROWS (List)
            @Result(property = "childObjects", column = "id",
                    many = @Many(select = "findChildrenByParentId", fetchType = FetchType.LAZY)),

            @Result(property = "objectFiles", column = "id",
                    many = @Many(select = "org.example.aggrevateddata.ObjectFileMapper.findByPossibleObjectsKey", fetchType = FetchType.LAZY))
    })
    PossibleObject findByIdWithObjectFiles(Long id);

    @Select("SELECT possible_objects.*, ( SELECT SUM(size) FROM object_files o WHERE o.possible_objects_key = possible_objects.id ) AS size FROM possible_objects LIMIT #{limit} OFFSET #{offset}")
    List<PossibleObject> findAll(Integer limit, Integer offset);

    @Select("SELECT possible_objects.*, ( SELECT SUM(size) FROM object_files o WHERE o.possible_objects_key = possible_objects.id ) AS size FROM possible_objects WHERE parent_id IS NULL LIMIT #{limit} OFFSET #{offset}")
    @Results({
            @Result(property = "id", column = "id", id = true),
            // Lazy load the SIZE (Count)
            @Result(property = "childCount", column = "id",
                    one = @One(select = "countChildrenByParentId", fetchType = FetchType.LAZY)),

            // Lazy load the ROWS (List)
            @Result(property = "childObjects", column = "id",
                    many = @Many(select = "findChildrenByParentId", fetchType = FetchType.LAZY))
    })
    List<PossibleObject> findAllRoots(Integer limit, Integer offset);

    @Select("SELECT COUNT(possible_objects.id) FROM possible_objects WHERE parent_id = #{id}")
    Long countChildrenByParentId(int id);

    @Select("SELECT possible_objects.*, ( SELECT SUM(size) FROM object_files o WHERE o.possible_objects_key = possible_objects.id ) AS size FROM possible_objects WHERE parent_id = #{id}")
    @Results({
            @Result(property = "id", column = "id", id = true),
            // Lazy load the SIZE (Count) - recursive
            @Result(property = "childCount", column = "id",
                    one = @One(select = "countChildrenByParentId", fetchType = FetchType.LAZY)),

            // Lazy load the ROWS (List) - recursive
            @Result(property = "childObjects", column = "id",
                    many = @Many(select = "findChildrenByParentId", fetchType = FetchType.LAZY))
    })
    List<PossibleObject> findChildrenByParentId(int id);

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
