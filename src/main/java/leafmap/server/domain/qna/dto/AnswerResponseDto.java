package leafmap.server.domain.qna.dto;

import leafmap.server.domain.qna.entity.Answer;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AnswerResponseDto {

    private String answerText;

    private LocalDateTime createdAt;

    public AnswerResponseDto(Answer answer) {
        this.answerText = answer.getAnswerText();
        this.createdAt = answer.getCreatedAt();
    }

}
