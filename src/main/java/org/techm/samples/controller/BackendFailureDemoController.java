package org.techm.samples.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.techm.samples.entity.User;
import org.techm.samples.service.UserService;

@RestController
@RequestMapping("/blog/users/debug")
@Profile("elk")
public class BackendFailureDemoController {

    @Autowired
    private UserService userService;

    @GetMapping("/backend-fallback")
    public Map<String, Object> backendFallback(@RequestParam(defaultValue = "false") boolean simulate) {
        String email = "fallback-demo@example.test";
        User fallback = userService.simulateSilentBackendFailure(email, simulate);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("simulate", simulate);
        response.put("message", simulate ? "Backend degraded; UI still worked with a fallback profile." : "Backend healthy");
        response.put("user", fallback);
        return response;
    }
}
