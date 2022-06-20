package ZeusFit.controller;

import ZeusFit.controller.dto.ListaPrenotazioniDto;
import ZeusFit.controller.dto.PrenotazioneDto;
import ZeusFit.controller.dto.SaldoDto;
import ZeusFit.controller.dto.User_KeyDto;
import ZeusFit.model.*;
import ZeusFit.repository.*;
import ZeusFit.service.PrenotazioneService;
import ZeusFit.service.RateLimiterService;
import ZeusFit.service.UserService;
import ZeusFit.service.User_KeyServiceImpl;
import io.github.bucket4j.ConsumptionProbe;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller

@RequestMapping("/homepage")
public class UserHomepageController {

    @Autowired
    private User_KeyServiceImpl user_keyService;

    @Autowired
    private User_KeyRepository user_keyRepository;
    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private RateLimiterService rateLimiterService;

    @Autowired
    private UserService userService;

    @Autowired
    private PrenotazioneService prenotazioneService;

    @Autowired
    private CorsoRepository corsoRepository;

    @Autowired
    private LezioneRepository lezioneRepository;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

/*
    @GetMapping
    public String showHomepage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("UTENTE"))) {

            String email = auth.getName();
            Utente utente = utenteRepository.findByEmail(email);

            ArrayList<Corso> listaCorsi = (ArrayList<Corso>) corsoRepository.findAll();

            //Nome utente e saldo mi servono per la navbar, la lista corsi per la visualizzazione dei corsi in homepage
            //Nome utente e saldo mi serviranno in tutte le pagine relative all'utente

            model.addAttribute("nome",utente.getNome());
            model.addAttribute("saldo",utente.getSaldo());
            model.addAttribute("corsi", listaCorsi);

            return "homepage";
        }
        else return "redirect:/login";
    }*/

    @GetMapping
    public String showHomepage(Model model) {

            final String id = getSecurityContext().getToken().getPreferredUsername();
            if(!(user_keyService.exists(id))){
                User_KeyDto user_keyDto = new User_KeyDto(id,0);
                user_keyService.save(user_keyDto);
            }

            ConsumptionProbe consumptionProbe = rateLimiterService.bucket_exist(id);

            if(consumptionProbe.getRemainingTokens() >=0) {
                System.out.println("CI SONO ANCORA "+consumptionProbe.getRemainingTokens()+"TOKEN DISPONIBILI!");

                ArrayList<Corso> listaCorsi = (ArrayList<Corso>) corsoRepository.findAll();

                //Nome utente e saldo mi servono per la navbar, la lista corsi per la visualizzazione dei corsi in homepage
                //Nome utente e saldo mi serviranno in tutte le pagine relative all'utente

                final String nome = getSecurityContext().getToken().getName();
                float saldo = user_keyService.loadUserByKey(id).getSaldo();
                model.addAttribute("nome", nome);
                model.addAttribute("saldo", saldo);
                model.addAttribute("corsi", listaCorsi);

                return "homepage";
            }
            return "redirect:/login?logout";

    }

/*
    @GetMapping("/riepilogo-prenotazioni")
    public String viewprenotazioni(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("UTENTE"))) {

            //Cerco tutte le prenotazioni relative ad un determinato utente tramite l'id dell'utente

            String email = auth.getName();
            Utente utente = utenteRepository.findByEmail(email);
            ArrayList<Prenotazione> prenotazionissime = prenotazioneRepository.findByIdUtente(utente.getId());
            ArrayList<ListaPrenotazioniDto> prenotazioni = new ArrayList<ListaPrenotazioniDto>();

            ListaPrenotazioniDto prenotazione;
            for(int i = 0; i<prenotazionissime.size(); i++){
                prenotazione = new ListaPrenotazioniDto();
                prenotazione.setCorso(prenotazionissime.get(i).getLezione().getCorso().getNome());
                prenotazione.setGiorno(prenotazionissime.get(i).getLezione().getGiorno());
                prenotazione.setOrario(prenotazionissime.get(i).getLezione().getOrario());
                prenotazione.setSala(prenotazionissime.get(i).getLezione().getSala());
                prenotazione.setDurata(prenotazionissime.get(i).getLezione().getDurata());
                prenotazione.setId(prenotazionissime.get(i).getId());
                prenotazioni.add(prenotazione);
            }

            model.addAttribute("prenotazioni", prenotazioni);
            model.addAttribute("nome",utente.getNome());
            model.addAttribute("saldo",utente.getSaldo());
            return "riepilogo-prenotazioni";
        }
        else return "redirect:/login";
    }

 */

