package com.tejobar.tejo_bar.exception;

import com.tejobar.tejo_bar.model.Persona;
import com.tejobar.tejo_bar.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private PersonaService personaService;

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                String correo = auth.getName();
                Persona persona = personaService.findByCorreo(correo);
                if (persona != null) {
                    model.addAttribute("nombreUsuario", persona.getNombre());
                    model.addAttribute("rolUsuario", persona.getRol().name());
                }
            }
        } catch (Exception e) {
            // Si hay algún error, simplemente no agregamos los atributos
        }
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "No tienes permisos para realizar esta acción.");
        return "redirect:/dashboard";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/dashboard";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Datos inválidos: " + ex.getMessage());
        return "redirect:/dashboard";
    }
}
