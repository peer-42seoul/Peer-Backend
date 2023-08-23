package peer.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import peer.backend.dto.security.Message;
import peer.backend.service.EmailAuthService;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class BackendApplicationTests {

	@Autowired
	private EmailAuthService service;
	@Test
	void contextLoads() {
		Message message = service.sendEmail("nfvvh25@naver.com");
		assertThat(message.getStatus())
				.isEqualByComparingTo(HttpStatus.OK);
		System.out.println(service.getCode());
	}

}