    @GetMapping("/riepilogo-prenotazioni")
    public String viewprenotazioni(Model model) {
        //Cerco tutte le prenotazioni relative ad un determinato utente tramite l'id dell'utente
        final String user_id = getSecurityContext().getToken().getPreferredUsername();
        ConsumptionProbe consumptionProbe = rateLimiterService.bucket_exist(user_id);

        if (consumptionProbe.getRemainingTokens() >= 0) {


            final String nome = getSecurityContext().getToken().getName();
            ArrayList<Prenotazione> prenotazionissime = prenotazioneRepository.findByIdUtente(user_id);
            ArrayList<ListaPrenotazioniDto> prenotazioni = new ArrayList<ListaPrenotazioniDto>();

            ListaPrenotazioniDto prenotazione;
            for (int i = 0; i < prenotazionissime.size(); i++) {
                prenotazione = new ListaPrenotazioniDto();
                prenotazione.setCorso(prenotazionissime.get(i).getLezione().getCorso().getNome());
                prenotazione.setGiorno(prenotazionissime.get(i).getLezione().getGiorno());
                prenotazione.setOrario(prenotazionissime.get(i).getLezione().getOrario());
                prenotazione.setSala(prenotazionissime.get(i).getLezione().getSala());
                prenotazione.setDurata(prenotazionissime.get(i).getLezione().getDurata());
                prenotazione.setId(prenotazionissime.get(i).getId());
                prenotazioni.add(prenotazione);
            }

            model.addAttribute("prenotazioni", prenotazioni);
            model.addAttribute("nome", nome);
            float saldo = user_keyService.loadUserByKey(user_id).getSaldo();
            model.addAttribute("saldo", saldo);
            return "riepilogo-prenotazioni";
        }
        return "redirect:/login?logout";
    }

    /*
    @GetMapping("/elimina-prenotazione/{id}")
    public String eliminaprenotazione(@PathVariable (value="id") long id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("UTENTE"))) {

            Prenotazione prenotazione = prenotazioneRepository.getById(id);

            String giorno_lez = prenotazione.getLezione().getGiorno();

            String giorno_now = LocalDate.now().getDayOfWeek().toString();

            String giorno_lezione = giorno_lez.toUpperCase(Locale.ITALIAN);

            switch(giorno_lezione){
                case "LUNEDÌ":
                    giorno_lezione ="MONDAY";
                    break;
                case "MARTEDÌ":
                    giorno_lezione ="TUESDAY";
                    break;
                case "MERCOLEDÌ":
                    giorno_lezione ="WEDNESDAY";
                    break;
                case "GIOVEDÌ":
                    giorno_lezione ="THURSDAY";
                    break;
                case "VENERDÌ":
                    giorno_lezione ="FRIDAY";
                    break;
                case "SABATO":
                    giorno_lezione ="SATURDAY";
                    break;
                case "DOMENICA":
                    giorno_lezione ="SUNDAY";
                    break;
                default:
                    break;
            }
            System.out.println(giorno_lezione);

            if (giorno_now.equals(giorno_lezione)) {

                String time = prenotazione.getLezione().getOrario();
                String time1 = LocalTime.now().toString();

                String[] orario_lez = time.split(":");
                String[] orario_now = time1.split(":");

                int hour1 = Integer.parseInt(orario_lez[0]);
                int hour2 = Integer.parseInt(orario_now[0]);
                int min1 = Integer.parseInt(orario_lez[1]);
                int min2 = Integer.parseInt(orario_now[1]);
                int lezione = hour1 * 60 + min1;
                int adesso = hour2 * 60 + min2;


                System.out.println(lezione);
                System.out.println(adesso);

                if (lezione - adesso > 120) {
                    prenotazioneRepository.delete(prenotazione);
                    return "redirect:/homepage/riepilogo-prenotazioni?success1";
                } else
                    return "redirect:/homepage/riepilogo-prenotazioni?error4";
            }
            prenotazioneRepository.delete(prenotazione);
            return "redirect:/homepage/riepilogo-prenotazioni?success1";
        }
        else
            return "redirect:/login";
    }

     */

