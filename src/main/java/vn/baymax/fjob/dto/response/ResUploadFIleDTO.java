package vn.baymax.fjob.dto.response;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ResUploadFIleDTO {
    private String fileName;
    private Instant uploadedAt;
}
