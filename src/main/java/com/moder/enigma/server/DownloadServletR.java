/**
 * ***********************************************************************
 *
 * MODER CONFIDENTIAL __________________
 *
 * [2015] Moder LLC All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of
 * Moder LLC and its suppliers, if any. The intellectual and technical concepts
 * contained herein are proprietary to Moder LLC and its suppliers and may be
 * covered by U.S. and Foreign Patents, patents in process, and are protected by
 * trade secret or copyright law. Dissemination of this information or
 * reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from ModerLLC.
 */
package com.moder.enigma.server;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.beans.PropertyVetoException;
import java.io.IOException;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Ethan
 */
public class DownloadServletR extends HttpServlet {

    private static ComboPooledDataSource cpds;

    @Override
    public void init(ServletConfig config) throws ServletException {

        super.init(config);
        try {
            cpds = new ComboPooledDataSource();
            cpds.setDriverClass("com.mysql.jdbc.Driver"); //loads the jdbc driver
            cpds.setJdbcUrl("jdbc:mysql://mysql31696-enigma.whelastic.net/moder?autoReconnect=true");
            cpds.setUser("root");
            cpds.setPassword("sRyrrLWqcT"); 
            cpds.setMinPoolSize(5);
            cpds.setAcquireIncrement(5);
            cpds.setMaxPoolSize(30);
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        }

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
     * @throws java.sql.SQLException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        response.setContentType("application/json");

        boolean loggedIn = false;
        String userID = "";
        String sessionID = "";
        final JSONObject obj = new JSONObject();
        final Cookie[] cookies = request.getCookies();
        Connection conn;
        String username = null;
        conn = cpds.getConnection();
        PreparedStatement pptsm = null;
        ResultSet resultSet = null;

        JSONArray enckey = new JSONArray();
        JSONArray vector = new JSONArray();
        JSONArray message = new JSONArray();

        try {
            // <editor-fold defaultstate="collapsed" desc="Login Verification Code. Click Plus on the left to open">
            if (conn == null) {
                ModerResponses.invalidDatabaseConnection(obj, response, "The database connection was null. ");
                return;
            }

            if (cookies == null) {
                obj.put("ResponseCode", 202);
                obj.put("ResponseMessage", "It appears that you are not logged in. No Cookies");
                response.getOutputStream().print(obj.toJSONString());
                response.getOutputStream().close();
                return;
            }

            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("Unique_ID")) {
                    sessionID = cookie.getValue();
                }
            }

            if (sessionID.equals("")) {
                obj.put("ResponseCode", 202);
                obj.put("ResponseMessage", "It appears that you are not logged in. No Session ID");
                response.getOutputStream().print(obj.toJSONString());
                response.getOutputStream().close();
                return;
            }

            String sqlSessionIDRecord = "Select User_ID from login_tracker where Session_ID = ? and Expiration_Date > NOW()";

            boolean sqlError = false;
            try {
                pptsm = conn.prepareStatement(sqlSessionIDRecord);
                pptsm.setString(1, sessionID);
                resultSet = pptsm.executeQuery();
                while (resultSet.next()) {
                    userID = resultSet.getString("User_ID");
                    
                }
            } catch (SQLException ex) {
                Logger.getLogger(DownloadServletR.class.getName()).log(Level.SEVERE, null, ex);
                sqlError = true;
            } finally {
                if (pptsm != null && !pptsm.isClosed()) {
                    pptsm.close();
                }
                if (resultSet != null && !resultSet.isClosed()) {
                    resultSet.close();
                }
            }

            if (sqlError) {
                obj.put("ResponseCode", 140);
                obj.put("ResponseMessage", "A Database error occured try again in a few seconds. ");
                response.getOutputStream().print(obj.toJSONString());
                response.getOutputStream().close();
                return;
            }

            if (userID == null || userID.equals("")) {
                obj.put("ResponseCode", 202);
                obj.put("ResponseMessage", "It appears that you are not logged in. UserID Not Found");
                response.getOutputStream().print(obj.toJSONString());
                response.getOutputStream().close();
                return;
            }
            // </editor-fold>
            
            String getUsername = "Select Email from user_information WHERE Unique_ID = ?";
            try {
                pptsm = conn.prepareStatement(getUsername);
                pptsm.setString(1, userID);
                resultSet = pptsm.executeQuery();
                while (resultSet.next()) {
                    username = resultSet.getString("Email");            
                }
            } catch (SQLException ex) {
                Logger.getLogger(DownloadServletR.class.getName()).log(Level.SEVERE, null, ex);
                obj.put("ResponseCode", 140);
                obj.put("ResponseMessage", "A database error occured");
                response.getOutputStream().print(obj.toJSONString());
                response.getOutputStream().close();
                return;
            } finally {
                if (pptsm != null && !pptsm.isClosed()) {
                    pptsm.close();
                }
                if (resultSet != null && !resultSet.isClosed()) {
                    resultSet.close();
                }
            }
            
            String getMessages = "SELECT * FROM message_log WHERE user = ? AND hasbeenread  = ?";

            try {
                pptsm = conn.prepareStatement(getMessages);
                pptsm.setString(1, username);
                pptsm.setBoolean(2, false);
                resultSet = pptsm.executeQuery();
                while (resultSet.next()) {
                    enckey.add(Base64.encode(resultSet.getBytes("enckey")));
                    vector.add(Base64.encode(resultSet.getBytes("vector")));
                    message.add(Base64.encode(resultSet.getBytes("message")));
                }

                obj.put("enckey", enckey);
                obj.put("vector", vector);
                obj.put("message", message);
                obj.put("user", username);
            } catch (SQLException e) {
                e.printStackTrace();
                obj.put("ResponseCode", 140);
                obj.put("ResponseMessage", "A database error occured");
                response.getOutputStream().print(obj.toJSONString());
                response.getOutputStream().close();
                return;
            } finally {
                if (pptsm != null && !pptsm.isClosed()) {
                    pptsm.close();
                }
                if (resultSet != null && !resultSet.isClosed()) {
                    resultSet.close();
                }
            }

            String read = "UPDATE message_log SET hasbeenread = ? WHERE user = ?";

            try {
                pptsm = conn.prepareStatement(read);
                pptsm.setBoolean(1, true);
                pptsm.setString(2, username);
                pptsm.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                obj.put("ResponseCode", 140);
                obj.put("ResponseMessage", "A database error occured");
                response.getOutputStream().print(obj.toJSONString());
                response.getOutputStream().close();
                return;
            } finally {
                if (pptsm != null && !pptsm.isClosed()) {
                    pptsm.close();
                }
            }

            obj.put("ResposneCode", 300);
            obj.put("ResponseMessage", "Completed Here are the messages");
            response.getOutputStream().print(obj.toJSONString());
            response.getOutputStream().close();

        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }

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
            Logger.getLogger(DownloadServletR.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(DownloadServletR.class.getName()).log(Level.SEVERE, null, ex);
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
