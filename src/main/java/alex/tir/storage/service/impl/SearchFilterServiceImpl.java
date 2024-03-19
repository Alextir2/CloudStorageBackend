package alex.tir.storage.service.impl;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.dto.SearchDTO;
import alex.tir.storage.entity.File;
import alex.tir.storage.entity.Folder;
import alex.tir.storage.entity.User;
import alex.tir.storage.exception.RecordNotFoundException;
import alex.tir.storage.mapper.MetadataMapper;
import alex.tir.storage.mapper.SearchMapper;
import alex.tir.storage.repo.FileRepository;
import alex.tir.storage.repo.FolderRepository;
import alex.tir.storage.repo.UserRepository;
import alex.tir.storage.service.SearchFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchFilterServiceImpl implements SearchFilterService {
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final SearchMapper searchMapper;
    private final MetadataMapper metadataMapper;

    @Override
    public List<Metadata> findItems(SearchDTO searchForm, Long ownerId) {
        if (userRepository.existsById(ownerId)) {
            File fileProbe = searchMapper.mapToFile(searchForm, ownerId);
            List<File> foundFiles = fileRepository.findAll(Example.of(fileProbe, getMatcher()));
            if (searchForm.getMimeType() != null) {
                return metadataMapper.mapFiles(foundFiles);
            } else {
                Folder folderProbe = searchMapper.mapToFolder(searchForm, ownerId);
                List<Folder> foundFolders = folderRepository.findAll(Example.of(folderProbe, getMatcher()));
                return metadataMapper.mapItems(foundFolders, foundFiles);
            }
        } else {
            throw new RecordNotFoundException(User.class, ownerId);
        }
    }

    @Override
    public List<Metadata> findRecentItems(Instant afterDate, Long ownerId) {
        if (userRepository.existsById(ownerId)) {
            List<Folder> recentFolders = folderRepository.findFoldersByDateModifiedAfterAndOwnerId(afterDate, ownerId);
            List<File> recentFiles = fileRepository.findFilesByDateModifiedAfterAndOwnerId(afterDate, ownerId);
            return metadataMapper.mapItems(recentFolders, recentFiles);
        } else {
            throw new RecordNotFoundException(User.class, ownerId);
        }
    }

    private ExampleMatcher getMatcher() {
        return ExampleMatcher
                .matchingAll()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
    }
}
