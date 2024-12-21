package leafmap.server.domain.place.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.global.common.BaseEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "place")
public class Place extends BaseEntity {
    @Id
    @Column(name = "place_id", nullable = false)
    private String id;

    @Column(name = "address")
    private String address;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Note> notes = new ArrayList<>();

}
