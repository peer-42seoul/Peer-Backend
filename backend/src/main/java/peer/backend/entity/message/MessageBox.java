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
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.user.User;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MessageBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "messageBox", cascade = CascadeType.PERSIST, orphanRemoval = true)
    Set<User> opponents = new HashSet<>();

    @OneToMany(mappedBy = "messageBox", cascade = CascadeType.PERSIST, orphanRemoval = true)
    List<Message> messageList = new ArrayList<>();
}
