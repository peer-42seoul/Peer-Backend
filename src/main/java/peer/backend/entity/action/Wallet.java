package peer.backend.entity.action;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "wallet")
public class Wallet {

    @Id
    private Long actionTypeCode;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_type_code")
    private ActionType actionType;

    @Column(nullable = false)
    private Long value;

    public Wallet(ActionType actionType, Long value) {
        this.actionType = actionType;
        this.value = value;
    }
}
