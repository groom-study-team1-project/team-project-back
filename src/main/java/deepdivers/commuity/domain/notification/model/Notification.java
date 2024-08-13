package deepdivers.commuity.domain.notification.model;

import java.time.LocalDateTime;

public class Notification {

    private Long id;
    private Long resourceId;
    private String resourceType;
    private Boolean isRead;
    private LocalDateTime notifiedAt;

}
