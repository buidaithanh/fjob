package vn.baymax.fjob.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.baymax.fjob.domain.Permission;
import vn.baymax.fjob.domain.Role;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.repository.PermissionRepository;
import vn.baymax.fjob.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role create(Role role) {
        if (role.getPermissions() != null) {
            List<Long> ids = role.getPermissions().stream()
                    .map(p -> p.getId())
                    .collect(Collectors.toList());

            List<Permission> permissions = this.permissionRepository.findByIdIn(ids);
            role.setPermissions(permissions);

        }
        return this.roleRepository.save(role);
    }

    public Role getRoleById(long id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (roleOptional.isPresent()) {
            return roleOptional.get();
        }
        return null;
    }

    public Role update(Role r) {
        Role role = this.getRoleById(r.getId());
        if (r.getPermissions() != null) {
            List<Long> ids = role.getPermissions().stream()
                    .map(p -> p.getId())
                    .collect(Collectors.toList());

            List<Permission> permissions = this.permissionRepository.findByIdIn(ids);
            r.setPermissions(permissions);
        }
        role.setName(r.getName());
        role.setDescription(r.getDescription());
        role.setActive(r.isActive());
        role.setPermissions(r.getPermissions());
        role = this.roleRepository.save(role);
        return role;
    }

    public void delete(long id) {
        this.roleRepository.deleteById(id);
    }

    public ResultPaginationDTO getAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageRole.getTotalPages());
        mt.setTotal(pageRole.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageRole.getContent());
        return rs;
    }
}
