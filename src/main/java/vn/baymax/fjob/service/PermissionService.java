package vn.baymax.fjob.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.baymax.fjob.domain.Permission;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission permission) {
        return permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(), permission.getApiPath(),
                permission.getMethod());

    }

    public Permission create(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission getPermissionById(long id) {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        if (permissionOptional.isPresent()) {
            return permissionOptional.get();
        }
        return null;
    }

    public Permission updatePermission(Permission p) {
        Permission permission = this.getPermissionById(p.getId());
        if (permission != null) {
            permission.setName(p.getName());
            permission.setApiPath(p.getApiPath());
            permission.setMethod(p.getMethod());
            permission.setModule(p.getModule());

            permission = this.permissionRepository.save(permission);
            return permission;
        }
        return null;
    }

    public void delete(long id) {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        Permission permission = permissionOptional.get();
        permission.getRoles().forEach(r -> r.getPermissions().remove(permission));

        this.permissionRepository.delete(permission);
    }

    public boolean isNameName(Permission permission) {
        return this.permissionRepository.findById(permission.getId()).get()
                .getName().equals(permission.getName());
    }

    public ResultPaginationDTO getAllPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pagePermission.getTotalPages());
        mt.setTotal(pagePermission.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pagePermission.getContent());
        return rs;
    }
}
