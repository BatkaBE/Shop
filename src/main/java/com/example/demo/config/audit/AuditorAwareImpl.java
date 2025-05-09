package com.example.demo.config.audit;

import com.example.demo.entity.BaseEntityAudit;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // access to header to get user_id
        // check userid
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("system");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return Optional.of(((User) principal).getUsername());
        } else if (principal instanceof String) {
            return Optional.of((String) principal);
        }

        return Optional.of("unknown");
    }
}