    @GetMapping("/elimina-prenotazione/{id}")
    public String eliminaprenotazione(@PathVariable (value="id") long id) {

        Prenotazione prenotazione = prenotazioneRepository.getById(id);

        User_Key user_key = user_keyService.loadUserByKey(getSecurityContext().getToken().getPreferredUsername());

        String id_user = getSecurityContext().getToken().getPreferredUsername();

        ConsumptionProbe consumptionProbe = rateLimiterService.bucket_exist(id_user);

        if (consumptionProbe.getRemainingTokens() >=0) {

            final float saldo = user_key.getSaldo();

            Lezione lezion = prenotazione.getLezione();

            float costo = lezion.getCosto();

            int posti = lezion.getNum_posti_disponibili();

            String giorno_lez = prenotazione.getLezione().getGiorno();

            String giorno_now = LocalDate.now().getDayOfWeek().toString();

            String giorno_lezione = giorno_lez.toUpperCase(Locale.ITALIAN);

            switch (giorno_lezione) {
                case "LUNEDÌ":
                    giorno_lezione = "MONDAY";
                    break;
                case "MARTEDÌ":
                    giorno_lezione = "TUESDAY";
                    break;
                case "MERCOLEDÌ":
                    giorno_lezione = "WEDNESDAY";
                    break;
                case "GIOVEDÌ":
                    giorno_lezione = "THURSDAY";
                    break;
                case "VENERDÌ":
                    giorno_lezione = "FRIDAY";
                    break;
                case "SABATO":
                    giorno_lezione = "SATURDAY";
                    break;
                case "DOMENICA":
                    giorno_lezione = "SUNDAY";
                    break;
                default:
                    break;
            }
            System.out.println(giorno_lezione);

            if (giorno_now.equals(giorno_lezione)) {

                String time = prenotazione.getLezione().getOrario();
                String time1 = LocalTime.now().toString();

                String[] orario_lez = time.split(":");
                String[] orario_now = time1.split(":");

                int hour1 = Integer.parseInt(orario_lez[0]);
                int hour2 = Integer.parseInt(orario_now[0]);
                int min1 = Integer.parseInt(orario_lez[1]);
                int min2 = Integer.parseInt(orario_now[1]);
                int lezione = hour1 * 60 + min1;
                int adesso = hour2 * 60 + min2;


                System.out.println(lezione);
                System.out.println(adesso);

                if (lezione - adesso > 120) {
                    prenotazioneRepository.delete(prenotazione);
                    lezion.setNum_posti_disponibili(posti + 1);
                    user_key.setSaldo(saldo + costo);
                    user_keyRepository.save(user_key);
                    lezioneRepository.save(lezion);
                    return "redirect:/homepage/riepilogo-prenotazioni?success1";
                } else
                    return "redirect:/homepage/riepilogo-prenotazioni?error4";
            }
            prenotazioneRepository.delete(prenotazione);
            lezion.setNum_posti_disponibili(posti + 1);
            user_key.setSaldo(saldo + costo);
            user_keyRepository.save(user_key);
            lezioneRepository.save(lezion);
            return "redirect:/homepage/riepilogo-prenotazioni?success1";
        }
        return "redirect:/login?logout";
    }

