package alex.tir.storage.service.test;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.entity.File;
import alex.tir.storage.entity.Folder;
import alex.tir.storage.exception.RecordNotFoundException;
import alex.tir.storage.mapper.MetadataMapper;
import alex.tir.storage.repo.FileRepository;
import alex.tir.storage.repo.FolderRepository;
import alex.tir.storage.repo.UserRepository;
import alex.tir.storage.service.impl.TrashServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static alex.tir.storage.util.EntityBuilders.*;
import static alex.tir.storage.util.EntityBuilders.*;
import static alex.tir.storage.util.ProjectionBuilders.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TrashServiceTest {

    @Mock
    private FolderRepository folderRepository;
    @Mock private FileRepository fileRepository;
    @Mock private UserRepository userRepository;
    @Mock private MetadataMapper metadataMapper;

    @InjectMocks
    private TrashServiceImpl trashService;


    @Test
    public void getTrashItems_whenOwnerFound_shouldReturnTrashItems() {
        long ownerId = 0L;
        List<Folder> foldersInTrash = List.of(
                defaultFolder().id(1L).build(),
                defaultFolder().id(2L).build());
        List<File> filesInTrash = List.of(
                defaultFile().id(3L).build(),
                defaultFile().id(4L).build());
        List<Metadata> metadataList = List.of(
                defaultMetadata().type(Metadata.Type.FOLDER).id(5L).build(),
                defaultMetadata().type(Metadata.Type.FILE).id(6L).build());

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(folderRepository.findDisconnectedFoldersByOwnerId(ownerId)).thenReturn(foldersInTrash);
        when(fileRepository.findDisconnectedFilesByOwnerId(ownerId)).thenReturn(filesInTrash);
        when(metadataMapper.mapItems(foldersInTrash, filesInTrash)).thenReturn(metadataList);

        List<Metadata> returnedMetadataList = trashService.getTrashItems(ownerId);

        assertThat(returnedMetadataList, equalTo(metadataList));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getTrashItems_whenOwnerNotFound_shouldThrowException() {
        long ownerId = 0L;
        when(userRepository.existsById(ownerId)).thenReturn(false);
        trashService.getTrashItems(ownerId);
    }

    @Test(expected = RecordNotFoundException.class)
    public void emptyTrash_whenOwnerNotFound_shouldThrowException() {
        long ownerId = 0L;
        when(userRepository.existsById(ownerId)).thenReturn(false);
        trashService.emptyTrash(ownerId);
    }

}
