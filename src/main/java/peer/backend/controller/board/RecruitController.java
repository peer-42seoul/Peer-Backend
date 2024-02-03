package peer.backend.controller.board;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.annotation.AuthorCheck;
import peer.backend.dto.board.recruit.*;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.enums.RecruitFavoriteEnum;
import peer.backend.entity.user.User;
import peer.backend.service.board.recruit.RecruitService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recruit")
@Slf4j
public class RecruitController {

    private final RecruitService recruitService;

    @ApiOperation(value = "", notes = "모집게시글을 불러온다.")
    @GetMapping("/{recruit_id}")
    public RecruitResponce getRecruit(@PathVariable("recruit_id") Long recruitId,
        Authentication auth) {
        return recruitService.getRecruit(recruitId, auth);
    }

    @ApiOperation(value = "", notes = "조건에 따라 list를 반환한다.")
    @GetMapping("")
    public Page<RecruitListResponse> getRecruitListByConditions(@Valid RecruitListRequest request,
        Authentication auth) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize());
        if (auth != null)
            return recruitService.getRecruitSearchList(pageable, request, User.authenticationToUser(auth));
        else
            return recruitService.getRecruitSearchList(pageable, request, null);
    }

    @ApiOperation(value = "", notes = "모집글과 팀을 함께 생성한다.")
    @PostMapping("/write")
    public String createRecruit(@RequestBody @Valid RecruitCreateRequest request,
        Authentication auth) {
        Recruit recruit = this.recruitService.createRecruit(request, auth);
        return recruit.getId().toString();
    }

    @ApiOperation(value = "", notes = "모집글을 업데이트 한다. 팀도 함께 업데이트 한다.")
    @PutMapping("/{recruit_id}")
    @AuthorCheck
    public Long updateRecruit(@PathVariable Long recruit_id,
        @RequestBody @Valid RecruitUpdateRequestDTO recruitUpdateRequestDTO,
        Authentication auth) {
        return recruitService.updateRecruit(recruit_id, recruitUpdateRequestDTO);
    }

    @ApiOperation(value = "", notes = "모집글을 삭제한다.")
    @DeleteMapping("/{recruit_id}")
    public void deleteRecruit(@PathVariable Long recruit_id) {
        recruitService.deleteRecruit(recruit_id);
    }

    @ApiOperation(value = "", notes = "모집에 지원한다.")
    @PostMapping("/interview/{recruit_id}")
    public void applyRecruit(@PathVariable Long recruit_id,
        @RequestBody ApplyRecruitRequest request, Authentication auth) {
        recruitService.applyRecruit(recruit_id, request, auth);
    }

    @PostMapping("/favorite/{recruit_id}")
    public void goFavorite(@PathVariable Long recruit_id, Authentication auth) {
        recruitService.changeRecruitFavorite(auth, recruit_id, RecruitFavoriteEnum.LIKE);
    }

    //TODO:admin에 tag 관리 기능이 만들어지면 해당 내용 수정 필요. 추후 글 생성, 수정이 어떻게 달라질지 몰라서 일단 동일한 기능이지만 api 분리해두었음.
    @ApiOperation(value = "", notes = "글 수정을 위한 정보를 불러온다.")
    @GetMapping("/edit/{recruit_id}")
    @AuthorCheck
    public RecruitUpdateResponse getRecruitForEdit(@PathVariable("recruit_id") Long recruitId,
        Authentication auth) {
        return recruitService.getRecruitwithInterviewList(recruitId);
    }

    @ApiOperation(value = "", notes = "모집글 지원을 위한 interviewList를 불러온다.")
    @GetMapping("/interview/{post_id}")
    public List<RecruitInterviewDto> getInterviewList(@PathVariable Long post_id) {
        return recruitService.getInterviewList(post_id);
    }

    @GetMapping("/favorites")
    public List<RecruitFavoriteResponse> getFavorites(@Valid RecruitListRequest request, Authentication auth) {
        try {
            User user = User.authenticationToUser(auth);
            return recruitService.getFavoriteList(request, user);
        } catch (NullPointerException e) {
            return recruitService.getFavoriteList(request, null);
        }
    }

    @GetMapping("/favorite/{recruit_id}")
    public boolean getFavorite(@PathVariable Long recruit_id, Authentication auth){
        try {
            User user = User.authenticationToUser(auth);
            return recruitService.getFavorite(recruit_id, user);
        } catch (NullPointerException e) {
            return recruitService.getFavorite(recruit_id, null);
        }
    }

}
