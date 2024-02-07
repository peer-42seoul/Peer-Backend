package peer.backend.controller.profile;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.annotation.CustomSize;
import peer.backend.annotation.OnlyEngKorNum;
import peer.backend.dto.profile.SkillDTO;
import peer.backend.dto.profile.request.EditProfileRequest;
import peer.backend.dto.profile.request.LinkListRequest;
import peer.backend.dto.profile.response.*;
import peer.backend.dto.profile.request.UserLinkRequest;
import peer.backend.entity.tag.Tag;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.ConflictException;
import peer.backend.service.profile.ProfileService;
import peer.backend.service.profile.UserPortfolioService;

import javax.persistence.LockModeType;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@EnableAspectJAutoProxy
public class ProfileController {

    private final ProfileService profileService;
    private final UserPortfolioService userPortfolioService;

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
        MyProfileResponse result = this.profileService.getProfile(auth);
        if (result == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "C-MYPAGE-", notes = "사용자 프로필 정보 조회하기")
    @GetMapping("/profile/otherUser")
    public ResponseEntity<Object> getOtherProfile(Authentication auth, @Param("userId") Long userId) {
        OtherProfileResponseDTO result;
        try {
             result = this.profileService.getOtherProfile(userId);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        if (result == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "닉네임 중복 확인하기.")
    @PostMapping("/signup/nickname") // "/membership/nickname/check" 로 테스트 진행 했음
    public ResponseEntity<Object> isExistNickname(@RequestBody @Valid NicknameResponse nickname) {
        if (profileService.isExistNickname(nickname.getNickname())) {
            return new ResponseEntity<>("닉네임이 중복됩니다. 다른 닉네임으로 시도해 주세요.", HttpStatus.CONFLICT);
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
        try {
            profileService.editProfile(auth, profile, convertBoolean(profile.getImageChange()));
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "", notes = "사용자가 스킬 tag 를 검색한다.")
    @GetMapping("/skill/search")
    public ResponseEntity<?> getSkilsList(Authentication auth,
                                         @RequestParam("keyword") String keyword) {
        if (keyword.length() > 15)
            return new ResponseEntity<>("검색 가능한 글자 수는 최소 1자부터 최대 15자까지 입니다.", HttpStatus.BAD_REQUEST);

        List<SkillDTO> result = this.profileService.searchTagsWithKeyword(keyword);
        if (result.isEmpty())
            return new ResponseEntity<>("태그가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "사용자가 스킬을 등록한다.")
    @PutMapping("/skill/regist")
    public ResponseEntity<?> setSkillList(Authentication auth, @RequestBody() List<SkillDTO> skillsDTOList){
        try {
            this.profileService.setUserSkills(User.authenticationToUser(auth), skillsDTOList);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "사용자가 작업물의 공개 여부를 결정한다.")
    @GetMapping("/myPortfolio")
    public ResponseEntity<?> setVisibilityForMyPortfolio(Authentication auth
            , @RequestParam("visibility") boolean visibility) {
        try {
            this.userPortfolioService.setVisibilityForMyPortfolioLogic(User.authenticationToUser(auth), visibility);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "내 프로필 페이지에서 사용자의 작업물 리스트를 호출한다.")
    @GetMapping("myPortfolio/list")
    public ResponseEntity<?> getMyPortfolioList(Authentication auth, @RequestParam("page") Long number) {
        List<PortfolioDTO> responseBody = this.userPortfolioService
                .getMyPortfolioList(User.authenticationToUser(auth), number);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "다른 사용자의 작업물 리스트를 호출한다.")
    @GetMapping("otherPortfolio/list")
    public ResponseEntity<?> getOtherPortfolioList(Authentication auth, @RequestParam("userId") Long targetId, @RequestParam("page") Long number) {
        List<PortfolioDTO> responseBody = this.userPortfolioService
                .getOtherPortfolioList(targetId, number);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
