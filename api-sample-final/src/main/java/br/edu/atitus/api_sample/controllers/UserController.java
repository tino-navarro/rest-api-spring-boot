@RestController
@RequestMapping("/ws/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/by-email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        return userRepository.findByEmail(email)
            .map(user -> ResponseEntity.ok(user))
            .orElse(ResponseEntity.notFound().build());
    }
}