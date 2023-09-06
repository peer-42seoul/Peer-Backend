package peer.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import peer.backend.dto.security.UserInfo;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.EmailAuthService;

@SpringBootTest
class BackendApplicationTests {

	@Autowired
	private EmailAuthService service;

	@Autowired
	private UserRepository userRepository;

	@Test
	void contextLoads() {
		UserInfo info = new UserInfo(
				"xms2007", "1234", "asdf", "xms2007@naver.com", "asdf",
				"1998-01-02", false, "010-5376-5531", "asdf"
		);

		userRepository.save(info.convertUser());
	}
}
