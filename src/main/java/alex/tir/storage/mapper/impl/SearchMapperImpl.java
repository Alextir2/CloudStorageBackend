package alex.tir.storage.mapper.impl;

import alex.tir.storage.dto.SearchDTO;
import alex.tir.storage.entity.File;
import alex.tir.storage.entity.Folder;
import alex.tir.storage.mapper.SearchMapper;
import org.springframework.stereotype.Component;

@Component
public class SearchMapperImpl implements SearchMapper {
    @Override
    public Folder mapToFolder(SearchDTO searchDTO, Long userId) {
        if (searchDTO == null && userId == null){
            return null;
        }
        Folder folder = new Folder();
        if (searchDTO != null){
            folder.setName(searchDTO.getName());
            folder.setParent(parentIdToParent(searchDTO.getParentId()));
        }
        if (userId != null) {
            folder.setOwner(ownerIdToOwner(userId));
        }
        return folder;
    }

    @Override
    public File mapToFile(SearchDTO searchDTO, Long userId) {
        if (searchDTO == null && userId == null) {
            return null;
        }
        File file = new File();
        if (searchDTO != null){
            file.setName(searchDTO.getName());
            file.setParent(parentIdToParent(searchDTO.getParentId()));
            file.setMimeType(searchDTO.getMimeType());
        }
        if (userId != null ){
            file.setOwner(ownerIdToOwner(userId));
        }
        return file;
    }
}
