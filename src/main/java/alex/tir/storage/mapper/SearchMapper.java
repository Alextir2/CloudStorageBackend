package alex.tir.storage.mapper;

import alex.tir.storage.dto.SearchDTO;
import alex.tir.storage.entity.File;
import alex.tir.storage.entity.Folder;
import alex.tir.storage.entity.User;

public interface SearchMapper {
    Folder mapToFolder(SearchDTO searchDTO, Long userId);

    File mapToFile(SearchDTO searchDTO, Long userId);

    default User ownerIdToOwner(Long ownerId) {
        User owner = new User();
        owner.setId(ownerId);
        return owner;
    }

    default Folder parentIdToParent(Long parentId) {
        Folder parent = new Folder();
        parent.setId(parentId);
        return parent;
    }
}
