package leafmap.server.domain.place.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import leafmap.server.domain.place.dto.*;
import leafmap.server.domain.place.entity.Place;
import leafmap.server.domain.place.repository.PlaceRepository;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class PlaceServiceImpl implements PlaceService {

    private static final double LATITUDE_MIN = 33.0;
    private static final double LATITUDE_MAX = 38.6278;
    private static final double LONGITUDE_MIN = 125.5666;
    private static final double LONGITUDE_MAX = 131.8696;

    private static final int MAX_RESULT_COUNT = 5;
    private static final double RADIUS = 500;
    private static final String FIELD_MASK = "places.id,places.displayName,places.types,places.formattedAddress,places.photos,places.location";
    private static final String DETAIL_FIELD_MASK = "displayName,formattedAddress,primaryType,photos";

    private static final Map<String, String> restaurants = new HashMap<>();
    private static final Map<String, String> cafes = new HashMap<>();
    private static final Map<String, String> attractions = new HashMap<>();
    private static final Map<String, String> leisure = new HashMap<>();

    @Value("${google.api-key}")
    private String apiKey;

    @Autowired
    PlaceRepository placeRepository;
    
    //키워드 있으면 위치 기반 키워드 검색하고 카테고리는 무시
    //키워드 없으면 위치 기반 검색하고 카테고리 필터링
    @Override
    public List<PlaceResponseDto> findAll(double latitude, double longitude, String category, String keyword) {
        
        if(latitude < LATITUDE_MIN || latitude > LATITUDE_MAX || longitude < LONGITUDE_MIN || longitude > LONGITUDE_MAX) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        String url = "https://places.googleapis.com/v1/places:searchNearby"; // 위치 또는 카테고리 검색 api
        Object requestBody;
        if(keyword != null) { // 키워드 검색
            url = "https://places.googleapis.com/v1/places:searchText"; // 키워드 검색 api
            requestBody = new GoogleApiKeywordRequestDto(keyword, MAX_RESULT_COUNT, RADIUS, latitude, longitude);
        } else { // 위치 또는 카테고리 검색
            List<String> includedTypes = new ArrayList<>();
            if(category != null) {
                switch (category) {
                    case "식당" ->
                            includedTypes = new ArrayList<>(restaurants.keySet());
                    case "카페" ->
                            includedTypes = new ArrayList<>(cafes.keySet());
                    case "관광명소" ->
                            includedTypes = new ArrayList<>(attractions.keySet());
                    case "여가문화" ->
                            includedTypes = new ArrayList<>(leisure.keySet());
                    default -> throw new CustomException(ErrorCode.BAD_REQUEST);
                }
            }
            requestBody = new GoogleApiRequestDto(includedTypes, MAX_RESULT_COUNT, RADIUS, latitude, longitude);
        }

        // google api에 post 요청 보내고 response 받아옴
        WebClient webClient = WebClient.create();
        String data = webClient
                .post()
                .uri(url)
                .bodyValue(requestBody)
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", FIELD_MASK)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // 받아온 response(String 형태)를 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        GoogleApiResponseDto googleResponse = null;
        try {
            googleResponse = objectMapper.readValue(data, GoogleApiResponseDto.class);
            List<GooglePlace> places = new ArrayList<>();
            for(GooglePlace googlePlace : googleResponse.getPlaces()) {
                List<String> translatedCategories = translateCategory(googlePlace.getTypes());
                if(!translatedCategories.isEmpty()) {
                    googlePlace.setTypes(translatedCategories);
                    places.add(googlePlace);
                }
            }
            return places.stream().map(PlaceResponseDto::new).toList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PlaceDetailResponseDto findById(String id) {
        String url = "https://places.googleapis.com/v1/places/" + id + "?languageCode=ko";

        WebClient webClient = WebClient.create();
        String data = webClient
                .get()
                .uri(url)
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", DETAIL_FIELD_MASK)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        GooglePlace response = null;
        try {
            response = objectMapper.readValue(data, GooglePlace.class);
            Optional<Place> placeOptional = placeRepository.findById(id);
            if(placeOptional.isPresent()) {
                response.setTypes(translateCategory(response.getTypes()));
                return new PlaceDetailResponseDto(response, placeOptional.get().getNotes());
            }
            throw new CustomException(ErrorCode.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> translateCategory(List<String> categories) {
        Set<String> result = new HashSet<>();

        for(String category : categories) {
            if(restaurants.containsKey(category)) {
                result.add(restaurants.get(category));
            } else if(cafes.containsKey(category)) {
                result.add(cafes.get(category));
            } else if(attractions.containsKey(category)) {
                result.add(attractions.get(category));
            } else if(leisure.containsKey(category)) {
                result.add(leisure.get(category));
            }
        }

        return new ArrayList<>(result);
    }


    static {
        restaurants.put("african_restaurant", "아프리칸음식");
        restaurants.put("american_restaurant", "양식");
        restaurants.put("bar", "바");
        restaurants.put("bar_and_grill", "바");
        restaurants.put("barbecue_restaurant", "바베큐");
        restaurants.put("brazilian_restaurant", "브라질음식");
        restaurants.put("breakfast_restaurant", "조식");
        restaurants.put("brunch_restaurant", "브런치");
        restaurants.put("buffet_restaurant", "뷔페");
        restaurants.put("chinese_restaurant", "중식");
        restaurants.put("diner", "식당");
        restaurants.put("fast_food_restaurant", "패스트푸드");
        restaurants.put("fine_dining_restaurant", "파인다이닝");
        restaurants.put("food_court", "푸드코트");
        restaurants.put("french_restaurant", "양식");
        restaurants.put("greek_restaurant", "그리스음식");
        restaurants.put("hamburger_restaurant", "햄버거");
        restaurants.put("indian_restaurant", "인도음식");
        restaurants.put("indonesian_restaurant", "인도네시아음식");
        restaurants.put("italian_restaurant", "양식");
        restaurants.put("japanese_restaurant", "일식");
        restaurants.put("korean_restaurant", "한식");
        restaurants.put("mediterranean_restaurant", "지중해식");
        restaurants.put("mexican_restaurant", "멕시칸");
        restaurants.put("middle_eastern_restaurant", "중동음식");
        restaurants.put("pizza_restaurant", "피자");
        restaurants.put("pub", "펍");
        restaurants.put("ramen_restaurant", "라멘");
        restaurants.put("restaurant", "식당");
        restaurants.put("sandwich_shop", "샌드위치");
        restaurants.put("seafood_restaurant", "해산물");
        restaurants.put("spanish_restaurant", "스페인음식");
        restaurants.put("steak_house", "스테이크");
        restaurants.put("sushi_restaurant", "초밥");
        restaurants.put("thai_restaurant", "태국음식");
        restaurants.put("turkish_restaurant", "터키음식");
        restaurants.put("vegan_restaurant", "비건");
        restaurants.put("vegetarian_restaurant", "채식");
        restaurants.put("vietnamese_restaurant", "베트남음식");
        restaurants.put("wine_bar", "와인바");

        cafes.put("bagel_shop", "베이글");
        cafes.put("bakery", "베이커리");
        cafes.put("candy_store", "사탕");
        cafes.put("cat_cafe", "고양이카페");
        cafes.put("chocolate_shop", "초콜릿");
        cafes.put("coffee_shop", "커피");
        cafes.put("confectionery", "제과점");
        cafes.put("dessert_restaurant", "디저트");
        cafes.put("dessert_shop", "디저트");
        cafes.put("dog_cafe", "강아지카페");
        cafes.put("donut_shop", "도넛");
        cafes.put("ice_cream_shop", "아이스크림");
        cafes.put("juice_shop", "주스");
        cafes.put("tea_house", "차");

        attractions.put("cultural_landmark", "관광명소");
        attractions.put("historical_place", "문화유산");
        attractions.put("monument", "기념물");
        attractions.put("historical_landmark", "문화유산");
        attractions.put("observation_deck", "전망대");
        attractions.put("beach","해변");
        attractions.put("sculpture", "조각");
        attractions.put("ferris_wheel", "대관람차");
        attractions.put("tourist_attraction", "관광명소");

        leisure.put("art_gallery", "아트갤러리");
        leisure.put("art_studio", "아트스튜디오");
        leisure.put("auditorium", "공연장");
        leisure.put("museum", "박물관");
        leisure.put("performing_arts_theater", "공연예술극장");
        leisure.put("library", "도서관");
        leisure.put("adventure_sports_center", "모험스포츠센터");
        leisure.put("amphitheatre", "경기장");
        leisure.put("amusement_center", "놀이공원");
        leisure.put("aquarium", "아쿠아리움");
        leisure.put("banquet_hall", "연회장");
        leisure.put("barbecue_area", "바베큐장");
        leisure.put("botanical_garden", "식물원");
        leisure.put("bowling_alley", "볼링장");
        leisure.put("community_center", "시민문화회관");
        leisure.put("concert_hall", "공연장");
        leisure.put("convention_center", "컨벤션센터");
        leisure.put("cultural_center", "문화센터");
        leisure.put("cycling_park", "자전거공원");
        leisure.put("dog_park", "강아지공원");
        leisure.put("garden", "정원");
        leisure.put("hiking_area", "등산");
        leisure.put("internet_cafe", "pc방");
        leisure.put("karaoke", "노래방");
        leisure.put("marina", "항구");
        leisure.put("movie_theater", "영화관");
        leisure.put("national_park", "국립공원");
        leisure.put("opera_house", "오페라하우스");
        leisure.put("park", "공원");
        leisure.put("philharmonic_hall", "공연장");
        leisure.put("picnic_ground", "피크닉");
        leisure.put("planetarium", "천문관");
        leisure.put("plaza", "광장");
        leisure.put("roller_coaster", "롤러코스터");
        leisure.put("skateboard_park", "스케이트보드공원");
        leisure.put("state_park", "공립공원");
        leisure.put("video_arcade", "오락실");
        leisure.put("visitor_center", "관광안내소");
        leisure.put("water_park", "워터파크");
        leisure.put("wildlife_park", "야생동물공원");
        leisure.put("zoo", "동물원");
        leisure.put("arena", "경기장");
        leisure.put("athletic_field", "운동장");
        leisure.put("ice_skating_rink", "아이스링크");
    }

}
