package peer.backend.service;

import org.apache.tika.Tika;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import peer.backend.service.file.FileService;
import peer.backend.service.file.ObjectService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test ProfileServiceTest")
public class ObjectServiceTest {
    @Mock
    Tika tika;
    @Mock
    RestTemplate restTemplate;
    @Mock
    private FileService fileService;
    @InjectMocks
    private ObjectService objectService;

//    @Test
//    @DisplayName("request token test")
//    public void requestTokenTest() {
//        String tokenId = objectService.requestToken();
//
//        System.out.println(tokenId);
//    }

}
