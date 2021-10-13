package Resources;

import dao.messagesDAO;
import db.messagesDB;
import io.dropwizard.jersey.sessions.Session;
import org.jdbi.v3.core.Jdbi;

import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;


@Path("/chat")
@Produces(MediaType.APPLICATION_JSON)
public class MessagesResource {


    private final messagesDAO meDAO;

    public MessagesResource(Jdbi db) {

        meDAO = db.onDemand(messagesDAO.class);

    }

//________________________________________________________________________________________________________________________________//
//get all messages in chat
//________________________________________________________________________________________________________________________________//

    @GET
    public Response getAllMessages(@Session HttpSession session) {

        if (session.getAttribute("user") != null) { //if logged in

            return Response.ok(meDAO.get()).build();

        } else { //if not logged in ->

            return Response.ok(new JSON_OUTPUT(401, "Unauthorized. You are not logged in")).build();

        }

    }


//________________________________________________________________________________________________________________________________//
//delete message at specific time
//________________________________________________________________________________________________________________________________//

    @DELETE
    @Path("/time/{time}")
    public Response deleteSpecificMessage(@PathParam("time") String time,@Session HttpSession session) { //delete message at time interval if your own

        if (session.getAttribute("user") != null) {//check login

                String tempUser = (String) session.getAttribute("user");
                boolean success = meDAO.check(time);

                if (!success) {//if no such time exists in the database. maybe a switch statement would make these neater...
                    return Response.ok(new JSON_OUTPUT(404, "NO ACTION. No such time in database exists.")).build();
                }
                messagesDB mes = meDAO.getIDfromTime(time);

                if (mes.user_name.equals(tempUser)) { //compare usernmame of user at time with logged in user

                    boolean deleted = meDAO.delete(time);
                    return Response.ok(new JSON_OUTPUT(201, "Message at time " + "'" + time + "'" + " Deleted")).build();

                } else { //if not the same user->
                    return Response.ok(new JSON_OUTPUT(401, "Unauthorized. Cannot delete someone else’s message")).build();
                }
            } else{ //if not logged in ->
                return Response.ok(new JSON_OUTPUT(401, "Unauthorized. You are not logged in")).build();

            }
    }

//________________________________________________________________________________________________________________________________//
//delete all messages if admin
//________________________________________________________________________________________________________________________________//
    @DELETE
    public Response deleteAllMessage(@Session HttpSession session) { //if admin login, delete all messages

        if (session.getAttribute("user") != null) {//check login
            if (session.getAttribute("user").equals("admin")) {//if admin, delete all messages ->
                boolean deleted = meDAO.deleteAll();
                return Response.ok(new JSON_OUTPUT(201, "All Messages Deleted")).build();
            } else {//if not admin
                return Response.ok(new JSON_OUTPUT(401, "Unauthorized. You are not logged in as admin")).build();
            }
        }

        return Response.ok(new JSON_OUTPUT(401, "Unauthorized. You are not logged in")).build();

    }

//________________________________________________________________________________________________________________________________//
//add a message to the chat under the name of the user currently logged in
//________________________________________________________________________________________________________________________________//
    @PUT
    @Path("message/{message}")
    public Object addMessage(@PathParam("message") String message,@Session HttpSession session) { //write message in chat

        if (session.getAttribute("user") != null) { //check login

            String tempUser = (String) session.getAttribute("user");
            int tempID = meDAO.getCount();
            tempID++;

            String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()); //get current time
            System.out.println("time is" + time);

            boolean success = meDAO.put(tempID, time, tempUser, message); //write message at current time with current logged in user

            if (success) { //if message successful ->
                return Response.ok(new JSON_OUTPUT(201, "Message added to chat: " + success)).build();
            }

            return Response.ok(new JSON_OUTPUT(404, "Not Found. Check URL parameters")).build();
        } else {
            return Response.ok(new JSON_OUTPUT(401, "Unauthorized. You are not logged in")).build();
        }
    }

//________________________________________________________________________________________________________________________________//
//edit a previous message of your own
//________________________________________________________________________________________________________________________________//

    @POST
    @Path("/time/{time}/newMessage/{newMessage}") //update a previous message if it's your own at specific time
    public Object editMessage(@PathParam("time") String time, @PathParam("newMessage") String newMessage,@Session HttpSession session) {

        if (session.getAttribute("user") != null) {//if logged in

            String tempUser = (String) session.getAttribute("user");
            boolean success = meDAO.check(time);
            if (success) { //if specific time is in database
                messagesDB mes = meDAO.getIDfromTime(time);

                if (mes.user_name.equals(tempUser)) { //compare message at time with user who sent it and current login
                    String oldTime = time;

                    time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()); //get new time

                    meDAO.post(mes.id, time, mes.user_name, newMessage); //update message with new message and new time

                    return Response.ok(new JSON_OUTPUT(201, "Message at time " + "'" + oldTime + "'" + "' changed to " +
                            "message " + "'" + newMessage + "'")).build();
                }
                else {
                    return Response.ok(new JSON_OUTPUT(401, "Unauthorized. Cannot edit a message you did not create.")).build();
                }
                }

            return Response.ok(new JSON_OUTPUT(404, "Not Found. Check that time entered is valid")).build();

        }
        else {
            return Response.ok(new JSON_OUTPUT(401, "Unauthorized. You are not logged in.")).build();
        }
    }
}