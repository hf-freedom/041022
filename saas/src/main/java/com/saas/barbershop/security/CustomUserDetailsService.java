package com.saas.barbershop.security;

import com.saas.barbershop.entity.Merchant;
import com.saas.barbershop.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MerchantRepository merchantRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        Merchant merchant = merchantRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("Merchant not found with phone: " + phone));

        return MerchantPrincipal.create(merchant);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Merchant not found with id: " + id));

        return MerchantPrincipal.create(merchant);
    }
}
