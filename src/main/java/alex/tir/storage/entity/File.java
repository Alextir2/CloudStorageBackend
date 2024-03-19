package alex.tir.storage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
        name = "files",
        uniqueConstraints = @UniqueConstraint(columnNames = "location"),
        indexes = {@Index(columnList = "parent_id"), @Index(columnList = "owner_id")})
@Getter
@Setter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class File extends AuditableEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Folder parent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

}
