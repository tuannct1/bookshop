package com.example.bookshop.config;
import com.example.bookshop.enums.BookStatus;
import com.example.bookshop.entity.Author;
import com.example.bookshop.entity.Book;
import com.example.bookshop.entity.Category;
import com.example.bookshop.entity.Publisher;
import com.example.bookshop.entity.Role;
import com.example.bookshop.entity.User;
import com.example.bookshop.repository.AuthorRepository;
import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.repository.CategoryRepository;
import com.example.bookshop.repository.PublisherRepository;
import com.example.bookshop.repository.RoleRepository;
import com.example.bookshop.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {
        initRolesAndAdmin();
        
        initCategories();
        initPublishers();
        initAuthors();
        
        initBooks();
    }

    private void initRolesAndAdmin() {
        if (roleRepository.count() == 0) {
            Role roleUser = new Role();
            roleUser.setRoleName("ROLE_USER"); 

            Role roleAdmin = new Role();
            roleAdmin.setRoleName("ROLE_ADMIN");

            roleRepository.saveAll(List.of(roleUser, roleAdmin));
            System.out.println("Đã khởi tạo thành công dữ liệu cho bảng Role!");
        }

        String adminEmail = "admin@gmail.com"; 
        if (!userRepository.existsByEmail(adminEmail)) {
            try {
                Role role = roleRepository.findByRoleName("ROLE_ADMIN")
                        .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy ROLE_ADMIN."));

                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("admin123")); 
                admin.setFullName("Quản trị viên");
                admin.setActive(true); 
                admin.setRoles(Set.of(role)); 

                userRepository.save(admin);
                System.out.println("=========================================================");
                System.out.println("-> TẠO ADMIN THÀNH CÔNG!");
                System.out.println("-> Email: " + adminEmail);
                System.out.println("=========================================================");
            } catch (Exception e) {
                System.err.println("-> LỖI KHI TẠO ADMIN:");
                e.printStackTrace();
            }
        } else {
            System.out.println("-> Tài khoản Admin (" + adminEmail + ") đã tồn tại trong database.");
        }
    }

    private void initCategories() {
        if (categoryRepository.count() == 0) {
            Category cat1 = new Category();
            cat1.setName("Văn học Trong Nước");
            cat1.setDescription("Các tác phẩm văn học của tác giả Việt Nam");
            
            Category cat2 = new Category();
            cat2.setName("Văn học Nước Ngoài");
            cat2.setDescription("Các tác phẩm văn học kinh điển thế giới");
            
            Category cat3 = new Category();
            cat3.setName("Khoa học - Viễn tưởng");
            cat3.setDescription("Sách khám phá khoa học và viễn tưởng");

            categoryRepository.saveAll(List.of(cat1, cat2, cat3));
            System.out.println("Đã khởi tạo dữ liệu Category!");
        }
    }

    private void initPublishers() {
        if (publisherRepository.count() == 0) {
            Publisher pub1 = new Publisher();
            pub1.setName("NXB Trẻ");
            
            Publisher pub2 = new Publisher();
            pub2.setName("NXB Kim Đồng");
            
            Publisher pub3 = new Publisher();
            pub3.setName("NXB Nhã Nam");

            publisherRepository.saveAll(List.of(pub1, pub2, pub3));
            System.out.println("Đã khởi tạo dữ liệu Publisher!");
        }
    }

    private void initAuthors() {
        if (authorRepository.count() == 0) {
            Author author1 = new Author();
            author1.setName("Nguyễn Nhật Ánh");
            
            Author author2 = new Author();
            author2.setName("J.K. Rowling");
            
            Author author3 = new Author();
            author3.setName("Tô Hoài");

            authorRepository.saveAll(List.of(author1, author2, author3));
            System.out.println("Đã khởi tạo dữ liệu Author!");
        }
    }

    private void initBooks() {
        if (bookRepository.count() == 0) {
            try {
                Category vanHocTrongNuoc = categoryRepository.findAll().stream()
                        .filter(c -> c.getName().equals("Văn học Trong Nước")).findFirst().orElse(null);
                
                Category vanHocNuocNgoai = categoryRepository.findAll().stream()
                        .filter(c -> c.getName().equals("Văn học Nước Ngoài")).findFirst().orElse(null);

                Publisher nxbTre = publisherRepository.findAll().stream()
                        .filter(p -> p.getName().equals("NXB Trẻ")).findFirst().orElse(null);
                        
                Publisher nxbKimDong = publisherRepository.findAll().stream()
                        .filter(p -> p.getName().equals("NXB Kim Đồng")).findFirst().orElse(null);

                Author nguyenNhatAnh = authorRepository.findAll().stream()
                        .filter(a -> a.getName().equals("Nguyễn Nhật Ánh")).findFirst().orElse(null);
                        
                Author jkRowling = authorRepository.findAll().stream()
                        .filter(a -> a.getName().equals("J.K. Rowling")).findFirst().orElse(null);

                if (vanHocTrongNuoc != null && nxbTre != null && nguyenNhatAnh != null) {
                    Book book1 = new Book();
                    book1.setTitle("Mắt Biếc"); 
                    book1.setDescription("Một trong những tác phẩm nổi tiếng nhất của nhà văn Nguyễn Nhật Ánh.");
                    book1.setImageUrl("https://example.com/mat-biec.jpg");
                    book1.setPrice(110000.0);   
                    book1.setQuantity(50);
                    book1.setPublishedYear(1990);
                    book1.setStatus(BookStatus.AVAILABLE);
                    book1.setCategory(vanHocTrongNuoc);
                    book1.setPublisher(nxbTre);
                    book1.setAuthors(Set.of(nguyenNhatAnh)); 

                    Book book2 = new Book();
                    book2.setTitle("Harry Potter và Hòn đá Phù thủy");
                    book2.setDescription("Cuốn sách đầu tiên trong loạt truyện giả tưởng Harry Potter.");
                    book2.setImageUrl("https://example.com/harry-potter-1.jpg");
                    book2.setPrice(150000.0);
                    book2.setQuantity(100);
                    book2.setPublishedYear(1997);
                    book2.setStatus(BookStatus.AVAILABLE);
                    book2.setCategory(vanHocNuocNgoai);
                    book2.setPublisher(nxbKimDong);
                    book2.setAuthors(Set.of(jkRowling));

                    bookRepository.saveAll(List.of(book1, book2));
                    System.out.println("Đã khởi tạo dữ liệu Book!");
                }
            } catch (Exception e) {
                System.err.println("-> LỖI KHI TẠO BOOK:");
                e.printStackTrace();
            }
        }
    }
}