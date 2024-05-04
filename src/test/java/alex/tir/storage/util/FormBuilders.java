package alex.tir.storage.util;

import alex.tir.storage.dto.MetadataForm;
import alex.tir.storage.dto.SearchDTO;
import alex.tir.storage.dto.UserForm;
import lombok.Builder;
import org.apache.tika.mime.MimeTypes;

@SuppressWarnings("unused")
public final class FormBuilders {

    private FormBuilders() {
    }

    public static class MetadataFormBuilder {
        private String name = "Data";
        private Long parentId = 0L;
    }

    @Builder(builderMethodName = "defaultMetadataForm")
    private static MetadataForm createMetadataForm(String name, Long parentId) {
        MetadataForm form = new MetadataForm();
        form.setName(name);
        form.setParentId(parentId);
        return form;
    }

    public static class UserFormBuilder {
        private String firstName = "Nick";
        private String lastName = "Kosiuk";
        private String email = "fidget@gmail.com";
        private String password = "Nick0533!";
    }

    @Builder(builderMethodName = "defaultUserForm")
    private static UserForm createUserForm(String firstName, String lastName, String email, String password) {
        UserForm form = new UserForm();
        form.setFirstName(firstName);
        form.setLastName(lastName);
        form.setEmail(email);
        form.setPassword(password);
        return form;
    }

    public static class SearchFormBuilder {
        private String name = "Data";
        private String mimeType = MimeTypes.OCTET_STREAM;
        private Long parentId = 0L;
    }

    @Builder(builderMethodName = "defaultSearchForm")
    private static SearchDTO createSearchForm(String name, String mimeType, Long parentId) {
        SearchDTO form = new SearchDTO();
        form.setName(name);
        form.setMimeType(mimeType);
        form.setParentId(parentId);
        return form;
    }
}

