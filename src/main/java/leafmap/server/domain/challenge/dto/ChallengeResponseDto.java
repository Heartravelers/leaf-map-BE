package leafmap.server.domain.challenge.dto;

import leafmap.server.domain.challenge.entity.Challenge;
import leafmap.server.domain.note.entity.CategoryFilter;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ChallengeResponseDto {

    private int countStamp; // 총 스탬프 수

    private List<CategoryChallengeResponseDto> categories;

    private boolean receivedHeart;

    private boolean haveFollower;

    private boolean haveVisited;

    public ChallengeResponseDto(Challenge challenge, List<CategoryFilter> categoryFilters) {
        this.countStamp = challenge.getCountStamp();
        this.receivedHeart = challenge.getReceivedHeart();
        this.haveFollower = challenge.getHaveFollower();
        this.haveVisited = challenge.getHaveVisited();

        List<CategoryChallengeResponseDto> categories = new ArrayList<>();
        for(CategoryFilter categoryFilter : categoryFilters) {
            categories.add(new CategoryChallengeResponseDto(categoryFilter));
        }
        this.categories = categories;
    }

}
