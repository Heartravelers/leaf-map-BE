package leafmap.server.domain.note.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import leafmap.server.domain.challenge.entity.CategoryChallenge;
import leafmap.server.domain.user.entity.User;
import leafmap.server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "CategoryFilter")
public class CategoryFilter extends BaseEntity {

    //folder 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "color")
    private String color;

    @Column(name = "is_default")
    private Boolean isDefault; //미사용

    @Column(name = "code")
    private String code;

    @Column(name = "count_note")
    private Integer countNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @OneToOne(mappedBy = "categoryFilter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private CategoryChallenge categoryChallenge;
}
