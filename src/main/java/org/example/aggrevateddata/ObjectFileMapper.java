package org.example.aggrevateddata;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ObjectFileMapper {

    @Select("SELECT * FROM object_files WHERE id = #{id}")
    ObjectFile findById(Long id);

    @Select("SELECT * FROM object_files")
    List<ObjectFile> findAll();

    @Select("SELECT * FROM object_files WHERE possible_objects_key = #{possibleObjectsKey} ORDER BY possible_objects_index")
    List<ObjectFile> findByPossibleObjectsKey(Integer possibleObjectsKey);

    @Insert("INSERT INTO object_files (identifier, file_format, file_function, size, digest, version_number, last_fixity_check, possible_objects_key, possible_objects_index) " +
            "VALUES (#{identifier}, #{fileFormat}, #{fileFunction}, #{size}, #{digest}, #{versionNumber}, #{lastFixityCheck}, #{possibleObjectsKey}, #{possibleObjectsIndex})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ObjectFile objectFile);

    @Update("UPDATE object_files SET identifier = #{identifier}, file_format = #{fileFormat}, file_function = #{fileFunction}, size = #{size}, digest = #{digest}, version_number = #{versionNumber}, last_fixity_check = #{lastFixityCheck}, possible_objects_key = #{possibleObjectKey}, possible_objects_index = #{possibleObjectIndex} WHERE id = #{id}")
    int update(ObjectFile objectFile);

    @Delete("DELETE FROM object_files WHERE id = #{id}")
    int delete(Long id);
}
