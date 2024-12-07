package leafmap.server.domain.qna.dto;

import leafmap.server.domain.qna.entity.Answer;
import leafmap.server.domain.qna.entity.Inquiry;
import lombok.Getter;

@Getter
public class AnswerResponseDto {

    private String inquiryTitle;

    private String inquiryText;

    private String answerText;

    public AnswerResponseDto(Inquiry inquiry) {
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.inquiryText = inquiry.getInquiryText();
        if(inquiry.getAnswer() != null) {
            this.answerText = inquiry.getAnswer().getAnswerText();
        } else {
            this.answerText = null;
        }

    }

}
