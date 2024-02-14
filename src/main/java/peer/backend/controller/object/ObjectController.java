package peer.backend.controller.object;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import peer.backend.entity.user.User;
import peer.backend.service.file.ObjectService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ObjectController {

    private final ObjectService objectService;

    @PostMapping({"/editor/image"})
    public String uploadImage(@RequestParam("image") MultipartFile image, Authentication auth)
        throws IOException {
        return objectService.uploadImage(image,
            "editor/" + User.authenticationToUser(auth).getId());
    }

    @PostMapping("/admin/editor/image")
    public String uploadAdminImage(@RequestParam("image") MultipartFile image)
        throws IOException {
        return objectService.uploadImage(image,
            "editor/admin");
    }
}
