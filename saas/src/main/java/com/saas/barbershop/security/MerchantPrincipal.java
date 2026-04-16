package com.saas.barbershop.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saas.barbershop.entity.Merchant;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor
public class MerchantPrincipal implements UserDetails {

    private Long id;
    private String shopName;
    private String phone;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public static MerchantPrincipal create(Merchant merchant) {
        return new MerchantPrincipal(
                merchant.getId(),
                merchant.getShopName(),
                merchant.getPhone(),
                merchant.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_MERCHANT"))
        );
    }

    @Override
    public String getUsername() {
        return phone;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
