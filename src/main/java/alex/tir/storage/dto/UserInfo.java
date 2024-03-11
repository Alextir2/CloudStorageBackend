package alex.tir.storage.dto;

import lombok.Data;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserInfo {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Instant dateRegistered; // Было dateRegistered
    private Instant dateModified; // Добавлено
    private Set<String> roles = new HashSet<>();

}