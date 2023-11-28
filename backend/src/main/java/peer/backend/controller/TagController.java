package peer.backend.controller;


import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.tag.DeleteTagRequest;
import peer.backend.dto.tag.InsertTagRequest;
import peer.backend.service.TagService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tag")
public class TagController {

    private final TagService tagService;

    @PostMapping()
    public void insertTag(@RequestBody @Valid InsertTagRequest request) {
        this.tagService.insertTag(request.getTag(), request.getColor());
    }

    @DeleteMapping()
    public void deleteTag(@RequestBody @Valid DeleteTagRequest request) {
        this.tagService.deleteTag(request.getTag());
    }
}
