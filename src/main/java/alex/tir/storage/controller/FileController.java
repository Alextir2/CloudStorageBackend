package alex.tir.storage.controller;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.dto.MetadataForm;
import alex.tir.storage.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Tag(name = "Файлы")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService service;

    @PostMapping(
            path = "/",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Загрузка файла")
    public ResponseEntity<Metadata> uploadFile(
                MetadataForm metadataForm,
                @RequestParam("file") MultipartFile file,
                UriComponentsBuilder uriComponentsBuilder) {
        Metadata fileMetadata = service.saveFile(metadataForm, file.getResource());
        URI locationUri = uriComponentsBuilder
                .path("/api/files/{fileId}")
                .buildAndExpand(fileMetadata.getId())
                .toUri();
        return ResponseEntity.created(locationUri).body(fileMetadata);
    }


}
