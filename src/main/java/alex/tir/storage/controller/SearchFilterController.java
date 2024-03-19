package alex.tir.storage.controller;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.dto.SearchDTO;
import alex.tir.storage.entity.UserPrincipal;
import alex.tir.storage.service.SearchFilterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Tag(name = "Поиск")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchFilterController {

    private final SearchFilterService service;

    @GetMapping("/")
    @Operation(summary = "Поиск")
    public List<Metadata> search(
            SearchDTO searchForm,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return service.findItems(searchForm, userPrincipal.getId());
    }

    @GetMapping("/date")
    @Operation(summary = "Поиск от даты (по умолчанию последние 7 дней)")
    public List<Metadata> findRecentItems(
            @RequestParam(name = "after", required = false) Instant date,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Instant defaultDate = date == null ? Instant.now().minus(7, ChronoUnit.DAYS) : date;
        return service.findRecentItems(defaultDate, userPrincipal.getId());
    }
}
