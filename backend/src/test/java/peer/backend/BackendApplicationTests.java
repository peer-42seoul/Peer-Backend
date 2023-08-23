package peer.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import peer.backend.dto.security.Message;
import peer.backend.dto.security.UserInfo;
import peer.backend.entity.user.User;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.service.EmailAuthService;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class BackendApplicationTests {

	@Autowired
	private EmailAuthService service;
	@Test
	void contextLoads() {
		UserInfo info = new UserInfo(
				"asdf", "asdf", "asdf", "asdf@asdf.com", "asdf",
				"1998-01-02", false, "010-5376-5531", "asdf"
		);
		User user = info.convertUser();
		PrincipalDetails principalDetails = new PrincipalDetails(user);
		System.out.println(principalDetails.getUserId());
		System.out.println(principalDetails.getPassword());
		System.out.println(principalDetails.getUsername());
		System.out.println(principalDetails.getEmail());
		System.out.println(principalDetails.getNickname());
		System.out.println(principalDetails.getBirthday());
		System.out.println(principalDetails.isAlarm());
		System.out.println(principalDetails.getPhone());
		System.out.println(principalDetails.getAddress());
		System.out.println(principalDetails.getImageUrl());
		System.out.println(principalDetails.isCertification());
		System.out.println(principalDetails.getCompany());
		System.out.println(principalDetails.getIntroduce());
		System.out.println(principalDetails.getPeerLevel());
		System.out.println(principalDetails.getRepresentAchievement());
		System.out.println(principalDetails.getUserPushKeywords());
		System.out.println(principalDetails.getPeerOperation());
		System.out.println(principalDetails.getUserAchievements());
		System.out.println(principalDetails.getUserLinks());
	}
}
