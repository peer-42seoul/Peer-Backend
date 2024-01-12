//package peer.backend.entity.noti;
//
//import lombok.*;
//import org.hibernate.annotations.DynamicUpdate;
//import org.hibernate.annotations.OnDelete;
//import org.hibernate.annotations.OnDeleteAction;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//import peer.backend.entity.BaseEntity;
//import peer.backend.dto.noti.enums.AlarmType;
//
//import javax.persistence.*;
//
//@Entity
//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@EntityListeners(AuditingEntityListener.class)
//@DynamicUpdate
//public class NotificationTargetUser extends BaseEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long logId;
//
////    @Column
////    private Long targetUserId;
////
////    @Column
////    private Boolean read;
////
////    @Column
////    private Boolean deleted;
//
////    @Column
////    @Enumerated(EnumType.STRING)
////    private AlarmType alarmType;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "notification_id")
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    private Notification targetNotification;
//}
