package rest;

import business.ChatApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api")
@CrossOrigin
@ComponentScan("business")

public class RestController {
    public static final String HELLOCOMMAND = "!hello ";
    @Autowired
    private ChatApplication chatApplication;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        System.out.println("hello world, I have just started up");
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.submit(chatApplication::runServer);
    }

    @GetMapping
    @Operation(summary = "Welcome Message", description = "Returns a welcome message for the Chat Application")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public String welcomeMessage() {
        return "Welcome to Chat Application";
    }

    @RequestMapping(value="/hello/{nickname}",method= RequestMethod.GET)
    public void helloMessage(@PathVariable String nickname) {
        chatApplication.sendRequestFromRestService(HELLOCOMMAND+nickname);
    }

    @RequestMapping(value="/acknowledge/person/{nickname}",method= RequestMethod.GET)
    public void acknowledgeUser(@PathVariable String nickname) {
        chatApplication.sendRequestFromRestService("!ack "+nickname);
    }

    @RequestMapping(value="/createGroup/{groupName}",method= RequestMethod.GET)
    public void createGroup(@PathVariable String groupName) {
        chatApplication.sendRequestFromRestService("!group "+groupName);
    }

    @RequestMapping(value="/inviteGroup/{groupName}/{personToBeInvited}",method= RequestMethod.GET)
    public void inviteToGroup(@PathVariable String groupName, @PathVariable String personToBeInvited) {
        chatApplication.sendRequestFromRestService("!invite "+groupName+" "+personToBeInvited);
    }

    @RequestMapping(value="/acknowledge/group/{groupName}",method= RequestMethod.GET)
    public void acknowledgeGroup(@PathVariable String groupName) {
        chatApplication.sendRequestFromRestService("!ackg "+groupName);
    }

    @RequestMapping(value="/sendMessage/person/{nickname}",method= RequestMethod.POST)
    public void acknowledgeGroup(@RequestBody String message,@PathVariable String nickname) {
        chatApplication.sendRequestFromRestService("#"+nickname);
        chatApplication.sendRequestFromRestService(message);
    }

    @RequestMapping(value="/sendMessage/group/{groupName}",method= RequestMethod.POST)
    public void sendMessageGroup(@RequestBody String message,@PathVariable String groupName) {
        chatApplication.sendRequestFromRestService("!sendGroup " + groupName + " " + message);
    }

//    @RequestMapping(value="/bye/{nickname}",method= RequestMethod.GET)
//    public void disconnectUser(@PathVariable String nickname) {
//        chatApplication.sendRequestFromRestService("!bye "+nickname);
//    }
}
