package peer.backend.service.dnd;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.dnd.RequestDnDDTO;
import peer.backend.entity.team.Team;
import peer.backend.mongo.entity.TeamDnD;
import peer.backend.mongo.repository.PeerLogDnDRepository;
import peer.backend.mongo.repository.TeamDnDRepository;
import peer.backend.repository.team.TeamRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DnDService {

    private final TeamDnDRepository teamDnDRepository;
    private final PeerLogDnDRepository peerLogDnDRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public TeamDnD createDnD(TeamDnD data) throws RuntimeException {
        Optional<Team> target = teamRepository.findById(data.getTeamId());
        if (target.isEmpty()) {
            throw new NoSuchElementException("Plz, check, There is no team id");
        }
        TeamDnD saveData = TeamDnD.builder()
                .teamId(data.getTeamId())
                .type(data.getType())
                .widgets(data.getWidgets())
                .build();

        TeamDnD ret;
        try {
            if (data.getType().equals("team"))
                ret = this.teamDnDRepository.save(saveData);
            else
                ret = this.peerLogDnDRepository.save(saveData);
        } catch (Exception e) {
            throw new RuntimeException("DB Server makes an error.");
        }
        return ret;
    }

    @Transactional(readOnly = true)
    public TeamDnD getDnD(RequestDnDDTO data) throws NoSuchElementException {
        TeamDnD ret;
        if (data.getType().equals("team")) {
            ret = this.teamDnDRepository.findByTeamId(data.getTeamId());
        }
        else {
            ret = this.peerLogDnDRepository.findByTeamId(data.getTeamId());
        }
        if (ret == null) {
            throw new NoSuchElementException("There is no DnD Data");
        }
        return ret;
    }

    @Transactional
    public TeamDnD updateDnD(TeamDnD data) throws Exception {
        TeamDnD example = new TeamDnD();
        TeamDnD ret;
        example.setTeamId(data.getTeamId());
        try {
            if (data.getType().equals("team")) {
                if (!this.teamDnDRepository.exists(Example.of(example))){
                    throw new NoSuchElementException("There is no that Dnd File.");
                }
                example = this.teamDnDRepository.findByTeamId(data.getTeamId());
                example.setWidgets(data.getWidgets());
                ret = this.teamDnDRepository.save(example);
            } else {
                if (!this.peerLogDnDRepository.exists(Example.of(example))){
                    throw new NoSuchElementException("There is no that Dnd File.");
                }
                example = this.peerLogDnDRepository.findByTeamId(data.getTeamId());
                example.setWidgets(data.getWidgets());
                ret = this.peerLogDnDRepository.save(data);
            }
        } catch (Exception e) {
            throw new Exception("DnD file update failed!");
        }
        return ret;
    }

    @Transactional
    public void deleteDnD(RequestDnDDTO data) throws RuntimeException {
        TeamDnD example = new TeamDnD();
        example.setTeamId(data.getTeamId());
        try {
            if (data.getType().equals("team")) {
                if (!this.teamDnDRepository.exists(Example.of(example))){
                    throw new NoSuchElementException("There is no that Dnd File.");
                }
                this.teamDnDRepository.deleteByTeamId(data.getTeamId());
            }
            else {
                if (!this.peerLogDnDRepository.exists(Example.of(example))){
                    throw new NoSuchElementException("There is no that Dnd File.");
                }
                this.peerLogDnDRepository.deleteByTeamId(data.getTeamId());
            }
        } catch (Exception e) {
            throw new RuntimeException("DnD file delete is failed!");
        }

    }
}
