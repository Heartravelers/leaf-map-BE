package leafmap.server.domain.note.repository;

import io.lettuce.core.dynamic.annotation.Param;
import leafmap.server.domain.note.entity.RegionFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionFilterRepository extends JpaRepository<RegionFilter, Long> {
    RegionFilter findByRegionName(String regionName);
    @Modifying
    @Query("UPDATE RegionFilter rf SET rf.countNote = rf.countNote + 1 WHERE rf.userId = :userId AND rf.regionName = :regionName")
    void increaseCountNote(@Param("userId") Long userId, @Param("regionName") String regionName);
    @Modifying
    @Query("UPDATE RegionFilter rf SET rf.countNote = rf.countNote - 1 WHERE rf.userId = :userId AND rf.regionName = :regionName")
    void decreaseCountNote(@Param("userId") Long userId, @Param("regionName") String regionName);
}
