package peer.backend.entity.board.recruit;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Entity;

@Getter
@AllArgsConstructor
public class Tag {
    private String name;
    private String color;
}
