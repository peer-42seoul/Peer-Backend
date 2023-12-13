package peer.backend.service.blacklist;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.entity.blacklist.Blacklist;
import peer.backend.entity.blacklist.BlacklistType;
import peer.backend.entity.report.ReportProcessingType;
import peer.backend.entity.user.User;
import peer.backend.repository.blacklist.BlacklistRepository;

@RequiredArgsConstructor
@Service
public class BlacklistService {

    private final BlacklistRepository blacklistRepository;

    @Transactional
    public void addBlacklistToUserList(List<User> userList,
        BlacklistType type, String content) {
        List<Blacklist> blacklist = new ArrayList<>();

        for (User user : userList) {
            blacklist.add(new Blacklist(user, type, content));
        }

        this.blacklistRepository.saveAll(blacklist);
    }

    public BlacklistType getBlacklistTypeToReportProcessingType(
        ReportProcessingType reportProcessingType) {
        BlacklistType result = null;

        if (Objects.requireNonNull(reportProcessingType)
            .equals(ReportProcessingType.PERMANENT_BAN)) {
            result = BlacklistType.PERMANENT_BAN;
        }

        return result;
    }
}
