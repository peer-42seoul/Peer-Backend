package peer.backend.entity.team;

import lombok.*;
import springfox.documentation.spring.web.json.Json;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Widget {
    private Long key;
    private String size; //L,M,S
    private DataGrid grid; // x,y,w,h
    private String type; // text, image,...etc
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Json data; // JSON
}
//GET
//HEAD(createdAt, teamId, updatedAt, widgetKey) -> Data changed? -> return
