package spring.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import spring.domain.Ingredient;
import spring.domain.Order;
import spring.domain.Taco;
import spring.domain.user.User;
import spring.repository.IngredientRepository;
import spring.repository.TacoRepository;
import spring.repository.UserRepository;
import spring.repository.jpa.IngredientJpaRepository;
import spring.repository.jpa.TacoJpaRepository;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/design")
@RequiredArgsConstructor
@SessionAttributes("order")
public class DesignTacoController {

    private final IngredientJpaRepository ingredientRepository;
    private final TacoJpaRepository tacoRepository;
    private final UserRepository userRepository;

    @ModelAttribute("order")
    public Order order() {
        return new Order();
    }

    @ModelAttribute("taco")
    public Taco taco() {
        return new Taco();
    }

    @GetMapping
    public String showDesignForm(Model model, Principal principal) {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredientRepository.findAll().forEach(ingredients::add);

        for (Ingredient.Type type : Ingredient.Type.values()) {
            model.addAttribute(type.toString().toLowerCase(), ingredients.stream().filter(x -> x.getType() == type).collect(Collectors.toList()));
        }

        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user",user);
        return "design";
    }

    @PostMapping
    public String processDesign(@Valid Taco design, Errors errors, @ModelAttribute Order order) {
        if (errors.hasErrors()) {
            return "design";
        }
        Taco savedTaco = tacoRepository.save(design);
        order.addDesign(savedTaco);

        return "redirect:/orders/current";
    }
}
