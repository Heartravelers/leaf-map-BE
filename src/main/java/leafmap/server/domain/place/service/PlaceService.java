package leafmap.server.domain.place.service;

import leafmap.server.domain.place.dto.PlaceDetailResponseDto;
import leafmap.server.domain.place.dto.PlaceResponseDto;

import java.util.List;

public interface PlaceService {

    List<PlaceResponseDto> findAll(double latitude, double longitude, String category, String keyword);

    //List<PlaceResponseDto> findAll(double latitude, double longitude, String category); // 이름 수정하기

    //List<PlaceResponseDto> findAllByKeyword(double latitude, double longitude, String keyword);

    PlaceDetailResponseDto findById(String id);

}
