package vn.baymax.fjob.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.baymax.fjob.domain.Role;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.service.RoleService;
import vn.baymax.fjob.util.annotation.ApiMessage;
import vn.baymax.fjob.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("create a role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) throws IdInvalidException {
        if (this.roleService.existByName(role.getName())) {
            throw new IdInvalidException("role with name " + role.getName() + " already exist");

        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(role));
    }

    @PutMapping("/roles")
    @ApiMessage("update a role")
    public ResponseEntity<Role> upldateRole(@Valid @RequestBody Role role) throws IdInvalidException {
        if (this.roleService.getRoleById(role.getId()) != null) {
            throw new IdInvalidException("role with id " + role.getId() + " already exist");
        }
        if (this.roleService.existByName(role.getName())) {
            throw new IdInvalidException("role with name " + role.getName() + " already exist");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.update(role));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) throws IdInvalidException {
        if (this.roleService.getRoleById(id) == null) {
            throw new IdInvalidException("role with name " + id + " is not exist");
        }
        this.roleService.delete(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/roles")
    @ApiMessage("get all roles")
    public ResponseEntity<ResultPaginationDTO> getAllRoles(
            @Filter Specification<Role> spec,
            Pageable pageable) throws IdInvalidException {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.getAllRoles(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("get all roles")
    public ResponseEntity<Role> getRoleById(
            @PathVariable long id) throws IdInvalidException {
        Role role = this.roleService.getRoleById(id);
        if (role != null) {
            throw new IdInvalidException("role with id: " + id + " is not found");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }
}
