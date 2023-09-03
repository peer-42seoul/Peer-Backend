package peer.backend.entity.message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.user.User;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "messageBox", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "message_box_opponents",
        joinColumns = @JoinColumn(name = "message_box_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<User> opponents = new LinkedHashSet<>();

    @OneToMany(mappedBy = "messageBox", cascade = CascadeType.PERSIST, orphanRemoval = true)
    List<Message> messageList = new ArrayList<>();


}
