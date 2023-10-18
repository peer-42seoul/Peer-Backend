package peer.backend.controller.board;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.Board.Recruit.RecruitUpdateRequestDTO;
import peer.backend.dto.board.recruit.RecruitRequestDTO;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.service.board.recruit.RecruitService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recruit")
public class RecruitController {
    private final RecruitService recruitService;

    @ApiOperation(value = "", notes = "모집글과 팀을 함께 생성한다.")
    @PostMapping("")
    public void createRecruit(@RequestBody RecruitRequestDTO recruitRequestDTO){
        recruitService.createRecruit(recruitRequestDTO);
    }

    @ApiOperation(value = "", notes = "모집글을 업데이트 한다. 팀도 함께 업데이트 한다.")
    @PutMapping("/{recruit_id}")
    public void updateRecruit(@PathVariable Long recruit_id, @RequestParam RecruitUpdateRequestDTO recruitUpdateRequestDTO){
        recruitService.updateRecruit(recruit_id, recruitUpdateRequestDTO);
    }
}
