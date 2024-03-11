package alex.tir.storage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@MappedSuperclass
@Getter
@Setter
public abstract class AuditableEntity extends BaseEntity {

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateCreated;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateModified;

    @PrePersist
    public void prePersist() {
        dateCreated = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        dateModified = dateCreated;
    }

    @PreUpdate
    public void preUpdate() {
        dateModified = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }

}
