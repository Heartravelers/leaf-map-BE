package leafmap.server.domain.qna.entity;

import jakarta.persistence.*;
import leafmap.server.domain.qna.dto.InquiryRequestDto;
import leafmap.server.domain.user.entity.User;
import leafmap.server.global.common.BaseEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "inquiry")
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id", nullable = false)
    private Long id;

    @Setter
    @Column(name = "inquiry_title")
    private String inquiryTitle;

    @Setter
    @Column(name = "inquiry_text", columnDefinition = "text")
    private String inquiryText;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InquiryStatus status;

    @Setter
    @Column(name = "email")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "answer_id")
    private Answer answer;

    public Inquiry(InquiryRequestDto inquiryRequestDto, User user) {
        this.inquiryTitle = inquiryRequestDto.getInquiryTitle();
        this.inquiryText = inquiryRequestDto.getInquiryText();
        this.status = InquiryStatus.PENDING;
        this.email = inquiryRequestDto.getEmail();
        this.user = user;
    }
}
