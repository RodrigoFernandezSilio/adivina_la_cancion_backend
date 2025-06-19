package adivina_la_cancion.prototipo.adivina_la_cancion.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonView;

import adivina_la_cancion.prototipo.adivina_la_cancion.service.api.Views;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@Entity
public class Partida {

    public static final Integer NUM_MAX_USUARIOS = 10;

    public static final Integer NUM_MAX_RONDAS = 20;

    public static final Integer TIEMPO_ENTRE_RONDAS = 12; // Intervalo de tiempo entre rondas en segundos

    public static final Integer TIEMPO_PARA_RESPONDER = 10; // Tiempo dado a los jugadores para responder en segundos

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView({ Views.PartidaPreview.class })
    private long id;

    @Enumerated(EnumType.STRING) // https://www.baeldung.com/jpa-persisting-enums-in-jpa#string
    private EstadoPartida estado = EstadoPartida.NO_INICIADA;

    // @ManyToOne(cascade = CascadeType.ALL)
    @Transient
    private ListaReproduccion listaReproduccion;

    private String listaReproduccionId;

    @JsonView({ Views.PartidaPreview.class })
    private String listaReproduccionNombre;

    @JsonView({ Views.PartidaPreview.class })
    private Integer numRondas;

    @JsonView({ Views.PartidaPreview.class })
    private Integer numMaxUsuariosPartida;

    private Boolean privada;

    private String codigoAcceso;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Ronda> rondas;

    @ManyToMany
    @JsonView({ Views.PartidaPreview.class })
    private List<Usuario> usuarios;

    @OneToMany(cascade = CascadeType.ALL)
    private Map<Usuario, ListaRespuesta> respuestasPorUsuario;


    public Partida(@NonNull ListaReproduccion listaReproduccion, @NonNull Integer numRondas,
            @NonNull Integer numMaxUsuariosPartida, @NonNull Boolean privada, String codigoAcceso) {
        this.listaReproduccion = listaReproduccion;
        this.numRondas = numRondas;
        this.numMaxUsuariosPartida = numMaxUsuariosPartida;
        this.privada = privada;
        this.codigoAcceso = codigoAcceso;
        this.rondas = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.respuestasPorUsuario = new HashMap<>();

        validarConfiguracionPartida();
        updateListaReproduccion();
    }

    private void validarConfiguracionPartida() {
        if (numMaxUsuariosPartida <= 0 || numMaxUsuariosPartida > NUM_MAX_USUARIOS) {
            throw new IllegalArgumentException("Error: El número máximo de usuarios debe ser > 0 y <= "
                    + NUM_MAX_USUARIOS + ". Valor ingresado: " + numMaxUsuariosPartida);
        }
        if (numRondas > NUM_MAX_RONDAS || numRondas < 0) {
            throw new IllegalArgumentException("Error: El número de rondas debe ser > 0 y <= "
                    + NUM_MAX_RONDAS + ". Valor ingresado: " + numRondas);
        }
    }

    private void updateListaReproduccion() {
        listaReproduccionId = listaReproduccion.getId();
        listaReproduccionNombre = listaReproduccion.getNombre();
    }

