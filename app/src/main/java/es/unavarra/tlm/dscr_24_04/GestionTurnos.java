package es.unavarra.tlm.dscr_24_04;

public class GestionTurnos {
    private int turno = 1; // Comienza con el jugador 1

    // Cambia el turno entre jugador 1 y jugador 2
    public void cambiarTurno() {
        turno = (turno == 1) ? 2 : 1; // Alterna entre 1 y 2
    }

    // Devuelve el jugador cuyo turno es actualmente
    public int obtenerTurno() {
        return turno;
    }
}
