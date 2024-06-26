package alex.tir.storage.service.test;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.dto.MetadataForm;
import alex.tir.storage.entity.Folder;
import alex.tir.storage.entity.User;
import alex.tir.storage.exception.CircularFolderStructureException;
import alex.tir.storage.exception.RecordNotFoundException;
import alex.tir.storage.mapper.MetadataMapper;
import alex.tir.storage.repo.FileRepository;
import alex.tir.storage.repo.FolderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static alex.tir.storage.util.EntityBuilders.*;
import static alex.tir.storage.util.FormBuilders.defaultMetadataForm;
import static alex.tir.storage.util.ProjectionBuilders.defaultMetadata;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FolderServiceTest {

    @Mock
    private FolderRepository folderRepository;
    @Mock private MetadataMapper metadataMapper;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private FolderServiceImpl folderService;

    @Captor
    private ArgumentCaptor<Folder> savedFolderCaptor;


    @Test
    public void createFolder_whenParentFound_shouldCreateFolder() {
        MetadataForm metadataForm = defaultMetadataForm().build();
        User owner = defaultUser().build();
        Folder parent = defaultFolder().id(0L).owner(owner).build();
        Folder savedFolder = defaultFolder().id(1L).build();
        Metadata metadata = defaultMetadata().type(Metadata.Type.FOLDER).build();

        when(folderRepository.findById(metadataForm.getParentId())).thenReturn(Optional.of(parent));
        when(folderRepository.save(any())).thenReturn(savedFolder);
        when(metadataMapper.mapFolder(savedFolder)).thenReturn(metadata);

        Metadata returnedMetadata = folderService.createFolder(metadataForm);

        verify(folderRepository).save(savedFolderCaptor.capture());

        Folder createdFolder = savedFolderCaptor.getValue();
        assertThat(createdFolder.getName(), equalTo(metadataForm.getName()));
        assertThat(createdFolder.getRoot(), equalTo(false));
        assertThat(createdFolder.getParent(), equalTo(parent));
        assertThat(createdFolder.getOwner(), equalTo(owner));

        assertThat(returnedMetadata, equalTo(metadata));
    }

    @Test(expected = RecordNotFoundException.class)
    public void createFolder_whenParentNotFound_shouldThrowException() {
        MetadataForm metadataForm = defaultMetadataForm().build();
        when(folderRepository.findById(metadataForm.getParentId())).thenReturn(Optional.empty());
        folderService.createFolder(metadataForm);
    }

    @Test
    public void getFolderMetadata_whenFolderFound_shouldReturnMetadata() {
        long folderId = 0L;
        Folder folder = defaultFolder().id(folderId).build();
        Metadata metadata = defaultMetadata().type(Metadata.Type.FOLDER).build();

        when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));
        when(metadataMapper.mapFolder(folder)).thenReturn(metadata);

        Metadata returnedMetadata = folderService.getFolderMetadata(folderId);

        assertThat(returnedMetadata, equalTo(metadata));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getFolderMetadata_whenFolderNotFound_shouldThrowException() {
        long folderId = 0L;
        when(folderRepository.findById(folderId)).thenReturn(Optional.empty());
        folderService.getFolderMetadata(folderId);
    }

    @Test
    public void getFolderItems_whenFolderFound_shouldReturnItemsMetadata() {
        long folderId = 0L;
        Folder folder = defaultFolder()
                .id(folderId)
                .files(Set.of(defaultFile().id(1L).build()))
                .subFolders(Set.of(defaultFolder().id(2L).build()))
                .build();
        List<Metadata> metadataList = List.of(
                defaultMetadata().type(Metadata.Type.FOLDER).id(3L).build(),
                defaultMetadata().type(Metadata.Type.FILE).id(4L).build());

        when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));
        when(metadataMapper.mapItems(folder.getSubfolders(), folder.getFiles())).thenReturn(metadataList);

        List<Metadata> returnedMetadataList = folderService.getFolderItems(folderId);

        assertThat(returnedMetadataList, equalTo(metadataList));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getFolderItems_whenFolderNotFound_shouldThrowException() {
        long folderId = 0L;
        when(folderRepository.findById(folderId)).thenReturn(Optional.empty());
        folderService.getFolderItems(folderId);
    }

    @Test
    public void getRootFolderMetadata_whenOwnerFound_shouldReturnMetadata() {
        long ownerId = 0L;
        Folder root = defaultFolder()
                .id(1L)
                .root(true)
                .parent(null)
                .files(Set.of(defaultFile().id(2L).build()))
                .subFolders(Set.of(defaultFolder().id(3L).build()))
                .build();
        Metadata metadata = defaultMetadata().type(Metadata.Type.FOLDER).build();

        when(folderRepository.findRootFolderByOwnerId(ownerId)).thenReturn(Optional.of(root));
        when(metadataMapper.mapFolder(root)).thenReturn(metadata);

        Metadata returnedMetadata = folderService.getRootFolderMetadata(ownerId);

        assertThat(returnedMetadata, equalTo(metadata));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getRootFolderMetadata_whenOwnerNotFound_shouldThrowException() {
        long ownerId = 0L;
        when(folderRepository.findRootFolderByOwnerId(ownerId)).thenReturn(Optional.empty());
        folderService.getRootFolderMetadata(ownerId);
    }

    @Test
    public void getRootFolderItems_whenOwnerFound_shouldReturnItemsMetadata() {
        long ownerId = 0L;
        Folder root = defaultFolder()
                .id(1L)
                .root(true)
                .parent(null)
                .files(Set.of(defaultFile().id(2L).build()))
                .subFolders(Set.of(defaultFolder().id(3L).build()))
                .build();
        List<Metadata> metadataList = List.of(
                defaultMetadata().type(Metadata.Type.FOLDER).id(3L).build(),
                defaultMetadata().type(Metadata.Type.FILE).id(4L).build());

        when(folderRepository.findRootFolderByOwnerId(ownerId)).thenReturn(Optional.of(root));
        when(metadataMapper.mapItems(root.getSubfolders(), root.getFiles())).thenReturn(metadataList);

        List<Metadata> returnedMetadataList = folderService.getRootFolderItems(ownerId);

        assertThat(returnedMetadataList, equalTo(metadataList));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getRootFolderItems_whenOwnerNotFound_shouldThrowException() {
        long ownerId = 0L;
        when(folderRepository.findRootFolderByOwnerId(ownerId)).thenReturn(Optional.empty());
        folderService.getRootFolderItems(ownerId);
    }

    @Test
    public void updateFolderMetadata_whenFolderFoundAndParentFound_shouldUpdateMetadata() {
        long folderId = 0L;
        Folder folder = defaultFolder().id(folderId).build();
        MetadataForm metadataForm = defaultMetadataForm().parentId(1L).build();
        Folder newParent = defaultFolder().id(1L).build();
        Metadata metadata = defaultMetadata().type(Metadata.Type.FOLDER).build();

        when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));
        when(folderRepository.findById(metadataForm.getParentId())).thenReturn(Optional.of(newParent));
        when(folderRepository.deepFindAllSubfoldersByParentIdIn(Set.of(folderId))).thenReturn(Collections.emptyList());
        when(metadataMapper.mapFolder(folder)).thenReturn(metadata);

        Metadata returnedMetadata = folderService.updateFolderMetadata(folderId, metadataForm);

        assertThat(folder.getName(), equalTo(metadataForm.getName()));
        assertThat(folder.getParent(), equalTo(newParent));

        assertThat(returnedMetadata, equalTo(metadata));
    }

    @Test(expected = RecordNotFoundException.class)
    public void updateFolderMetadata_whenFolderNotFound_shouldThrowException() {
        long folderId = 0L;
        MetadataForm metadataForm = defaultMetadataForm().parentId(1L).build();

        when(folderRepository.findById(folderId)).thenReturn(Optional.empty());

        folderService.updateFolderMetadata(folderId, metadataForm);
    }

    @Test(expected = RecordNotFoundException.class)
    public void updateFolderMetadata_whenParentNotFound_shouldThrowException() {
        long folderId = 0L;
        Folder folder = defaultFolder().id(folderId).build();
        MetadataForm metadataForm = defaultMetadataForm().parentId(1L).build();

        when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));
        when(folderRepository.findById(metadataForm.getParentId())).thenReturn(Optional.empty());

        folderService.updateFolderMetadata(folderId, metadataForm);
    }

    @Test(expected = CircularFolderStructureException.class)
    public void updateFolderMetadata_whenNewParentIsChild_shouldThrowException() {
        long folderId = 0L;
        Folder folder = defaultFolder().id(folderId).build();
        MetadataForm metadataForm = defaultMetadataForm().parentId(1L).build();
        Folder newParent = defaultFolder().id(2L).build();

        when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));
        when(folderRepository.findById(metadataForm.getParentId())).thenReturn(Optional.of(newParent));
        when(folderRepository.deepFindAllSubfoldersByParentIdIn(Set.of(folderId))).thenReturn(List.of(newParent));

        folderService.updateFolderMetadata(folderId, metadataForm);
    }

    @Test
    public void deleteFolder_whenFolderFoundAndNotPermanent_shouldDisconnectFolder() {
        long folderId = 0L;
        Folder folder = defaultFolder().id(folderId).build();
        Folder parent = defaultFolder().id(1L).build();
        folder.setParent(parent);
        parent.getSubfolders().add(folder);

        when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));

        folderService.deleteFolder(folderId, false);

        verify(folderRepository).findById(folderId);
        verifyNoMoreInteractions(folderRepository, eventPublisher);

        assertThat(folder.getParent(), is(nullValue()));
        assertThat(parent.getSubfolders(), not(hasItem(folder)));
    }

    @Test(expected = RecordNotFoundException.class)
    public void deleteFolder_whenFolderNotFoundAndNotPermanent_shouldThrowException() {
        long folderId = 0L;
        when(folderRepository.findById(folderId)).thenReturn(Optional.empty());
        folderService.deleteFolder(folderId, false);
    }

    @Test(expected = RecordNotFoundException.class)
    public void deleteFolder_whenFolderNotFoundAndPermanent_shouldThrowException() {
        long folderId = 0L;
        when(folderRepository.findById(folderId)).thenReturn(Optional.empty());
        folderService.deleteFolder(folderId, true);
    }

}

