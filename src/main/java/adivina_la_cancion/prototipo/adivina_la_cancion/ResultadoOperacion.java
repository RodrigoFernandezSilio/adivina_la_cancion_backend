package adivina_la_cancion.prototipo.adivina_la_cancion;

public class ResultadoOperacion<T> {
    private boolean exito; // Indica si la operación fue exitosa
    private String mensaje; // Mensaje informativo
    private T datos; // Datos adicionales

    
    public ResultadoOperacion() {
    }

    public ResultadoOperacion(boolean exito, String mensaje, T datos) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.datos = datos;
    }

    public boolean getExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public T getDatos() {
        return datos;
    }

    public void setDatos(T datos) {
        this.datos = datos;
    }
}