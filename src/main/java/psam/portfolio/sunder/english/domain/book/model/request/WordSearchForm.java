package psam.portfolio.sunder.english.domain.book.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class WordSearchForm {

    @Size(min = 1, max = 20)
    @NotEmpty
    private final List<UUID> bookIds;
}
