package alex.tir.storage.mapper.impl;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.entity.File;
import alex.tir.storage.entity.Folder;
import alex.tir.storage.entity.User;
import alex.tir.storage.mapper.MetadataMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MetadataMapperImpl implements MetadataMapper {
    @Override
    public Metadata mapFolder(Folder folder) {
        if (folder == null){
            return null;
        }
        Metadata metadata = new Metadata();

        metadata.setOwnerId(folderOwnerId(folder));
        metadata.setParentId(folderParentId(folder));
        metadata.setId(folder.getId());
        metadata.setName(folder.getName());
        metadata.setDateCreated(folder.getDateCreated());
        metadata.setDateModified(folder.getDateModified());
        metadata.setRoot(folder.getRoot());
        metadata.setType(Metadata.Type.FOLDER);
        return metadata;
    }

    @Override
    public List<Metadata> mapFolders(Iterable<Folder> folders) {
        if (folders == null){
            return null;
        }
        List<Metadata> list = new ArrayList<>();
        for (Folder folder : folders) {
            list.add(mapFolder(folder));
        }
        return list;
    }

    @Override
    public Metadata mapFile(File file) {
        if (file == null){
            return null;
        }
        Metadata metadata = new Metadata();

        metadata.setOwnerId(fileOwnerId(file));
        metadata.setParentId(fileParentId(file));
        metadata.setId(file.getId());
        metadata.setName(file.getName());
        metadata.setDateCreated(file.getDateCreated());
        metadata.setDateModified(file.getDateModified());
        metadata.setSize(file.getSize());
        metadata.setMimeType(file.getMimeType());
        metadata.setType(Metadata.Type.FILE);
        return metadata;
    }

    @Override
    public List<Metadata> mapFiles(Iterable<File> files) {
        if (files == null){
            return null;
        }
        List<Metadata> list = new ArrayList<>();
        for (File file : files){
            list.add(mapFile(file));
        }
        return list;
    }
    private Long folderOwnerId(Folder folder) {
        if (folder == null){
            return null;
        }
        User owner = folder.getOwner();
        if (owner == null){
            return null;
        }
        Long id = owner.getId();
        if (id == null) {
            return null;
        }
        return id;
    }

    private Long folderParentId(Folder folder) {
        if (folder == null){
            return null;
        }
        Folder parent = folder.getParent();
        if (parent == null){
            return null;
        }
        Long id = parent.getId();
        if (id == null){
            return null;
        }
        return id;
    }

    private Long fileOwnerId(File file) {
        if (file == null){
            return null;
        }
        User owner = file.getOwner();
        if (owner == null){
            return null;
        }
        Long id = owner.getId();
        if (id == null){
            return null;
        }
        return id;
    }

    private Long fileParentId(File file) {
        if (file == null){
            return null;
        }
        Folder parent = file.getParent();
        if (parent == null){
            return null;
        }
        Long id = parent.getId();
        if (id == null){
            return null;
        }
        return id;
    }
}
