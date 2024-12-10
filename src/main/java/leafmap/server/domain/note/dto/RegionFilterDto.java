package leafmap.server.domain.note.dto;

import jakarta.validation.constraints.NotNull;
import leafmap.server.domain.note.entity.RegionFilter;
import leafmap.server.domain.user.entity.User;

import java.time.LocalDate;

public class RegionFilterDto {
    private Long id;
    private String regionName;
    private Integer countNote;
    private User user;

    public RegionFilterDto(RegionFilter regionFilter){
        this.regionName = regionFilter.getRegionName();
        this.countNote = regionFilter.getCountNote();
        this.user = regionFilter.getUser();
    }
}
