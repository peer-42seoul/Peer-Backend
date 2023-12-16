package peer.backend.entity.action;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "action_type")
public class ActionType {

    @Id
    private Long code;

    @Column(nullable = false)
    private String actionTypeName;

    @Column(nullable = false)
    private Boolean isDeletable;
  
    public ActionType(Long code, String actionTypeName) {
        this.code = code;
        this.actionTypeName = actionTypeName;
        this.isDeletable = Boolean.TRUE;
    }
}
