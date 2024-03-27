package peer.backend.entity.noti;

import lombok.*;
import peer.backend.dto.noti.SubscriptionDTO;
import peer.backend.dto.noti.enums.DeviceType;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.user.User;

import javax.persistence.*;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notification_subscription_keys")
public class NotificationSubscriptionKeys extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String subscriptionKey;

    @Column
    private Long userId;

    @Column
    private DeviceType type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber")
    private User user;
    
    public void convertRawData(User user, SubscriptionDTO data) {
        this.subscriptionKey = data.getFirebaseToken();
        this.userId  = user.getId();
        DeviceType target = null;
        switch (data.getDeviceInfo()) {
            case "iOS" :
                target = DeviceType.MOBILE_I;
                break;
            case "android" :
                target = DeviceType.MOBILE_A;
                break;
            case "Mac" :
                target = DeviceType.PC_I;
                break;
            case "Windows" :
                target = DeviceType.PC_W;
                break;
            case "Others" :
                target = DeviceType.PC_O;
                break;
        }
        this.type = target;
        this.user = user;
    }
}
