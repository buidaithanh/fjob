package vn.baymax.fjob.dto.request;

import vn.baymax.fjob.domain.Application.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {

    private long id;

    private long jobId;

    private String jobTitle;

    private long applicantId;

    private String applicantName;

    private String applicantEmail;

    private ApplicationStatus status;

    private String cvPath;

    private String coverLetter;

    private Instant createdAt;

    private Instant updatedAt;

    private String reviewNote;
}