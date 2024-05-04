package alex.tir.storage.service.test;

import alex.tir.storage.config.FilesProperties;
import alex.tir.storage.dto.Metadata;
import alex.tir.storage.dto.MetadataForm;
import alex.tir.storage.entity.File;
import alex.tir.storage.entity.Folder;
import alex.tir.storage.entity.User;
import alex.tir.storage.exception.ExpiredFileTokenException;
import alex.tir.storage.exception.RecordNotFoundException;
import alex.tir.storage.exception.StorageLimitExceededException;
import alex.tir.storage.mapper.MetadataMapper;
import alex.tir.storage.repo.FileRepository;
import alex.tir.storage.repo.FolderRepository;
import alex.tir.storage.util.JWTUtils;
import org.apache.tika.mime.MimeTypes;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

import static alex.tir.storage.util.EntityBuilders.*;
import static alex.tir.storage.util.FormBuilders.defaultMetadataForm;
import static alex.tir.storage.util.ProjectionBuilders.defaultMetadata;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FileServiceTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private FolderRepository folderRepository;
    @Mock
    private FileRepository fileRepository;
    @Mock
    private MetadataMapper metadataMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private FilesProperties properties;

    @InjectMocks
    private FileServiceImpl fileService;

    @Captor
    private ArgumentCaptor<File> savedFileCaptor;


    @Test
    public void saveFile_whenParentFoundAndLimitIsNotExceeded_shouldSaveFile() throws IOException {
        byte[] fileContents = new byte[32];
        new Random().nextBytes(fileContents);

        MetadataForm metadataForm = defaultMetadataForm().name("test.txt").build();
        ByteArrayResource fileResource = new ByteArrayResource(fileContents);
        User owner = defaultUser().build();
        Folder parent = defaultFolder().owner(owner).build();
        File savedFile = defaultFile().build();
        Metadata metadata = defaultMetadata().build();

        when(folderRepository.findById(metadataForm.getParentId())).thenReturn(Optional.of(parent));
        when(fileRepository.calculateTotalFileSizeByOwnerId(owner.getId())).thenReturn(0L);
        when(properties.getBaseLimit()).thenReturn(Long.MAX_VALUE);
        when(properties.getBaseFolder()).thenReturn(temporaryFolder.getRoot().getPath());
        when(fileRepository.save(any())).thenReturn(savedFile);
        when(metadataMapper.mapFile(savedFile)).thenReturn(metadata);

        Metadata returnedMetadata = fileService.saveFile(metadataForm, fileResource);

        verify(fileRepository).save(savedFileCaptor.capture());

        File createdFile = savedFileCaptor.getValue();
        assertThat(createdFile.getName(), equalTo(metadataForm.getName()));
        assertThat(createdFile.getSize(), equalTo(Long.valueOf(fileContents.length)));
        assertThat(createdFile.getMimeType(), equalTo(MimeTypes.PLAIN_TEXT));
        assertThat(createdFile.getLocation(), is(notNullValue()));
        assertThat(createdFile.getParent(), equalTo(parent));
        assertThat(createdFile.getOwner(), equalTo(owner));

        Path createdFilePath = Paths.get(temporaryFolder.getRoot().getPath() + createdFile.getLocation());
        assertThat(Files.exists(createdFilePath), equalTo(true));
        assertThat(Files.readAllBytes(createdFilePath), equalTo(fileContents));

        assertThat(returnedMetadata, equalTo(metadata));
    }

    @Test
    public void saveFile_whenParentNotFound_shouldThrowException() {
        MetadataForm metadataForm = defaultMetadataForm().build();
        Resource fileResource = new ByteArrayResource(new byte[32]);

        when(folderRepository.findById(metadataForm.getParentId())).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> fileService.saveFile(metadataForm, fileResource));
    }

    @Test
    public void saveFile_whenLimitIsExceeded_shouldThrowException() {
        MetadataForm metadataForm = defaultMetadataForm().build();
        Resource fileResource = new ByteArrayResource(new byte[32]);
        User owner = defaultUser().build();
        Folder parent = defaultFolder().owner(owner).build();

        when(folderRepository.findById(metadataForm.getParentId())).thenReturn(Optional.of(parent));
        when(fileRepository.calculateTotalFileSizeByOwnerId(owner.getId())).thenReturn(0L);
        when(properties.getBaseLimit()).thenReturn(Long.MIN_VALUE);

        assertThrows(StorageLimitExceededException.class, () -> fileService.saveFile(metadataForm, fileResource));
    }

    @Test
    public void generateFileAccessToken_whenFileFound_shouldReturnJwt() {
        long fileId = 0L;
        long validitySeconds = 60L;
        String secret = "super secret";

        when(fileRepository.existsById(fileId)).thenReturn(true);
        when(properties.getFileTokenValidity()).thenReturn(validitySeconds);
        when(properties.getFileTokenSecret()).thenReturn(secret);

        String token = fileService.generateToken(fileId);

        String subject = JWTUtils.verifyToken(token, secret);
        assertThat(Long.valueOf(subject), equalTo(fileId));
    }

    @Test
    public void generateFileAccessToken_whenFileNotFound_shouldThrowException() {
        long fileId = 0L;
        when(fileRepository.existsById(fileId)).thenReturn(false);
        assertThrows(RecordNotFoundException.class, () -> fileService.generateToken(fileId));
    }

    @Test
    public void getFileMetadata_whenFileFound_shouldReturnMetadata() {
        long fileId = 0L;
        File file = defaultFile().id(fileId).build();
        Metadata metadata = defaultMetadata().build();

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
        when(metadataMapper.mapFile(file)).thenReturn(metadata);

        Metadata returnedMetadata = fileService.getFileMetadata(fileId);

        assertThat(returnedMetadata, equalTo(metadata));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getFileMetadata_whenFileNotFound_shouldThrowException() {
        long fileId = 0L;
        when(fileRepository.findById(fileId)).thenReturn(Optional.empty());
        fileService.getFileMetadata(fileId);
    }

    @Test
    public void getFileContents_whenFileFound_shouldReturnFileSystemResource() {
        long fileId = 0L;
        File file = defaultFile().id(fileId).build();
        String baseFolder = "/base";

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
        when(properties.getBaseFolder()).thenReturn(baseFolder);

        FileSystemResource fileResource = fileService.getFileContents(fileId);

        assertThat(fileResource.getPath(), equalTo(baseFolder + file.getLocation()));
        assertThat(fileResource.getFilename(), equalTo(file.getName()));
    }

    @Test(expected = RecordNotFoundException.class)
    public void getFileContents_whenFileNotFound_shouldThrowException() {
        long fileId = 0L;
        when(fileRepository.findById(fileId)).thenReturn(Optional.empty());
        fileService.getFileContents(fileId);
    }

    @Test
    public void getFileContents_whenTokenIsValid_shouldReturnFileSystemResource() {
        long fileId = 0L;
        File file = defaultFile().id(fileId).build();
        String baseFolder = "/base";
        Instant expiration = Instant.now().plus(1, ChronoUnit.HOURS);
        String secret = "super secret";
        String token = JWTUtils.generateToken(String.valueOf(fileId), expiration, secret);

        when(properties.getBaseFolder()).thenReturn(baseFolder);
        when(properties.getFileTokenSecret()).thenReturn(secret);
        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));

        FileSystemResource fileResource = fileService.getFileContents(token);

        assertThat(fileResource.getPath(), equalTo(baseFolder + file.getLocation()));
        assertThat(fileResource.getFilename(), equalTo(file.getName()));
    }

    @Test(expected = ExpiredFileTokenException.class)
    public void getFileContents_whenTokenExpired_shouldThrowException() {
        String subject = "subject";
        Instant expiration = Instant.now().minus(1, ChronoUnit.HOURS);
        String secret = "super secret";
        String token = JWTUtils.generateToken(subject, expiration, secret);

        when(properties.getFileTokenSecret()).thenReturn(secret);

        fileService.getFileContents(token);
    }

    @Test(expected = RecordNotFoundException.class)
    public void getFileContents_whenTokenIsNotValid_shouldThrowException() {
        when(properties.getFileTokenSecret()).thenReturn("super secret");
        fileService.getFileContents("invalid token");
    }

    @Test
    public void updateFileMetadata_whenFileFoundAndParentFound_shouldUpdateMetadata() {
        long fileId = 0L;
        File file = defaultFile().id(fileId).build();
        MetadataForm metadataForm = defaultMetadataForm().build();
        Folder newParent = defaultFolder().build();
        Metadata metadata = defaultMetadata().build();

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
        when(folderRepository.findById(metadataForm.getParentId())).thenReturn(Optional.of(newParent));
        when(metadataMapper.mapFile(file)).thenReturn(metadata);

        Metadata returnedMetadata = fileService.updateFileMetadata(fileId, metadataForm);

        assertThat(file.getName(), equalTo(metadataForm.getName()));
        assertThat(file.getParent(), equalTo(newParent));

        assertThat(returnedMetadata, equalTo(metadata));
    }

    @Test(expected = RecordNotFoundException.class)
    public void updateFileMetadata_whenFileNotFound_shouldThrowException() {
        long fileId = 0L;
        MetadataForm metadataForm = defaultMetadataForm().build();

        when(fileRepository.findById(fileId)).thenReturn(Optional.empty());

        fileService.updateFileMetadata(fileId, metadataForm);
    }

    @Test(expected = RecordNotFoundException.class)
    public void updateFileMetadata_whenParentNotFound_shouldThrowException() {
        long fileId = 0L;
        File file = defaultFile().id(fileId).build();
        MetadataForm metadataForm = defaultMetadataForm().build();

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
        when(folderRepository.findById(metadataForm.getParentId())).thenReturn(Optional.empty());

        fileService.updateFileMetadata(fileId, metadataForm);
    }

    @Test
    public void deleteFile_whenFileFoundAndNotPermanent_shouldDisconnectFile() {
        long fileId = 0L;
        File file = defaultFile().id(fileId).build();
        Folder parent = defaultFolder().build();
        file.setParent(parent);
        parent.getFiles().add(file);

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));

        fileService.deleteFile(fileId, false);

        verify(fileRepository).findById(fileId);
        verifyNoMoreInteractions(fileRepository, eventPublisher);

        assertThat(file.getParent(), is(nullValue()));
        assertThat(parent.getFiles(), not(hasItem(file)));
    }

    @Test(expected = RecordNotFoundException.class)
    public void deleteFile_whenFileNotFoundAndNotPermanent_shouldThrowException() {
        long fileId = 0L;
        when(fileRepository.findById(fileId)).thenReturn(Optional.empty());
        fileService.deleteFile(fileId, false);
    }


    @Test(expected = RecordNotFoundException.class)
    public void deleteFile_whenFileNotFoundAndPermanent_shouldThrowException() {
        long fileId = 0L;
        when(fileRepository.findById(fileId)).thenReturn(Optional.empty());
        fileService.deleteFile(fileId, true);
    }

}

