package leafmap.server.domain.challenge.dto;

import leafmap.server.domain.note.entity.CategoryFilter;
import lombok.Getter;

@Getter
public class CategoryChallengeResponseDto {

    private String categoryName;

    private int countStamp;

    public CategoryChallengeResponseDto(CategoryFilter categoryFilter) {
        this.categoryName = categoryFilter.getName();
        this.countStamp = categoryFilter.getCategoryChallenge().getCountStamp();
    }

}
