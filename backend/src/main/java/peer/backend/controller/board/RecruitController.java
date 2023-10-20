package peer.backend.controller.board;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.Board.Recruit.RecruitUpdateRequestDTO;
import peer.backend.dto.board.recruit.RecruitListResponce;
import peer.backend.dto.board.recruit.RecruitRequest;
import peer.backend.dto.board.recruit.RecruitRequestDTO;
import peer.backend.dto.board.recruit.RecruitResponce;
import peer.backend.dto.team.TeamApplicantListDto;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.RecruitApplicant;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.RecruitRole;
import peer.backend.entity.board.recruit.enums.RecruitApplicantStatus;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.service.board.recruit.RecruitService;

import javax.print.attribute.standard.PageRanges;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recruit")
public class RecruitController {
    private final RecruitService recruitService;

//    @ApiOperation(value = "", notes = "조건에 맞는 모집게시글 리스트를 불러온다.")
//    @GetMapping("")
//    public List<RecruitListResponce> getRecruitSearchList(@RequestParam Long page, @RequestParam Long pageSize, @ModelAttribute RecruitRequest recruitRequest){
//        //TODO: 페이지로 변환 필요, 쿼리 한땀한땀 자아낼 예정
//        return recruitService.getRecruitSearchList(page, pageSize, recruitRequest);
//    }

    @ApiOperation(value = "", notes = "모집게시글을 불러온다.")
    @GetMapping("/{recruit_id}")
    public RecruitResponce getRecruit(Long recruit_id){
        return  recruitService.getRecruit(recruit_id);
    }

    @ApiOperation(value = "", notes = "모집게시글 리스트를 불러온다.")
    @GetMapping("")
    public Page<RecruitListResponce> getAllRecruits(@RequestParam int page, @RequestParam int pageSize, Principal principal) {
        return recruitService.getRecruitList(page, pageSize, principal);
    }

    @ApiOperation(value = "", notes = "모집글과 팀을 함께 생성한다.")
    @PostMapping("")
    public void createRecruit(@RequestBody RecruitRequestDTO recruitRequestDTO){
        recruitService.createRecruit(recruitRequestDTO);
    }

    @ApiOperation(value = "", notes = "모집글을 업데이트 한다. 팀도 함께 업데이트 한다.")
    @PutMapping("/{recruit_id}")
    public void updateRecruit(@PathVariable Long recruit_id, @RequestBody RecruitUpdateRequestDTO recruitUpdateRequestDTO, Principal principal){
        //TODO:principal로 권한검사
        recruitService.updateRecruit(recruit_id, recruitUpdateRequestDTO);
    }

    @ApiOperation(value = "", notes = "모집글을 삭제한다.")
    @DeleteMapping("/{recruit_id}")
    public void deleteRecruit(@PathVariable Long recruit_id){
        recruitService.deleteRecruit(recruit_id);
    }


//    @GetMapping("/test/{user_id}")
//    public List<TeamApplicantListDto> test(@PathVariable Long user_id) {
//        return recruitService.getTeamApplicantList(user_id);
//    }
}
