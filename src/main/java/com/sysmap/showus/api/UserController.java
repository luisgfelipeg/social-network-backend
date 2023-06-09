package com.sysmap.showus.api;

import com.sysmap.showus.services.authentication.security.IJwtService;
import com.sysmap.showus.services.user.IUserService;
import com.sysmap.showus.services.user.dto.FollowersResponse;
import com.sysmap.showus.services.user.dto.UserRequest;
import com.sysmap.showus.services.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User")
public class UserController {
    @Autowired
    private IUserService _userService;
    @Autowired
    private IJwtService _jwtService;
    @GetMapping()
    @Operation(summary = "Get a User", security = @SecurityRequirement(name = "token"))
    @Parameter(name = "RequestedBy", description = "User Id Authorization", required = true, schema = @Schema(type = "string"))
    public ResponseEntity<UserResponse> getUserByEmail(String email, @RequestHeader("RequestedBy") String CurrentUserId){
        return ResponseEntity.ok().body(_userService.getUserByEmail(email));
    }

    @GetMapping("/follow")
    @Operation(summary = "Get a list of users to follow", security = @SecurityRequirement(name = "token"))
    @Parameter(name = "RequestedBy", description = "User Id Authorization", required = true, schema = @Schema(type = "string"))
    public ResponseEntity<List<FollowersResponse>> getUsersToFollow(@RequestHeader("RequestedBy") String CurrentUserId){
        return ResponseEntity.ok().body(_userService.getUsersToFollow());
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new user")
    public ResponseEntity<UUID> createUser(@RequestBody UserRequest request){
        UUID user = _userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping(value = "/photo/upload", name = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload photo user profile", security = @SecurityRequirement(name = "token"))
    @Parameter(name = "RequestedBy", description = "User Id Authorization", required = true, schema = @Schema(type = "string"))
    public ResponseEntity<Void> uploadPhotoProfile(@RequestParam("photo") MultipartFile photo, @RequestHeader("RequestedBy") String CurrentUserId){
        try {
            _userService.uploadPhotoProfile(photo);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/add")
    @Operation(summary = "Add follower", security = @SecurityRequirement(name = "token"))
    @Parameter(name = "RequestedBy", description = "User Id Authorization", required = true, schema = @Schema(type = "string"))
    public ResponseEntity<UserResponse> addFollower(String email, @RequestHeader("RequestedBy") String CurrentUserId){
        return ResponseEntity.ok().body(_userService.addFollower(email));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete a user", security = @SecurityRequirement(name = "token"))
    @Parameter(name = "RequestedBy", description = "User Id Authorization", required = true, schema = @Schema(type = "string"))
    public ResponseEntity<Void> deleteById(@RequestHeader("RequestedBy") String CurrentUserId){
        _userService.deleteUser();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/follower/unfollow")
    @Operation(summary = "Unfollow a follower", security = @SecurityRequirement(name = "token"))
    @Parameter(name = "RequestedBy", description = "User Id Authorization", required = true, schema = @Schema(type = "string"))
    public ResponseEntity<Void> unfollow(String email, @RequestHeader("RequestedBy") String CurrentUserId){
        _userService.unfollow(email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update")
    @Operation(summary = "Update user", security = @SecurityRequirement(name = "token"))
    @Parameter(name = "RequestedBy", description = "User Id Authorization", required = true, schema = @Schema(type = "string"))
    public ResponseEntity<UserResponse> updateUser(@RequestBody UserRequest request, @RequestHeader("RequestedBy") String CurrentUserId) {
        var user = new UserResponse(_userService.getUserById(_userService.updateUser(request).getId()));
        return ResponseEntity.ok().body(user);
    }
}
