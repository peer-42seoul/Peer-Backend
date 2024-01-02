package peer.backend.controller.dnd;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.dnd.RequestDnDDTO;
import peer.backend.entity.user.User;
import peer.backend.mongo.entity.TeamDnD;
import peer.backend.service.dnd.DnDService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dnd-main")
public class DnDController {
    private final DnDService dndService;

    @PostMapping("/create")
    public ResponseEntity<?> create(Authentication auth, @RequestBody TeamDnD data) {
        TeamDnD ret;

        if (this.dndService.checkValidMemberFromTeam(data.getTeamId(), User.authenticationToUser(auth))){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            ret = this.dndService.createDnD(data);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<TeamDnD>(ret,HttpStatus.CREATED);
    }

    @PostMapping("/read")
    public ResponseEntity<?> read(Authentication auth, @RequestBody peer.backend.dto.dnd.RequestDnDDTO data) {
        TeamDnD ret;

        if (this.dndService.checkValidMemberFromTeam(data.getTeamId(), User.authenticationToUser(auth))){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            ret = this.dndService.getDnD(data);
            if (ret == null)
                return new ResponseEntity<>("There is no that DnD data", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<TeamDnD>(ret,HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(Authentication auth, @RequestBody TeamDnD data) {
        TeamDnD ret;

        if (this.dndService.checkValidMemberFromTeam(data.getTeamId(), User.authenticationToUser(auth))){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try{
            ret = this.dndService.updateDnD(data);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<TeamDnD>(ret, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(Authentication auth, @RequestBody RequestDnDDTO data) {

        if (this.dndService.checkValidMemberFromTeam(data.getTeamId(), User.authenticationToUser(auth))){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            this.dndService.deleteDnD(data);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
