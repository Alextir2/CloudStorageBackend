package alex.tir.storage.mapper;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.entity.File;
import alex.tir.storage.entity.Folder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public interface MetadataMapper {

    Metadata mapFolder(Folder folder);

    List<Metadata> mapFolders(Iterable<Folder> folders);

    Metadata mapFile(File file);

    List<Metadata> mapFiles(Iterable<File> files);
}
