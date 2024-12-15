package leafmap.server.domain.note.service;

import jakarta.transaction.Transactional;
import leafmap.server.domain.challenge.entity.CategoryChallenge;
import leafmap.server.domain.challenge.repository.CategoryChallengeRepository;
import leafmap.server.domain.note.dto.FolderRequestDto;
import leafmap.server.domain.note.dto.FolderResponseDto;
import leafmap.server.domain.note.dto.NoteDetailResponseDto;
import leafmap.server.domain.note.dto.NoteResponseDto;
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

        CategoryChallenge categoryChallenge = checkService.checkCategoryChallenge(user, folder);

        folder.update(folderRequestDto);
        folder.syncCategoryChallenge(categoryChallenge); // categoryChallenge 와 동기화

        folderRepository.save(folder); //폴더와 함께 챌린지도 저장(Cascade.All)

        //폴더가 비공개라면 안의 노트도 모두 비공개
        if (folder.getIsPublic()){
            List<Note> notes = noteRepository.findByUserAndFolder(user, folder);
            for (Note note : notes) {
                note.folderIsPrivate(folder);
            }
        }
    }

    @Override
    public void deleteFolder(Long myUserId, List<Long> folderIds){  //폴더 삭제
        User user = checkService.checkUser(myUserId);
        for (Long folderId : folderIds) { //폴더 하나씩 check 후 삭제
            Folder folder = checkService.checkUserFolder(user, folderId);
            if (!Objects.equals(user, folder.getUser())) {
                throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
            }

            folderRepository.deleteById(folderId); //폴더와 함께 챌린지도 삭제(Cascade.All 설정)
        }
    }

    @Override
    public List<NoteResponseDto> filterNotes(Long userId, String regionName){ //지역 필터링(**폴더목록화면, 노트목록 화면 모두 생각해야함)
        User user = checkService.checkUser(userId);
        RegionFilter regionFilter = checkService.checkRegionFilterAndMakeOrReturn(user, regionName);

        RegionFilter regionFilter = regionFilterRepository.findByRegionName(regionName); //** 이거 갈아엎을 필요 있음,, repository 형태가..
        List<Note> notes = noteRepository.findByUserAndRegionFilter(optionalUser.get(), regionFilter);

        return notes.stream()
                .map(NoteResponseDto::new)
                .collect(Collectors.toList()); //**regionFilterDto의 리스트로 반환되어야 할듯...?그리고 dto안에는 noteDto형태여야 하나..
    }

}
