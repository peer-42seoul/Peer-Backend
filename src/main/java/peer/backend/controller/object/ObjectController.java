package peer.backend.controller.object;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import peer.backend.service.file.ObjectService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ObjectController {

    private final ObjectService objectService;

    @PostMapping("/editor/image")
    public String uploadImage(@RequestParam("image")MultipartFile image) throws IOException {
        return objectService.uploadImage(image);
    }
}
