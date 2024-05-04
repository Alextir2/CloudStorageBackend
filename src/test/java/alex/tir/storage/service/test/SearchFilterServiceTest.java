package alex.tir.storage.service.test;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.dto.SearchDTO;
import alex.tir.storage.entity.File;
import alex.tir.storage.entity.Folder;
import alex.tir.storage.exception.RecordNotFoundException;
import alex.tir.storage.mapper.MetadataMapper;
import alex.tir.storage.mapper.SearchMapper;
import alex.tir.storage.repo.FileRepository;
import alex.tir.storage.repo.FolderRepository;
import alex.tir.storage.repo.UserRepository;
import alex.tir.storage.service.impl.SearchFilterServiceImpl;
import alex.tir.storage.util.FormBuilders;
import alex.tir.storage.util.ProjectionBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Example;

import java.time.Instant;
import java.util.List;

import static alex.tir.storage.util.EntityBuilders.*;
import static alex.tir.storage.util.FormBuilders.*;
import static alex.tir.storage.util.ProjectionBuilders.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SearchFilterServiceTest {

    @Mock
    private FolderRepository folderRepository;
    @Mock private FileRepository fileRepository;
    @Mock private UserRepository userRepository;
    @Mock private SearchMapper searchMapper;
    @Mock private MetadataMapper metadataMapper;

    @InjectMocks
    private SearchFilterServiceImpl searchService;

    @Captor
    private ArgumentCaptor<Example<File>> fileExampleCaptor;
    @Captor private ArgumentCaptor<Example<Folder>> folderExampleCaptor;

    @Test
    @SuppressWarnings("unchecked")
    public void findItems_whenMimeTypeNull_shouldReturnFoundItems() {
        long ownerId = 0L;
        SearchDTO searchForm = defaultSearchForm().mimeType(null).build();

        File fileProbe = defaultFile().id(1L).build();
        Folder folderProbe = defaultFolder().id(2L).build();

        List<File> foundFiles = List.of(
                defaultFile().id(3L).build(),
                defaultFile().id(4L).build());
        List<Folder> foundFolders = List.of(
                defaultFolder().id(5L).build(),
                defaultFolder().id(6L).build());

        List<Metadata> metadataList = List.of(
                ProjectionBuilders.defaultMetadata().type(Metadata.Type.FILE).id(7L).build(),
                ProjectionBuilders.defaultMetadata().type(Metadata.Type.FOLDER).id(8L).build());

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(searchMapper.mapToFile(searchForm, ownerId)).thenReturn(fileProbe);
        when(searchMapper.mapToFolder(searchForm, ownerId)).thenReturn(folderProbe);
        when(fileRepository.findAll(any(Example.class))).thenReturn(foundFiles);
        when(folderRepository.findAll(any(Example.class))).thenReturn(foundFolders);
        when(metadataMapper.mapItems(foundFolders, foundFiles)).thenReturn(metadataList);

        List<Metadata> returnedMetadataList = searchService.findItems(searchForm, ownerId);

        verify(fileRepository).findAll(fileExampleCaptor.capture());
        verify(folderRepository).findAll(folderExampleCaptor.capture());

        assertThat(fileExampleCaptor.getValue().getProbe(), equalTo(fileProbe));
        assertThat(folderExampleCaptor.getValue().getProbe(), equalTo(folderProbe));
        assertThat(returnedMetadataList, equalTo(metadataList));
    }

    @Test(expected = RecordNotFoundException.class)
    public void findItems_whenOwnerNotFound_shouldThrowException() {
        long ownerId = 0L;
        SearchDTO searchForm = FormBuilders.defaultSearchForm().build();
        when(userRepository.existsById(ownerId)).thenReturn(false);
        searchService.findItems(searchForm, ownerId);
    }

    @Test
    public void findRecentItems_whenOwnerFound_shouldReturnRecentItems() {
        long ownerId = 0L;
        Instant afterDate = Instant.EPOCH;

        List<File> foundFiles = List.of(
                defaultFile().id(1L).build(),
                defaultFile().id(2L).build());
        List<Folder> foundFolders = List.of(
                defaultFolder().id(3L).build(),
                defaultFolder().id(4L).build());

        List<Metadata> metadataList = List.of(
                defaultMetadata().type(Metadata.Type.FILE).id(5L).build(),
                defaultMetadata().type(Metadata.Type.FOLDER).id(6L).build());

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(fileRepository.findFilesByDateModifiedAfterAndOwnerId(afterDate, ownerId)).thenReturn(foundFiles);
        when(folderRepository.findFoldersByDateModifiedAfterAndOwnerId(afterDate, ownerId)).thenReturn(foundFolders);
        when(metadataMapper.mapItems(foundFolders, foundFiles)).thenReturn(metadataList);

        List<Metadata> returnedMetadataList = searchService.findRecentItems(afterDate, ownerId);

        assertThat(returnedMetadataList, equalTo(metadataList));
    }

    @Test(expected = RecordNotFoundException.class)
    public void findRecentItems_whenOwnerFound_shouldThrowException() {
        long ownerId = 0L;
        Instant afterDate = Instant.EPOCH;
        when(userRepository.existsById(ownerId)).thenReturn(false);
        searchService.findRecentItems(afterDate, ownerId);
    }

}

