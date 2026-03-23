package vn.baymax.fjob.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatsDTO {

    private long jobId;

    private long totalApplications;

    private long pendingCount;

    private long reviewingCount;

    private long acceptedCount;

    private long rejectedCount;
}