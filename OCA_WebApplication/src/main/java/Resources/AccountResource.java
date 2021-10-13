package Resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.accountDAO;
import db.accountDB;
import io.dropwizard.jersey.sessions.Session;
import org.apache.commons.io.IOUtils;
import org.jdbi.v3.core.Jdbi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    String tok;


    private final accountDAO acDAO;

    public AccountResource(Jdbi db) {

        acDAO = db.onDemand(accountDAO.class);

    }


//________________________________________________________________________________________________________________________________//
//login to account
//________________________________________________________________________________________________________________________________//
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response auth(@Session HttpSession session, @Context HttpServletRequest request) throws IOException {

        StringWriter writer = new StringWriter();             //read json parameters as input
        try {
            IOUtils.copy(request.getInputStream(), writer);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read input");
        }

        String json = writer.toString();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(json, Map.class); //store json data in map
        String tempUser = (String) map.get("user"); //tempUser is the username the user is trying to log in with
        String tempPass = (String) map.get("pass"); //tempPass is the password the user is trying to log in with

        List<accountDB> list = acDAO.get();
        boolean doesAccountExist = false;

        for (accountDB user : list) { //check through database to see if username exists

            if (user.username.equals(tempUser)) {
                doesAccountExist = true;
            }

        }


        if (doesAccountExist) { //if the username the user is attempting to log in with does exist in the database

            accountDB acc = acDAO.get(tempUser);

            if (tempUser.equals(acc.username) && tempPass.equals(acc.password)) { //if the login credentials match the database credentials
                                                                                  //store user and pass as tokens in the session
                                                                                  //could technically do only one token, but two is utilized later
                session.setAttribute("user", map.get("user"));
                session.setAttribute("pass", map.get("pass"));

                return Response.ok(new JSON_OUTPUT(201, "Successfully Logged In")).build();

            } else {
                return Response.ok(new JSON_OUTPUT(404, "Credentials do not match")).build();
            }
        } else {
            return Response.ok(new JSON_OUTPUT(404, "Not Found. Credentials do not match")).build();
        }
    }


//________________________________________________________________________________________________________________________________//
//Show list of all accounts and info
//________________________________________________________________________________________________________________________________//


    @GET
    public Response getAllAccounts(@Session HttpSession session) { //if admin, show list of all accounts and details


        if (session.getAttribute("user") == null) { //if user is logged in
            //error printed after next else statement
        }
        else {
            if (session.getAttribute("user").equals("admin")) { //is user is the admin account who is appropriately titled "admin"
                return Response.ok(acDAO.get()).build();
            } else {//if not admin
                return Response.ok(new JSON_OUTPUT(401, "You are not an admin")).build();
            }
        }//if not logged in
        return Response.ok(new JSON_OUTPUT(401, "Unauthorized. You are not logged in")).build();
    }



//________________________________________________________________________________________________________________________________//
//show your account information
//________________________________________________________________________________________________________________________________//


    @GET
    @Path("/specific")
    public Object getSpecificAccount(@Session HttpSession session) {

        if (session.getAttribute("user") == null) { //check if user is logged in
            //error printed below after else
        }
        else { //get user's login info
            String token = (String) session.getAttribute("user");
            accountDB acc = acDAO.get(token);
            return Response.ok(acc).build();
        } //not logged in:
        return Response.ok(new JSON_OUTPUT(401, "Unauthorized. You are not logged in")).build();
    }


//________________________________________________________________________________________________________________________________//
//
//________________________________________________________________________________________________________________________________//

    @DELETE
    public Object deleteSpecificAccount(@Session HttpSession session) {

        if (session.getAttribute("user") == null) { //check if user is logged in ->
            //error printed below after else
        } else { //if user is logged in and this request is selected, delete their account

            String token = (String) session.getAttribute("user");
            acDAO.delete(token);
            return Response.ok(new JSON_OUTPUT(201, "Username " + "'" + token + "'" + " Deleted")).build();

        }
        //if not logged in ->
        return Response.ok(new JSON_OUTPUT(401, "Unauthorized. You are not logged in")).build();

    }


