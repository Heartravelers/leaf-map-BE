package leafmap.server.domain.note.entity;
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

    @Column(name = "count_note")
    private Integer countNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
