package leafmap.server.domain.note.service;

import leafmap.server.domain.note.dto.ListDto;
import leafmap.server.domain.note.dto.NoteDto;
import leafmap.server.domain.note.entity.CategoryFilter;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.repository.CategoryRepository;
import leafmap.server.domain.note.repository.NoteRepository;
import leafmap.server.domain.place.entity.Place;
import leafmap.server.domain.place.repository.PlaceRepository;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.entity.repository.UserRepository;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteServiceImpl implements NoteService{
    private NoteRepository noteRepository;
    private PlaceRepository placeRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private NoteDto noteDto;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository, PlaceRepository placeRepository,
                           UserRepository userRepository, CategoryRepository categoryRepository,
                           NoteDto noteDto){
        this.noteRepository = noteRepository;
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
        this.noteDto = noteDto;
    }

    @Override     //노트 상세 조회(본인)
    public NoteDto getMyNote(Long noteId){
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isEmpty()) {
            throw new CustomException.NotFoundNoteException(ErrorCode.NOT_FOUND);
        }
        Note note = optionalNote.get();
        noteDto.builder()
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

    @Override     //노트 상세 조회(다른 사용자)
    public NoteDto getUserNote(Long noteId){
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isEmpty()) {
            throw new CustomException.NotFoundNoteException(ErrorCode.NOT_FOUND);
        }
        Note note = optionalNote.get();
        Optional<User> optionalUser = noteRepository.findByNoteId(noteId);
        if (optionalUser.isEmpty()) {
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }
        if (note.getIsPublic() == false){
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
            throw new CustomException.NotFoundUserException(ErrorCode.NOT_FOUND);
        }
        Optional<Place> optionalPlace = placeRepository.findById(noteDto.getPlaceId());
        if (optionalPlace.isEmpty()){
            placeRepository.save(optionalPlace.get());
        } //**place 가 db에 없으면 추가하라는건데 place 정보 관리를 어떻게 하는건지 재확인 필요 (#1)

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
    }
    @Override   //노트 수정
    public void updateNote(Long noteId, NoteDto noteDto){
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isEmpty()){
            throw new CustomException.NotFoundNoteException(ErrorCode.NOT_FOUND);
        }
        Optional<Place> optionalPlace = placeRepository.findById(noteDto.getPlaceId());
        if (optionalPlace.isEmpty()){
            placeRepository.save(optionalPlace.get());
        } //**place 가 db에 없으면 추가하라는건데 place 정보 관리를 어떻게 하는건지 재확인 필요 (#2)

        Note note = Note.builder()
                .title(noteDto.getTitle())
                .date(noteDto.getDate())
                .content(noteDto.getContent())
                .isPublic(noteDto.getIsPublic())
                .noteImages(noteDto.getNoteImages())
                .categoryFilter(noteDto.getCategoryFilter())
                .place(optionalPlace.get()).build();
        noteRepository.save(note);
    }
    @Override   //노트 삭제
    public void deleteNote(Long noteId){
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isEmpty()){
            throw new CustomException.NotFoundNoteException(ErrorCode.NOT_FOUND);
        }
        noteRepository.deleteById(noteId);
    }

    @Override    //폴더 내 노트목록 조회
    public List<NoteDto> getList(Long userId, String categoryName){
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new CustomException.NotFoundUserException(ErrorCode.NOT_FOUND);
        }
        Optional<CategoryFilter> optionalCategory = categoryRepository.findByCategoryName(categoryName);
        if (optionalCategory.isEmpty()){
            throw new CustomException.NotFoundCategoryException(ErrorCode.NOT_FOUND);
        }

        return noteRepository.findByUserIdAndCategory(userId, categoryName);
    }
}
