package deepdivers.community.global.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

	private static final String SERVER_DESCRIPTION = "구름 커뮤니티 API";
	private static final String DOCS_TITLE = "구름 딥다이브 API";
	private static final String DOCS_VERSION = "v1.0.0";
	private static final String DOCS_DESCRIPTION = "구름 딥다이브 수강생 커뮤니티 API";

	private final String devUrl;

	public OpenApiConfig(@Value("${goorm.community.server.url}") final String dveUrl) {
		this.devUrl = dveUrl;
	}

	@Bean
	public OpenAPI openAPI() {
		final Server server = new Server();
		server.setUrl(this.devUrl);
		server.setDescription(SERVER_DESCRIPTION);

		final Info info = new Info()
			.title(DOCS_TITLE)
			.version(DOCS_VERSION)
			.description(DOCS_DESCRIPTION);

		return new OpenAPI()
			.info(info)
			.servers(List.of(server));
	}

}
