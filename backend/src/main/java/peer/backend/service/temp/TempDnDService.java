package peer.backend.service.temp;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.temp.RequestDnDDTO;
import peer.backend.entity.team.Team;
import peer.backend.mongo.entity.TeamDnD;
import peer.backend.mongo.repository.PeerLogDnDRepository;
import peer.backend.mongo.repository.TeamDnDRepository;
import peer.backend.repository.team.TeamRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TempDnDService {

    private final TeamDnDRepository teamDnDRepository;
    private final PeerLogDnDRepository peerLogDnDRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public void createDnD(TeamDnD data) throws RuntimeException {
        Optional<Team> target = teamRepository.findById(data.getTeamId());
        if (target.isEmpty()) {
            throw new NoSuchElementException("Plz, check, There is no team id");
        }
        TeamDnD saveData = TeamDnD.builder()
                .teamId(data.getTeamId())
                .type(data.getType())
                .widgets(data.getWidgets())
                .build();

        try {
            if (data.getType().equals("team"))
                this.teamDnDRepository.save(saveData);
            else
                this.peerLogDnDRepository.save(saveData);
        } catch (Exception e) {
            throw new RuntimeException("DB Server makes an error.");
        }
    }

    @Transactional(readOnly = true)
    public TeamDnD getDnD(RequestDnDDTO data) throws NoSuchElementException {
        Optional<TeamDnD> ret;
        if (data.getType().equals("team")) {
            ret = this.teamDnDRepository.findById(data.getMongoId());
        }
        else {
            ret = this.peerLogDnDRepository.findById(data.getMongoId());
        }
        if (ret.isEmpty()) {
            throw new NoSuchElementException("There is no DnD Data");
        }
        return ret.get();
    }

    @Transactional
    public void updateDnD(TeamDnD data) throws Exception {
        TeamDnD example = new TeamDnD();
        example.set_id(data.get_id());
        try {
            if (data.getType().equals("team")) {
                if (!this.teamDnDRepository.exists(Example.of(example))){
                    throw new NoSuchElementException("There is no that Dnd File.");
                }
                this.teamDnDRepository.save(data);
            } else {
                if (!this.peerLogDnDRepository.exists(Example.of(example))){
                    throw new NoSuchElementException("There is no that Dnd File.");
                }
                this.peerLogDnDRepository.save(data);
            }
        } catch (Exception e) {
            throw new Exception("DnD file update failed!");
        }
    }

    @Transactional
    public void deleteDnD(RequestDnDDTO data) throws RuntimeException {
        TeamDnD example = new TeamDnD();
        example.set_id(data.getMongoId());
        try {
            if (data.getType().equals("team")) {
                if (!this.teamDnDRepository.exists(Example.of(example))){
                    throw new NoSuchElementException("There is no that Dnd File.");
                }
                this.teamDnDRepository.deleteById(data.getMongoId());
            }
            else {
                if (!this.peerLogDnDRepository.exists(Example.of(example))){
                    throw new NoSuchElementException("There is no that Dnd File.");
                }
                this.peerLogDnDRepository.deleteById(data.getMongoId());
            }
        } catch (Exception e) {
            throw new RuntimeException("DnD file delete is failed!");
        }

    }
}
