package adivina_la_cancion.prototipo.adivina_la_cancion.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class Ronda {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NonNull
    // @ManyToMany(cascade = CascadeType.ALL)
    @ElementCollection
    private List<Cancion> canciones;

    @NonNull
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "cancion_id")),
        @AttributeOverride(name = "nombre", column = @Column(name = "cancion_nombre")),
        @AttributeOverride(name = "audioURL", column = @Column(name = "cancion_audioURL", length = 1024))
    })
    private Cancion cancionCorrecta;

    // @NonNull
    // @OneToMany(cascade = CascadeType.ALL)
    // private List<Respuesta> respuestas;

    @NonNull
    private Instant inicioRonda;

    @NonNull
    private Instant finRonda;

    public Ronda(@NonNull List<Cancion> canciones, @NonNull Cancion cancionCorrecta) {
        this.canciones = canciones;
        this.cancionCorrecta = cancionCorrecta;
    }

    /**
     * Establece el intervalo de tiempo de la ronda, definiendo su inicio y su fin.
     *
     * @param inicioRonda         El instante en que comienza la ronda.
     * @param tiempoParaResponder El tiempo en segundos que los jugadores tienen para responder.
     */
    public void establecerIntervaloTiempo(Instant inicioRonda, Integer tiempoParaResponder) {
        this.inicioRonda = inicioRonda;
        this.finRonda = inicioRonda.plus(tiempoParaResponder, ChronoUnit.SECONDS);
    }
}
