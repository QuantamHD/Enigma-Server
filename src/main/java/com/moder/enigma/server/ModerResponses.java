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

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/**
 *
 * @author Ethan
 */
public final class ModerResponses {
     
    /**
     * This usually results in a broken pipe. I.e the Database connection was open for too long 
     * then it is reset immeadiatly.
     * 
     * @param obj
     * @param response
     * @throws IOException 
     */
    public final static void invalidDatabaseConnection(JSONObject obj, HttpServletResponse response) throws IOException {
        //A Database connection error occured.
        obj.put("ResponseCode", 140);
        obj.put("ResponseMessage", "A Database error occured try again in a few seconds. ");
        response.getOutputStream().print(obj.toJSONString());
        response.getOutputStream().close();
    }
       /**
     * This usually results in a broken pipe. I.e the Database connection was open for too long 
     * then it is reset immeadiatly.
     * 
     * @param obj
     * @param response
     * @param message
     * @throws IOException 
     */
    public final static void invalidDatabaseConnection(JSONObject obj, HttpServletResponse response, String message) throws IOException {
        //A Database connection error occured.
        obj.put("ResponseCode", 140);
        obj.put("ResponseMessage", message);
        response.getOutputStream().print(obj.toJSONString());
        response.getOutputStream().close();
    }


    /**
     * If for some reason the parameters supplied by the user/agent were
     * faulty.
     * 
     * 
     * @param obj
     * @param response
     * @throws IOException 
     */
    public final static void invalidParameters(JSONObject obj, HttpServletResponse response) throws IOException {
        obj.put("ResponseCode", 100);
        obj.put("ResponseMessage", "One or more pieces of information is missing. ");
        response.getOutputStream().print(obj.toJSONString());
        response.getOutputStream().close();
    }
    
    /**
     * If for some reason the parameters supplied by the user/agent were
     * faulty.
     * 
     * 
     * @param obj
     * @param response
     * @param message
     * @throws IOException 
     */
    public final static void invalidParameters(JSONObject obj, HttpServletResponse response, String message) throws IOException {
        obj.put("ResponseCode", 100);
        obj.put("ResponseMessage", message);
        response.getOutputStream().print(obj.toJSONString());
        response.getOutputStream().close();
    }
    
    /**
     * A generic success response that can be customized by supplying the response 
     * message.
     * 
     * @param obj
     * @param response
     * @param responseMessage
     * @throws IOException 
     */
    public final static void requestSuccessful(JSONObject obj, HttpServletResponse response, String responseMessage) throws IOException {
        obj.put("ResponseCode", 300);
        obj.put("ResponseMessage",responseMessage);
        response.getOutputStream().print(obj.toJSONString());
        response.getOutputStream().close();
    }
    
    /**
     * If the login information supplied by the user did
     * not match any records in the database.
     * 
     * @param obj
     * @param response
     * @throws IOException 
     */
    public final static void invalidLoginInformation(JSONObject obj, HttpServletResponse response) throws IOException {
        obj.put("ResponseCode", 100);
        obj.put("ResponseMessage", "Either the email or password provided was inncorrect. ");
        response.getOutputStream().print(obj.toJSONString());
        response.getOutputStream().close();
    }
    
    /**
     * this should be called if any kind of error occurs that isn't covered
     * under the other responses. Or if you don't want the user to know exactly
     * what error occurred.
     * 
     * @param obj
     * @param response
     * @throws IOException 
     */
    public final static void internalServerError(final JSONObject obj, HttpServletResponse response) throws IOException {
        obj.put("ResponseCode", 200);
        obj.put("ResponseMessage", "There was an internal server error.");
        response.getOutputStream().print(obj.toJSONString());
        response.getOutputStream().close();
    }
    
    /**
     * this should be called if any kind of error occurs that isn't covered
     * under the other responses. Or if you don't want the user to know exactly
     * what error occurred.
     * 
     * @param obj
     * @param response
     * @throws IOException 
     */
    public final static void noSuchResourceFound(final JSONObject obj, HttpServletResponse response) throws IOException {
        obj.put("ResponseCode", 127);
        obj.put("ResponseMessage", "The Resource Requested was not found. ");
        response.getOutputStream().print(obj.toJSONString());
        response.getOutputStream().close();
    }
    /**
     * this should be called if any kind of error occurs that isn't covered
     * under the other responses. Or if you don't want the user to know exactly
     * what error occurred.
     * 
     * @param obj
     * @param response
     * @throws IOException 
     */
    public final static void accessDenied(final JSONObject obj, HttpServletResponse response) throws IOException {
        obj.put("ResponseCode", 269);
        obj.put("ResponseMessage", "You do not have access to the requested resource. ");
        response.getOutputStream().print(obj.toJSONString());
        response.getOutputStream().close();
    }
    
    /**
     * this should be called if any kind of error occurs that isn't covered
     * under the other responses. Or if you don't want the user to know exactly
     * what error occurred.
     * 
     * @param obj
     * @param response
     * @throws IOException 
     */
    public final static void noNewPhotos(final JSONObject obj, HttpServletResponse response) throws IOException {
        obj.put("ResponseCode", 281);
        obj.put("ResponseMessage", "There are no new photos to be rated. ");
        response.getOutputStream().print(obj.toJSONString());
        response.getOutputStream().close();
        
    }
}
