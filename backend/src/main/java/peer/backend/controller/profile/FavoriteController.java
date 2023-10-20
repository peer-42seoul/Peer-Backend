package peer.backend.controller.profile;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.profile.FavoritePage;
import peer.backend.dto.profile.FavoriteResponse;
import peer.backend.exception.BadRequestException;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.service.profile.FavoriteService;

import java.security.Principal;
import java.security.Security;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FavoriteController {
    private final FavoriteService favoriteService;

    private void checkType(String type) {
        if (!type.equals("project") && !type.equals("study")) {
            throw new BadRequestException("잘못된 type입니다.");
        }
    }

    private PrincipalDetails getPrincipalDetails(Principal principal) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (PrincipalDetails) auth.getPrincipal();
    }

    @ApiOperation(value = "C-MYPAGE-37, 38", notes = "관심 리스트를 가져옵니다.")
    @GetMapping("/recruit/favorite")
    public ResponseEntity<Object> getFavorite(Principal principal,
                                              @RequestParam(value = "type") String type,
                                              @RequestParam(value = "page") int page,
                                              @RequestParam(value = "pagesize") int pageSize) {
        checkType(type);
        FavoritePage ret = favoriteService.getFavorite(getPrincipalDetails(principal), type, page, pageSize);
        return new ResponseEntity<> (ret, HttpStatus.OK);
    }

    @ApiOperation(value = "C-MYPAGE-69", notes = "관심 리스트를 전부 삭제합니다.")
    @DeleteMapping("/recruit/favorite")
    public ResponseEntity<Object> deleteAll(Principal principal,
                                            @RequestParam(value = "type") String type) {
        checkType(type);
        favoriteService.deleteAll(getPrincipalDetails(principal), type);
        return new ResponseEntity<> (HttpStatus.CREATED);
    }
}
