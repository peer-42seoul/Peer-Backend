package peer.backend.entity.action;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "action_type")
public class ActionType {

    @Id
    private Long code;

    @Column(nullable = false)
    private String actionTypeName;

    @Column(nullable = false)
    private Boolean isDeleteable;
}
