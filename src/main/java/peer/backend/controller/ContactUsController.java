package peer.backend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.service.ContactUsService;

@RestController
@RequiredArgsConstructor
@RequestMapping(ContactUsController.CONTACT_US)
public class ContactUsController {
    public static final String CONTACT_US = "api/v1/contactUs";
    private final ContactUsService contactUsService;

    @ApiOperation(value = "", notes = "Contact Us 로 연락을 수신합니다.")
    @PostMapping()
    public ResponseEntity<Void> receiveContactUs() {

        return ResponseEntity.ok().build();
    }
}
