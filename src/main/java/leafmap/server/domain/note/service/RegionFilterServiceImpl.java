package leafmap.server.domain.note.service;

import jakarta.transaction.Transactional;
import leafmap.server.domain.note.entity.RegionFilter;
import leafmap.server.domain.note.repository.RegionFilterRepository;
import leafmap.server.domain.user.entity.User;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class RegionFilterServiceImpl implements RegionFilterService{
    private RegionFilterRepository regionFilterRepository;

    @Autowired
    private RegionFilterServiceImpl(RegionFilterRepository regionFilterRepository){
        this.regionFilterRepository = regionFilterRepository;
    }

    @Override
    public String getRegion(String address) {
        if (address.contains("대전")) {
            return "대전";
        } else if (address.contains("세종")) {
            return "세종";
        } else if (address.contains("대구")) {
            return "대구";
        } else if (address.contains("울산")) {
            return "울산";
        } else if (address.contains("부산")) {
            return "부산";
        } else if (address.contains("광주")) {
            return "광주";
        } else if (address.contains("인천")) {
            return "인천";
        } else if (address.contains("서울")) {
            return "서울";
        } else if (address.contains("강원")) {
            return "강원";
        } else if (address.contains("충청남도")) {
            return "충청남도";
        } else if (address.contains("경기")) {
            return "경기";
        } else if (address.contains("충청북도")) {
            return "충청북도";
        } else if (address.contains("경상북도")) {
            return "경상북도";
        } else if (address.contains("경상남도")) {
            return "경상남도";
        } else if (address.contains("전라북도")) {
            return "전라북도";
        } else if (address.contains("전라남도")) {
            return "전라남도";
        } else if (address.contains("제주")) {
            return "제주";
        } else {
            return "";
        }
    }

    @Override
    public void increaseRegionNoteCount(Long userId, String regionName){ // 이미 userId는 이전 메서드에서 검사
        regionFilterRepository.increaseCountNote(userId, regionName);
    }

    @Override
    public void decreaseRegionNoteCount(Long userId, String regionName) { // 이미 userId는 이전 메서드에서 검사
        regionFilterRepository.decreaseCountNote(userId, regionName);
    }
}
