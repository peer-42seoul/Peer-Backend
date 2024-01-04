package peer.backend.service.blacklist;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import peer.backend.annotation.tracking.UserBanTracking;
import peer.backend.entity.blacklist.Blacklist;
import peer.backend.entity.blacklist.BlacklistType;
import peer.backend.entity.report.ReportHandleType;
import peer.backend.entity.user.User;
import peer.backend.repository.blacklist.BlacklistRepository;
import peer.backend.service.UserService;

@RequiredArgsConstructor
@Service
public class BlacklistService {

    private final BlacklistRepository blacklistRepository;
    private final UserService userService;

    @UserBanTracking
    @Transactional
    public List<Blacklist> addBlacklistToUserList(List<User> userList,
        BlacklistType type, String content) {
        List<Blacklist> blacklist = new ArrayList<>();

        for (User user : userList) {
            blacklist.add(new Blacklist(user, type, content));
        }

        this.blacklistRepository.saveAll(blacklist);
        return blacklist;
    }

    @Transactional
    public Page<Blacklist> getBlacklists(Pageable pageable) {
        return this.blacklistRepository.findAll(pageable);
    }

    @Transactional
    public void addBlacklistToEmail(String email, BlacklistType type, String content) {
        User user = this.userService.findByEmail(email);
        this.blacklistRepository.save(new Blacklist(user, type, content));
    }

    @Transactional
    public void deleteBlacklist(Long blacklistId) {
        this.blacklistRepository.deleteById(blacklistId);
    }

    @Transactional
    public boolean isExistsByUserId(Long userId) {
        return this.blacklistRepository.existsByUserId(userId);
    }

    public BlacklistType getBlacklistTypeToReportHandleType(
        ReportHandleType reportHandleType) {
        BlacklistType result = null;

        if (Objects.requireNonNull(reportHandleType)
            .equals(ReportHandleType.PERMANENT_BAN)) {
            result = BlacklistType.PERMANENT_BAN;
        }

        return result;
    }
}
