package peer.backend.entity.board;

import javax.persistence.GeneratedValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardType {

    PROJECT("프로젝트"),
    STUDY("스터디")
    ;
    private final String type;
}
