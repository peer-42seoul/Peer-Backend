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
import peer.backend.dto.tag.CreateTagRequest;
import peer.backend.dto.tag.UpdateTagRequest;
import peer.backend.dto.tag.TagResponse;
import peer.backend.entity.Tag;
import peer.backend.service.TagService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class TagController {

    private final TagService tagService;

    @PostMapping("/admin/tag")
    public void createTag(@RequestBody @Valid CreateTagRequest request) {
        this.tagService.createTag(request.getName(), request.getColor());
    }

    @DeleteMapping("/admin/tag")
    public void deleteTag(@RequestBody @Valid DeleteTagRequest request) {
        this.tagService.deleteTag(request.getTagId());
    }

    @PutMapping("/admin/tag")
    public void modifyTag(@RequestBody @Valid UpdateTagRequest request) {
        this.tagService.updateTag(request.getTypeId(), request.getName(), request.getColor());
    }

    @GetMapping("/tag")
    public List<TagResponse> getTagList() {
        List<Tag> tagList = this.tagService.getTagList();
        return tagList.stream().map(TagResponse::new).collect(Collectors.toList());
    }
}
