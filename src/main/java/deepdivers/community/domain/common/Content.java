package deepdivers.community.domain.common;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Content {

    protected String value;

    protected Content(final String value) {
        checkText(value);
        this.value = value;
    }

    public void updateContent(final String contentText) {
        checkText(contentText);
        this.value = contentText;
    }

    protected abstract void checkText(String contentText);

}
