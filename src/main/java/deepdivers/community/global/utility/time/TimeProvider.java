package deepdivers.community.global.utility.time;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Component
public class TimeProvider {

    private Clock clock = Clock.systemDefaultZone();

    public Date getCurrentDate() {
        return Date.from(Instant.now(clock));
    }

    public void reset() {
        this.clock = Clock.systemDefaultZone();
    }

}
