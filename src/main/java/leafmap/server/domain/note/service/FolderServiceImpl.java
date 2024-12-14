package leafmap.server.domain.note.service;

import jakarta.transaction.Transactional;
import leafmap.server.domain.challenge.entity.CategoryChallenge;
import leafmap.server.domain.challenge.repository.CategoryChallengeRepository;
import leafmap.server.domain.note.dto.FolderRequestDto;
import leafmap.server.domain.note.dto.FolderResponseDto;
import leafmap.server.domain.note.dto.NoteDetailResponseDto;
import leafmap.server.domain.note.entity.Folder;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.entity.RegionFilter;
import leafmap.server.domain.note.repository.FolderRepository;
import leafmap.server.domain.note.repository.NoteRepository;
import leafmap.server.domain.note.repository.RegionFilterRepository;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.repository.UserRepository;
import leafmap.server.global.common.CheckService;
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
    CheckService checkService;

    @Autowired
    public FolderServiceImpl(UserRepository userRepository, FolderRepository folderRepository,
                             CategoryChallengeRepository categoryChallengeRepository, NoteRepository noteRepository,
                             RegionFilterRepository regionFilterRepository, CheckService checkService){
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
        this.categoryChallengeRepository = categoryChallengeRepository;
        this.noteRepository = noteRepository;
        this.regionFilterRepository = regionFilterRepository;
        this.checkService = checkService;
    }

    @Override
    public List<FolderResponseDto> getFolder(Long myUserId, Long userId){ //폴더 목록 조회
        User user = checkService.checkUser(userId);
        List<Folder> folders = folderRepository.findByUser(user);

        if (!Objects.equals(myUserId, user.getId())) //본인 폴더가 아닐 때
            for (Folder folder : folders) {
                if (!folder.getIsPublic()) { //다른 유저 폴더가 isPublic False 이면 해당 객체 제외
                    folders.remove(folder);
                }
            }

        return folders.stream()
                .map(FolderResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public void makeFolder(Long myUserId, FolderRequestDto folderRequestDto){ //폴더 생성
        User user = checkService.checkUser(myUserId);

        Folder folder = Folder.builder()
                .name(folderRequestDto.getName())
                .color(folderRequestDto.getColor())
                .isPublic(true) //기본 형태는 공개
                .user(user).build();

        folderRepository.save(folder); //folder 생성 후 저장

        if (checkService.checkCategoryChallenge(user, folder) != null){
            throw new CustomException.BadRequestException(ErrorCode.BAD_REQUEST); //이미 존재
        }
        CategoryChallenge categoryChallenge = CategoryChallenge.builder()
                .user(user)
                .folder(folder)
                .countStamp(0).build();

        categoryChallengeRepository.save(categoryChallenge); //categoryChallenge 생성 후 저장
    }

    @Override
    public void updateFolder(Long myUserId, Long folderId, FolderRequestDto folderRequestDto){ //폴더 수정
        User user = checkService.checkUser(myUserId);
        Folder folder = checkService.checkUserFolder(user, folderId);

        if (!Objects.equals(user, folder.getUser())){
            throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
        }

        folder.update(folderRequestDto);

        CategoryChallenge categoryChallenge = checkService.checkCategoryChallenge(user, folder);
        folder.syncCategoryChallenge(categoryChallenge); // categoryChallenge 와 동기화

        folderRepository.save(folder);
    }

    @Override
    public void deleteFolder(Long myUserId, Long folderId){  //폴더 삭제
        User user = checkService.checkUser(myUserId);
        Folder folder = checkService.checkUserFolder(user, folderId);
        if (!Objects.equals(user, folder.getUser())){
            throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
        }

        folderRepository.deleteById(folderId); //폴더와 함께 챌린지도 함께 삭제(Cascade.All 설정)

        if (checkService.checkCategoryChallenge(user, folder) == null){
            throw new CustomException.NotFoundChallengeException(ErrorCode.NOT_FOUND); //챌린지 존재하지 않음
        }

        categoryChallengeRepository.deleteById(folder.getCategoryChallenge().getId());
    }

    @Override
    public List<NoteDetailResponseDto> filterNotes(Long userId, String regionName){ //지역 필터링
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }

        RegionFilter regionFilter = regionFilterRepository.findByRegionName(regionName); //** 이거 갈아엎을 필요 있음,, repository 형태가..
        List<Note> notes = noteRepository.findByUserAndRegionFilter(optionalUser.get(), regionFilter);

        return notes.stream()
                .map(NoteDetailResponseDto::new)
                .collect(Collectors.toList());
    }

}
