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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.json.simple.JSONObject;

/**
 *
 * @author Ethan
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1024, // 100 MB 
        maxFileSize = 1024 * 1024 * 1024, // 100 MB
        maxRequestSize = 1024 * 1024 * 100)      // 100 MB
public class UploadServlet extends HttpServlet {

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
        response.setContentType("application/json;charset=UTF-8");
        JSONObject obj = new JSONObject();
        Connection conn = cpds.getConnection();
        PreparedStatement ppstm = null;
        ResultSet result = null;
        Part message = request.getPart("message");
        Part vector = request.getPart("vector");
        Part key = request.getPart("key");
        String user = request.getParameter("user");

        if(message == null || vector == null || key == null || user == null){
            obj.put("ResponseCode", 202);
            obj.put("ResponseMessage", "Some Paramters were missing");
            response.getOutputStream().print(obj.toJSONString());
            response.getOutputStream().close();
            return;
        }   
        
        String updateMessageLog = "INSERT INTO message_log (user,enckey,vector,message,message_ID) VALUES (?,?,?,?,?)";
        boolean sqlError = false;
        try {
            ppstm = conn.prepareStatement(updateMessageLog);
            ppstm.setString(1, user);
            ppstm.setBlob(2, key.getInputStream());
            ppstm.setBlob(3, vector.getInputStream());
            ppstm.setBlob(4, message.getInputStream());
            ppstm.setString(5, generateUniqueID());
            ppstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            sqlError = true;
        } finally {
            if (ppstm != null && !ppstm.isClosed()) {
                ppstm.close();
            }
        }

        if (sqlError) {
            obj.put("ResponseCode", 140);
            obj.put("ResponseMessage", "A database error occured");
            response.getOutputStream().print(obj.toJSONString());
            response.getOutputStream().close();
            return;
        }

        obj.put("ResponseCode", 300);
        obj.put("ResposneMessage", "Request successful you have sent your message");
        response.getOutputStream().print(obj.toJSONString());
        response.getOutputStream().close();
    }

    public String generateUniqueID() {
        return String.valueOf(UUID.randomUUID());
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
            Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex);
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
