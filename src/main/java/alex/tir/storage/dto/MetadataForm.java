package alex.tir.storage.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MetadataForm {

    @Size(min = 1, max = 255)
    private String name;


    @Positive
    private Long parentId;

}
