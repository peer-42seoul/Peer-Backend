package peer.backend.entity.action;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    private Long actionTypeCode;

    @MapsId
    @OneToOne
    @JoinColumn(name = "action_type_code")
    private ActionType actionType;

    @Column(nullable = false)
    private Long value;
}
