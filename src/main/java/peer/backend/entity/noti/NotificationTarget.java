package peer.backend.entity.noti;

import lombok.*;
import peer.backend.comparator.LongComparator;
import peer.backend.dto.noti.enums.NotificationType;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.user.User;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.*;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notification_target")
public class NotificationTarget extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long notificationId;

    @Column(nullable = false)
    private Long columnIndex;

    @Column
    private String userList;

    @Column
    private NotificationType messageType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_event_id")
    private Notification specificEvent;

    private void repackUserIds(List<Long> userIds) {
        this.userList = "";
        userIds.forEach(l -> {
            this.userList += l + "###";
        });
    }

    public List<Long> getUserIds(){
        if (userList.isEmpty())
            return null;
        List<String> users = List.of(this.userList.split("###"));
        List<Long> results = new ArrayList<>();
        users.forEach(m -> {
            Long id = Long.parseLong(m);
            results.add(id);
        });
        return results;
    }

    @Transactional
    public void appendUserId(Long userId) {
        List<Long> userIds = this.getUserIds();
        if (userIds == null) {
            this.userList = userId + "###";
        }
        else {
            userIds.add(userId);
            userIds.sort(new LongComparator());
            this.repackUserIds(userIds);
        }
        this.specificEvent.referenceCounter++;
    }

    @Transactional
    public boolean deleteUserId(Long userId) {
        List<Long> userIds = this.getUserIds();
        int index = userIds.indexOf(userId);
        if (index == -1)
            return false;
        else {
            userIds.remove(index);
            this.repackUserIds(userIds);
            this.specificEvent.referenceCounter--;
        }
        return true;
    }

    public boolean findUserId(Long userId){
        List<Long> userIds = this.getUserIds();
        int index = userIds.indexOf(userId);
        return index != -1;
    }
}
