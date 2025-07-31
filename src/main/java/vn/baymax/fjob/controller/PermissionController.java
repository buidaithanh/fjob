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
import vn.baymax.fjob.domain.Permission;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.service.PermissionService;
import vn.baymax.fjob.util.annotation.ApiMessage;
import vn.baymax.fjob.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("create a new permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException {
        if (this.permissionService.isPermissionExist(permission)) {
            throw new IdInvalidException("this permission is already exist");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(permission));
    }

    @PutMapping("/permissions")
    @ApiMessage("update a new permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException {
        if (this.permissionService.getPermissionById(permission.getId()) == null) {
            throw new IdInvalidException("permission with id: +" + permission.getId() + "is not exist");
        }
        if (this.permissionService.isPermissionExist(permission) && this.permissionService.isNameName(permission)) {
            throw new IdInvalidException("this permission is already exist");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.updatePermission(permission));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("delete a new permission")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) throws IdInvalidException {
        if (this.permissionService.getPermissionById(id) == null) {
            throw new IdInvalidException("permission with id: +" + id + "is not exist");
        }
        this.permissionService.delete(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/permissions")
    @ApiMessage("get all permission")
    public ResponseEntity<ResultPaginationDTO> getAllPermission(
            @Filter Specification<Permission> spec,
            Pageable pageable) throws IdInvalidException {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.getAllPermissions(spec, pageable));
    }

}
