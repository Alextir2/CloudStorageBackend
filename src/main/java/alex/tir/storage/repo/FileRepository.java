package alex.tir.storage.repo;

import alex.tir.storage.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileRepository extends JpaRepository<File, Long> {
    @Query("SELECT COALESCE(SUM(f.size), 0) FROM File f WHERE f.owner.id = :ownerId")
    long calculateTotalFileSizeByOwnerId(@Param("ownerId") Long ownerId);
}
