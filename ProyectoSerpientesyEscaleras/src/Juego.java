/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author JimmyNaranjo
 */

public class Juego extends Canvas {

    private int tamanoTablero;
    private int tamanoCasilla;
    private int[] posicionesJugadores;
    private Color[] coloresJugadores = {Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW};
    private int jugadorActual = 0;
    private Map<Integer, Integer> serpientes;
    private Map<Integer, Integer> escaleras;

    public Juego(int tamanoTablero, int numSerpientes, int numEscaleras, int numJugadores) {
        this.tamanoTablero = tamanoTablero;
        this.tamanoCasilla = 600 / tamanoTablero; 
        this.posicionesJugadores = new int[numJugadores];
        Arrays.fill(this.posicionesJugadores, 1); 
        setSize(tamanoTablero * tamanoCasilla, tamanoTablero * tamanoCasilla);
        generarSerpientesYEscaleras(numSerpientes, numEscaleras);
    }

    private void generarSerpientesYEscaleras(int numSerpientes, int numEscaleras) {
        serpientes = new HashMap<>();
        escaleras = new HashMap<>();
        Random rand = new Random();
        
        while (serpientes.size() < numSerpientes) {
            int inicio = rand.nextInt(tamanoTablero * tamanoTablero - 1) + 1;
            int fin = rand.nextInt(inicio - 1) + 1;
            if (!serpientes.containsKey(inicio) && !escaleras.containsKey(inicio) && !escaleras.containsKey(fin)) {
                serpientes.put(inicio, fin);
            }
        }

        while (escaleras.size() < numEscaleras) {
            int inicio = rand.nextInt(tamanoTablero * tamanoTablero - 1) + 1;
            int fin = rand.nextInt(tamanoTablero * tamanoTablero - inicio) + inicio + 1;
            if (!escaleras.containsKey(inicio) && !serpientes.containsKey(inicio) && !serpientes.containsKey(fin)) {
                escaleras.put(inicio, fin);
            }
        }
    }

    public void paint(Graphics g) {
        dibujarTablero(g);
        dibujarSerpientesYEscaleras(g);
        dibujarJugadores(g);
    }

    private void dibujarTablero(Graphics g) {
        boolean blanco = true;
        for (int y = 0; y < tamanoTablero; y++) {
            for (int x = 0; x < tamanoTablero; x++) {
                g.setColor(blanco ? Color.WHITE : Color.LIGHT_GRAY);
                g.fillRect(x * tamanoCasilla, y * tamanoCasilla, tamanoCasilla, tamanoCasilla);
                g.setColor(Color.BLACK);
                g.drawRect(x * tamanoCasilla, y * tamanoCasilla, tamanoCasilla, tamanoCasilla);

                int numeroCasilla = y * tamanoTablero + (y % 2 == 0 ? x : (tamanoTablero - 1 - x)) + 1;
                numeroCasilla = tamanoTablero * tamanoTablero - numeroCasilla + 1;
                
                g.drawString(String.valueOf(numeroCasilla), x * tamanoCasilla + 5, y * tamanoCasilla + 15);
                blanco = !blanco;
            }
            blanco = !blanco;
        }
    }

    private void dibujarSerpientesYEscaleras(Graphics g) {
        g.setColor(Color.RED);
        for (Map.Entry<Integer, Integer> entry : serpientes.entrySet()) {
            dibujarLineaEntreCasillas(g, entry.getKey(), entry.getValue());
        }

        g.setColor(Color.GREEN);
        for (Map.Entry<Integer, Integer> entry : escaleras.entrySet()) {
            dibujarLineaEntreCasillas(g, entry.getKey(), entry.getValue());
        }
    }

    private void dibujarLineaEntreCasillas(Graphics g, int inicio, int fin) {
        int inicioX = (inicio - 1) % tamanoTablero;
        int inicioY = (inicio - 1) / tamanoTablero;
        if (inicioY % 2 == 1) {
            inicioX = tamanoTablero - 1 - inicioX;
        }
        int inicioCasillaX = inicioX * tamanoCasilla + tamanoCasilla / 2;
        int inicioCasillaY = (tamanoTablero - 1 - inicioY) * tamanoCasilla + tamanoCasilla / 2;

        int finX = (fin - 1) % tamanoTablero;
        int finY = (fin - 1) / tamanoTablero;
        if (finY % 2 == 1) {
            finX = tamanoTablero - 1 - finX;
        }
        int finCasillaX = finX * tamanoCasilla + tamanoCasilla / 2;
        int finCasillaY = (tamanoTablero - 1 - finY) * tamanoCasilla + tamanoCasilla / 2;

        g.drawLine(inicioCasillaX, inicioCasillaY, finCasillaX, finCasillaY);
    }

    private void dibujarJugadores(Graphics g) {
        for (int i = 0; i < posicionesJugadores.length; i++) {
            int fila = (posicionesJugadores[i] - 1) / tamanoTablero;
            int columna = (posicionesJugadores[i] - 1) % tamanoTablero;
            if (fila % 2 == 1) {
                columna = tamanoTablero - 1 - columna;
            }
            int x = columna * tamanoCasilla + tamanoCasilla / 4;
            int y = (tamanoTablero - 1 - fila) * tamanoCasilla + tamanoCasilla / 4;
            g.setColor(coloresJugadores[i]);
            g.fillOval(x, y, tamanoCasilla / 2, tamanoCasilla / 2);
        }
    }

