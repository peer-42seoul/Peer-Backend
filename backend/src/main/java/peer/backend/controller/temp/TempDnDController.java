package peer.backend.controller.temp;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.temp.RequestDnDDTO;
import peer.backend.mongo.entity.TeamDnD;
import peer.backend.service.team.TeamService;
import peer.backend.service.temp.TempDnDService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/temp/dnd")
public class TempDnDController {
    private final TempDnDService tempDnDService;
    private final TeamService teamService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody TeamDnD data) {
        try {
            this.tempDnDService.createDnD(data);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/read")
    public ResponseEntity<?> read(@RequestBody RequestDnDDTO data) {
        TeamDnD ret;
        try {
            ret = this.tempDnDService.getDnD(data);
            if (ret == null)
                return new ResponseEntity<>("There is no that DnD data", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<TeamDnD>(ret,HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody TeamDnD data) {
        try{
            this.tempDnDService.updateDnD(data);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody RequestDnDDTO data) {
        try {
            this.tempDnDService.deleteDnD(data);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
