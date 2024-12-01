package leafmap.server.domain.place.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import leafmap.server.domain.place.entity.Category;
import leafmap.server.domain.place.service.PlaceService;
import leafmap.server.global.common.ApiResponse;
import leafmap.server.global.common.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Place", description = "플레이스 관련 API")
public class PlaceController {

    @Autowired
    PlaceService placeService;

    @Operation(summary = "플레이스 조회")
    @GetMapping("/places")
    public ResponseEntity<ApiResponse<?>> getPlaces(@RequestParam double latitude, @RequestParam double longitude,
                                                    @RequestParam(required = false) String keyword, @RequestParam(required = false) String category) {
        try {
            placeService.findAll(latitude, longitude, category);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR.getErrorResponse());
    }

}
