package adivina_la_cancion.prototipo.adivina_la_cancion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Embeddable
public class Cancion {

    @NonNull
    private String id;

    @NonNull
    private String nombre;

    @NonNull
    @Column(length = 1024)
    private String audioURL;
}
