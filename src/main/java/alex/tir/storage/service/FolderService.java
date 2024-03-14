package alex.tir.storage.service;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.dto.MetadataForm;

import java.util.List;

public interface FolderService {
    Metadata createFolder(MetadataForm metadataForm);

    Metadata getFolderMetadata(Long folderId);

    List<Metadata> getFolderItems(Long folderId);

    Metadata getRootFolderMetadata(Long id);

    List<Metadata> getRootFolderItems(Long id);

    Metadata updateFolderMetadata(Long folderId, MetadataForm metadataForm);

    void deleteFolder(Long folderId, boolean permanent);
}
