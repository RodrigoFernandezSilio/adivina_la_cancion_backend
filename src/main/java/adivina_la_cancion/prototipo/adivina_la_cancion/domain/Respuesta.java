package adivina_la_cancion.prototipo.adivina_la_cancion.domain;

import java.time.Instant;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
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
public class Respuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NonNull
    @Embedded
        @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "cancion_id")),
        @AttributeOverride(name = "nombre", column = @Column(name = "cancion_nombre")),
        @AttributeOverride(name = "audioURL", column = @Column(name = "cancion_audioURL", length = 1024))
    })
    private Cancion cancionSeleccionada;

    @NonNull
    private Instant instanteRespuesta;

    @NonNull
    private Integer puntuacion;

    public Respuesta(@NonNull Integer puntuacion) {
        this.puntuacion = puntuacion;
    }   

    
}
