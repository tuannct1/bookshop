package com.example.bookshop.security;

import com.example.bookshop.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final User user; // Nhúng Entity User của bạn vào đây

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Biến Set<Role> của bạn thành định dạng GrantedAuthority của Spring Security
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName())) 
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // Trả về mật khẩu đã băm trong DB
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // Trả về username 
    }

    // Mặc định cho phép tài khoản hoạt động. 
    // Sau này nếu có tính năng "Khóa tài khoản", bạn có thể sửa logic ở đây.
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    // Thêm một hàm tiện ích để lấy ngược lại Entity User gốc khi cần thiết
    public User getUser() {
        return user;
    }
}