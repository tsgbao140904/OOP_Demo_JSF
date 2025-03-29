package controller;

import dao.PostDAO;
import jakarta.faces.context.FacesContext;
import model.Post;
import model.User;
import utils.DatabaseConnection;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Named("postController")
@SessionScoped
public class PostController implements Serializable {
    private Post post = new Post();

    public String savePost() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            User loggedInUser = (User) facesContext.getExternalContext().getSessionMap().get("loggedInUser");

            if (loggedInUser == null) {
                System.out.println("Lỗi: Người dùng chưa đăng nhập!");
                return "login.xhtml?faces-redirect=true";
            }

            String username = loggedInUser.getUsername(); // Lấy username từ session
            System.out.println("Đăng bài với username: " + username); // Debug

            PostDAO postDAO = new PostDAO();
            boolean success = postDAO.insertPost(post, username);

            if (success) {
                return "dashboard.xhtml?faces-redirect=true";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM posts ORDER BY created_at DESC")) {
            while (rs.next()) {
                Post p = new Post();
                p.setId(rs.getInt("id"));
                p.setTitle(rs.getString("title"));
                p.setBody(rs.getString("body"));
                p.setUserId(rs.getInt("user_id"));
                p.setStatus(rs.getString("status"));
                p.setCreatedAt(rs.getTimestamp("created_at"));
                posts.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}