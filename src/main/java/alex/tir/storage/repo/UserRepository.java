package alex.tir.storage.repo;

import alex.tir.storage.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findUserByEmailIgnoreCase(String email);

    @Query("SELECT u FROM User u")
    @EntityGraph(attributePaths = "roles")
    List<User> findAllJoinRoles();

}