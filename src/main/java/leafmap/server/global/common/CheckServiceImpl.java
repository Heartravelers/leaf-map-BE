package leafmap.server.global.common;

import leafmap.server.domain.note.dto.RegionFilterDto;
import leafmap.server.domain.note.entity.Folder;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.entity.RegionFilter;
import leafmap.server.domain.note.repository.FolderRepository;
import leafmap.server.domain.note.repository.NoteRepository;
import leafmap.server.domain.note.repository.RegionFilterRepository;
import leafmap.server.domain.note.service.RegionFilterService;
import leafmap.server.domain.place.entity.Place;
import leafmap.server.domain.place.repository.PlaceRepository;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.repository.UserRepository;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.Optional;

// 객체에 대해 check 예외처리 하고 service 하는 클래스
public class CheckServiceImpl implements CheckService {
    private UserRepository userRepository;
    private NoteRepository noteRepository;
    private FolderRepository folderRepository;
    private RegionFilterRepository regionFilterRepository;
    private RegionFilterService regionFilterService;
    private PlaceRepository placeRepository;

    @Autowired
    public CheckServiceImpl(UserRepository userRepository, NoteRepository noteRepository,
                            FolderRepository folderRepository, RegionFilterRepository regionFilterRepository,
                            RegionFilterService regionFilterService, PlaceRepository placeRepository){
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
        this.folderRepository = folderRepository;
        this.regionFilterRepository = regionFilterRepository;
        this.regionFilterService = regionFilterService;
        this.placeRepository = placeRepository;
    }

    @Override
    public User checkUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }
        else{
            return optionalUser.get();
        }
    }

    @Override
    public Note checkNote(Long noteId){
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isEmpty()) {
            throw new CustomException.NotFoundNoteException(ErrorCode.NOT_FOUND);
        }
        else{
            return optionalNote.get();
        }
    }

    @Override
    public Folder checkFolder(String folderName){ //** user 도 넘겨줘야 정확한 user에 대한 폴더 확인 가능
        Optional<Folder> optionalFolder = folderRepository.findByName(folderName);
        if (optionalFolder.isEmpty()){
            throw new CustomException.NotFoundFolderException(ErrorCode.NOT_FOUND);
        }
        else{
            return optionalFolder.get();
        }
    }

    @Override
    public String checkAndGetRegionName(String address){
        String regionName = regionFilterService.getRegion(address); //주소 통해 regionName 받아오기
        if (Objects.equals(regionName, "")){ //해당 region 존재하지 않음
            throw new CustomException.BadRequestException(ErrorCode.BAD_REQUEST);
        }
        else{ //존재하면 regionName 리턴
            return regionName;
        }
    }

    @Override
    public RegionFilter checkRegionFilterAndMakeOrReturn(User user, String regionName) {
        Optional<RegionFilter> optionalRegionFilter = regionFilterRepository.findByUserAndRegionName(user, regionName);
        if (optionalRegionFilter.isEmpty()) { //존재하지 않으면 새로 생성하고 리턴
            RegionFilter regionFilter = RegionFilter.builder()
                    .regionName(regionName)
                    .countNote(0)
                    .user(user).build();
            regionFilterRepository.save(regionFilter);
            return regionFilter;
        } else { //존재하면 해당 regionFilter 리턴
            return optionalRegionFilter.get();
        }
    }

    @Override
    public Place checkPlaceAndSaveReturn(String placeId, String regionName) {
        Optional<Place> optionalPlace = placeRepository.findById(placeId);
        if (optionalPlace.isEmpty()) { //해당 place 가 존재하지 않으면 생성하고 return
            Place place = Place.builder()
                    .id(placeId)
                    .regionName(regionName).build();
            placeRepository.save(place);
            return place;
        }
        else{ //해당 place 가 존재하면 바로 return
            return optionalPlace.get();
        }
    }
}
