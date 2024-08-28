package deepdivers.community;

import deepdivers.community.utility.encryptor.PasswordEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

class BackendApplicationTests {

	public void generateMemberQueries(int dataCount) {
		System.out.println("-- Member 테이블 INSERT 쿼리");
		for (int i = 1; i <= dataCount; i++) {
			PasswordEncryptor passwordEncryptor = new PasswordEncryptor();
			String format = String.format("password%d!", i);
			String password = passwordEncryptor.encrypt(format);
			System.out.printf("INSERT INTO member (email, password, about_me, comment_count, post_count, blog_addr, github_addr, phone_number, created_at, image_url, nickname, role, status, updated_at) " +
							"VALUES ('email%d@test.com', '%s', 'About me %d', 0, 0, 'http://blog%d.com', 'http://github.com/user%d', '010-1234-%04d', " +
							"CURRENT_TIMESTAMP, 'http://example.com/image%d.jpg', 'User%d', 'NORMAL', 'REGISTERED', CURRENT_TIMESTAMP);\n",
					i, password, i, i, i, i, i, i);
		}
		System.out.println();
	}


	@Test
	void contextLoads() {
		int dataCount = 10;
		generateMemberQueries(dataCount);
	}

}
