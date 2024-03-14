package alex.tir.storage.service.impl;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.dto.MetadataForm;
import alex.tir.storage.entity.File;
import alex.tir.storage.entity.Folder;
import alex.tir.storage.entity.User;
import alex.tir.storage.exception.CircularFolderStructureException;
import alex.tir.storage.exception.RecordNotFoundException;
import alex.tir.storage.mapper.MetadataMapper;
import alex.tir.storage.repo.FileRepository;
import alex.tir.storage.repo.FolderRepository;
import alex.tir.storage.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;
    private final MetadataMapper metadataMapper;

    @Override
    @Transactional
    public Metadata createFolder(MetadataForm metadataForm) {
        Folder folder = new Folder();
        folder.setName(metadataForm.getName());
        folder.setRoot(false);
        folder.setParent(getFolder(metadataForm.getParentId()));
        folder.setOwner(folder.getParent().getOwner());
        folder = folderRepository.save(folder);
        return metadataMapper.mapFolder(folder);
    }

    @Override
    @Transactional(readOnly = true)
    public Metadata getFolderMetadata(Long folderId) {
        Folder folder = getFolder(folderId);
        return metadataMapper.mapFolder(folder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Metadata> getFolderItems(Long folderId) {
        Folder folder = getFolder(folderId);
        return metadataMapper.mapItems(folder.getSubfolders(), folder.getFiles());
    }

    @Override
    @Transactional(readOnly = true)
    public Metadata getRootFolderMetadata(Long ownerId) {
        Folder rootFolder = getRoot(ownerId);
        return metadataMapper.mapFolder(rootFolder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Metadata> getRootFolderItems(Long ownerId) {
        Folder rootFolder = getRoot(ownerId);
        return metadataMapper.mapItems(rootFolder.getSubfolders(), rootFolder.getFiles());
    }

    @Override
    @Transactional
    public Metadata updateFolderMetadata(Long folderId, MetadataForm metadataForm) {
        Folder folder = getFolder(folderId);
        if (metadataForm.getParentId() != null) {
            Folder newParent = getFolder(metadataForm.getParentId());
            assertCircularReferenceNotOccurs(folder, newParent);
            folder.setParent(newParent);
        }
        if (metadataForm.getName() != null) {
            folder.setName(metadataForm.getName());
        }
        return metadataMapper.mapFolder(folder);
    }

    @Override
    @Transactional
    public void deleteFolder(Long folderId, boolean permanent) {
        Folder folder = getFolder(folderId);
        if (permanent) folderRepository.delete(folder);
            else if (folder.getParent() != null) {
                folder.getParent().getSubfolders().remove(folder);
                folder.setParent(null);
        }
    }

    private Folder getRoot(Long ownerId) {
        return folderRepository
                .findRootFolderByOwnerId(ownerId)
                .orElseThrow(() -> new RecordNotFoundException(User.class, ownerId));
    }

    private void assertCircularReferenceNotOccurs(Folder folder, Folder newParent) {
        List<Folder> subfolders = folderRepository.deepFindAllSubfoldersByParentIdIn(Set.of(folder.getId()));
        for (Folder subfolder : subfolders) {
            if (subfolder.getId().equals(newParent.getId())) {
                throw new CircularFolderStructureException();
            }
        }
    }

    private Folder getFolder(Long folderId) {
        return folderRepository
                .findById(folderId)
                .orElseThrow(() -> new RecordNotFoundException(Folder.class, folderId));
    }
}
