package alex.tir.storage.controller;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.dto.MetadataForm;
import alex.tir.storage.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Tag(name = "Файлы")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService service;

    @PostMapping(
            path = "/{parentId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Загрузка файла")
    public ResponseEntity<Metadata> uploadFile(
                @PathVariable("parentId") Long parentId,
                @RequestParam("file") MultipartFile file,
                UriComponentsBuilder uriComponentsBuilder) {
        MetadataForm metadataForm = new MetadataForm();
        metadataForm.setName(file.getOriginalFilename());
        metadataForm.setParentId(parentId);
        Metadata fileMetadata = service.saveFile(metadataForm, file.getResource());
        URI locationUri = uriComponentsBuilder
                .path("/api/files/{fileId}")
                .buildAndExpand(fileMetadata.getId())
                .toUri();
        return ResponseEntity.created(locationUri).body(fileMetadata);
    }

    @GetMapping(path = "/{fileId}/contents", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Скачать файл")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") Long fileId) {
        Resource fileResource = service.getFileContents(fileId);
        return getFileResponseEntity(fileResource);
    }

    @GetMapping(path = "/{fileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Получение информации о файле")
    public Metadata getFileMetadata(@PathVariable("fileId") Long fileId) {
        return service.getFileMetadata(fileId);
    }

    @GetMapping(path = "/{fileId}/link", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Создание ссылки на файл")
    public Map<String, String> getFileLink(
            @PathVariable("fileId") Long fileId,
            UriComponentsBuilder uriComponentsBuilder) {
        String accessToken = service.generateToken(fileId);
        String uriString = uriComponentsBuilder.path("/tl/{token}").buildAndExpand(accessToken).toUriString();
        return Map.of("link", uriString);
    }

    @Operation(summary = "Скачивание файла по ссылке (токену)")
    @GetMapping(path = "/{token}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadFileByLink(@PathVariable("token") String token) {
        Resource fileResource = service.getFileContents(token);
        return getFileResponseEntity(fileResource);
    }

    @PatchMapping(
            path = "/{fileId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Обновление информации о файле")
    public Metadata updateFileMetadata(
            @PathVariable("fileId") Long fileId,
            @RequestBody MetadataForm metadataForm) {
        return service.updateFileMetadata(fileId, metadataForm);
    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "Удаление файла")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFile(
            @PathVariable("fileId") Long fileId,
            @RequestParam(name = "permanent", required = false, defaultValue = "false") boolean permanent) {
        service.deleteFile(fileId, permanent);
    }
    private ResponseEntity<Resource> getFileResponseEntity(Resource fileResource) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }
}
