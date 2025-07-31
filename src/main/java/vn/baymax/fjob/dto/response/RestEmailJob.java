package vn.baymax.fjob.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RestEmailJob {
    private String name;
    private double salary;
    private CompanyEmail company;
    private List<SkillEmail> skills;

    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    public static class CompanyEmail {
        private String name;
    }

    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    public static class SkillEmail {
        private String name;
    }

}
