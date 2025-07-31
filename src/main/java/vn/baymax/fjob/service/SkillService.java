package vn.baymax.fjob.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.baymax.fjob.domain.Skill;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean isNameExist(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill createSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public Skill getSkillById(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        if (skillOptional.isPresent()) {
            return skillOptional.get();
        }
        return null;
    }

    public Skill updateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public void deleteSkill(Skill skill) {
        Optional<Skill> skillOptional = this.skillRepository.findById(skill.getId());
        Skill currentSkill = skillOptional.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));
        currentSkill.getSubcribers().forEach(sub -> sub.getSkills().remove(currentSkill));
        this.skillRepository.deleteById(currentSkill.getId());
    }

    public ResultPaginationDTO getAllSkill(Specification<Skill> spec,
            Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageSkill.getTotalPages());
        mt.setTotal(pageSkill.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageSkill.getContent());
        return rs;
    }

}
