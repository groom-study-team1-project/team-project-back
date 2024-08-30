package deepdivers.community.global.config;

import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

public class SimpleLocalStackContainer extends LocalStackContainer {

    public SimpleLocalStackContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
    }

    @Override
    protected void configure() {
        super.configure();
        this.setPortBindings();
    }

    private void setPortBindings() {
        this.addFixedExposedPort(4566, 4566);
    }

}
