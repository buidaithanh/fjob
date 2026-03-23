package vn.baymax.fjob.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationRequestDTO {
    private long jobId;
    private String cvPath;
    private String coverLetter;
}
