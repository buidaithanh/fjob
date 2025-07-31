package vn.baymax.fjob.dto.response;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.baymax.fjob.util.constant.ResumeStateEnum;

@Getter
@Setter
@Builder
public class ResGetResumeDTO {

    private long id;
    private String email;
    private String url;

    @Enumerated(EnumType.STRING)
    private ResumeStateEnum status;

    private Instant createdAt;
    private Instant updatedAt;

    private String createdBy;
    private String updatedBy;

    private String companyName;
    private UserResume user;
    private JobResume job;

    @Getter
    @Setter
    @Builder
    public static class UserResume {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @Builder
    public static class JobResume {
        private long id;
        private String name;
    }
}
