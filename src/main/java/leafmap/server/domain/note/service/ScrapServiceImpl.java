package leafmap.server.domain.note.service;

import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.repository.NoteRepository;
import leafmap.server.domain.note.repository.ScrapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScrapServiceImpl implements ScrapService{
    private ScrapRepository scrapRepository;
    private NoteRepository noteRepository;

    @Autowired
    public ScrapServiceImpl(ScrapRepository scrapRepository, NoteRepository noteRepository){
        this.scrapRepository = scrapRepository;
        this.noteRepository = noteRepository;
    }


    @Override
    public void makeScrap(Long noteId){
        Optional<Note> note = noteRepository.findById(noteId);



    }

    @Override
    public void deleteScrap(Long noteId){

    }
}
