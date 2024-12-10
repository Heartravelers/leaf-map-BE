package leafmap.server.domain.note.service;

import jakarta.transaction.Transactional;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.entity.Scrap;
import leafmap.server.domain.note.repository.NoteRepository;
import leafmap.server.domain.note.repository.ScrapRepository;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.repository.UserRepository;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class ScrapServiceImpl implements ScrapService{
    private UserRepository userRepository;
    private ScrapRepository scrapRepository;
    private NoteRepository noteRepository;

    @Autowired
    public ScrapServiceImpl(UserRepository userRepository, ScrapRepository scrapRepository, NoteRepository noteRepository){
        this.userRepository = userRepository;
        this.scrapRepository = scrapRepository;
        this.noteRepository = noteRepository;
    }


    @Override
    public void makeScrap(Long userId, Long noteId){
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isEmpty()){
            throw new CustomException.NotFoundNoteException(ErrorCode.NOT_FOUND);
        }
        Note note = optionalNote.get();

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }

        Optional<Scrap> optionalScrap = scrapRepository.findByNoteIdAndUser(noteId, optionalUser.get());
        if (optionalScrap.isPresent()){
            throw new CustomException.BadRequestException(ErrorCode.BAD_REQUEST);
        }


        note.increaseHeart();
        noteRepository.save(note);

        Scrap scrap = Scrap.builder()
                .note(note)
                .user(optionalUser.get())
                .status(true).build();

        scrapRepository.save(scrap);
    }

    @Override
    public void deleteScrap(Long userId, Long noteId){
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isEmpty()){
            throw new CustomException.NotFoundNoteException(ErrorCode.NOT_FOUND);
        }
        Note note = optionalNote.get();

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }

        if (!Objects.equals(userId, note.getUser().getId())){
            throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
        }

        Optional<Scrap> optionalScrap = scrapRepository.findByNoteIdAndUser(noteId, optionalUser.get());
        if (optionalScrap.isEmpty()){
            throw new CustomException.BadRequestException(ErrorCode.BAD_REQUEST);
        }

        note.decreaseHeart();

        scrapRepository.deleteById(noteId);
    }
}
