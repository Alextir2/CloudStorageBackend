package alex.tir.storage.controller;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.dto.MetadataForm;
import alex.tir.storage.entity.UserPrincipal;
import alex.tir.storage.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Папки")
@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService service;

    @PostMapping(path = "/",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание папки")
    public ResponseEntity<Metadata> createFolder(
            @RequestBody MetadataForm metadataForm,
            UriComponentsBuilder uriComponentsBuilder) {
        Metadata folderMetadata = service.createFolder(metadataForm);
        URI locationUri = uriComponentsBuilder
                .path("/api/folders/{folderId}")
                .buildAndExpand(folderMetadata.getId())
                .toUri();
        return ResponseEntity.created(locationUri).body(folderMetadata);
    }

    @GetMapping(path = "/{folderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Получение данных о папке")
    public Metadata getFolderMetadata(@PathVariable("folderId") Long folderId) {
        return service.getFolderMetadata(folderId);
    }

    @GetMapping(path = "/{folderId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Получение элементов папки")
    public List<Metadata> getFolderItems(@PathVariable("folderId") Long folderId) {
        return service.getFolderItems(folderId);
    }

    @GetMapping(path = "/root", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Получение данных о корневой папке текущего пользователя")
    public Metadata getRootFolderMetadata(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return service.getRootFolderMetadata(userPrincipal.getId());
    }

    @GetMapping(path = "/root/items", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Получение элементов корней папки текущего пользователя")
    public List<Metadata> getRootFolderItems(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return service.getRootFolderItems(userPrincipal.getId());
    }

    @PatchMapping(
            path = "/{folderId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Изменение данных папки")
    public Metadata updateFolderMetadata(
            @PathVariable("folderId") Long folderId,
            @RequestBody MetadataForm metadataForm) {
        return service.updateFolderMetadata(folderId, metadataForm);
    }

    @DeleteMapping("/{folderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление папки")
    public void deleteFolder(
            @PathVariable("folderId") Long folderId,
            @RequestParam(name = "permanent", required = false, defaultValue = "false") boolean permanent) {
        service.deleteFolder(folderId, permanent);
    }
}
