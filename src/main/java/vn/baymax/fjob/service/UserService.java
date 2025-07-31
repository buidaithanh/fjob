package vn.baymax.fjob.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.baymax.fjob.domain.Company;
import vn.baymax.fjob.domain.Role;
import vn.baymax.fjob.domain.User;
import vn.baymax.fjob.dto.response.ResCreateUserDTO;
import vn.baymax.fjob.dto.response.ResUserDTO;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.repository.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;
    private CompanyService companyService;
    private RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User handleSaveUser(User user) {
        if (user.getCompany() != null) {
            Optional<Company> companyOptional = this.companyService.getCompanyById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }
        if (user.getRole() != null) {
            Role role = this.roleService.getRoleById(user.getRole().getId());
            user.setRole(role != null ? role : null);
        }
        return this.userRepository.save(user);
    }

    public void deleteUserById(long id) {
        this.userRepository.deleteById(id);
    }

    public User getUserById(long id) {
        Optional<User> userOp = this.userRepository.findById(id);
        if (userOp.isPresent()) {
            return userOp.get();
        }
        return null;
    }

    public ResultPaginationDTO getAllUsers(Specification<User> specification, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(specification, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> this.mappingUserToResUserDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listUser);
        return rs;
    }

    public User updateUserById(long id, User user) {
        User currentUser = getUserById(id);
        if (currentUser != null) {
            currentUser.setName(user.getName());
            currentUser.setGender(user.getGender());
            currentUser.setAddress(user.getAddress());
            currentUser.setAge(user.getAge());

        }
        if (user.getCompany() != null) {
            Optional<Company> companyOptional = this.companyService.getCompanyById(user.getCompany().getId());
            currentUser.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }
        if (user.getRole() != null) {
            Role role = this.roleService.getRoleById(user.getRole().getId());
            currentUser.setRole(role != null ? role : null);
        }
        currentUser = this.userRepository.save(currentUser);
        return currentUser;
    }

    public User handleGetUserByName(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean isEmailExsit(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO mappingUserToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);
        }
        return res;
    }

    public ResUserDTO mappingUserToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser com = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();

        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);
        }
        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRole(roleUser);
        }
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByName(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);

    }
}