    /*
    @GetMapping("/effettua-prenotazione/{id}/{idcorso}")
    public String prenotati(@PathVariable (value = "id") long id,@PathVariable (value ="idcorso") long idcorso){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("UTENTE"))) {

            String email = auth.getName();
            Utente utente = utenteRepository.findByEmail(email);
            long iduser = utente.getId();
            long idlez = id;
            Lezione lezione = lezioneRepository.findBy_Id(id);

            if (!prenotazioneService.prenotazione_exist(iduser, idlez)) {

                if (lezione.getNum_posti_disponibili() > 0) {

                    if (utente.getSaldo() > lezione.getCosto()) {

                        utente.setSaldo(utente.getSaldo() - lezione.getCosto());
                        lezione.setNum_posti_disponibili(lezione.getNum_posti_disponibili() - 1);
                        Prenotazione prenotazione = new Prenotazione(Calendar.getInstance().getTime().toString(), "valido");
                        utenteRepository.save(utente);
                        lezioneRepository.save(lezione);
                        System.out.println("Sono entrato nella zona critica");
                        prenotazione.setLezione(lezione);
                        prenotazione.setUtente(utente);
                        prenotazioneRepository.save(prenotazione);


                        return "redirect:/homepage/visualizza-lezioni-user/" + idcorso + "?success0";
                    } else
                        return "redirect:/homepage/visualizza-lezioni-user/" + idcorso + "?error3";
                } else
                    return "redirect:/homepage/visualizza-lezioni-user/" + idcorso + "?error2";

            } else return "redirect:/homepage/visualizza-lezioni-user/"+idcorso+"?error1";
        }
        else
            return "redirect:/login";
    }
    */

    @GetMapping("/effettua-prenotazione/{id}/{idcorso}")
    public String prenotati(@PathVariable (value = "id") long id,@PathVariable (value ="idcorso") long idcorso) {
        final String id_utente = getSecurityContext().getToken().getPreferredUsername();

        ConsumptionProbe consumptionProbe = rateLimiterService.bucket_exist(id_utente);
        if (consumptionProbe.getRemainingTokens() >= 0) {
            float saldo = user_keyService.loadUserByKey(id_utente).getSaldo();
            User_Key user_key = user_keyService.loadUserByKey(id_utente);

            long idlez = id;
            Lezione lezione = lezioneRepository.findBy_Id(id);
            //System.out.println("lezione eccomi");
            if (!prenotazioneService.prenotazione_exist(id_utente, idlez)) {

                if (lezione.getNum_posti_disponibili() > 0) {

                    if (saldo > lezione.getCosto()) {

                        user_key.setSaldo(saldo - lezione.getCosto()); //Calcolare saldo nuovo
                        lezione.setNum_posti_disponibili(lezione.getNum_posti_disponibili() - 1);
                        Prenotazione prenotazione = new Prenotazione(Calendar.getInstance().getTime().toString(), "valido", id_utente);
                        user_keyRepository.save(user_key); //Aggiornare saldo
                        lezioneRepository.save(lezione);
                        System.out.println("Sono entrato nella zona critica");
                        prenotazione.setLezione(lezione);
                        prenotazioneRepository.save(prenotazione);


                        return "redirect:/homepage/visualizza-lezioni-user/" + idcorso + "?success0";
                    } else
                        return "redirect:/homepage/visualizza-lezioni-user/" + idcorso + "?error3";
                } else
                    return "redirect:/homepage/visualizza-lezioni-user/" + idcorso + "?error2";

            } else return "redirect:/homepage/visualizza-lezioni-user/" + idcorso + "?error1";
        }
        return "redirect:/login?logout";
    }



    @GetMapping("/visualizza-lezioni-user/{id}")
    public String showlessons(@PathVariable (value="id") long id,Model model) {
        Corso corso = corsoRepository.findbyId(id);
        ArrayList<Lezione> lezioni = lezioneRepository.findByCorso(corso.getId());
        final String nome = getSecurityContext().getToken().getName();
        final String id_user = getSecurityContext().getToken().getPreferredUsername();
        ConsumptionProbe consumptionProbe = rateLimiterService.bucket_exist(id_user);
        if (consumptionProbe.getRemainingTokens() >= 0) {


            final float saldo = user_keyService.loadUserByKey(getSecurityContext().getToken().getPreferredUsername()).getSaldo();
            model.addAttribute("nome", nome);
            model.addAttribute("saldo", saldo);
            model.addAttribute("corso", corso);
            model.addAttribute("lezioni", lezioni);
            model.addAttribute("id", id);

            return "visualizza-lezioni-user";
        }
        return "redirect:/login?logout";
    }

