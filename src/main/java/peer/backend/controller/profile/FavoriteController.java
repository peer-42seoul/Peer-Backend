package peer.backend.controller.profile;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.profile.FavoritePage;
import peer.backend.dto.profile.response.RecruitFavoriteDto;
import peer.backend.entity.board.recruit.RecruitFavorite;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.OutOfRangeException;
import peer.backend.service.profile.FavoriteService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FavoriteController {
    private final FavoriteService favoriteService;

    private void checkType(String type) {
        if (!type.equals("PROJECT") && !type.equals("STUDY")) {
            throw new BadRequestException("잘못된 type입니다.");
        }
    }

    @ApiOperation(value = "C-MYPAGE-37, 38", notes = "관심 리스트를 가져옵니다.")
    @GetMapping("/recruit/favorite")
    public Page<RecruitFavoriteDto> getFavorite(Authentication auth,
                                                @RequestParam(value = "type") String type,
                                                @RequestParam(value = "page") int page,
                                                @RequestParam(value = "pagesize") int pageSize) {
        checkType(type);
        if (page < 1 || pageSize < 0)
            throw new OutOfRangeException("유효하지 않은 페이지 요청입니다.");
        return favoriteService.getFavorite(auth, type, page - 1, pageSize);
    }

    @ApiOperation(value = "C-MYPAGE-69", notes = "관심 리스트를 전부 삭제합니다.")
    @DeleteMapping("/recruit/favorite")
    public ResponseEntity<Object> deleteAll(Authentication auth,
                                            @RequestParam(value = "type") String type) {
        checkType(type);
        favoriteService.deleteAll(auth, type);
        return new ResponseEntity<> (HttpStatus.NO_CONTENT);
    }
}
