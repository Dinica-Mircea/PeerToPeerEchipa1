package rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api/")
@CrossOrigin
public class RestController {

    @GetMapping
    @Operation(summary = "Welcome Message", description = "Returns a welcome message for the Chat Application")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public String welcomeMessage() {
        return "Welcome to Chat Application";
    }
}
