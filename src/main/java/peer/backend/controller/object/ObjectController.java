package peer.backend.controller.object;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import peer.backend.entity.user.User;
import peer.backend.service.file.ObjectService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ObjectController {

    private final ObjectService objectService;

    @PostMapping({"/editor/image", "/admin/editor/image"})
    public String uploadImage(@RequestParam("image") MultipartFile image, Authentication auth)
        throws IOException {
        return objectService.uploadImage(image,
            "editor/" + User.authenticationToUser(auth).getId());
    }
}
