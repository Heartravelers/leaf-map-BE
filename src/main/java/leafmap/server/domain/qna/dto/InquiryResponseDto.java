package leafmap.server.domain.qna.dto;

import leafmap.server.domain.qna.entity.Inquiry;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class InquiryResponseDto {

    private Long id;

    private String title;

    private String createdAt;

    public InquiryResponseDto(Inquiry inquiry) {
        this. id = inquiry.getId();
        this.title = inquiry.getInquiryTitle();
        this.createdAt = inquiry.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

}
