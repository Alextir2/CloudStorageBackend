package alex.tir.storage.repo;

import alex.tir.storage.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    @Query("SELECT f FROM Folder f WHERE f.owner.id = :ownerId AND f.root = TRUE")
    Optional<Folder> findRootFolderByOwnerId(@Param("ownerId") Long ownerId);

    @Query(
            value = "WITH RECURSIVE tree AS (" +
                    "SELECT * FROM folders WHERE id IN :parentsIdSet " +
                    "UNION ALL " +
                    "SELECT tb.* FROM folders AS tb " +
                    "JOIN tree ON tree.id = tb.parent_id) " +
                    "SELECT * FROM tree;",
            nativeQuery = true)
    List<Folder> deepFindAllSubfoldersByParentIdIn(@Param("parentsIdSet") Set<Long> parentIdSet);

    @Query("SELECT f FROM Folder f WHERE f.owner.id = :ownerId AND f.parent IS NULL AND f.root = FALSE")
    List<Folder> findDisconnectedFoldersByOwnerId(@Param("ownerId") Long ownerId);


    List<Folder> findFoldersByDateModifiedAfterAndOwnerId(Instant afterDate, Long ownerId);
}
