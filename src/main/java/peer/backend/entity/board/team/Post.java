package peer.backend.entity.board.team;

import lombok.*;
import peer.backend.dto.board.team.PostLinkResponse;
import peer.backend.dto.board.team.PostUpdateRequest;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
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
    private List<PostComment> comments = new ArrayList<>();

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

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<PostFile> files;

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<PostLink> links;

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

    public void increaseHit() {
        this.hit += 1;
    }

    public void decreaseLike() {
        if (this.liked == 0)
            return ;
        this.liked -= 1;
    }
    public void addLinks(List<PostLinkResponse> linkList){
        if (this.links == null)
            this.links = new ArrayList<>();
        if (linkList != null && !linkList.isEmpty())
            linkList.forEach(link -> this.links.add(new PostLink(link)));
    }

    public void addFile(String url){
        if (this.files == null)
            this.files = new ArrayList<>();
        files.add(PostFile.builder()
                .url(url)
                .post(this)
                .build());
    }

    public void addComment(String content, User user){
        if (!Objects.nonNull(this.comments))
            comments = new ArrayList<>();
        this.comments.add(PostComment.builder()
                .post(this)
                .user(user)
                .content(content)
                .build()
        );
    }
}
