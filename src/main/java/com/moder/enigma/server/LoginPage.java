/*************************************************************************
 * 
 * MODER CONFIDENTIAL
 * __________________
 * 
 *  [2015] Moder LLC 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Moder LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Moder LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from ModerLLC.
 */
package com.moder.enigma.server;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/**
 *
 * @author Ethan
 */
public class LoginPage extends HttpServlet {

    private static final SecureRandom random = new SecureRandom();
    private static String GET_PASSWORD_QUERY;
    private static String INSERT_INTO_LOGIN_TABLE;
    private static String ALREADY_LOGGED_IN_QUERY;
    private static String WELCOME_TO__MODER;
    private static ComboPooledDataSource cpds;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        GET_PASSWORD_QUERY = "SELECT Unique_ID,Password from user_information where Email = ?;";
        INSERT_INTO_LOGIN_TABLE = "INSERT INTO login_tracker (Session_ID,User_ID,Expiration_Date) VALUES (?,?,NOW() + INTERVAL 1 DAY);";
        ALREADY_LOGGED_IN_QUERY = "SELECT Session_ID FROM login_tracker WHERE User_ID = ? and Expiration_Date > NOW();";
        WELCOME_TO__MODER = "Welcome to Moder, You are now logged in. ";
        
        cpds = new ComboPooledDataSource();
        
        try {
            cpds.setDriverClass("com.mysql.jdbc.Driver"); //loads the jdbc driver
        } catch (PropertyVetoException ex) {
            Logger.getLogger(LoginPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        cpds.setJdbcUrl("jdbc:mysql://mysql31696-enigma.whelastic.net/moder?autoReconnect=true");
        cpds.setUser("root");
        cpds.setPassword("sRyrrLWqcT");
        cpds.setMinPoolSize(5);
        cpds.setAcquireIncrement(5);
        cpds.setMaxPoolSize(30);
    }
    
    @Override
    public void destroy() {
        cpds.close();
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        /**
         * These variables are considered in this server implementation as 
         * our semi-global variables they are the most important objects
         */
        final String      email             = request.getParameter("email");
        final String      password          = request.getParameter("pwd");
        final JSONObject  obj               = new JSONObject();
        PreparedStatement ppstm             = null;
        ResultSet         resultSet         = null;
        Connection        conn              = cpds.getConnection();
        //End Psudeo-Globals

        
        final boolean invalidParamters  = email == null || password == null;
        if (invalidParamters) {
            ModerResponses.invalidParameters(obj, response);
            return;
        }

        String  passwordHash = "";
        String  user_UUID    = "";
        boolean sqlErrorFlag = false;

        try {
            try {
                ppstm = conn.prepareStatement(GET_PASSWORD_QUERY);
                ppstm.setString(1, email);
                resultSet = ppstm.executeQuery();

                while (resultSet.next()) {
                    passwordHash = resultSet.getString("Password");
                    user_UUID = resultSet.getString("Unique_ID");
                    break;
                }
            } catch (SQLException ex) {
                Logger.getLogger(LoginPage.class.getName()).log(Level.SEVERE, null, ex);
                sqlErrorFlag = true;
            } finally {
                if (ppstm != null && !ppstm.isClosed()) {
                    ppstm.close();
                }
                if (resultSet != null && !resultSet.isClosed()) {
                    resultSet.close();
                }
            }

            //No password was found attached to the supplied email.
            if (passwordHash.equals("") && !sqlErrorFlag) {
                ModerResponses.invalidParameters(obj, response);
                return;
            } else if (sqlErrorFlag) {
                ModerResponses.invalidDatabaseConnection(obj, response);
                return;
            }

            boolean passCorrect;

            try {
                passCorrect = PasswordHash.validatePassword(password, passwordHash);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                Logger.getLogger(LoginPage.class.getName()).log(Level.SEVERE, null, ex);
                ModerResponses.internalServerError(obj, response);
                return;
            }

            if (!passCorrect) {
                ModerResponses.invalidLoginInformation(obj, response);
                return;
            }

            try {
                ppstm = conn.prepareStatement(ALREADY_LOGGED_IN_QUERY);
                ppstm.setString(1, user_UUID);
                ResultSet set = ppstm.executeQuery();
                if (set.next()) {
                    addLoginCookie(set.getString("Session_ID"), response);
                    ModerResponses.requestSuccessful(obj, response, WELCOME_TO__MODER);
                    return;
                }

            } catch (SQLException ex) {
                Logger.getLogger(LoginPage.class.getName()).log(Level.SEVERE, null, ex);
                ModerResponses.invalidDatabaseConnection(obj, response);
                return;
            } finally {
                if (ppstm != null && !ppstm.isClosed()) {
                    ppstm.close();
                }
            }

            final String sessionID = nextSessionID();// The Session ID
            try {
                ppstm = conn.prepareStatement(INSERT_INTO_LOGIN_TABLE);//Insert the session ID and the userID 
                ppstm.setString(1, sessionID);
                ppstm.setString(2, user_UUID);
                ppstm.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(LoginPage.class.getName()).log(Level.SEVERE, null, ex);
                ModerResponses.invalidDatabaseConnection(obj, response);
                return;
            } finally {
                if (ppstm != null && !ppstm.isClosed()) {
                    ppstm.close();
                }

            }
            
            addLoginCookie(sessionID, response);
            ModerResponses.requestSuccessful(obj, response, WELCOME_TO__MODER);
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }
    }

    /**
     * Adds a Session ID to the clients browser or other device.
     *
     * @param sessionID A cryptographically secure key that cannot be guessed.
     * @param response The HTTPResponse from the doPost method.
     */
    private void addLoginCookie(String sessionID, HttpServletResponse response) {
        Cookie cookie = new Cookie("Unique_ID", sessionID);
        cookie.setMaxAge(24 * 60 * 60);//Cookie gets deleted every 24 hours
        response.addCookie(cookie);
    }

    /**
     * The session ID uses the SecureRandom class to create these session IDs.
     *
     * @return A random session ID for the user to keep track of.
     */
    public String nextSessionID() {
        return new BigInteger(130, random).toString(32);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(LoginPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(LoginPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
