package alex.tir.storage.service.impl;

import alex.tir.storage.config.FilesProperties;
import alex.tir.storage.dto.Metadata;
import alex.tir.storage.dto.MetadataForm;
import alex.tir.storage.entity.File;
import alex.tir.storage.entity.Folder;
import alex.tir.storage.entity.User;
import alex.tir.storage.exception.FileException;
import alex.tir.storage.exception.RecordNotFoundException;
import alex.tir.storage.exception.StorageLimitExceededException;
import alex.tir.storage.mapper.MetadataMapper;
import alex.tir.storage.repo.FileRepository;
import alex.tir.storage.repo.FolderRepository;
import alex.tir.storage.service.FileService;
import alex.tir.storage.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private final MetadataMapper metadataMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final FilesProperties properties;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Metadata saveFile(MetadataForm metadataForm, Resource fileResource) {
        try {
            Folder parent = getParent(metadataForm.getParentId());
            assertLimitIsNotExceeded(fileResource.contentLength(), parent.getOwner());

            File file = new File();
            file.setName(metadataForm.getName());
            file.setSize(fileResource.contentLength());
            file.setMimeType(FileUtils.detectContentType(file.getName(), fileResource));
            file.setLocation(generateLocation());
            file.setParent(parent);
            file.setOwner(parent.getOwner());


            FileUtils.saveFile(fileResource, properties.getBaseFolder() + file.getLocation());
            file = fileRepository.save(file);
            return metadataMapper.mapFile(file);
        } catch (IOException exception) {
            throw new FileException(exception);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FileSystemResource getFileContents(Long fileId) {
        File file = getFile(fileId);
        return new FileSystemResource(properties.getBaseFolder() + file.getLocation()) {
            @Override
            public String getFilename() {
                return file.getName();
            }
        };
    }

    private static String generateLocation() {
        String uuidFileName = UUID.randomUUID().toString().replace("-", "");
        String separator = FileSystems.getDefault().getSeparator();
        return new StringBuilder()
                .append(separator)
                .append(uuidFileName, 0, 2)
                .append(separator)
                .append(uuidFileName, 2, 4)
                .append(separator)
                .append(uuidFileName, 4, 6)
                .toString();
    }

    private Folder getParent(Long parentId) {
        return folderRepository
                .findById(parentId)
                .orElseThrow(() -> new RecordNotFoundException(Folder.class, parentId));
    }

    private void assertLimitIsNotExceeded(long newFileSize, User owner) {
        long usedSpace = fileRepository.calculateTotalFileSizeByOwnerId(owner.getId());
        Long baseLimit = properties.getBaseLimit();
        if (usedSpace + newFileSize > baseLimit) {
            throw new StorageLimitExceededException("Maximum storage limit " + baseLimit + " is exceeded");
        }
    }

    private File getFile(Long fileId) {
        return fileRepository
                .findById(fileId)
                .orElseThrow(() -> new RecordNotFoundException(File.class, fileId));
    }
}
