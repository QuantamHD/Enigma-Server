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
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
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
public class GetPublicKeyByUser extends HttpServlet {

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
        Connection conn;
        conn = cpds.getConnection();
        String userName = request.getParameter("user");
        PreparedStatement pptsm = null;
        ResultSet resultSet = null;

        try {
            String getUserPublicKey = "SELECT pkey FROM user_information WHERE Email = ?";
            InputStream publicKey = null;
            try {
                pptsm = conn.prepareStatement(getUserPublicKey);
                pptsm.setString(1, userName);
                resultSet = pptsm.executeQuery();
                while (resultSet.next()) {
                    publicKey = resultSet.getBinaryStream("pkey");
                    break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (pptsm != null && !pptsm.isClosed()) {
                    pptsm.close();
                }
                if (resultSet != null && !resultSet.isClosed()) {
                    resultSet.close();
                }
            }

            if (publicKey == null) {
                obj.put("ResponseCode", 220);
                obj.put("ResponseMessage", "There is no user who has that name");
                response.getOutputStream().print(obj.toJSONString());
                response.getOutputStream().close();
                return;
            }

            int data;
            while ((data = publicKey.read()) != -1) {
                response.getOutputStream().write(data);
            }
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
            Logger.getLogger(GetPublicKeyByUser.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(GetPublicKeyByUser.class.getName()).log(Level.SEVERE, null, ex);
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
