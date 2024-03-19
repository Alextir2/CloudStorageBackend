package alex.tir.storage.dto;

import lombok.Data;

@Data
public class SearchDTO {
    private String name;

    private String mimeType;

    private Long parentId;
}
