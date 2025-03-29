package dao;

import model.Post;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class PostDAO {

    // Hàm lấy user_id từ username
    public int getUserIdByUsername(String username) {
        System.out.println("Đang tìm user_id cho username: " + username); // Kiểm tra đầu vào

        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                System.out.println("Tìm thấy user_id: " + userId); // Kiểm tra đầu ra
                return userId;
            } else {
                System.out.println("Không tìm thấy user_id cho username: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Trả về -1 nếu không tìm thấy user_id
    }



    // Hàm chèn bài viết với user_id lấy từ username
    public boolean insertPost(Post post, String username) {
        int userId = getUserIdByUsername(username); // Lấy user_id từ username

        if (userId == -1) { // Nếu userId không hợp lệ, không thêm bài viết
            System.out.println("Lỗi: Không tìm thấy user_id cho username: " + username);
            return false;
        }

        String sql = "INSERT INTO posts (title, body, user_id, status, created_at) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getBody());
            stmt.setInt(3, userId); // Gán user_id lấy từ DB
            stmt.setString(4, "published");

            int rowsInserted = stmt.executeUpdate();
            System.out.println("Bài viết đã được thêm thành công!");
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}