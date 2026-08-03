package com.example.bookshop.config; // Bạn có thể đặt trong package config hoặc init

import com.example.bookshop.entity.Role;
import com.example.bookshop.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    // Bỏ comment dòng dưới khi bạn sẵn sàng tạo tài khoản Admin
    // private final UserRepository userRepository; 
    // private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem bảng Role đã có dữ liệu chưa. Nếu chưa (count == 0) thì mới thêm vào.
        if (roleRepository.count() == 0) {
            Role roleUser = new Role();
            roleUser.setRoleName("ROLE_USER"); // Tên trường name tùy thuộc vào entity Role của bạn

            Role roleAdmin = new Role();
            roleAdmin.setRoleName("ROLE_ADMIN");

            roleRepository.saveAll(List.of(roleUser, roleAdmin));
            System.out.println("Đã khởi tạo thành công dữ liệu cho bảng Role!");

            // ---------------------------------------------------------
            // TƯƠNG LAI: CODE TẠO TÀI KHOẢN ADMIN CỦA BẠN SẼ NẰM Ở ĐÂY
            // ---------------------------------------------------------
            // User admin = new User();
            // admin.setUsername("admin");
            // admin.setPassword(passwordEncoder.encode("admin123"));
            // admin.setRoles(Collections.singleton(roleAdmin));
            // userRepository.save(admin);
            // System.out.println("Đã khởi tạo tài khoản Admin!");
        }
    }
}