package alex.tir.storage.service;

import alex.tir.storage.dto.Metadata;
import alex.tir.storage.dto.SearchDTO;

import java.time.Instant;
import java.util.List;

public interface SearchFilterService {

    List<Metadata> findItems(SearchDTO searchForm, Long id);

    List<Metadata> findRecentItems(Instant defaultDate, Long id);
}
