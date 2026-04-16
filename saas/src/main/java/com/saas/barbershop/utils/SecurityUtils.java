package com.saas.barbershop.utils;

import com.saas.barbershop.security.MerchantPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getCurrentMerchantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof MerchantPrincipal) {
            return ((MerchantPrincipal) authentication.getPrincipal()).getId();
        }
        return null;
    }

    public static MerchantPrincipal getCurrentMerchant() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof MerchantPrincipal) {
            return (MerchantPrincipal) authentication.getPrincipal();
        }
        return null;
    }
}
