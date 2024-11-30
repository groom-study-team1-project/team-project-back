package deepdivers.community.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
	public ModelResolver modelResolver(final ObjectMapper objectMapper) {
		return new ModelResolver(objectMapper);
	}

	@Bean
	public OpenAPI openAPI() {
        return new OpenAPI().info(generateInfo())
			.servers(List.of(generateServer()))
			.components(generateComponents())
			.security(List.of(generateSecurityRequirement()));
	}

	private Components generateComponents() {
		final SecurityScheme accessTokenScheme = generateAccessTokenScheme();
		final SecurityScheme refreshTokenScheme = generateRefreshTokenScheme();
		return new Components().addSecuritySchemes("bearerAuth", accessTokenScheme)
			.addSecuritySchemes("refreshAuth", refreshTokenScheme);
	}

	private SecurityRequirement generateSecurityRequirement() {
		return new SecurityRequirement().addList("bearerAuth")
			.addList("refreshAuth");
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

	private SecurityScheme generateAccessTokenScheme() {
		return new SecurityScheme()
				.type(Type.HTTP)
				.in(In.HEADER)
				.name("Authorization")
				.scheme("bearer")
				.bearerFormat("JWT")
				.description("Bearer JWT");
	}

	private SecurityScheme generateRefreshTokenScheme() {
		return new SecurityScheme()
				.type(Type.APIKEY)
				.in(In.HEADER)
				.name("Refresh-Token")
				.description("Refresh Token");
	}

}
