package peer.backend.controller.profile;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
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
    @PostMapping("/membership/nickname/check")
    public ResponseEntity<Object> isExistNickname(@RequestBody NicknameResponse nickname) {
        if (profileService.isExistNickname(nickname.getNickname())) {
            throw new BadRequestException("이미 사용 중인 닉네임입니다.");
        }
        return new ResponseEntity<> (HttpStatus.OK);
    }

    @PutMapping("/profile/link")
    public ResponseEntity<Object> editLinks(Authentication auth, @RequestBody List<UserLinkDTO> linkList) {
        if (linkList.size() > 3) {
            throw new BadRequestException("링크의 개수가 너무 많습니다.");
        }
        profileService.editLinks(auth.getName(), linkList);
        return new ResponseEntity<> (HttpStatus.CREATED);
    }
}
