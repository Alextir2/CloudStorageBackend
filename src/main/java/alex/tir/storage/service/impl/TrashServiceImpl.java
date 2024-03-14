package alex.tir.storage.service.impl;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.entity.File;
import alex.tir.storage.entity.Folder;
import alex.tir.storage.entity.User;
import alex.tir.storage.exception.RecordNotFoundException;
import alex.tir.storage.mapper.MetadataMapper;
import alex.tir.storage.repo.FileRepository;
import alex.tir.storage.repo.FolderRepository;
import alex.tir.storage.repo.UserRepository;
import alex.tir.storage.service.TrashService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrashServiceImpl implements TrashService {
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final MetadataMapper metadataMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Metadata> getTrashItems(Long ownerId) {
        if (userRepository.existsById(ownerId)) {
            List<Folder> foldersInTrash = folderRepository.findDisconnectedFoldersByOwnerId(ownerId);
            List<File> filesInTrash = fileRepository.findDisconnectedFilesByOwnerId(ownerId);
            return metadataMapper.mapItems(foldersInTrash, filesInTrash);
        } else {
            throw new RecordNotFoundException(User.class, ownerId);
        }
    }

    @Override
    @Transactional
    public void emptyTrash(Long ownerId) {
        if (userRepository.existsById(ownerId)) {
            List<Folder> foldersInTrash = folderRepository.findDisconnectedFoldersByOwnerId(ownerId);
            List<File> filesInTrash = fileRepository.findDisconnectedFilesByOwnerId(ownerId);

            List<File> filesToDelete = new ArrayList<>(filesInTrash);
            if (!foldersInTrash.isEmpty()) {
                Set<Long> folderIdSet = foldersInTrash.stream().map(Folder::getId).collect(Collectors.toSet());
                filesToDelete.addAll(fileRepository.deepFindAllFilesByParentIdIn(folderIdSet));
            }

            folderRepository.deleteAll(foldersInTrash);
            fileRepository.deleteAll(filesInTrash);
        } else {
            throw new RecordNotFoundException(User.class, ownerId);
        }
    }
}
