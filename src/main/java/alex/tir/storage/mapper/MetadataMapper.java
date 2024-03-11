package alex.tir.storage.mapper;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.entity.File;
import alex.tir.storage.entity.Folder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(
        implementationName = "MetadataMapperImpl",
        implementationPackage = "alex.tir.storage.mapper.impl")
public interface MetadataMapper {

    @Mapping(target = "type", expression = "java(Metadata.Type.FOLDER)")
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "mimeType", ignore = true)
    @Mapping(target = "size", ignore = true)
    Metadata mapFolder(Folder folder);

    List<Metadata> mapFolders(Iterable<Folder> folders);

    @Mapping(target = "type", expression = "java(Metadata.Type.FILE)")
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "root", ignore = true)
    Metadata mapFile(File file);

    List<Metadata> mapFiles(Iterable<File> files);

    default List<Metadata> mapItems(Iterable<Folder> folders, Iterable<File> files) {
        return Stream
                .concat(mapFolders(folders).stream(), mapFiles(files).stream())
                .collect(Collectors.toList());
    }

}
