package deepdivers.community.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
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
		final Server server = generateServer();
		final Info info = generateInfo();
		final SecurityScheme securityScheme = generateScheme();
		final SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

		return new OpenAPI()
			.info(info)
			.servers(List.of(server))
			.components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
			.security(List.of(securityRequirement));
	}

	private Server generateServer() {
		final Server server = new Server();
		server.setUrl(this.devUrl);
		server.setDescription(SERVER_DESCRIPTION);
		return server;
	}

	private Info generateInfo() {
		return new Info()
				.title(DOCS_TITLE)
				.version(DOCS_VERSION)
				.description(DOCS_DESCRIPTION);
	}

	private SecurityScheme generateScheme() {
		return new SecurityScheme()
				.type(Type.HTTP)
				.in(In.HEADER)
				.name("Authorization")
				.scheme("bearer")
				.bearerFormat("JWT")
				.description("Bearer JWT");
	}

}
