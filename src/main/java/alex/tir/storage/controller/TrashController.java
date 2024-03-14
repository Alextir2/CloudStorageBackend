package alex.tir.storage.controller;

import alex.tir.storage.entity.UserPrincipal;
import alex.tir.storage.service.TrashService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import alex.tir.storage.dto.Metadata;

import java.util.List;

@Tag(name = "Корзина")
@RestController
@RequestMapping("/api/trash")
@RequiredArgsConstructor
public class TrashController {
    private final TrashService service;

    @GetMapping(path = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Получение элементов корзины")
    public List<Metadata> getTrashItems(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return service.getTrashItems(userPrincipal.getId());
    }

    @DeleteMapping("/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Очистить корзину")
    public void emptyTrash(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        service.emptyTrash(userPrincipal.getId());
    }
}
