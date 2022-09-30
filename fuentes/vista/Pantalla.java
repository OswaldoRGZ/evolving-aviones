package vista;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;

import modelo.Cementerio;

import controlador.Mundo;
import controlador.RNA;
import entrada.EntradaUsuario;

public class Pantalla extends Canvas implements Runnable
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 7498342999599172958L;
    public static final int ANCHO = Toolkit.getDefaultToolkit().getScreenSize().width;
    public static final int ALTO = Toolkit.getDefaultToolkit().getScreenSize().height - 40;

    // Toolkit.getDefaultToolkit().getScreenSize()
    private Mundo juego;
    private Camara camara;
    private Thread hilo;
    private EntradaUsuario entrada;
    private boolean ejecutar, mostrar;
    private BufferStrategy strategy;
    private JFrame frame;
    private int espera;

    /**
     * @param args
     */
    Pantalla()
    {
        ejecutar = false;
        mostrar = false;
        camara = new Camara(ANCHO, ALTO);
        juego = new Mundo(camara);
        entrada = new EntradaUsuario();
        frame = null;
        addKeyListener(entrada);
        addFocusListener(entrada);
        addMouseListener(entrada);
        addMouseMotionListener(entrada);
        mostrar();
        iniciar();
    }

    /*private void agregarContrincantes()
    {
        RNA cerebro = null;
        this.juego.agregarSer(cerebro);
    }*/

    public void setCementerio(Cementerio c)
    {
        if (juego != null)
            juego.setCementerio(c);
    }

    private void iniciar()
    {
        if (ejecutar)
            return;
        createBufferStrategy(2);
        strategy = getBufferStrategy();
        hilo = new Thread(this);
        hilo.start();
        ejecutar = true;
    }

    public void parar()
    {
        if (!ejecutar)
            return;
        ejecutar = false;
        try
        {
            hilo.join();
        } catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void run()
    {
        // TODO Auto-generated method stub
        System.out.println("hilo corriendo");
        // boolean mostrarfps = false;
        // long tiempo2, tiempo = 0;
        while (ejecutar)
        {
            /*
             * mostrarfps = System.currentTimeMillis() % 1000 == 0; if (mostrarfps) tiempo =
             * System.currentTimeMillis();
             */
            actualizar();
            if (mostrar)
            {
                dibujar();
                esperar();
            }
            // esperar();
            /*
             * if (mostrarfps) { tiempo2 = System.currentTimeMillis(); if (tiempo2 - tiempo != 0)
             * fps = (1000 / (tiempo2 - tiempo)); }
             */
        }
        System.out.println("hilo fin");
    }

    private void actualizar()
    {
        juego.actualizar();
        //Controles
        if (entrada.tecla[KeyEvent.VK_W])
            camara.mover(0, -10);
        if (entrada.tecla[KeyEvent.VK_A])
            camara.mover(-10, 0);
        if (entrada.tecla[KeyEvent.VK_D])
            camara.mover(+10, 0);
        if (entrada.tecla[KeyEvent.VK_S])
            camara.mover(0, +10);
        if (entrada.tecla[KeyEvent.VK_Q])
        {
            camara.seguir(juego.anteriorElegido());
        }
        if (entrada.tecla[KeyEvent.VK_E])
        {
            camara.seguir(juego.siguienteElegido());
        }
        if (entrada.tecla[KeyEvent.VK_ESCAPE])
        {
            camara.seguir(null);
        }

        camara.actualizar();
    }

    private void dibujar()
    {
        // TODO Auto-generated method stub
        Graphics2D g = null;

        try
        {
            g = (Graphics2D) strategy.getDrawGraphics();
            //g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
            g.translate(-camara.getPosx(), -camara.getPosy());
            juego.dibujar(g);
            
            //g.drawString("FPS      : " + fps, camara.getPosx() + 200, camara.getPosy() + 10);
            
        } finally
        {
            if (g != null)
                g.dispose();
        }
        strategy.show();
    }

    private void esperar()
    {
        if (espera <=0 )
            return;
        try
        {
            Thread.sleep(espera);
        } catch (InterruptedException e)
        {
            // ODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // @Override
    // public void paint(Graphics g) {
    // g.drawRect( 0, 0, ANCHO, ALTO);
    //
    // }

    public void moverCampara(int x, int y)
    {
        camara.mover(x, y);
    }

    /*
     * INICIADOR DEL PROGRAMA
     */
    public void mostrar()
    {
        // TODO Auto-generated method stub
        mostrar = true;
        if (frame != null)
        {
            // frame.setVisible(true);
            return;
        }
        Pantalla mip = this;
        frame = new JFrame();
        frame.add(mip);
        frame.pack();
        // frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Evolucion");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(ANCHO, ALTO);
        frame.setVisible(true);
        frame.setLocation(0, 0);
        createBufferStrategy(2);
        strategy = getBufferStrategy();
    }

    public void ocultar()
    {
        // TODO Auto-generated method stub
        mostrar = false;
        if (frame == null)
            return;
        // frame.setVisible(false);
    }

    public Mundo getJuego()
    {
        return this.juego;
    }

    public boolean isMostrando()
    {
        return mostrar;
    }
    
    public void setEspera(int espera)
    {
        this.espera = espera;
    }
}
