package adivina_la_cancion.prototipo.adivina_la_cancion.domain;

import com.fasterxml.jackson.annotation.JsonView;

import adivina_la_cancion.prototipo.adivina_la_cancion.service.api.Views;
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
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NonNull
    @JsonView({Views.PartidaPreview.class})
    private String nombre;
}