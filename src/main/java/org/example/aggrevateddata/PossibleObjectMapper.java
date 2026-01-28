package org.example.aggrevateddata;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;
import java.util.Map;

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

    @Select("SELECT * FROM current_possible_objects WHERE identifier = #{identifier}")
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
    PossibleObject findCurrentByIdentifier(String identifier);

    @Select("SELECT current_possible_objects.*, ( SELECT SUM(size) FROM object_files o WHERE o.possible_objects_key = current_possible_objects.id ) AS size FROM current_possible_objects LIMIT #{limit} OFFSET #{offset}")
    List<PossibleObject> findAll(Integer limit, Integer offset);

    @Select("SELECT current_possible_objects.*, ( SELECT SUM(size) FROM object_files o WHERE o.possible_objects_key = current_possible_objects.id ) AS size FROM current_possible_objects WHERE parent_id IS NULL ORDER BY id LIMIT #{limit} OFFSET #{offset}")
    @Results({
            @Result(property = "id", column = "id", id = true),
            // Lazy load the SIZE (Count)
            @Result(property = "childCount", column = "id",
                    one = @One(select = "countChildrenByParentId", fetchType = FetchType.LAZY)),

            // Lazy load the ROWS (List)
            @Result(property = "childObjects", column = "id",
                    many = @Many(select = "findChildrenByParentId", fetchType = FetchType.LAZY))
    })
    List<PossibleObject> findAllCurrentRoots(Integer limit, Integer offset);

    @Select("SELECT COUNT(id) FROM current_possible_objects")
    Long countCurrentRoots();

    @Select("SELECT COUNT(possible_objects.id) FROM possible_objects WHERE parent_id = #{id}")
    Long countChildrenByParentId(int id);

    @Select("SELECT possible_objects.*, ( SELECT SUM(size) FROM object_files o WHERE o.possible_objects_key = possible_objects.id ) AS size FROM possible_objects WHERE parent_id = #{id} ORDER BY id")
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


    // debug queries because pg_dump will set search_path to empty and
    // the application really doesn't like that
    @Select("SELECT current_database() AS current_database, current_user, current_schema() AS current_schema")
    Map<String, Object> debugDb();

    @Select("SELECT * FROM public.object_files LIMIT 1")
    Map<String, Object> testExplicitSchema();

    @Select("SHOW search_path")
    Map<String, Object> debugSearchPath();
}
