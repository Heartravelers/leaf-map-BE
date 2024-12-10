package leafmap.server.domain.note.repository;

import io.lettuce.core.dynamic.annotation.Param;
import leafmap.server.domain.note.entity.RegionFilter;
import leafmap.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionFilterRepository extends JpaRepository<RegionFilter, Long> {
    RegionFilter findByRegionName(String regionName);
    Optional<RegionFilter> findByUserAndRegionName(User user, String regionName);
    @Modifying
    @Query("UPDATE RegionFilter rf SET rf.countNote = rf.countNote + 1 WHERE rf.user = :user AND rf.regionName = :regionName")
    void increaseCountNote(@Param("user") User user, @Param("regionName") String regionName);
    @Modifying
    @Query("UPDATE RegionFilter rf SET rf.countNote = rf.countNote - 1 WHERE rf.user = :user AND rf.regionName = :regionName")
    void decreaseCountNote(@Param("user") User user, @Param("regionName") String regionName);
}
