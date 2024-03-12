package alex.tir.storage.service;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.dto.MetadataForm;
import org.springframework.core.io.Resource;

public interface FileService {
    Metadata saveFile(MetadataForm metadataForm, Resource resource);

    Resource getFileContents(Long fileId);
}
