package es.unavarra.tlm.dscr_24_04;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    private String type; // Tipo del barco (e.g., "carrier", "battleship", etc.)
    private List<Position> positions; // Lista de posiciones ocupadas por el barco

    // Constructor para inicializar el tipo de barco
    public Ship(String type) {
        this.type = type;
        this.positions = new ArrayList<>();
    }

    // Método para obtener el tipo de barco
    public String getType() {
        return type;
    }

    // Método para obtener las posiciones del barco
    public List<Position> getPositions() {
        return positions;
    }

    // Añadir una posición al barco
    public void addPosition(int row, int col) {
        this.positions.add(new Position(row, col));
    }

    // Método para validar el barco (lo implementaremos en un paso posterior)
    public boolean isValid() {
        if (positions.size() < 2) return true; // Solo una celda, no necesita validación de alineación

        boolean sameRow = true;
        boolean sameCol = true;
        int row = positions.get(0).getRow();
        int col = positions.get(0).getCol();

        for (Position pos : positions) {
            if (pos.getRow() != row) sameRow = false;
            if (pos.getCol() != col) sameCol = false;
        }

        // Las posiciones deben estar en la misma fila o columna
        if (!sameRow && !sameCol) return false;

        // Verificar que las posiciones sean consecutivas
        final boolean isSameRow = sameRow; // Copia de `sameRow` para usar en la lambda
        positions.sort((p1, p2) -> isSameRow ? Integer.compare(p1.getCol(), p2.getCol()) : Integer.compare(p1.getRow(), p2.getRow()));

        for (int i = 1; i < positions.size(); i++) {
            int diff = sameRow ? positions.get(i).getCol() - positions.get(i - 1).getCol() : positions.get(i).getRow() - positions.get(i - 1).getRow();
            if (diff != 1) return false;
        }

        return true;
    }


    // Clase interna Position para representar cada celda ocupada
    public static class Position {
        private int row;
        private int col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }
}

