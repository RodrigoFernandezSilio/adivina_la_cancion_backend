
package adivina_la_cancion.prototipo.adivina_la_cancion.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Cancion;
import adivina_la_cancion.prototipo.adivina_la_cancion.domain.EstadoPartida;
import adivina_la_cancion.prototipo.adivina_la_cancion.domain.ListaReproduccion;
import adivina_la_cancion.prototipo.adivina_la_cancion.domain.ListaRespuesta;
import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Partida;
import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Ronda;
import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Usuario;
import adivina_la_cancion.prototipo.adivina_la_cancion.dto.ListaRespuestaDTO;
import adivina_la_cancion.prototipo.adivina_la_cancion.dto.PartidaDTO;
import adivina_la_cancion.prototipo.adivina_la_cancion.dto.RondaDTO;
import adivina_la_cancion.prototipo.adivina_la_cancion.dto.UsuariosDTO;
import adivina_la_cancion.prototipo.adivina_la_cancion.repositories.PartidaRepository;
import adivina_la_cancion.prototipo.adivina_la_cancion.repositories.UsuarioRepository;

@Service
public class PartidaService implements ApplicationListener<RespuestaEvent> {

    @Autowired
    protected MusicaService musicaService;

    @Autowired
    protected PartidaRepository partidaRepo;

    @Autowired
    protected PartidaServiceAux partidaServiceAux;

    @Autowired
    protected UsuarioRepository ur;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    private ConcurrentHashMap<Long, Integer> indRondaActualPorPartida = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Long, ScheduledFuture<?>> hashMapFuture1 = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> hashMapFuture2 = new ConcurrentHashMap<>();
    private ScheduledFuture<?> future;

    // private int TIEMPO_ENTRE_RONDAS = 12; // Intervalo de tiempo entre rondas en segundos
    // private int TIEMPO_PARA_RESPONDER = 10; // Tiempo dado a los jugadores para responder en segundos
    private Duration TIEMPO_ENTRE_RONDAS_DURATION = Duration.ofSeconds(Partida.TIEMPO_ENTRE_RONDAS);

    /* Tiempo de espera para garantizar que todos los usuarios que se hayan unido a la partida
     * hayan establecido la conexión WebSocket antes de iniciar la partida */
    private int TIEMPO_ESPERA_CONEXION_WEBSOCKET = 2;

    @Autowired
    private PartidaHandler partidaHandler;

    /*
     * No lo uso, solo sirve como ejemplo de uso de taskScheduler
     * // TODO: Solo puede iniciar partida el anfitrion
     * public RespuestaService<Partida> iniciarPartidaPorAnfitrion(long partidaID) {
     * Optional<Partida> partidaOptional = partidaRepo.findById(partidaID);
     * 
     * if (partidaOptional.isPresent()) {
     * partidaActual = partidaOptional.get();
     * future = taskScheduler.scheduleAtFixedRate(() -> crearRonda(1234),
     * INTERVALO);
     * 
     * return new RespuestaService<>(true, "Partida iniciada", partidaActual);
     * } else {
     * return new RespuestaService<>(false, "Partida no encontrada", null);
     * }
     * }
     * 
     * @Transactional
     * private void crearRonda(int idRonda) {
     * if (partidaActual != null) {
     * if (rondasCreadas < 5) {
     * System.out.println("Ejecutando método crearRonda, ronda número " +
     * rondasCreadas);
     * System.out.println(partidaActual.getRondas().size());
     * new Ronda();
     * partidaRepo.save(partidaActual);
     * rondasCreadas++;
     * } else {
     * future.cancel(false);
     * System.out.println("Ejecución completada de método crearRonda " +
     * rondasCreadas + " veces, terminando tarea.");
     * }
     * } else {
     * future.cancel(false);
     * System.out.println("Ejecución completada de método crearRonda " +
     * rondasCreadas + " veces, terminando tarea.");
     * }
     * }
     */

    /**
     * Obtiene todas las partidas disponibles.
     * Una partida se considera disponible si su estado es NO_INICIADA y no es privada.
     *
     * @return ResponseEntity con la lista de partidas disponibles y estado HTTP 200 (OK).
     */
    public ResponseEntity<List<Partida>> obtenerPartidasDisponibles() {
        List<Partida> partidasDisponibles = partidaRepo.findByEstadoAndPrivada(EstadoPartida.NO_INICIADA, false);
        return new ResponseEntity<>(partidasDisponibles, HttpStatus.OK);
    }

