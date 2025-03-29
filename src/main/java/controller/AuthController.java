package controller;

import dao.UserDAO;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import model.User;

@Named
@SessionScoped
public class AuthController implements Serializable {
    private String username;
    private String password;

    private String searchUsername;  // Biến lưu tên người cần tìm kiếm
    private String searchResult;    // Biến lưu kết quả tìm kiếm

    @Inject
    private UserDAO userDAO;

    // Getter & Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSearchUsername() { return searchUsername; }
    public void setSearchUsername(String searchUsername) { this.searchUsername = searchUsername; }

    public String getSearchResult() { return searchResult; }

    // Xử lý đăng ký
    public String register() {
        boolean success = userDAO.registerUser(username, password);
        if (success) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Đăng ký thành công!"));
            return "login.xhtml";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Đăng ký thất bại!"));
            return null;
        }
    }

    // Xử lý đăng nhập
    public String login() {
        User user = userDAO.getUserByUsernameAndPassword(username, password);
        if (user != null) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("loggedInUser", user);
            System.out.println("Người dùng đã đăng nhập: " + user.getUsername()); // Debug
            return "dashboard?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Đăng nhập thất bại!", "Tên đăng nhập hoặc mật khẩu không đúng."));
            return null;
        }
    }


    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "login?faces-redirect=true";
    }

    // Xử lý tìm kiếm người dùng theo username
    public void searchUser() {
        int postCount = userDAO.getPostCountByUsername(searchUsername);

        if (postCount == -1) {
            searchResult = "Không tìm thấy người dùng!";
        } else {
            searchResult = "Người dùng " + searchUsername + " có " + postCount + " bài viết.";
        }
    }
}
