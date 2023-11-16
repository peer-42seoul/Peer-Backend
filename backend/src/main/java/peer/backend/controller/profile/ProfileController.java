package peer.backend.controller.profile;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.profile.request.EditProfileRequest;
import peer.backend.dto.profile.request.LinkListRequest;
import peer.backend.dto.profile.response.NicknameResponse;
import peer.backend.dto.profile.request.UserLinkRequest;
import peer.backend.dto.profile.response.OtherProfileResponse;
import peer.backend.dto.profile.response.OtherProfileImageUrlResponse;
import peer.backend.dto.profile.response.OtherProfileNicknameResponse;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.ConflictException;
import peer.backend.service.profile.ProfileService;

import javax.persistence.LockModeType;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@EnableAspectJAutoProxy
public class ProfileController {

    private final ProfileService profileService;

    private boolean convertBoolean(String boolString) throws BadRequestException{
        if (boolString.equals("TRUE"))
            return true;
        else if (boolString.equals("FALSE"))
            return false;
        else
            throw new BadRequestException("올바른 Bool값이 아닙니다.");
    }

    @ApiOperation(value = "C-MYPAGE-", notes = "사용자 프로필 정보 조회하기")
    @GetMapping("/profile")
    public ResponseEntity<Object> getProfile(Authentication auth) {
        return new ResponseEntity<>(
            profileService.getProfile(auth), HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "닉네임 중복 확인하기.")
    @PostMapping("/signup/nickname") // "/membership/nickname/check" 로 테스트 진행 했음
    public ResponseEntity<Object> isExistNickname(@RequestBody NicknameResponse nickname) {
        if (profileService.isExistNickname(nickname.getNickname())) {
            throw new ConflictException("이미 사용 중인 닉네임입니다.");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @ApiOperation(value = "C-MYPAGE-20", notes = "사용자 프로필 정보 링크 수정하기")
    @PutMapping("/profile/link")
    public ResponseEntity<Object> editLinks(Authentication auth,
        @RequestBody LinkListRequest linkList) {
        List<UserLinkRequest> links = linkList.getLinkList();
        if (links.size() > 3) {
            throw new BadRequestException("링크는 3개만 등록할 수 있습니다.");
        }
        for (UserLinkRequest link : links) {
            if (link.getLinkName().length() > 20 || link.getLinkUrl().length() > 300) {
                throw new BadRequestException("링크 글자 수가 너무 많습니다.");
            }
        }
        profileService.editLinks(auth, linkList.getLinkList());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "C-MYPAGE-09", notes = "다른 사용자 프로필 정보 조회하기")
    @GetMapping("/profile/other")
    public ResponseEntity<Object> getOtherProfile(@RequestParam(value = "userId") Long userId,
                                                  @RequestParam(value = "infoList") List<String> infoList) {
        OtherProfileResponse otherProfile = profileService.getOtherProfile(userId, infoList);
        if (infoList.size() == 1) {
            if (otherProfile.getNickname() == null) {
                OtherProfileImageUrlResponse otherUrl = new OtherProfileImageUrlResponse(
                    otherProfile.getProfileImageUrl());
                return new ResponseEntity<>(otherUrl, HttpStatus.OK);
            }
            if (otherProfile.getProfileImageUrl() == null) {
                OtherProfileNicknameResponse otherNickname = new OtherProfileNicknameResponse(
                    otherProfile.getNickname());
                return new ResponseEntity<>(otherNickname, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(otherProfile, HttpStatus.OK);
    }

    @ApiOperation(value = "C-MYPAGE-", notes = "사용자 프로필 정보 수정 하기")
    @PutMapping("/profile/introduction/edit")
    public ResponseEntity<Object> editProfile(Authentication auth,
        @ModelAttribute @Valid EditProfileRequest profile) throws IOException {
        if (profile.getNickname().isEmpty() || profile.getNickname().isBlank()) {
            throw new BadRequestException("닉네임은 반드시 입력해야 합니다.");
        }
        else if (profile.getNickname().length() > 7 || profile.getNickname().length() < 2) {
            throw new BadRequestException("닉네임은 2자 이상, 7자 이하여야 합니다.");
        }
        if (profile.getIntroduction() != null && profile.getIntroduction().length() > 150) {
            throw new BadRequestException("자기소개는 150자 이내여야 합니다.");
        }
        profileService.editProfile(auth, profile, convertBoolean(profile.getImageChange()));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
