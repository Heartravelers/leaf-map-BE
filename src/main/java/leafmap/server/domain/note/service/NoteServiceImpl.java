package leafmap.server.domain.note.service;

import leafmap.server.domain.note.dto.NoteRequestDto;
import leafmap.server.domain.note.dto.NoteDto;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.repository.NoteRepository;
import leafmap.server.domain.place.entity.Place;
import leafmap.server.domain.place.repository.PlaceRepository;
import leafmap.server.domain.user.entity.User;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static javax.swing.text.rtf.RTFAttributes.BooleanAttribute.False;

@Service
public class NoteServiceImpl implements NoteService{
    private NoteRepository noteRepository;
    private PlaceRepository placeRepository;
    private NoteDto noteDto;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository, PlaceRepository placeRepository, NoteDto noteDto){
        this.noteRepository = noteRepository;
        this.placeRepository = placeRepository;
        this.noteDto = noteDto;
    }

    @Override     //노트 상세 조회(본인)
    public NoteDto getMyNote(Long noteId){
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isEmpty()) {
            throw new CustomException.NotFoundEntityException(ErrorCode.NOT_FOUND);
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
            throw new CustomException.NotFoundEntityException(ErrorCode.NOT_FOUND);
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
    public void postNote(NoteDto noteDto){
        Optional<Place> optionalPlace = placeRepository.findById(noteDto.getPlaceId());
        if (optionalPlace.isEmpty()){
            placeRepository.save(optionalPlace.get());
        } //**place 가 db에 없으면 추가하라는건데 place 정보 관리를 어떻게 하는건지 재확인 필요

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
    @Override   //노트 수정
    public void updateNote(Long noteId, NoteDto noteDto){
        Place place = placeRepository.getOneByNameAndAddress(noteDto.getPlaceName(),noteDto.getAddress());
        Note note = Note.builder()
                .title(noteDto.getTitle())
                .date(noteDto.getDate())
                .content(noteDto.getContent())
                .isPublic(noteDto.getIsPublic())
                .noteImages(noteDto.getNoteImages())
                .categoryFilter(noteDto.getCategoryFilter())
                .place(place).build();
        noteRepository.save(note);
    }
    @Override   //노트 삭제 **fin
    public void deleteNote(Long noteId){
        noteRepository.deleteById(noteId);
    }


}
