package alex.tir.storage.service;

import alex.tir.storage.dto.Metadata;

import java.util.List;

public interface TrashService {
    List<Metadata> getTrashItems(Long id);

    void emptyTrash(Long id);
}
