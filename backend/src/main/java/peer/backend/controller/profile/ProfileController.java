package peer.backend.controller.profile;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.profile.request.LinkListRequest;
import peer.backend.dto.profile.response.NicknameResponse;
import peer.backend.dto.profile.request.UserLinkDTO;
import peer.backend.exception.BadRequestException;
import peer.backend.service.profile.ProfileService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProfileController{
    private final ProfileService profileService;

    @ApiOperation(value = "C-MYPAGE-", notes = "사용자 프로필 정보 조회하기")
    @GetMapping("/profile")
    public ResponseEntity<Object> getProfile(Authentication auth) {
        return new ResponseEntity<> (profileService.getProfile(auth.getName()), HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "사용자 프로필 닉네임 중복 확인하기.")
    @PostMapping("/signup/nickname") // "/membership/nickname/check" 로 테스트 진행 했음
    public ResponseEntity<Object> isExistNickname(@RequestBody NicknameResponse nickname) {
        if (profileService.isExistNickname(nickname.getNickname())) {
            throw new BadRequestException("이미 사용 중인 닉네임입니다.");
        }
        return new ResponseEntity<> (HttpStatus.OK);
    }

    @ApiOperation(value = "C-MYPAGE-20", notes = "사용자 프로필 정보 링크 수정하기")
    @PutMapping("/profile/link")
    public ResponseEntity<Object> editLinks(Authentication auth, @RequestBody LinkListRequest linkList) {
        List<UserLinkDTO> links = linkList.getLinkList();
        for (UserLinkDTO link : links) {
            if (link.getLinkName().isBlank() || link.getLinkName().isEmpty())
                throw new BadRequestException("링크 이름이 없습니다.");
            if (link.getLinkUrl().isBlank() || link.getLinkUrl().isEmpty())
                throw new BadRequestException("링크 URL이 없습니다.");
            if (link.getLinkName().length() > 20 || link.getLinkUrl().length() > 100)
                throw new BadRequestException("링크 글자 수가 너무 많습니다.");
        }
        profileService.editLinks(auth.getName(), linkList.getLinkList());
        return new ResponseEntity<> (HttpStatus.CREATED);
    }
}
