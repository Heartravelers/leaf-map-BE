package leafmap.server.domain.note.entity;

import leafmap.server.domain.challenge.CategoryChallenge;
import leafmap.server.domain.user.entity.User;
import leafmap.server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "CategoryFilter")
public class CategoryFilter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "color")
    private String color;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "code")
    private String code;

    @Column(name = "count_note")
    private Integer countNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "categoryFilter", cascade = CascadeType.ALL, orphanRemoval = true)
    private CategoryChallenge categoryChallenge;
}
