package peer.backend.controller;


import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.tag.DeleteTagRequest;
import peer.backend.dto.tag.InsertTagRequest;
import peer.backend.dto.tag.ModifyTagRequest;
import peer.backend.dto.tag.TagResponse;
import peer.backend.entity.Tag;
import peer.backend.service.TagService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tag")
public class TagController {

    private final TagService tagService;

    @PostMapping()
    public void insertTag(@RequestBody @Valid InsertTagRequest request) {
        this.tagService.insertTag(request.getName(), request.getColor());
    }

    @DeleteMapping()
    public void deleteTag(@RequestBody @Valid DeleteTagRequest request) {
        this.tagService.deleteTag(request.getTagId());
    }

    @PutMapping()
    public void modifyTag(@RequestBody @Valid ModifyTagRequest request) {
        this.tagService.modifyTag(request.getTypeId(), request.getName(), request.getColor());
    }

    @GetMapping
    public List<TagResponse> getTagList() {
        List<Tag> tagList = this.tagService.getTagList();
        return tagList.stream().map(TagResponse::new).collect(Collectors.toList());
    }
}
