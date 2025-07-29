/*
 * package org.techm.samples.controller;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.http.ResponseEntity; import
 * org.springframework.web.bind.annotation.*; import
 * org.techm.samples.entity.User; import org.techm.samples.service.UserService;
 * 
 * @RestController
 * 
 * @RequestMapping("/blog/users") public class UserController {
 * 
 * @Autowired private UserService userService;
 * 
 * @PostMapping("/register") public ResponseEntity<User>
 * registerUser(@RequestBody User user) { User savedUser =
 * userService.register(user); return ResponseEntity.ok(savedUser); }
 * 
 * @GetMapping("/login/{email}") public ResponseEntity<?>
 * getUserDetails(@PathVariable String email) { try { return
 * ResponseEntity.ok(userService.userByUsername(email)); } catch (Exception e) {
 * return ResponseEntity.status(404).body("User not found"); } } }
 */

package org.techm.samples.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.techm.samples.entity.User;
import org.techm.samples.service.UserService;

@Controller
@RequestMapping("/blog/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "blogs/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "blogs/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
    	System.out.println("Saving user: " + user.getEmail());
        userService.register(user);
        return "redirect:/blog/users/login";
    }

    @GetMapping("/blogger/dashboard")
    @PreAuthorize("hasRole('BLOGGER')")
    public String bloggerDashboard() {
        return "blogs/blogger-dashboard";
    }

    @GetMapping("/guest/dashboard")
    @PreAuthorize("hasRole('GUEST')")
    public String readerDashboard() {
        return "blogs/guest-dashboard";
    }
    

    @GetMapping("/redirect")
    public String redirectAfterLogin(Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_BLOGGER"))) {
            return "redirect:/blog/users/blogger/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_GUEST"))) {
            return "redirect:/blog/users/guest/dashboard";
        }
        return "redirect:/blog/users/login?error";
    }

}