    /*
    @GetMapping("/visualizza-abbonamenti")
    public String viewabbonamenti(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("UTENTE"))) {

            String email = auth.getName();
            Utente utente = utenteRepository.findByEmail(email);
            model.addAttribute("nome",utente.getNome());
            model.addAttribute("saldo",utente.getSaldo());

            //DA IMPLEMENTARE
            return "visualizza-abbonamenti";
        }
        else return "redirect:/login";
    }

     */

    @GetMapping("/visualizza-abbonamenti")
    public String viewabbonamenti(Model model){

            final String nome = getSecurityContext().getToken().getName();
            final float saldo = user_keyService.loadUserByKey(getSecurityContext().getToken().getPreferredUsername()).getSaldo();
            model.addAttribute("nome",nome);
            model.addAttribute("saldo",saldo);

            //DA IMPLEMENTARE
            return "visualizza-abbonamenti";
    }


    @ModelAttribute("ricaric")
    public SaldoDto saldoDto(){return new SaldoDto();}


    /*
        @GetMapping("/carica-saldo")
    public String carica_saldo(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("UTENTE"))) {

            String email = auth.getName();
            Utente utente = utenteRepository.findByEmail(email);
            model.addAttribute("nome",utente.getNome());
            model.addAttribute("saldo",utente.getSaldo());
            return "carica-saldo";
        }
        else return "redirect:/login";
    }
     */

    @GetMapping("/carica-saldo")
    public String carica_saldo(Model model){
            final String nome = getSecurityContext().getToken().getName();
            final float saldo = user_keyService.loadUserByKey(getSecurityContext().getToken().getPreferredUsername()).getSaldo();
            model.addAttribute("nome",nome);
            model.addAttribute("saldo",saldo);
            return "carica-saldo";
    }


    /*
        @PostMapping("/carica-saldo")
    public String soldi(Model model,@ModelAttribute("ricaric") SaldoDto saldoDto){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("UTENTE"))) {

            // Prendo l'ammontare della ricarica inserito e lo aggiungo al saldo dell'utente
            String email = auth.getName();
            Utente utente = utenteRepository.findByEmail(email);
            model.addAttribute("nome",utente.getNome());
            model.addAttribute("saldo",utente.getSaldo());

            float temp = utente.getSaldo();
            if(saldoDto.getRicarica() <=0){
                return "redirect:/homepage/carica-saldo?error1";
            }
            utente.setSaldo(temp+saldoDto.getRicarica());
            utenteRepository.save(utente);

            return "redirect:/homepage/carica-saldo?success0";
        }
        else
            return "redirect:/login";
    }
     */

    @PostMapping("/carica-saldo")
    public String soldi(Model model,@ModelAttribute("ricaric") SaldoDto saldoDto){
            // Prendo l'ammontare della ricarica inserito e lo aggiungo al saldo dell'utente
            final String nome = getSecurityContext().getToken().getName();
            User_Key user_key = user_keyService.loadUserByKey(getSecurityContext().getToken().getPreferredUsername());
            final float saldo = user_keyService.loadUserByKey(getSecurityContext().getToken().getPreferredUsername()).getSaldo();
            model.addAttribute("nome",nome);
            model.addAttribute("saldo",saldo);


            if(saldoDto.getRicarica() <=0){
                return "redirect:/homepage/carica-saldo?error1";
            }
            //
            user_key.setSaldo(saldo+saldoDto.getRicarica());
            user_keyRepository.save(user_key);

            return "redirect:/homepage/carica-saldo?success0";
        }


    private KeycloakSecurityContext getSecurityContext() {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
    }
}

