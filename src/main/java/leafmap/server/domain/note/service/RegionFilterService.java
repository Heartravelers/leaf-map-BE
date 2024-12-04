package leafmap.server.domain.note.service;

public interface RegionFilterService {
    String getRegion(String address); // address 받아서 region 으로 바꾸기
    void increaseRegionNoteCount(Long userId, String regionName); // 해당 유저 해당 region note 수 1개 +
    void decreaseRegionNoteCount(Long userId, String regionName); // 해당 유저 해당 region note 수 1개 -
}
