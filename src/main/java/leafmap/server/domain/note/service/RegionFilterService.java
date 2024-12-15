package leafmap.server.domain.note.service;

import leafmap.server.domain.note.entity.RegionFilter;
import leafmap.server.domain.user.entity.User;

public interface RegionFilterService {
    String getRegion(String address); // address 받아서 region 으로 바꾸기
    void increaseRegionNoteCount(User user, String regionName); // 해당 유저 해당 region note 수 1개 +
    void decreaseRegionNoteCount(User user, String regionName); // 해당 유저 해당 region note 수 1개 -
}
