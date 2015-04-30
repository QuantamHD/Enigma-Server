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
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
import org.json.simple.JSONObject;

/**
 *
 * @author Ethan
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 10, // 1 MB 
        maxFileSize = 1024 * 1024 * 5, // 5 MB
        maxRequestSize = 1024 * 1024 * 100)      // 100 MB
public class RegistrationPage extends HttpServlet {

    private static ComboPooledDataSource cpds;
    private static final String emailPart1 = "<div class=\"email-wrapped\" id=\"yui_3_16_0_1_1422746600603_27227\"><div id=\"yiv1291285837\"><div dir=\"ltr\" id=\"yui_3_16_0_1_1422746600603_27226\"> <title>Landy - Responsive Email Template</title> <style type=\"text/css\"> #yiv1291285837 body{ width:100%; background-color:#ffffff; margin:0; padding:0; } #yiv1291285837 p, #yiv1291285837 h1, #yiv1291285837 h2, #yiv1291285837 h3, #yiv1291285837 h4{ margin-top:0;margin-bottom:0;padding-top:0;padding-bottom:0;} #yiv1291285837 span.yiv1291285837preheader{display:none;font-size:1px;} #yiv1291285837 html{ width:100%; } #yiv1291285837 table{ font-size:14px;border:0;} #yiv1291285837 _filtered #yiv1291285837 { } #yiv1291285837 body .filtered99999 .yiv1291285837container800_img{width:40% !important;} #yiv1291285837 body .filtered99999 .yiv1291285837section800_img img{width:100% !important;} #yiv1291285837 body .filtered99999 .yiv1291285837half-container800{width:55% !important;} #yiv1291285837 _filtered #yiv1291285837 { } #yiv1291285837 body .filtered99999 .yiv1291285837main-section-header{font-size:38px !important;} #yiv1291285837 body .filtered99999 .yiv1291285837show{display:block;} #yiv1291285837 body .filtered99999 .yiv1291285837hide{display:none;} #yiv1291285837 body .filtered99999 .yiv1291285837align-center{text-align:center;} #yiv1291285837 #yiv1291285837 body .filtered99999 .yiv1291285837main-image img{width:440px !important;height:auto !important;} #yiv1291285837 #yiv1291285837 body .filtered99999 .yiv1291285837divider img{width:440px !important;} #yiv1291285837 #yiv1291285837 body .filtered99999 .yiv1291285837banner img{width:440px !important;height:auto !important;} #yiv1291285837 #yiv1291285837 body .filtered99999 .yiv1291285837container590{width:440px !important;} #yiv1291285837 body .filtered99999 .yiv1291285837container580{width:420px !important;} #yiv1291285837 body .filtered99999 .yiv1291285837container800{width:440px !important;} #yiv1291285837 body .filtered99999 .yiv1291285837container800_img{width:100% !important;} #yiv1291285837 body .filtered99999 .yiv1291285837section800_img img{width:80% !important;} #yiv1291285837 body .filtered99999 .yiv1291285837half-container800{width:55% !important;} #yiv1291285837 body .filtered99999 .yiv1291285837half-container{width:220px !important;} #yiv1291285837 body .filtered99999 .yiv1291285837main-button{width:220px !important;} #yiv1291285837 #yiv1291285837 body .filtered99999 .yiv1291285837section-item{width:440px !important;} #yiv1291285837 body .filtered99999 .yiv1291285837section-img img{width:440px !important;height:auto !important;} #yiv1291285837 _filtered #yiv1291285837 { } #yiv1291285837 body .filtered99999 .yiv1291285837main-section-header{font-size:30px !important;} #yiv1291285837 #yiv1291285837 body .filtered99999 .yiv1291285837main-image img{width:280px !important;height:auto !important;} #yiv1291285837 #yiv1291285837 body .filtered99999 .yiv1291285837divider img{width:280px !important;} #yiv1291285837 body .filtered99999 .yiv1291285837align-center{text-align:center;} #yiv1291285837 #yiv1291285837 body .filtered99999 .yiv1291285837banner img{width:280px !important;height:auto !important;} #yiv1291285837 #yiv1291285837 body .filtered99999 .yiv1291285837container590{width:280px !important;} #yiv1291285837 body .filtered99999 .yiv1291285837container580{width:260px !important;} #yiv1291285837 body .filtered99999 .yiv1291285837container800{width:280px !important;} #yiv1291285837 body .filtered99999 .yiv1291285837container800_img{width:100% !important;} #yiv1291285837 body .filtered99999 .yiv1291285837section800_img img{width:80% !important;} #yiv1291285837 body .filtered99999 .yiv1291285837half-container800{width:55% !important;} #yiv1291285837 body .filtered99999 .yiv1291285837half-container{width:200px !important;} #yiv1291285837 body .filtered99999 .yiv1291285837main-button{width:200px !important;} #yiv1291285837 #yiv1291285837 body .filtered99999 .yiv1291285837section-item{width:280px !important;} #yiv1291285837 body .filtered99999 .yiv1291285837section-item-iphone{width:280px !important;} #yiv1291285837 body .filtered99999 .yiv1291285837section-img img{width:280px !important;height:auto !important;} #yiv1291285837 body .filtered99999 .yiv1291285837section-iphone-img img{width:280px !important;height:auto !important;} #yiv1291285837 #yiv1291285837 body .filtered99999 .yiv1291285837cta-btn img{width:260px !important;height:auto !important;} #yiv1291285837 </style> <table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"57c5aa\" style=\"background-repeat:no-repeat;\" class=\"yiv1291285837main-bg\" id=\"yui_3_16_0_1_1422746600603_27225\"> <tbody id=\"yui_3_16_0_1_1422746600603_27224\"><tr id=\"yui_3_16_0_1_1422746600603_27223\"> <td align=\"center\" id=\"yui_3_16_0_1_1422746600603_27222\"> <table border=\"0\" align=\"center\" width=\"590\" cellpadding=\"0\" cellspacing=\"0\" class=\"yiv1291285837container590\" id=\"yui_3_16_0_1_1422746600603_27232\"> <tbody id=\"yui_3_16_0_1_1422746600603_27231\"><tr id=\"yui_3_16_0_1_1422746600603_27251\"><td height=\"65\" style=\"font-size:65px;line-height:65px;\" id=\"yui_3_16_0_1_1422746600603_27250\">&nbsp;</td></tr> <tr id=\"yui_3_16_0_1_1422746600603_27230\"><td height=\"100\" style=\"font-size:100px;line-height:100px;\" id=\"yui_3_16_0_1_1422746600603_27229\">&nbsp;</td></tr> <tr> <td align=\"center\" style=\"color:#ffffff;font-size:32px;font-family:Montserrat, Calibri, sans-serif;line-height:32px;\" class=\"yiv1291285837white_color yiv1291285837main-header\"> <div class=\"yiv1291285837editable_text\" style=\"line-height:32px;\"> <span class=\"yiv1291285837text_container\">Welcome To Moder, Thank you for taking the time to sign up.</span> </div> </td> </tr> <tr><td height=\"20\" style=\"font-size:20px;line-height:20px;\">&nbsp;</td></tr> <tr id=\"yui_3_16_0_1_1422746600603_27272\"> <td align=\"center\" id=\"yui_3_16_0_1_1422746600603_27271\"> <table border=\"0\" width=\"480\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" class=\"yiv1291285837container580\" id=\"yui_3_16_0_1_1422746600603_27270\"> <tbody id=\"yui_3_16_0_1_1422746600603_27269\"><tr id=\"yui_3_16_0_1_1422746600603_27268\"> <td align=\"center\" style=\"color:#ffffff;font-size:16px;font-family:Quattrocento, Calibri, sans-serif;line-height:24px;\" class=\"yiv1291285837white_color\" id=\"yui_3_16_0_1_1422746600603_27267\"> <div class=\"yiv1291285837editable_text\" style=\"line-height:24px;\" id=\"yui_3_16_0_1_1422746600603_27266\"> <span class=\"yiv1291285837text_container\">Verify your account to get rapid fashion feedback.</span> </div> </td> </tr> </tbody></table> </td> </tr> <tr id=\"yui_3_16_0_1_1422746600603_27274\"><td height=\"70\" style=\"font-size:70px;line-height:70px;\" id=\"yui_3_16_0_1_1422746600603_27273\">&nbsp;</td></tr> <tr id=\"yui_3_16_0_1_1422746600603_27276\"> <td align=\"center\" id=\"yui_3_16_0_1_1422746600603_27275\"> <table border=\"0\" width=\"360\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" class=\"yiv1291285837half-container\"> <tbody><tr> <td> <a href=";
    private static final String emailPart2 = "> <table border=\"0\" align=\"left\" width=\"180\" cellpadding=\"0\" cellspacing=\"0\" class=\"yiv1291285837orange_button yiv1291285837main-button\" bgcolor=\"e66363\"> <tbody><tr><td height=\"13\" style=\"font-size:13px;line-height:13px;\">&nbsp;</td></tr> <tr> <td align=\"center\" style=\"color:#ffffff;font-size:16px;font-family:Ubuntu, Calibri, sans-serif;line-height:22px;\"> <div class=\"yiv1291285837editable_text\" style=\"line-height:22px;\"> <span class=\"yiv1291285837text_container\">Verify Account</span> </div> </td> </tr> <tr><td height=\"13\" style=\"font-size:13px;line-height:13px;\">&nbsp;</td></tr> </tbody></table> </a> <table border=\"0\" align=\"left\" width=\"2\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse;\" class=\"yiv1291285837half-container\"> <tbody><tr><td height=\"20\" width=\"2\" style=\"font-size:20px;line-height:20px;\">&nbsp;</td></tr> </tbody></table> <a href=\"https://www.moderapp.wordpress.com\"> <table border=\"0\" align=\"right\" width=\"160\" cellpadding=\"0\" cellspacing=\"0\" class=\"yiv1291285837main_color yiv1291285837main-button\" style=\"border:1px solid #fff;\"> <tbody><tr><td height=\"13\" style=\"font-size:13px;line-height:13px;\">&nbsp;</td></tr> <tr> <td align=\"center\" style=\"color:#ffffff;font-size:16px;font-family:Ubuntu, Calibri, sans-serif;line-height:20px;\"> <div class=\"yiv1291285837editable_text\" style=\"line-height:20px;\"> <span class=\"yiv1291285837text_container\">Visit Our Website</span> </div> </td> </tr> <tr><td height=\"13\" style=\"font-size:13px;line-height:13px;\">&nbsp;</td></tr> </tbody></table> </a> </td> </tr> <tr><td height=\"30\" style=\"font-size:30px;line-height:30px;\">&nbsp;</td></tr> <tr> <td align=\"left\" style=\"color:#ffffff;font-size:14px;line-height:20px;\" class=\"yiv1291285837white_color\"> <div class=\"yiv1291285837editable_text\" style=\"line-height:20px;\"> <span class=\"yiv1291285837text_container\"></span> </div> </td> </tr> </tbody></table> </td> </tr> <tr class=\"yiv1291285837hide\"><td height=\"30\" style=\"font-size:30px;line-height:30px;\">&nbsp;</td></tr> <tr><td height=\"60\" style=\"font-size:60px;line-height:50px;\">&nbsp;</td></tr> </tbody></table> </td> </tr> </tbody></table> <table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"ffffff\" class=\"yiv1291285837bg_color\"> <tbody><tr class=\"yiv1291285837hide\"><td height=\"50\" style=\"font-size:50px;line-height:50px;\">&nbsp;</td></tr> <tr><td height=\"50\" style=\"font-size:50px;line-height:50px;\">&nbsp;</td></tr><tr> <td> <table style=\"margin-left:auto;margin-right:auto;\" border=\"0\" width=\"290\" cellpadding=\"0\" cellspacing=\"0\"> <tbody><tr> <td> <table border=\"0\" width=\"285\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"> <tbody><tr> <td align=\"left\" style=\"color:#42474c;font-size:28px;font-family:Ubuntu, Calibri, sans-serif;font-weight:300;line-height:28px;\"> <div style=\"line-height:28px;\"> <span>About Moder</span> </div> </td> </tr> <tr><td height=\"40\" style=\"font-size:40px;line-height:40px;\">&nbsp;</td></tr> <tr> <td align=\"left\" style=\"color:#8d959d;font-family:Quattrocento, Calibri, sans-serif;font-size:16px;line-height:24px;\"> <div style=\"line-height:24px;\"> <span>Moder started on the simple principle that everyone should have the right to \"Be Who You Are\". Our app strives to provide a service that gives everyone access to unbiased judgement-free feedback.</span> </div> </td> </tr> <tr><td height=\"45\" style=\"font-size:45px;line-height:45px;\">&nbsp;</td></tr> <tr> <td align=\"left\" style=\"color:#e66363;font-size:18px;font-family:Montserrat, Calibri, sans-serif;line-height:22px;\"> <div style=\"line-height:22px;\"> <span> NOTE </span> </div> </td> </tr> <tr><td height=\"10\" style=\"font-size:10px;line-height:10px;\">&nbsp;</td></tr> <tr> <td align=\"left\" style=\"color:#8d959d;font-family:Quattrocento, Calibri, sans-serif;font-size:16px;line-height:24px;\"> <div style=\"line-height:24px;\"> <span>Moder will not tolerate bullying of any kind through our service. From day one we have designed our app around that idea.</span> </div> </td> </tr> </tbody></table> </td> </tr> </tbody></table> </td> </tr> <tr><td height=\"25\" style=\"font-size:25px;line-height:25px;\">&nbsp;</td></tr> </tbody></table> <table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" class=\"yiv1291285837bg_color\"> <tbody><tr><td height=\"25\" style=\"font-size:25px;line-height:25px;\">&nbsp;</td></tr> <tr> <td align=\"center\" class=\"yiv1291285837divider\"> <img height=\"1\" border=\"0\" width=\"100%\" style=\"display:block;width:100%;min-height:1px;\" src=\"http://promail.ma/envato/landy/img/divider.png\" alt=\"main image\"> </td> </tr> <tr><td height=\"25\" style=\"font-size:25px;line-height:25px;\">&nbsp;</td></tr> </tbody></table> <table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" class=\"yiv1291285837bg_color\"> <tbody><tr><td height=\"25\" style=\"font-size:25px;line-height:25px;\">&nbsp;</td></tr> <tr> <td align=\"center\" class=\"yiv1291285837divider\"> <img height=\"1\" border=\"0\" width=\"590\" style=\"display:block;width:590px;min-height:1px;\" src=\"http://promail.ma/envato/landy/img/divider.png\" alt=\"\"> </td> </tr> <tr><td height=\"25\" style=\"font-size:25px;line-height:25px;\">&nbsp;</td></tr> </tbody></table> <table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"ffffff\"> <tbody><tr><td height=\"20\" style=\"font-size:20px;line-height:20px;\">&nbsp;</td></tr> <tr> <td align=\"center\"> <table border=\"0\" align=\"center\" width=\"590\" cellpadding=\"0\" cellspacing=\"0\" class=\"yiv1291285837container590\"> <tbody><tr> <td> <table border=\"0\" align=\"left\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse;\" class=\"yiv1291285837container590\"> <tbody><tr> <td align=\"center\" style=\"color:#c6c7c9;font-size:12px;font-family:Ubuntu, Calibri, sans-serif;line-height:24px;\" class=\"yiv1291285837text_color\"> <div class=\"yiv1291285837editable_text\" style=\"line-height:24px;\"> <span class=\"yiv1291285837text_container\">Moder LLC</span> <span class=\"yiv1291285837text_container\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"https://moderapp.wordpress.com/download/tosprivacy/\">Privacy Policy and Terms of Service</a></span> </div> </td> </tr> </tbody></table> <table border=\"0\" align=\"left\" width=\"5\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse;\" class=\"yiv1291285837container590\"> <tbody><tr><td height=\"20\" width=\"5\" style=\"font-size:20px;line-height:20px;\">&nbsp;</td></tr> </tbody></table> <table border=\"0\" align=\"right\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse;\" class=\"yiv1291285837container590\"> <tbody><tr> <td> <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"> <tbody><tr> <td align=\"center\" style=\"color:#c6c7c9;font-size:12px;font-family:Ubuntu, Calibri, sans-serif;line-height:24px;\"> <div class=\"yiv1291285837editable_text\" style=\"line-height:24px;\"> <span class=\"yiv1291285837text_container\"> <a rel=\"nofollow\" style=\"color:#c6c7c9;text-decoration:none;\" class=\"yiv1291285837text_color\">Unsubscribe</a> </span> </div> </td> </tr> </tbody></table> </td> </tr> </tbody></table> </td> </tr> </tbody></table> </td> </tr> <tr><td height=\"20\" style=\"font-size:20px;line-height:20px;\">&nbsp;</td></tr> </tbody></table> </div></div></div>";

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
            Logger.getLogger(RegistrationPage.class.getName()).log(Level.SEVERE, null, ex);
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
        response.setCharacterEncoding("UTF-8");
        StringBuilder buffer = new StringBuilder();
        String        email   = request.getParameter("email");
        String        pwd1    = request.getParameter("pwd1");
        String        pwd2    = request.getParameter("pwd2");
        String        message = "";
        InputStream   pkeyInput = request.getPart("pkey").getInputStream();
                
        JSONObject obj  = new JSONObject();
        int        code = 0;
        
        
        /*
        * The boolean parametersAreMissing will return true if any of the required
        * parameters are null. This means that they were not supplied.
        */
        boolean parametersAreMissing = (pwd1 == null) || (pwd2 == null) || (email == null) || (pkeyInput == null);
        if (parametersAreMissing) {
            ModerResponses.invalidParameters(obj, response, "The server detected missing parameters: Line 91");
            return;
        }

        if (pwd1.length() > 64 || pwd2.length() > 64) {
            obj.put("ResponseCode", 367);
            obj.put("ResponseMessage", "The paramter password is too long. The maximum length allowed is 64 characters");
            response.getOutputStream().print(obj.toJSONString());
            response.getOutputStream().close();
            return;
        }

        if (!pwd1.equals(pwd2)) {
            code = 100;
            buffer.append("Invalid Input:Your Passwords didn't match. ");
        }
        if (!isValidEmailAddress(email)) {
            code = 100;
            buffer.append("Invalid Input:Your Email appears to be invalid.");
        }

        if (code == 100) {
            message = buffer.toString();
            obj.put("ResponseCode", code);
            obj.put("ResponseMessage", message);
            response.getOutputStream().print(obj.toJSONString());
            response.getOutputStream().close();
            buffer.setLength(0);
            return;
        }

        String hashedPass = "";
        try {
            hashedPass = PasswordHash.createHash(pwd1);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            Logger.getLogger(RegistrationPage.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (hashedPass.equals("")) {
            code = 200;
            buffer.append("Interal Error:Internal Server Error Please Try Again.");
            message = buffer.toString();
            obj.put("ResponseCode", code);
            obj.put("ResponseMessage", message);
            response.getOutputStream().print(obj.toJSONString());
            response.getOutputStream().close();
            buffer.setLength(0);
            return;
        }

        Connection conn = cpds.getConnection();

        String sqlQuery = "select Email from user_information where Email = ?";
        boolean emailExists = false;
        PreparedStatement ppstm = null;
        ResultSet result = null;
        try {
            ppstm = conn.prepareStatement(sqlQuery);
            ppstm.setString(1, email);
            
            result = ppstm.executeQuery();
            emailExists = result.next();
        } catch (SQLException ex) {
            Logger.getLogger(RegistrationPage.class.getName()).log(Level.SEVERE, null, ex);
            ModerResponses.invalidDatabaseConnection(obj, response);
            return;
        } finally {
            if (ppstm != null && !ppstm.isClosed()) {
                ppstm.close();
            }
            if (result != null && !result.isClosed()) {
                result.close();
            }
        }

        if (emailExists) {
            obj.put("ResponseCode", 100);
            obj.put("ResponseMessage", "The Email Address provided has already been used. ");
            response.getOutputStream().print(obj.toJSONString());
            response.getOutputStream().close();
            buffer.setLength(0);
            return;
        }

        String updateVerification = "INSERT INTO Verification (VerificationCode,User_ID) Values (?,?)";
        String UserID = generateUniqueID();
        String verificationCode = generateUniqueID();
        try {
            ppstm = conn.prepareStatement(updateVerification);
            ppstm.setString(1, verificationCode);
            ppstm.setString(2, UserID);
            ppstm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(RegistrationPage.class.getName()).log(Level.SEVERE, null, ex);
            ModerResponses.invalidDatabaseConnection(obj, response);
            return;
        } finally {
            if (ppstm != null && !ppstm.isClosed()) {
                ppstm.close();
            }
        }

        boolean failed = false;
        buffer.setLength(0);
        buffer.append(emailPart1);
        buffer.append("\"");
        buffer.append("https://www.moderapp.com/verify/");
        buffer.append(verificationCode);
        buffer.append("\"");
        buffer.append(emailPart2);
        int indexer = 0;
        /*
        do {
            try {
                SendGrid sendgrid = new SendGrid("ModerLLC", "59845984a!");
                SendGrid.Email sendGridEmail = new SendGrid.Email();
                sendGridEmail.addTo(email);
                sendGridEmail.setFrom("users@moderapp.com");
                sendGridEmail.setSubject("Moder Verfication");
                sendGridEmail.setHtml(buffer.toString());

                SendGrid.Response responseEmail = sendgrid.send(sendGridEmail);
                failed = !responseEmail.getStatus();
                indexer++;
            } catch (SendGridException ex) {
                Logger.getLogger(RegistrationPage.class.getName()).log(Level.SEVERE, null, ex);
                ModerResponses.invalidDatabaseConnection(obj, response, "Email Verifcation Sending Failed.");
                return;
            }
        } while (failed && indexer < 10);

        if (failed) {
            ModerResponses.invalidDatabaseConnection(obj, response, "The Verification Email Could not be sent.");
            return;
        }
        */

        String sqlCommand = "Insert into user_information (email,gender,Date_Joined,Age,Unique_ID,Number_of_Photos,Password,Verified,pkey) Values (?,?,NOW(),?,?,?,?,?,?)";
        boolean sqlErrorFlag = false;
        try {
            ppstm = conn.prepareStatement(sqlCommand);
            ppstm.setString(1, email);
            ppstm.setString(2, "");
            ppstm.setInt(3, -1);
            ppstm.setString(4, UserID);
            ppstm.setInt(5, 0);
            ppstm.setString(6, hashedPass);
            ppstm.setBoolean(7, false);
            ppstm.setBlob(8, pkeyInput);
            ppstm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(RegistrationPage.class.getName()).log(Level.SEVERE, null, ex);
            sqlErrorFlag = true;
        } finally {
            if (ppstm != null && !ppstm.isClosed()) {
                ppstm.close();
            }
        }

        if (sqlErrorFlag) {
            obj.put("ResponseCode", 140);
            obj.put("ResponseMessage", "A Database error occured try again in a few seconds. ");
            response.getOutputStream().print(obj.toJSONString());
            response.getOutputStream().close();
            buffer.setLength(0);
            return;
        }

        obj.put("ResponseCode", 300);
        obj.put("ResponseMessage", "Your Account has been created, welcome to Moder.");
        response.getOutputStream().print(obj.toJSONString());
        response.getOutputStream().close();
        buffer.setLength(0);
    }

    public String generateUniqueID() {
        return String.valueOf(UUID.randomUUID());
    }

    public boolean isValidEmailAddress(String email) {
        return true;
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
            Logger.getLogger(RegistrationPage.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(RegistrationPage.class.getName()).log(Level.SEVERE, null, ex);
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
