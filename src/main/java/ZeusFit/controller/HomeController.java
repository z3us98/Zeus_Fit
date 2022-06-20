package ZeusFit.controller;


import ZeusFit.model.Corso;
import ZeusFit.repository.CorsoRepository;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Set;

@Controller
public class HomeController {

    @Autowired
    private CorsoRepository corsoRepository;

        @GetMapping("/login")
        public String login(Model model) {
            Set<String> ruolo = getSecurityContext().getToken().getRealmAccess().getRoles();
            if (ruolo.contains("Admin")){
                return "redirect:/home-admin";
            }

            ArrayList<Corso> listaCorsi = (ArrayList<Corso>) corsoRepository.findAll();
            model.addAttribute("corsi", listaCorsi);
            final String nome = getSecurityContext().getToken().getPreferredUsername();
            model.addAttribute("nome",nome);

            //Old Auth Controls used with spring security Auth
            /*
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Admin"))) {

                return "redirect:/home-admin";
            }
            else if(auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Member"))) {

                return "redirect:/homepage";
            }
            else

             */
            return "redirect:/homepage";
        }

        @GetMapping("/")
        public String homepage(Model model) {
            Set<String> ruolo = getSecurityContext().getToken().getRealmAccess().getRoles();
            if (ruolo.contains("Admin")){
                return "redirect:/home-admin";
            }

            ArrayList<Corso> listaCorsi = (ArrayList<Corso>) corsoRepository.findAll();
            model.addAttribute("corsi", listaCorsi);
            final String nome = getSecurityContext().getToken().getPreferredUsername();
            model.addAttribute("nome",nome);


            //Old Auth Controls used with spring security Auth
            /*
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Admin"))) {

                return "redirect:/home-admin";
            }
            else if(auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Member"))) {

                return "redirect:/homepage";
            }
            else

             */
                return "redirect:/homepage";
        }


        /*
        @GetMapping("/about-us")
        public String showabout(){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {

                return "about-us";
            }
            else if(auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("UTENTE"))) {

                return "about-us";
            }
            else
                return "rediret:/login";
        }

         */


    //Support Function used to get user information for the web page
    private KeycloakSecurityContext getSecurityContext() {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
    }

}




