/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import businesslogic.AccountService;
import dataaccess.NotesDBException;
import dataaccess.UserDB;
import domainmodel.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author awarsyle
 */
public class AccountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getParameter("logout") != null) {
            HttpSession session = request.getSession();
            session.invalidate();
        }

        getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // validate
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            request.setAttribute("message", "Invalid.  Please try again.");
            getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            return;
        }

        AccountService as = new AccountService();
        if (as.loginHandler(username, password) != null) {
            UserDB userDB = new UserDB();
            try {

                User user = userDB.getUser(username);

                if (user.getActive() == true) {
                    if (user.getRole().equals("1")) {
                        session.setAttribute("username", username);
                        response.sendRedirect("admin");
                    } else {
                        session.setAttribute("username", username);
                        response.sendRedirect("notes");
                    }

                } else {
                    request.setAttribute("message", "Non-active users cannot sign in.");
                    getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);

                }
                return;
            } catch (NotesDBException ex) {
                Logger.getLogger(AccountServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            request.setAttribute("message", "Invalid.  Please try again.");
            getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);

        }

    }
}
