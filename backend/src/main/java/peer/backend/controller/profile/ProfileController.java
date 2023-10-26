package peer.backend.controller.profile;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.ConflictException;
import peer.backend.service.profile.ProfileService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProfileController {

    private final ProfileService profileService;

    @ApiOperation(value = "C-MYPAGE-", notes = "사용자 프로필 정보 조회하기")
    @GetMapping("/profile")
    public ResponseEntity<Object> getProfile(Authentication auth) {
        return new ResponseEntity<>(
            profileService.getProfile(User.authenticationToUser(auth)), HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "닉네임 중복 확인하기.")
    @PostMapping("/signup/nickname") // "/membership/nickname/check" 로 테스트 진행 했음
    public ResponseEntity<Object> isExistNickname(@RequestBody NicknameResponse nickname) {
        if (profileService.isExistNickname(nickname.getNickname())) {
            throw new ConflictException("이미 사용 중인 닉네임입니다.");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "C-MYPAGE-20", notes = "사용자 프로필 정보 링크 수정하기")
    @PutMapping("/profile/link")
    public ResponseEntity<Object> editLinks(Authentication auth,
        @RequestBody LinkListRequest linkList) {
        List<UserLinkRequest> links = linkList.getLinkList();
        for (UserLinkRequest link : links) {
            if (link.getLinkName().isBlank() || link.getLinkName().isEmpty()) {
                throw new BadRequestException("링크 이름이 없습니다.");
            }
            if (link.getLinkUrl().isBlank() || link.getLinkUrl().isEmpty()) {
                throw new BadRequestException("링크 URL이 없습니다.");
            }
            if (link.getLinkName().length() > 20 || link.getLinkUrl().length() > 300) {
                throw new BadRequestException("링크 글자 수가 너무 많습니다.");
            }
        }
        profileService.editLinks(User.authenticationToUser(auth), linkList.getLinkList());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "C-MYPAGE-09", notes = "다른 사용자 프로필 정보 조회하기")
    @GetMapping("/profile/other")
    public ResponseEntity<Object> getOtherProfile(Authentication auth,
        @RequestParam(value = "userId", required = true) Long userId,
        @RequestParam(value = "infoList", required = true) List<String> infoList) {
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
        @ModelAttribute EditProfileRequest profile) throws IOException {
        if (profile.getIntroduction().length() > 150) {
            throw new BadRequestException("자기소개는 150자 이내여야 합니다.");
        }
        if (profile.getNickname().isEmpty()) {
            throw new BadRequestException("닉네임은 반드시 입력해야 합니다.");
        }
        if (profile.getNickname().isBlank()) {
            throw new BadRequestException("닉네임은 반드시 입력해야 합니다.");
        }
        if (profile.getNickname().length() > 7 || profile.getNickname().length() < 3) {
            throw new BadRequestException("닉네임은 7자 이내여야 합니다.");
        }
        profileService.editProfile(User.authenticationToUser(auth), profile);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
