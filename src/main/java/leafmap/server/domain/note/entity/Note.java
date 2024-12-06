package leafmap.server.domain.note.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import leafmap.server.domain.place.entity.Place;
import leafmap.server.domain.user.entity.User;
import leafmap.server.global.common.BaseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "note")
public class Note extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    @JsonManagedReference
    private Place place;

    @Column(name = "title")
    private String title;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "content", length = 1000)
    private String content;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "count_heart")
    private Integer countHeart;

    @Column(name = "count_visit")
    private Integer countVisit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private RegionFilter regionFilter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryFilter categoryFilter;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<NoteImage> noteImages = new ArrayList<>();

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Scrap> scraps = new ArrayList<>();

    public void increaseHeart() { // makeScrap 에서 사용
        this.countHeart++;
    }

    public void decreaseHeart() { // deleteScrap 에서 사용
        this.countHeart--;
    }
}
