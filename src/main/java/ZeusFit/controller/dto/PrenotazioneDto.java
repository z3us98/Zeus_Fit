package ZeusFit.controller.dto;

import java.sql.Time;

public class PrenotazioneDto {

    private String ora;

    private String stato;

    private Long lezione;

    private String utente;

    public PrenotazioneDto() {
    }

    public PrenotazioneDto(String ora, String stato, Long lezione, String utente) {
        this.ora = ora;
        this.stato = stato;
        this.lezione = lezione;
        this.utente = utente;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public Long getLezione() {
        return lezione;
    }

    public void setLezione(Long lezione) {
        this.lezione = lezione;
    }

    public String getUtente() {
        return utente;
    }

    public void setUtente(String utente) {
        this.utente = utente;
    }
}
