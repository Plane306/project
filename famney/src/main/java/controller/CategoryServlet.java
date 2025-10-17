package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.User;
import model.Category;
import model.dao.CategoryManager;

// Handles category management actions by Family Head and Adults
// Supports category creation, editing, and deletion with validation
@WebServlet("/CategoryServlet")
public class CategoryServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Check if user is logged in
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Check permissions - only Family Head and Adult can manage categories
        if (!"Family Head".equals(currentUser.getRole()) && !"Adult".equals(currentUser.getRole())) {
            session.setAttribute("errorMessage", "Only Family Head and Adults can manage categories");
            response.sendRedirect("categories.jsp");
            return;
        }
        
        // Get action parameter
        String action = request.getParameter("action");
        
        if (action == null) {
            session.setAttribute("errorMessage", "Invalid action");
            response.sendRedirect("categories.jsp");
            return;
        }
        
        try {
            // Get CategoryManager from session
            CategoryManager categoryManager = (CategoryManager) session.getAttribute("categoryManager");
            
            if (categoryManager == null) {
                session.setAttribute("errorMessage", "System error. Please try again");
                response.sendRedirect("categories.jsp");
                return;
            }
            
            // Handle different actions
            if ("create".equals(action)) {
                handleCreateCategory(request, response, session, categoryManager, currentUser);
                
            } else if ("edit".equals(action)) {
                handleEditCategory(request, response, session, categoryManager, currentUser);
                
            } else if ("delete".equals(action)) {
                // Only Family Head can delete categories
                if (!"Family Head".equals(currentUser.getRole())) {
                    session.setAttribute("errorMessage", "Only Family Head can delete categories");
                    response.sendRedirect("categories.jsp");
                    return;
                }
                handleDeleteCategory(request, response, session, categoryManager, currentUser);
                
            } else {
                session.setAttribute("errorMessage", "Unknown action");
                response.sendRedirect("categories.jsp");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "An error occurred. Please try again");
            response.sendRedirect("categories.jsp");
        }
    }
    
    // Handle category creation
    // Validates input and checks for duplicate names
    private void handleCreateCategory(HttpServletRequest request, HttpServletResponse response,
                                     HttpSession session, CategoryManager categoryManager, User currentUser)
            throws Exception {
        
        String categoryName = request.getParameter("categoryName");
        String categoryType = request.getParameter("categoryType");
        String description = request.getParameter("description");
        
        // Validate inputs
        CategoryValidator validator = new CategoryValidator();
        
        if (categoryName == null || categoryName.trim().isEmpty() || 
            categoryType == null || categoryType.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Please fill in all required fields");
            response.sendRedirect("category_form.jsp");
            return;
        }
        
        // Validate category name
        if (!validator.validateCategoryName(categoryName)) {
            session.setAttribute("errorMessage", "Category name must be 2-50 characters");
            response.sendRedirect("category_form.jsp");
            return;
        }
        
        // Validate category type
        if (!validator.validateCategoryType(categoryType)) {
            session.setAttribute("errorMessage", "Invalid category type. Choose Expense or Income");
            response.sendRedirect("category_form.jsp");
            return;
        }
        
        // Validate description
        if (!validator.validateDescription(description)) {
            session.setAttribute("errorMessage", "Description must not exceed 200 characters");
            response.sendRedirect("category_form.jsp");
            return;
        }
        
        String familyId = currentUser.getFamilyId();
        
        // Check if category name already exists for this family
        if (categoryManager.categoryNameExists(familyId, categoryName.trim())) {
            session.setAttribute("errorMessage", "Category name already exists. Please choose a different name");
            response.sendRedirect("category_form.jsp");
            return;
        }
        
        // Create new category
        Category category = new Category();
        category.setFamilyId(familyId);
        category.setCategoryName(categoryName.trim());
        category.setCategoryType(categoryType);
        category.setDescription(description != null ? description.trim() : "");
        category.setDefault(false); // User-created categories are not default
        
        boolean created = categoryManager.createCategory(category);
        
        if (created) {
            session.setAttribute("successMessage", "Category '" + categoryName + "' created successfully!");
            response.sendRedirect("categories.jsp");
        } else {
            session.setAttribute("errorMessage", "Failed to create category. Please try again");
            response.sendRedirect("category_form.jsp");
        }
    }
    
    // Handle category editing
    // Cannot edit default categories or category type
    private void handleEditCategory(HttpServletRequest request, HttpServletResponse response,
                                   HttpSession session, CategoryManager categoryManager, User currentUser)
            throws Exception {
        
        String categoryId = request.getParameter("categoryId");
        String categoryName = request.getParameter("categoryName");
        String description = request.getParameter("description");
        
        // Validate category ID
        if (categoryId == null || categoryId.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Invalid category");
            response.sendRedirect("categories.jsp");
            return;
        }
        
        // Get existing category
        Category existingCategory = categoryManager.findByCategoryId(categoryId);
        
        if (existingCategory == null) {
            session.setAttribute("errorMessage", "Category not found");
            response.sendRedirect("categories.jsp");
            return;
        }
        
        // Check if trying to edit default category
        if (existingCategory.isDefault()) {
            session.setAttribute("errorMessage", "Cannot edit default categories");
            response.sendRedirect("categories.jsp");
            return;
        }
        
        // Validate inputs
        CategoryValidator validator = new CategoryValidator();
        
        if (categoryName == null || categoryName.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Please fill in all required fields");
            response.sendRedirect("category_form.jsp?action=edit&id=" + categoryId);
            return;
        }
        
        // Validate category name
        if (!validator.validateCategoryName(categoryName)) {
            session.setAttribute("errorMessage", "Category name must be 2-50 characters");
            response.sendRedirect("category_form.jsp?action=edit&id=" + categoryId);
            return;
        }
        
        // Validate description
        if (!validator.validateDescription(description)) {
            session.setAttribute("errorMessage", "Description must not exceed 200 characters");
            response.sendRedirect("category_form.jsp?action=edit&id=" + categoryId);
            return;
        }
        
        String familyId = currentUser.getFamilyId();
        
        // Check if new name already exists (excluding current category)
        if (categoryManager.categoryNameExistsExcept(familyId, categoryName.trim(), categoryId)) {
            session.setAttribute("errorMessage", "Category name already exists. Please choose a different name");
            response.sendRedirect("category_form.jsp?action=edit&id=" + categoryId);
            return;
        }
        
        // Update category
        existingCategory.setCategoryName(categoryName.trim());
        existingCategory.setDescription(description != null ? description.trim() : "");
        
        boolean updated = categoryManager.updateCategory(existingCategory);
        
        if (updated) {
            session.setAttribute("successMessage", "Category '" + categoryName + "' updated successfully!");
            response.sendRedirect("categories.jsp");
        } else {
            session.setAttribute("errorMessage", "Failed to update category. Please try again");
            response.sendRedirect("category_form.jsp?action=edit&id=" + categoryId);
        }
    }
    
    // Handle category deletion
    // Only Family Head can delete, and only custom categories
    private void handleDeleteCategory(HttpServletRequest request, HttpServletResponse response,
                                     HttpSession session, CategoryManager categoryManager, User currentUser)
            throws Exception {
        
        String categoryId = request.getParameter("categoryId");
        
        // Validate category ID
        if (categoryId == null || categoryId.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Invalid category");
            response.sendRedirect("categories.jsp");
            return;
        }
        
        // Get existing category
        Category existingCategory = categoryManager.findByCategoryId(categoryId);
        
        if (existingCategory == null) {
            session.setAttribute("errorMessage", "Category not found");
            response.sendRedirect("categories.jsp");
            return;
        }
        
        // Check if trying to delete default category
        if (existingCategory.isDefault()) {
            session.setAttribute("errorMessage", "Cannot delete default categories");
            response.sendRedirect("categories.jsp");
            return;
        }
        
        // Delete category
        boolean deleted = categoryManager.deleteCategory(categoryId);
        
        if (deleted) {
            session.setAttribute("successMessage", "Category '" + existingCategory.getCategoryName() + "' deleted successfully");
            response.sendRedirect("categories.jsp");
        } else {
            session.setAttribute("errorMessage", "Failed to delete category. Please try again");
            response.sendRedirect("categories.jsp");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to categories page
        response.sendRedirect("categories.jsp");
    }
}