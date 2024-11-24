package leafmap.server.domain.place.service;

import leafmap.server.domain.place.dto.PlaceResponseDto;

import java.util.List;

public interface PlaceService {

    List<PlaceResponseDto> findAll(double latitude, double longitude); // 이름 수정하기

}
