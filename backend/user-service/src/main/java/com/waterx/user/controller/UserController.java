package com.waterx.user.controller;

import com.waterx.user.entity.User;
import com.waterx.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;

    @PostMapping("/sync")
    public ResponseEntity<User> syncUser(@RequestBody Map<String, String> payload) {
        String firebaseUid = payload.get("firebaseUid");
        if (firebaseUid == null || firebaseUid.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userRepository.findByFirebaseUid(firebaseUid).orElse(new User());
        
        user.setFirebaseUid(firebaseUid);
        if (payload.containsKey("name")) user.setName(payload.get("name"));
        if (payload.containsKey("phone")) user.setPhone(payload.get("phone"));
        if (payload.containsKey("email")) user.setEmail(payload.get("email"));
        
        user.setLastLogin(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        log.info("Synced user: {}", savedUser.getFirebaseUid());
        
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/{firebaseUid}")
    public ResponseEntity<User> getUser(@PathVariable String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
