package leafmap.server.domain.note.dto;

import jakarta.validation.constraints.NotNull;
import leafmap.server.domain.user.entity.User;

import java.time.LocalDate;

public class RegionFilterDto {
    private Long id;
    private String regionName;
    private Integer countNote;
    private User user;
}