    public List<Partida> obtenerPartidas() {
        List<Partida> partidas = partidaRepo.findAll();
        return partidas;
    }

    public Partida obtenerPartida(Long partidaID) {
        return partidaRepo.findById(partidaID).orElse(null);
    }

    public Partida obtenerPartidaConUsuarios(Long id) {
        return partidaRepo.findByIdWithUsuarios(id); // Usando un query con JOIN FETCH
    }

    public ResponseEntity<Partida> crearPartida(PartidaDTO partidaDTO) {
        //Optional<Playlist> playlistOptional = Optional.empty();
        ResponseEntity<ListaReproduccion> listaReproduccionResponseEntity = musicaService
                .obtenerPlaylistValida(partidaDTO.getPlaylistID());
        Optional<Usuario> usuarioOptional = ur.findById(partidaDTO.getUsuarioID());

        if (listaReproduccionResponseEntity.getStatusCode() == HttpStatus.OK && usuarioOptional.isPresent()) {
            ListaReproduccion listaReproduccion = listaReproduccionResponseEntity.getBody();
            Usuario usuario = usuarioOptional.get();

            Partida partida = new Partida(listaReproduccion, partidaDTO.getNumRondas(), partidaDTO.getNumMaxUsuariosPartida(), partidaDTO.isVotoModificable(),
                partidaDTO.isPrivada(), partidaDTO.getCodigoAcceso());

            if (partida.anhadirUsuario(usuario, partidaDTO.getCodigoAcceso())) {
                partidaRepo.save(partida);

                // Tras añadir al usuario, comprobar si la partida se ha llenado
                if (partida.getUsuarios().size() == partida.getNumMaxUsuariosPartida()) {
                    // Si la partida se ha llenado, se inicia
                    iniciarPartidaAsync(partida);
                }

                return new ResponseEntity<>(partida, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Partida> anhadirUsuario(Long partidaID, Long usuarioID, String codigoAcceso) {
        Optional<Partida> partidaOptional = partidaRepo.findById(partidaID);
        Optional<Usuario> usuarioOptional = ur.findById(usuarioID);

        if (partidaOptional.isPresent() && usuarioOptional.isPresent()) {
            Partida partida = partidaOptional.get();
            Usuario usuario = usuarioOptional.get();

            if (partida.anhadirUsuario(usuario, codigoAcceso)) {
                // Enviar a través del websocket los usuarios de la partida
                UsuariosDTO usuariosDTO = new UsuariosDTO(partida.getUsuarios());
                try {
                    partidaHandler.enviarUsuarios(partidaID, usuariosDTO);
                    System.out.println("Enviar lista de usuarios");
                } catch (JsonProcessingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // Tras añadir al usuario, comprobar si la partida se ha llenado
                if (partida.getUsuarios().size() == partida.getNumMaxUsuariosPartida()) {
                    // Si la partida se ha llenado, se inicia
                    iniciarPartidaAsync(partida);
                }
                return new ResponseEntity<>(partida, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    protected void iniciarPartida(Partida partida) {
        ResponseEntity<ListaReproduccion> listaReproduccionResponseEntity = musicaService
            .obtenerPlaylistValida(partida.getListaReproduccionId());

        if (listaReproduccionResponseEntity.getStatusCode() == HttpStatus.OK) {
            partida.setListaReproduccion(listaReproduccionResponseEntity.getBody());

            partida.iniciarPartida();
            
            indRondaActualPorPartida.put(partida.getId(), 0);
            
            Instant instanteDeInicio = Instant.now().plusSeconds(TIEMPO_ESPERA_CONEXION_WEBSOCKET);
            future = taskScheduler.scheduleAtFixedRate(() -> enviarSiguienteCancion(partida), instanteDeInicio,
            TIEMPO_ENTRE_RONDAS_DURATION);
            hashMapFuture1.put(partida.getId(), future);
            
            instanteDeInicio = Instant.now().plusSeconds(TIEMPO_ESPERA_CONEXION_WEBSOCKET + Partida.TIEMPO_PARA_RESPONDER);
            future = taskScheduler.scheduleAtFixedRate(() -> analizarRespuestasYemitirResultados(partida.getId()),
            instanteDeInicio, TIEMPO_ENTRE_RONDAS_DURATION);
            hashMapFuture2.put(partida.getId(), future);
        }
    }

    @Async
    protected void iniciarPartidaAsync(Partida partida) {
        iniciarPartida(partida);
    }

    public ResponseEntity<Void> iniciarPartidaPorAnfitrion(Long partidaID, Long usuarioID) {
        Optional<Partida> partidaOptional = partidaRepo.findById(partidaID);
        Optional<Usuario> usuarioOptional = ur.findById(usuarioID);

        if (partidaOptional.isPresent() && usuarioOptional.isPresent()) {
            Partida partida = partidaOptional.get();
            Usuario usuario = usuarioOptional.get();

            // Comprobar que el usuario que inicia la partida es el anfitrion
            if (partida.getUsuarios().get(0) == usuario) {
                // Si es el anfitrion se inicia la partida
                iniciarPartida(partida);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public void enviarSiguienteCancion(Partida partida) {
        Integer indRondaActual = indRondaActualPorPartida.get(partida.getId());

        if (indRondaActual < partida.getNumRondas()) {

            Ronda rondaActual = partida.getRondas().get(indRondaActual);

            // Establecer el intervalo de tiempo de la ronda
            Instant inicioRonda = Instant.now();
            rondaActual.establecerIntervaloTiempo(inicioRonda, Partida.TIEMPO_PARA_RESPONDER);

            partidaRepo.save(partida);

            RondaDTO rondaActualDTO = new RondaDTO(rondaActual);

            try {
                partidaHandler.enviarRonda(partida.getId(), rondaActualDTO);
            } catch (JsonProcessingException e) {
                // TODO TERMINAR PARTIDA
                e.printStackTrace();
            }

            System.out.println("enviarSiguienteCancion de la ronda " + indRondaActual);
        } else {
            System.out.println("Fin de enviarSiguienteCancion");
            future = hashMapFuture1.get(partida.getId());
            future.cancel(false);
        }
    }

    public void analizarRespuestasYemitirResultados(long partidaID) {
        Partida partida = partidaRepo.findById(partidaID).get();
        Integer indRondaActual = indRondaActualPorPartida.get(partida.getId());
        
        if (indRondaActual < partida.getNumRondas()) {

            Cancion cancionCorrecta = partida.getRondas().get(indRondaActual).getCancionCorrecta();

            ListaRespuesta listaRespuesta = partidaServiceAux.analizarRespuestasYemitirResultados(partidaID, indRondaActual);
            ListaRespuestaDTO listaRespuestaDTO = new ListaRespuestaDTO(listaRespuesta);

            try {
                partidaHandler.enviarCancionCorrecta(partida.getId(), cancionCorrecta);
                partidaHandler.enviarListaRespuesta(partida.getId(), listaRespuestaDTO);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            System.out.println("analizarRespuestasYemitirResultados de la ronda " + indRondaActual);
            indRondaActualPorPartida.put(partida.getId(), indRondaActual+1);
        } else {
            System.out.println("Fin de analizarRespuestasYemitirResultados");
            future = hashMapFuture2.get(partida.getId());
            future.cancel(false);

            partidaHandler.cerrarSesionesWebSocket(partidaID); // Cerrar las conexiones WebSocket asociadas a la partida
        }
    }

    @Transactional
    private void anhadirRespuesta(Long partidaID, Long usuarioID, Cancion cancion, Instant instanteRespuesta) {
        Optional<Partida> partidaOptional = partidaRepo.findById(partidaID);
        Optional<Usuario> usuarioOptional = ur.findById(usuarioID);

        if (partidaOptional.isPresent() && usuarioOptional.isPresent()) {
            Partida partida = partidaOptional.get();
            Integer indRondaActual = indRondaActualPorPartida.get(partida.getId());
            Usuario usuario = usuarioOptional.get();

            partida.anhadirRespuesta(usuario, indRondaActual, cancion, instanteRespuesta);

            partidaRepo.save(partida);
        }
    }

    @Transactional
    @Override
    public void onApplicationEvent(RespuestaEvent event) {
        Long partidaID = event.getPartidaID();
        Long usuarioID = event.getUsuarioID();
        Cancion cancion = event.getCancionSeleccionada();
        Instant instanteRespuesta = event.getInstanteRespuesta();

        anhadirRespuesta(partidaID, usuarioID, cancion, instanteRespuesta);
    }
}
