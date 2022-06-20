package ZeusFit.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;
import javax.xml.crypto.Data;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.sql.Time;
import java.util.Objects;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.exception.DataException;

@Entity
@Table(name =  "PRENOTAZIONI", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class Prenotazione {

    @Id
    @Column(name = "id",insertable = false,updatable = false)
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ora",nullable=false)
    private String ora;

    @Column(name = "stato",nullable = false,length = 20)
    private String stato;

    @Column(name= "utente",nullable =false,length = 255)
    private String utente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_lezione", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lezione lezione;

    @Override
    public String toString() {
        return "Prenotazione{" +
                "id=" + id +
                ", ora=" + ora +
                ", stato='" + stato + '\'' +
                ", utente=" + utente +
                ", lezione=" + lezione +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prenotazione that = (Prenotazione) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Prenotazione() {

    }

    public Prenotazione(String ora, String stato,String utente) {
        this.ora = ora;
        this.stato = stato;
        this.utente = utente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUtente() {
        return utente;
    }

    public void setUtente(String utente) {
        this.utente = utente;
    }

    public Lezione getLezione() {
        return lezione;
    }

    public void setLezione(Lezione lezione) {
        this.lezione = lezione;
    }
}