    /**
     * Añade un usuario a la partida.
     * 
     * Este método añade el usuario a la partida si se cumplen las siguientes condiciones:
     * - La partida no ha comenzado.
     * - No se ha alcanzado el número máximo de usuarios.
     * - El usuario no se ha unido previamente.
     * - La partida es pública, o si es privada, se proporciona un código de acceso válido.
     * 
     * @param usuario El usuario que se añade a la partida
     * @param codigoAcceso El código de acceso proporcionado, requerido solo si la partida es privada
     * @return true si se añade el usuario, false sino
     */
    public boolean anhadirUsuario(Usuario usuario, String codigoAcceso) {
        // Comprobar que no esté iniciada, que no esté llena y que el usuario no esté ya
        if (estado == EstadoPartida.NO_INICIADA && usuarios.size() < numMaxUsuariosPartida
                && !usuarios.contains(usuario)) { 
            // Comprobar que sea publica o que sea privada y el código de acceso sea correcto
            if (!privada || this.codigoAcceso.equals(codigoAcceso)) {
                usuarios.add(usuario);
                respuestasPorUsuario.put(usuario, new ListaRespuesta(new ArrayList<>()));
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Genera las rondas de la partida y actualiza el estado de la partida a "INICIADA".
     */
    public void iniciarPartida() {
        estado = EstadoPartida.INICIADA;
        generarRondas();
    }

    /**
     * Genera las rondas de la partida.
     * 
     * Cada ronda se compone de un conjunto de 4 canciones seleccionadas aleatoriamente
     * y una canción correcta elegida al azar entre esas 4.
     */
    private void generarRondas() {
        Random random = new Random();

        // Obtener todas las canciones de la playlist
        List<Cancion> todasLasCanciones = new ArrayList<Cancion>(listaReproduccion.getCanciones());

        for (int numRonda = 0; numRonda < numRondas; numRonda++) {
            // Barajar las canciones para obtener 4 al azar
            Collections.shuffle(todasLasCanciones);
            List<Cancion> cancionesSeleccionadas = new ArrayList<Cancion>(todasLasCanciones.subList(0, 4));

            // Elegir una canción correcta al azar de las 4 seleccionadas
            int indiceCancionCorrecta = random.nextInt(4); // Genera un número entre 0 (incluido) y 4 (excluido)
            Cancion cancionCorrecta = cancionesSeleccionadas.get(indiceCancionCorrecta);

            // Crear la nueva ronda
            Ronda ronda = new Ronda(cancionesSeleccionadas, cancionCorrecta);

            // Añadir la ronda a la lista de rondas
            rondas.add(ronda);
        }
    }

    public void anhadirRespuesta(Usuario usuario, Integer indRonda, Cancion cancionSeleccionada, Instant instanteRespuesta) {
        Ronda ronda = rondas.get(indRonda);

        if (esRespuestaPosible(usuario, ronda, cancionSeleccionada, instanteRespuesta)) {
            int puntuacion = calcularPuntuacion(ronda, cancionSeleccionada, instanteRespuesta);

            Respuesta respuesta = new Respuesta(cancionSeleccionada, instanteRespuesta, puntuacion);

            List<Respuesta> respuestas = respuestasPorUsuario.get(usuario).getRespuestas();
            
            // Si no hay una respuesta para esta ronda
            if (indRonda == respuestas.size()) {
                respuestas.add(respuesta); // Se añade la nueva respuesta
            // Si ya hay una respuesta para esta ronda
            } else if (indRonda == respuestas.size() - 1) {
                respuestas.set(indRonda, respuesta); // Se reemplaza la antigua por la nueva
            }
        }
    }

    /**
     * Verifica si una respuesta es posible.
     * 
     * Se comprueba si el usuario está en la partida
     * si la canción seleccionada pertenece a las de la ronda
     * y si el instante de respuesta está dentro del intervalo de tiempo de la ronda.
     * 
     * @param usuario             Usuario que intenta responder
     * @param ronda               La ronda en la que se está intentando responder
     * @param cancionSeleccionada La canción seleccionada por el usuario
     * @param instanteRespuesta   El instante en que el usuario respondió
     * @return true si la respuesta es posible, false si no.
     */
    private boolean esRespuestaPosible(Usuario usuario, Ronda ronda, Cancion cancionSeleccionada, Instant instanteRespuesta) {
        boolean esPosible = true;

        // Comprobar si el usuario está en la partida
        if (!respuestasPorUsuario.containsKey(usuario)) {
            esPosible = false;
        }

        // Comprobar si la canción no está en la lista de canciones de la ronda
        if (!ronda.getCanciones().contains(cancionSeleccionada)) {
            esPosible = false;
        }

        // Comprobar si el instante de la respuesta está dentro del intervalo de tiempo
        // de la ronda
        if (instanteRespuesta.isBefore(ronda.getInicioRonda()) || instanteRespuesta.isAfter(ronda.getFinRonda())) {
            esPosible = false;
        }

        return esPosible;
    }

    /**
     * Calcula la puntuación obtenida en una respuesta según la respuesta del jugador.
     * 
     * La puntuación se compone de 50 puntos por responder correctamente,
     * más un bonus adicional basado en la rapidez de respuesta.
     * El bonus se calcula proporcionalmente al tiempo sobrante,
     * que es la diferencia de tiempo entre el instante de la respuesta y el instante de fin de la ronda.
     * 
     * @param ronda               La ronda para la cual se calcula la puntuación
     * @param cancionSeleccionada La canción seleccionada por el usuario
     * @param instanteRespuesta   El instante en que el usuario respondió
     * @return La puntuación obtenida
     */
    private int calcularPuntuacion(Ronda ronda, Cancion cancionSeleccionada, Instant instanteRespuesta) {
        int puntuacion = 0;

        if (ronda.getCancionCorrecta().equals(cancionSeleccionada)) { // La canción seleccionada es la correcta
            puntuacion = 50; // 50 puntos por responder correctamente

            long tiempoSobrante = Duration.between(instanteRespuesta, ronda.getFinRonda()).toMillis();

            int tiempoParaResponderMs = TIEMPO_PARA_RESPONDER * 1000; // Convertir de segundos a milisegundos

            // Calcular el bonus basado en la rapidez de respuesta
            int bonusPorRapidez = (int) Math.round(50 * ((double) tiempoSobrante / tiempoParaResponderMs));

            puntuacion += bonusPorRapidez;
        }

        return puntuacion;
    }

    /**
     * 
     * Completa las respuestas de la ronda anhadiendo una respuesta vacía para
     * usuarios que no han respondido.
     * 
     * @param indiceRondaActual Índice de la ronda.
     */
    public void completarRespuestasRonda(int indRonda) {
        for (Usuario usuario : usuarios) {
            List<Respuesta> respuestas = respuestasPorUsuario.get(usuario).getRespuestas();
            // Si el número de respuestas coincide con el índice de la ronda,  el usuario aún no ha respondido
            if (respuestas != null && respuestas.size() == indRonda) {
                respuestas.add(new Respuesta()); // Se agrega una respuesta vacía
            }
        }
    }

    public ListaRespuesta obtenerRespuestasRonda(int indRonda) {
        List<Respuesta> respuestasRonda = new ArrayList<>();

        for(Usuario usuario : usuarios) {
            List<Respuesta> respuestasUsuario = respuestasPorUsuario.get(usuario).getRespuestas();
            if (respuestasUsuario.size() == indRonda + 1) {
                respuestasRonda.add(respuestasUsuario.get(indRonda));
            }
        }
        ListaRespuesta listaRespuestaRonda = new ListaRespuesta(respuestasRonda);
        return listaRespuestaRonda;
    }
}
