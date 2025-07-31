package vn.baymax.fjob.dto.response;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import vn.baymax.fjob.util.constant.LevelEnum;

@Getter
@Setter
public class ResUpdateJobDTO {
    private long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private LevelEnum level;
    private Instant startDate;
    private Instant endDate;
    private boolean acctive;
    private Instant updatedAt;
    private String updatedBy;
    private List<String> skills;
}
