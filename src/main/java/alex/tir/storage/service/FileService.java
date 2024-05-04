package alex.tir.storage.service;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.dto.MetadataForm;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface FileService {
    Metadata saveFile(MetadataForm metadataForm, Resource resource);

    FileSystemResource getFileContents(Long fileId);

    Metadata getFileMetadata(Long fileId);

    String generateToken(Long fileId);
    @PreAuthorize("permitAll()")
    FileSystemResource getFileContents(String fileAccessToken);

    Metadata updateFileMetadata(Long fileId, MetadataForm metadataForm);

    void deleteFile(Long fileId, boolean permanent);
}
