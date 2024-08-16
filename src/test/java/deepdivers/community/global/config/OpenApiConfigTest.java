package deepdivers.community.global.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@SpringBootTest
class OpenApiConfigTest {

	private static final String SERVER_DESCRIPTION = "구름 커뮤니티 API";
	private static final String DOCS_TITLE = "구름 딥다이브 API";
	private static final String DOCS_VERSION = "v1.0.0";
	private static final String DOCS_DESCRIPTION = "구름 딥다이브 수강생 커뮤니티 API";

	@Autowired
	private final OpenAPI openAPI;
	private final String devUrl;

	@Autowired
	OpenApiConfigTest(final OpenAPI openAPI, @Value("${goorm.community.server.url}") final String devUrl) {
		this.openAPI = openAPI;
		this.devUrl = devUrl;
	}

	@Test
	@DisplayName("OpenAPI Bean 생성을 확인한다.")
	public void openApiBeanCreation() {
		assertNotNull(openAPI);
	}

	@Test
	@DisplayName("Swagger 서버 정보를 확인한다.")
	public void openApiServer() {
		assertFalse(openAPI.getServers().isEmpty());

		final Server server = openAPI.getServers().get(0);
		assertEquals(devUrl, server.getUrl());
		assertEquals(server.getDescription(), SERVER_DESCRIPTION);
	}

	@Test
	@DisplayName("Swagger 페이지 정보를 확인한다.")
	public void testAPIInfo() {
		final Info info = openAPI.getInfo();
		assertNotNull(info);
		assertEquals(info.getTitle(), DOCS_TITLE);
		assertEquals(info.getDescription(), DOCS_DESCRIPTION);
		assertEquals(info.getVersion(), DOCS_VERSION);
	}

}