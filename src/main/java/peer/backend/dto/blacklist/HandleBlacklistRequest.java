package peer.backend.dto.blacklist;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import peer.backend.annotation.ValidEnum;
import peer.backend.entity.blacklist.BlacklistHandleType;

@Getter
public class HandleBlacklistRequest {

    @NotNull
    private Long blacklistId;

    @ValidEnum(enumClass = BlacklistHandleType.class)
    private BlacklistHandleType type;
}
