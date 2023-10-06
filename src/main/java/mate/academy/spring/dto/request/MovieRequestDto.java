package mate.academy.spring.dto.request;

import javax.validation.constraints.NotEmpty;

public class MovieRequestDto {
    @NotEmpty
    private String title;
    @NotEmpty
    private String description;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
