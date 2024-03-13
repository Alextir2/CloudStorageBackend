package alex.tir.storage.service;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.dto.MetadataForm;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface FileService {
    Metadata saveFile(MetadataForm metadataForm, Resource resource);

    Resource getFileContents(Long fileId);

    Metadata getFileMetadata(Long fileId);

    String generateToken(Long fileId);
    @PreAuthorize("permitAll()")
    Resource getFileContents(String fileAccessToken);

    Metadata updateFileMetadata(Long fileId, MetadataForm metadataForm);

    void deleteFile(Long fileId, boolean permanent);
}
