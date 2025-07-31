package vn.baymax.fjob.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.baymax.fjob.domain.Skill;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.service.SkillService;
import vn.baymax.fjob.util.annotation.ApiMessage;
import vn.baymax.fjob.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("create new skill")
    public ResponseEntity<Skill> createSkill(@RequestBody Skill skill) throws IdInvalidException {
        if (skill.getName() != null && this.skillService.isNameExist(skill.getName())) {
            throw new IdInvalidException("skill with name " + skill.getName() + " already exist");

        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.createSkill(skill));
    }

    @PutMapping("/skills")
    @ApiMessage("update new skill")
    public ResponseEntity<Skill> updateSkill(@RequestBody Skill skill) throws IdInvalidException {
        Skill currenttSkill = this.skillService.getSkillById(skill.getId());
        if (currenttSkill == null) {
            throw new IdInvalidException("skill with id " + skill.getId() + " not exist");
        }
        if (skill.getName() != null && this.skillService.isNameExist(skill.getName())) {
            throw new IdInvalidException("skill with name " + skill.getName() + " already exist");
        }
        currenttSkill.setName(skill.getName());
        return ResponseEntity.ok().body(this.skillService.updateSkill(currenttSkill));
    }

    @GetMapping("/skills")
    @ApiMessage("get all skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkill(
            @Filter Specification<Skill> spec,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(
                this.skillService.getAllSkill(spec, pageable));
    }

}
