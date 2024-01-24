package peer.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.repository.ContactUsRepository;

@Service
@RequiredArgsConstructor
public class ContactUsService {
    private final ContactUsRepository contactUsRepository;

    public void saveContactUs() {

    }
}
