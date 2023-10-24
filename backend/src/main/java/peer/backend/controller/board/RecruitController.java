package peer.backend.controller.board;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.Board.Recruit.RecruitUpdateRequestDTO;
import peer.backend.dto.board.recruit.*;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.board.recruit.RecruitService;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recruit")
public class RecruitController {
    private final RecruitService recruitService;
    private final UserRepository userRepository;

//    @ApiOperation(value = "", notes = "조건에 맞는 모집게시글 리스트를 불러온다.")
//    @GetMapping("")
//    public List<RecruitListResponse> getRecruitSearchList(@RequestParam Long page, @RequestParam Long pageSize, @ModelAttribute RecruitRequest recruitRequest){
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
    public Page<RecruitListResponse> getAllRecruits(@RequestParam int page, @RequestParam int pageSize, Principal principal) {
        return recruitService.getRecruitList(page, pageSize, principal);
    }

    @ApiOperation(value = "", notes = "모집글과 팀을 함께 생성한다.")
    @PostMapping("")
    public void createRecruit(@RequestBody RecruitRequestDTO recruitRequestDTO) throws IOException{
        recruitService.createRecruit(recruitRequestDTO);
    }

    @ApiOperation(value = "", notes = "모집글을 업데이트 한다. 팀도 함께 업데이트 한다.")
    @PutMapping("/{recruit_id}")
    public void updateRecruit(@PathVariable Long recruit_id, @RequestBody RecruitUpdateRequestDTO recruitUpdateRequestDTO, Principal principal) throws IOException {
        //TODO:principal로 권한검사
        recruitService.updateRecruit(recruit_id, recruitUpdateRequestDTO);
    }

    @ApiOperation(value = "", notes = "모집글을 삭제한다.")
    @DeleteMapping("/{recruit_id}")
    public void deleteRecruit(@PathVariable Long recruit_id){
        recruitService.deleteRecruit(recruit_id);
    }


    @GetMapping("/test/{user_id}")
    public Page<RecruitListResponse> getRecruitListByConditions(@PathVariable Long user_id, @RequestParam int page, @RequestParam int pageSize, @ModelAttribute RecruitRequest request) throws IOException {
        Pageable pageable = PageRequest.of(page, pageSize);
        return recruitService.getRecruitSearchList(pageable, request, user_id);
    }
}
