package adivina_la_cancion.prototipo.adivina_la_cancion.service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Cancion;
import adivina_la_cancion.prototipo.adivina_la_cancion.dto.ListaRespuestaDTO;
import adivina_la_cancion.prototipo.adivina_la_cancion.dto.RondaDTO;
import adivina_la_cancion.prototipo.adivina_la_cancion.dto.UsuariosDTO;

@Service
public class PartidaHandler extends TextWebSocketHandler {

    /**
     * CopyOnWriteArrayList:
     * Tipo de lista diseñada para ser eficiente en entornos donde hay concurrencia,
     * pero no en constante modificación
     */
    // private final CopyOnWriteArrayList<WebSocketSession> sessions = new
    // CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<Long, CopyOnWriteArrayList<WebSocketSession>> sesiones = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        ConcurrentHashMap<String, Object> attributes = (ConcurrentHashMap<String, Object>) session.getAttributes();

        // Obtener la lista de sesiones asociadas con el partidaID
        Long partidaID = (Long) attributes.get("partidaID");
        CopyOnWriteArrayList<WebSocketSession> sesionesPartida = sesiones.get(partidaID);

        // Comprobar si no existe una lista de sesiones para el partidaID
        if (sesionesPartida == null) {
            // Crear la lista de sesiones para el partidaID
            sesionesPartida = new CopyOnWriteArrayList<>();
            sesiones.put(partidaID, sesionesPartida);
        }

        // Agregar la sesion actual a la lista correspondiente
        sesionesPartida.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status)
            throws Exception {

        ConcurrentHashMap<String, Object> attributes = (ConcurrentHashMap<String, Object>) session.getAttributes();

        // Obtener la lista de sesiones asociadas con el partidaID
        Long partidaID = (Long) attributes.get("partidaID");

        // Eliminar la sesion de la lista de sesiones de esa partida
        CopyOnWriteArrayList<WebSocketSession> sesionesPartida = sesiones.get(partidaID);
        if (sesionesPartida != null) {
            sesionesPartida.remove(session);
            if (sesionesPartida.isEmpty()) {
                sesiones.remove(partidaID); // Eliminar la entrada del mapa si no quedan mas sesiones
            }
        }
    }

    /**
     * Cuando desde el cliente (navegador) llegue un mensaje aquí,
     * se va a enviar a todas las sesiones que se encuentren activas (sessiones que
     * estén en la lista)
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        // Usar TypeReference para deserializar con type-safety
        List<Object> data = objectMapper.readValue(message.getPayload(), new TypeReference<List<Object>>() {
        });

        if (data.size() == 2) {
            String nombreClase = data.get(0).toString();
            Object payload = data.get(1);

            if (nombreClase.equals("Cancion")) {
                Cancion cancion = objectMapper.convertValue(payload, Cancion.class);

                ConcurrentHashMap<String, Object> attributes = (ConcurrentHashMap<String, Object>) session.getAttributes();
                
                Long partidaID = (Long) attributes.get("partidaID");
                Long usuarioID = (Long) attributes.get("usuarioID");

                Instant instanteRespuesta = Instant.now();

                RespuestaEvent respuestaEvent = new RespuestaEvent(this, partidaID, usuarioID, cancion, instanteRespuesta);
                applicationEventPublisher.publishEvent(respuestaEvent);
                
            } else {
                System.out.println("El mensaje no tiene el formato esperado");
            }
        } else {
            System.out.println("El mensaje no tiene el formato esperado");
        }

        /*
         * ConcurrentHashMap<String, Object> attributes = (ConcurrentHashMap<String,
         * Object>) session.getAttributes();
         * 
         * // Obtener la lista de sesiones asociadas con el partidaID
         * Long partidaID = (Long) attributes.get("partidaID");
         * 
         * List<WebSocketSession> sesionesPartida = sesiones.get(partidaID);
         * 
         * // Enviar el mensaje a todas las sesiones conectadas a esta partida
         * if (sesionesPartida != null) {
         * for (WebSocketSession partidaSession : sesionesPartida) {
         * partidaSession.sendMessage(message);
         * }
         * }
         */

    }

    public void enviarUsuarios(long partidaID, UsuariosDTO usuariosDTO) throws JsonProcessingException {
        List<WebSocketSession> sesionesPartida = sesiones.get(partidaID);
        if (sesionesPartida != null) {
            enviarObjeto(sesionesPartida, usuariosDTO);
        }
    }

    public void enviarRonda(long partidaID, RondaDTO rondaDTO) throws JsonProcessingException {
        List<WebSocketSession> sesionesPartida = sesiones.get(partidaID);
        if (sesionesPartida != null) {
            enviarObjeto(sesionesPartida, rondaDTO);
        }
    }

    public void enviarCancionCorrecta(long partidaID, Cancion cancion) throws JsonProcessingException {
        List<WebSocketSession> sesionesPartida = sesiones.get(partidaID);
        if (sesionesPartida != null) {
            enviarObjeto(sesionesPartida, cancion);
        }
    }

    public void enviarListaRespuesta(long partidaID, ListaRespuestaDTO listaRespuestaDTO) throws JsonProcessingException {
        List<WebSocketSession> sesionesPartida = sesiones.get(partidaID);
        if (sesionesPartida != null) {
            enviarObjeto(sesionesPartida, listaRespuestaDTO);
        }
    }

    // Método genérico para serializar el objeto y enviarlo a todas las sesiones
    private <T> void enviarObjeto(List<WebSocketSession> sesionesPartida, T objeto) throws JsonProcessingException {

        // Crear la lista que contiene el nombre de la clase y el objeto
        String nombreClase = objeto.getClass().getName();
        List<Object> mensaje = List.of(nombreClase, objeto);

        ObjectMapper objectMapper = new ObjectMapper(); // Crear un ObjectMapper para serializar el objeto

        String json = objectMapper.writeValueAsString(mensaje); // Serializa el objeto a JSON

        TextMessage textMessage = new TextMessage(json); // Envuelve el JSON en un TextMessage para poder enviarlo

        // Enviar el mensaje a todas las sesiones
        enviarTextMessage(sesionesPartida, textMessage);
    }

    // Método para enviar el TextMessage a todas las sesiones
    private void enviarTextMessage(List<WebSocketSession> sesionesPartida, TextMessage textMessage) {
        for (WebSocketSession partidaSession : sesionesPartida) {
            if (partidaSession.isOpen()) { // Si la sesión está abierta
                try {
                    partidaSession.sendMessage(textMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else { // Si la sesión está cerrada
                // TODO
            }
        }
    }

    /**
     * Cierra todas las sesiones WebSocket abiertas asociadas a una partida específica.
     * 
     * @param partidaID el ID de la partida cuyas sesiones WebSocket deben ser cerradas
     */
    public void cerrarSesionesWebSocket(long partidaID) {
        CopyOnWriteArrayList<WebSocketSession> sesionesPartida = sesiones.get(partidaID);

        if (sesionesPartida != null) {
            for (WebSocketSession partidaSession : sesionesPartida) {
                if (partidaSession.isOpen()) { // Si la sesión está abierta
                    try {
                        partidaSession.close(CloseStatus.NORMAL); // Cerrar la sesión con estado NORMAL
                    } catch (IOException e) {
                        e.printStackTrace();
                    } 
                }
            }
        }
        sesiones.remove(partidaID); // Eliminar la entrada del mapa porque ya estarán cerradas todas las sesiones
    }
}
