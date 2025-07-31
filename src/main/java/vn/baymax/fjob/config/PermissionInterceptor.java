package vn.baymax.fjob.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.baymax.fjob.domain.Permission;
import vn.baymax.fjob.domain.Role;
import vn.baymax.fjob.domain.User;
import vn.baymax.fjob.service.UserService;
import vn.baymax.fjob.util.SecurityUtil;
import vn.baymax.fjob.util.error.IdInvalidException;

public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email != null && email.isEmpty()) {
            User user = this.userService.handleGetUserByName(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions.stream()
                            .anyMatch(p -> p.getApiPath().equals(path) && p.getMethod().equals(httpMethod));
                    if (isAllow == false) {
                        throw new IdInvalidException("you dont have permission for this endpoint");
                    }
                } else {
                    throw new IdInvalidException("you dont have permission for this endpoint");

                }
            }
        }
        return true;
    }
}
