package vn.baymax.fjob.dto.response;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResUpdateResumeDTO {
    private Instant updatedAt;
    private String updatedBy;
}
