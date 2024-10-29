package es.unavarra.tlm.dscr_24_04;

public class BoardCell {
    private int row;
    private int col;
    private CellState state;

    public BoardCell(int row, int column) {
        this.row = row;
        this.col = col;
        this.state = CellState.EMPTY;  // Estado inicial
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    // Enumeración para los estados posibles
    public enum CellState {
        EMPTY, SHIP, WATER, HIT, SUNK
    }
}

