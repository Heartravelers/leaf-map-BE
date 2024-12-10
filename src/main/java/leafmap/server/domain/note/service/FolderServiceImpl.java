package leafmap.server.domain.note.service;

import jakarta.transaction.Transactional;
import leafmap.server.domain.challenge.entity.CategoryChallenge;
import leafmap.server.domain.challenge.repository.CategoryChallengeRepository;
import leafmap.server.domain.note.dto.FolderDto;
import leafmap.server.domain.note.dto.NoteDto;
import leafmap.server.domain.note.entity.Folder;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.entity.RegionFilter;
import leafmap.server.domain.note.repository.FolderRepository;
import leafmap.server.domain.note.repository.NoteRepository;
import leafmap.server.domain.note.repository.RegionFilterRepository;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.repository.UserRepository;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FolderServiceImpl implements FolderService {
    UserRepository userRepository;
    FolderRepository folderRepository;
    CategoryChallengeRepository categoryChallengeRepository;
    NoteRepository noteRepository;
    RegionFilterRepository regionFilterRepository;

    @Autowired
    public FolderServiceImpl(UserRepository userRepository, FolderRepository folderRepository,
                             CategoryChallengeRepository categoryChallengeRepository, NoteRepository noteRepository,
                             RegionFilterRepository regionFilterRepository){
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
        this.categoryChallengeRepository = categoryChallengeRepository;
        this.noteRepository = noteRepository;
        this.regionFilterRepository = regionFilterRepository;
    }

    @Override
    public List<FolderDto> getFolder(Long userId){ //폴더 목록 조회
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }

        List<Folder> folders = folderRepository.findByUser(optionalUser.get());

        return folders.stream()
                .map(FolderDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public void makeFolder(Long userId, FolderDto folderDto){ //폴더 생성
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }

        Folder folder = Folder.builder()
                .name(folderDto.getName())
                .color(folderDto.getColor())
                .user(optionalUser.get()).build();

        folderRepository.save(folder);

        Optional<CategoryChallenge> optionalChallenge = categoryChallengeRepository.findByUserAndFolder(optionalUser.get(), folder);
        if (optionalChallenge.isPresent()){
            throw new CustomException.BadRequestException(ErrorCode.BAD_REQUEST); //이미 존재
        }

        CategoryChallenge categoryChallenge = CategoryChallenge.builder()
                .user(optionalUser.get())
                .folder(folder)
                .countStamp(0).build();

        categoryChallengeRepository.save(categoryChallenge);
    }

    @Override
    public void updateFolder(Long userId, Long folderId, FolderDto folderDto){ //폴더 수정
        Optional<Folder> optionalFolder = folderRepository.findById(folderId);
        if (optionalFolder.isEmpty()){
            throw new CustomException.NotFoundNoteException(ErrorCode.NOT_FOUND);
        }

        if (!Objects.equals(userId, optionalFolder.get().getUser().getId())){
            throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
        }

        Folder newFolder = optionalFolder.get().toBuilder()
                .name(folderDto.getName())
                .color(folderDto.getColor())
                .build();

        folderRepository.save(newFolder);
    }

    @Override
    public void deleteFolder(Long userId, Long folderId){  //폴더 삭제
        Optional<Folder> optionalFolder = folderRepository.findById(folderId);
        if (optionalFolder.isEmpty()){
            throw new CustomException.NotFoundFolderException(ErrorCode.NOT_FOUND);
        }
        if (!Objects.equals(userId, optionalFolder.get().getUser().getId())){
            throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
        }

        folderRepository.deleteById(folderId); //폴더와 함께 챌린지도 함께 삭제(Cascade.All 설정)

//        Optional<CategoryChallenge> optionalChallenge = categoryChallengeRepository.findByCategoryFilter(optionalCategory.get());
//        if (optionalChallenge.isEmpty()){
//            throw new CustomException.NotFoundChallengeException(ErrorCode.NOT_FOUND);
//        }
//
//        categoryChallengeRepository.deleteById(optionalChallenge.get().getId());
    }

    @Override
    public List<NoteDto> filterNotes(Long userId, String regionName){ //지역 필터링
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }

        RegionFilter regionFilter = regionFilterRepository.findByRegionName(regionName); //** 이거 갈아엎을 필요 있음,, repository 형태가..
        List<Note> notes = noteRepository.findByUserAndRegionFilter(optionalUser.get(), regionFilter);

        return notes.stream()
                .map(NoteDto::new)
                .collect(Collectors.toList());
    }

}
