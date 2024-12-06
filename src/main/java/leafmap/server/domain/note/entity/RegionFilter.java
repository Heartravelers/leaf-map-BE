package leafmap.server.domain.note.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import leafmap.server.domain.user.entity.User;
import lombok.*;
@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "RegionFilter")
public class RegionFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "filter_id", nullable = false)
    private Long id;

    @Column(name = "region_name")
    private String regionName;
    //대구 인천 강원도 대전 전라남도 경기도 부산 전라북도 경상남도 서울 제주도 경상북도 세종
    //충청남도 광주 울산 충청북도

    @Column(name = "count_note")
    private int countNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;
}