//________________________________________________________________________________________________________________________________//
//add an account to database
//________________________________________________________________________________________________________________________________//

    @PUT
    public Object addAccount(@Context HttpServletRequest request,@Session HttpSession session) throws JsonProcessingException {

        if (session.getAttribute("user") == null) {//check if user is logged in

            StringWriter writer = new StringWriter();   //get parameters from json ->
            try {
                IOUtils.copy(request.getInputStream(), writer);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to read input");
            }

            String json = writer.toString();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(json, Map.class); //store parameters of json input in map
            //assign json values to strings ->
            String tempUser = (String) map.get("user");
            String tempPass = (String) map.get("pass");
            String tempAge = (String) map.get("age");
            String tempGender = (String) map.get("gender");


            List<accountDB> list = acDAO.get();
            boolean doesAccountExist = false;

            for (accountDB user : list) { //check to see if account exists in database

                if (user.username.equals(tempUser)) {
                    doesAccountExist = true;
                }

            }

            if (!doesAccountExist) { //if no account exists with said name, then make one ->

                int tempID = acDAO.getCount(); //count database
                tempID++; //add one to count of database for new id. id's out of whack, likely due to zero place
                tempID++;
                tempID++; //this is messy..
                System.out.println("count is here for debug" + tempID);
                boolean success = acDAO.put(tempID, tempUser,tempPass, tempAge, tempGender); //make new entry to database

                if (success) {
                    return Response.ok(new JSON_OUTPUT(201, "Account Made: " + success)).build();
                }
            }
            return Response.ok(new JSON_OUTPUT(404, "Not Found. Account Already Exists")).build();
        }

        return Response.ok(new JSON_OUTPUT(406, "Not Acceptable. Must Log out to make Account")).build();

    }



//________________________________________________________________________________________________________________________________//
//change own account info
//________________________________________________________________________________________________________________________________//



@POST
public Object updateAccount(@Context HttpServletRequest request,@Session HttpSession session) throws JsonProcessingException {

    if (session.getAttribute("user") != null) {//check to see if logged in

        StringWriter writer = new StringWriter(); //get parameters from json body ->
        try {
            IOUtils.copy(request.getInputStream(), writer);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read input");
        }

        String json = writer.toString();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(json, Map.class); //store json from body in map
        //store json in strings ->
        String paramUser = (String) map.get("user");
        String paramPass = (String) map.get("pass");
        //get session tokens ->
        String tempUser = (String) session.getAttribute("user");
        String tempPass = (String) session.getAttribute("pass");
        List<accountDB> list = acDAO.get();
        int tempID = 0;

        for (accountDB user : list) {//get id of account logged into

            if (user.username.equals(tempUser)) {
                tempID = Integer.parseInt(user.id);
            }

        }

        acDAO.post(tempID, paramUser,paramPass); //update account username and password.

        //set new tokens ->
        session.setAttribute("user", paramUser);
        session.setAttribute("pass", paramPass);

        return Response.ok(new JSON_OUTPUT(201, "Account username " + "'" + tempUser + "'" + " and password " + "'"
                    + tempPass + "' changed to username " + "'" + paramUser + "'" + " and password " + "'"
                    + paramPass + "'")).build();

    } else {

        return Response.ok(new JSON_OUTPUT(401, "Unauthorized. You are Not Logged In")).build();

    }
}

//________________________________________________________________________________________________________________________________//
//log out
//________________________________________________________________________________________________________________________________//

@DELETE
@Path("/logOut")
@Produces(MediaType.APPLICATION_JSON)
@ExceptionMetered
public Response logOut(@Session HttpSession session) {

    if (session.getAttribute("user") != null) { //if logged in
        session.removeAttribute("user");
        session.removeAttribute("pass");
        return Response.ok(new JSON_OUTPUT(202, "Succesfully Logged Out")).build();
        //successfuly logged out

    } else { //if not logged in ->
        return Response.ok(new JSON_OUTPUT(401, "Unauthorized. Cannot Log Out if not Logged In")).build();
    }
}

}
