package peer.backend.entity.board.team;

import lombok.*;
import peer.backend.dto.board.team.PostUpdateRequest;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<PostAnswer> answers = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<PostLike> postLike = new ArrayList<>();

    @Column(nullable = false)
    private String title;

    @Lob
    @NotNull
    private String content;
    private int hit;
    private String image;
    private int liked;

    public void update(PostUpdateRequest request){
        this.title = request.getTitle();
        this.content = request.getContent();
    }
    public void setImage(String url){
        this.image = url;
    }
    public void increaseLike() {
        this.liked += 1;
    }

    public void decreaseLike() {
        if (this.liked == 0)
            return ;
        this.liked -= 1;
    }
}
