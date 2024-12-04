package leafmap.server.domain.note.service;

import jakarta.transaction.Transactional;
import leafmap.server.domain.note.dto.NoteDto;
import leafmap.server.domain.note.entity.CategoryFilter;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.repository.CategoryRepository;
import leafmap.server.domain.note.repository.NoteRepository;
import leafmap.server.domain.note.repository.RegionFilterRepository;
import leafmap.server.domain.place.entity.Place;
import leafmap.server.domain.place.repository.PlaceRepository;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.repository.UserRepository;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class NoteServiceImpl implements NoteService{
    private NoteRepository noteRepository;
    private PlaceRepository placeRepository;
    private UserRepository userRepository;
    private RegionFilterRepository regionFilterRepository;
    private NoteDto noteDto;
    private RegionFilterServiceImpl regionFilterService;
    private CategoryRepository categoryRepository;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository, PlaceRepository placeRepository,
                           UserRepository userRepository, RegionFilterRepository regionFilterRepository,
                           NoteDto noteDto, RegionFilterServiceImpl regionFilterService,
                           CategoryRepository categoryRepository){
        this.noteRepository = noteRepository;
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
        this.regionFilterRepository = regionFilterRepository;
        this.noteDto = noteDto;
        this.regionFilterService = regionFilterService;
        this.categoryRepository = categoryRepository;
    }

    @Override     //노트 상세 조회
    public NoteDto getNote(Long userId, Long noteId){
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isEmpty()) {
            throw new CustomException.NotFoundNoteException(ErrorCode.NOT_FOUND);
        }
        Note note = optionalNote.get();
        Optional<User> optionalUser = noteRepository.findByNoteId(noteId);
        if (optionalUser.isEmpty()) {
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }
        if (!Objects.equals(userId, optionalUser.get().getId()))
            if (!note.getIsPublic()){
                throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
            }
        noteDto.builder()
                .userId(note.getUser().getId())
                .profilePicture(note.getUser().getProfilePicture())
                .title(note.getTitle())
                .date(note.getDate())
                .placeName(note.getPlace().getName())
                .address(note.getPlace().getAddress())
                .placeId(note.getPlace().getId())
                .content(note.getContent())
                .noteImages(note.getNoteImages())
                .categoryFilter(note.getCategoryFilter())
                .countHeart(note.getCountHeart());
        return noteDto;
    }


    @Override   //노트 생성
    public void postNote(Long userId, NoteDto noteDto){
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }

        String region = regionFilterService.getRegion(noteDto.getAddress());
        if (Objects.equals(region, "")){
            throw new CustomException.BadRequestException(ErrorCode.BAD_REQUEST);
        }

        Optional<Place> optionalPlace = placeRepository.findById(noteDto.getPlaceId());
        if (optionalPlace.isEmpty()){
            Place place = Place.builder()
                    .id(noteDto.getPlaceId())
                    .address(noteDto.getAddress())
                    .regionName(region).build();
            placeRepository.save(optionalPlace.get());
        }

        Note note = Note.builder()
                .title(noteDto.getTitle())
                .date(noteDto.getDate())
                .content(noteDto.getContent())
                .isPublic(noteDto.getIsPublic())
                .noteImages(noteDto.getNoteImages())
                .categoryFilter(noteDto.getCategoryFilter())
                .place(optionalPlace.get())
                .user(optionalUser.get()).build();
        noteRepository.save(note);
        regionFilterService.increaseRegionNoteCount(userId, region);
    }
    @Override   //노트 수정
    public void updateNote(Long userId, Long noteId, NoteDto noteDto){
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isEmpty()){
            throw new CustomException.NotFoundNoteException(ErrorCode.NOT_FOUND);
        }

        if (!Objects.equals(userId, optionalNote.get().getUser().getId())){
            throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
        }

        Note note = Note.builder()
                .title(noteDto.getTitle())
                .date(noteDto.getDate())
                .content(noteDto.getContent())
                .isPublic(noteDto.getIsPublic())
                .noteImages(noteDto.getNoteImages())
                .categoryFilter(noteDto.getCategoryFilter()).build();
        noteRepository.save(note);
    }
    @Override   //노트 삭제
    public void deleteNote(Long userId, Long noteId){
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isEmpty()){
            throw new CustomException.NotFoundNoteException(ErrorCode.NOT_FOUND);
        }

        String region = regionFilterService.getRegion(optionalNote.get().getPlace().getAddress());
        if (Objects.equals(region, "")){
            throw new CustomException.BadRequestException(ErrorCode.BAD_REQUEST);
        }

        if (!Objects.equals(userId, optionalNote.get().getUser().getId())){
            throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
        }

        noteRepository.deleteById(noteId);
        regionFilterService.decreaseRegionNoteCount(userId, region);
    }

    @Override    //폴더 내 노트목록 조회
    public List<NoteDto> getList(Long userId, String categoryName){
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }
        Optional<CategoryFilter> optionalCategory = categoryRepository.findByCategoryName(categoryName);
        if (optionalCategory.isEmpty()){
            throw new CustomException.NotFoundCategoryException(ErrorCode.NOT_FOUND);
        }

        return noteRepository.findByUserIdAndCategory(userId, categoryName); //**내거인지 아닌지, isPublic, notes 엔티티가 아닌 dto return 고려해야 함
    }
}