    public void moverJugador(int pasos) {
        posicionesJugadores[jugadorActual] += pasos;
        if (posicionesJugadores[jugadorActual] > tamanoTablero * tamanoTablero) {
            posicionesJugadores[jugadorActual] = tamanoTablero * tamanoTablero;
        } else if (serpientes.containsKey(posicionesJugadores[jugadorActual])) {
            posicionesJugadores[jugadorActual] = serpientes.get(posicionesJugadores[jugadorActual]);
        } else if (escaleras.containsKey(posicionesJugadores[jugadorActual])) {
            posicionesJugadores[jugadorActual] = escaleras.get(posicionesJugadores[jugadorActual]);
        }

        if (posicionesJugadores[jugadorActual] == tamanoTablero * tamanoTablero) {
            JOptionPane.showMessageDialog(this, "El Jugador " + (jugadorActual + 1) + " ha ganado la partida.");
            reiniciarPartida();
            return;
        }
        
        repaint();
        jugadorActual = (jugadorActual + 1) % posicionesJugadores.length;
    }

    public void reiniciarPartida() {
        SwingUtilities.getWindowAncestor(this).dispose();
        main(null);
    }

    public static void main(String[] args) {
        String[] opcionesTablero = {"10x10", "13x13", "15x15"};
        int eleccionTablero = JOptionPane.showOptionDialog(
            null, 
            "Seleccione el tamaño del tablero:", 
            "Tamaño del Tablero", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            opcionesTablero, 
            opcionesTablero[0]
        );

        int tamanoTablero = 10; 
        if (eleccionTablero == 0) {
            tamanoTablero = 10;
        } else if (eleccionTablero == 1) {
            tamanoTablero = 13;
        } else if (eleccionTablero == 2) {
            tamanoTablero = 15;
        }

        String entradaSerpientes = JOptionPane.showInputDialog("Ingrese la cantidad de serpientes (4-8)(Si ingresa una cantidad no valida de serpientes se seleccionara la cantidad minima):");
        int numSerpientes = 4; 
        try {
            numSerpientes = Integer.parseInt(entradaSerpientes);
            if (numSerpientes < 4 || numSerpientes > 8) {
                numSerpientes = 4;
            }
        } catch (NumberFormatException e) {
            
        }

        String entradaEscaleras = JOptionPane.showInputDialog("Ingrese la cantidad de escaleras (4-8)(Si ingresa una cantidad no valida de escaleras se seleccionara la cantidad minima):");
        int numEscaleras = 4; 
        try {
            numEscaleras = Integer.parseInt(entradaEscaleras);
            if (numEscaleras < 4 || numEscaleras > 8) {
                numEscaleras = 4;
                            }
        } catch (NumberFormatException e) {
           
        }

        String[] opcionesJugadores = {"2", "3", "4"};
        int eleccionJugadores = JOptionPane.showOptionDialog(
            null, 
            "Seleccione la cantidad de jugadores:", 
            "Cantidad de Jugadores", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            opcionesJugadores, 
            opcionesJugadores[0]
        );

        int numJugadores = 2;
        if (eleccionJugadores == 0) {
            numJugadores = 2;
        } else if (eleccionJugadores == 1) {
            numJugadores = 3;
        } else if (eleccionJugadores == 2) {
            numJugadores = 4;
        }

        JFrame ventana = new JFrame("Serpientes y Escaleras");
        Juego juego = new Juego(tamanoTablero, numSerpientes, numEscaleras, numJugadores);

        JButton botonTirarDado = new JButton("Tirar Dado");
        botonTirarDado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int resultadoDado = tirarDado();
                JOptionPane.showMessageDialog(null, "El dado ha salido " + resultadoDado + ". Se moverá el Jugador " + (juego.jugadorActual + 1) + ".");
                juego.moverJugador(resultadoDado);
            }
        });

        JButton botonReinicio = new JButton("Reiniciar Partida");
        botonReinicio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                juego.reiniciarPartida();
            }
        });

        JPanel panelBotones = new JPanel();
        panelBotones.add(botonTirarDado);
        panelBotones.add(botonReinicio);

        ventana.add(juego, BorderLayout.CENTER);
        ventana.add(panelBotones, BorderLayout.SOUTH);
        ventana.setSize(tamanoTablero * juego.tamanoCasilla, tamanoTablero * juego.tamanoCasilla + 50); 
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setVisible(true);

        juego.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    int resultadoDado = tirarDado();
                    JOptionPane.showMessageDialog(null, "El dado ha salido " + resultadoDado + ". Se movera el Jugador " + (juego.jugadorActual + 1) + ".");
                    juego.moverJugador(resultadoDado);
                }
            }
        });
        juego.setFocusable(true);
        juego.requestFocusInWindow();
    }

    public static int tirarDado() {
        Random rand = new Random();
        return rand.nextInt(6) + 1; 
    }
}

           


