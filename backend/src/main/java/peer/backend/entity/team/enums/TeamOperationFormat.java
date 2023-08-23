package peer.backend.entity.team.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TeamOperationFormat {
    ONLINE("온라인"),
    OFFLINE("오프라인"),
    MIX("온라인/오프라인");

    private final String value;
}
