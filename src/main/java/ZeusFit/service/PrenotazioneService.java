package ZeusFit.service;

import ZeusFit.controller.dto.PrenotazioneDto;
import ZeusFit.model.Prenotazione;

import java.util.ArrayList;

public interface PrenotazioneService {

    Prenotazione save(PrenotazioneDto prenotazioneDto,String id_utente);
    boolean prenotazione_exist(String id_utente,long lezione);
    ArrayList<Prenotazione> loadPrenotazionebyid(String id);

}
