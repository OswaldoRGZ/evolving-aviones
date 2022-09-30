package vista;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;

import controlador.Mundo;

import modelo.Cementerio;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JCheckBox;

public class Menu extends JFrame
{

    /**
     * 
     */
    private static final long serialVersionUID = 3092820725324053647L;
    private JPanel contentPane;
    private DefaultListModel lista;
    private Pantalla simulacion;
    private Cementerio cementerio;
    private Mundo juego;
    private JTextField textAlto;
    private JTextField textAncho;
    private JTextField textSeres;
    private JTextField textEspera;
    private JTextField textPromClon;
    private JTextField textMaxSeres;

    /**
     * Launch the application.
     */
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    Menu frame = new Menu();
                    frame.setVisible(true);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Menu()
    {
        setTitle("Evolucion Naves");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 443);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblConfiguracion = new JLabel("Configuracion");
        lblConfiguracion.setFont(new Font("Arial", Font.PLAIN, 12));
        lblConfiguracion.setBounds(10, 11, 146, 14);
        contentPane.add(lblConfiguracion);

        final JButton btnVerSimulacion = new JButton("Ver/Ocultar");
        btnVerSimulacion.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                verSimulacion();
            }
        });
        btnVerSimulacion.setBounds(168, 374, 123, 23);
        btnVerSimulacion.setEnabled(false);
        contentPane.add(btnVerSimulacion);

        final JButton btnAplicarParametros = new JButton("Aplicar parametros");
        btnAplicarParametros.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                aplicarParametros();
            }
        });
        btnAplicarParametros.setEnabled(false);
        btnAplicarParametros.setBounds(10, 374, 151, 23);
        contentPane.add(btnAplicarParametros);
        
        final JButton btnCargar = new JButton("Cargar RNAs");
        btnCargar.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                cargarRedes();
            }
        });
        btnCargar.setEnabled(false);
        btnCargar.setBounds(301, 340, 123, 23);
        contentPane.add(btnCargar);

        final JButton btnSimularRapido = new JButton("Simular");
        btnSimularRapido.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                iniciarSimulacion();
                btnSimularRapido.setEnabled(false);
                btnVerSimulacion.setEnabled(true);
                btnAplicarParametros.setEnabled(true);
                btnCargar.setEnabled(true);
                aplicarParametros();
            }
        });
        btnSimularRapido.setBounds(301, 374, 123, 23);
        contentPane.add(btnSimularRapido);

        JLabel lblInformacion = new JLabel("Informacion");
        lblInformacion.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInformacion.setBounds(10, 152, 180, 14);
        contentPane.add(lblInformacion);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 170, 414, 156);
        contentPane.add(scrollPane);

        JList listHistoria = new JList();
        scrollPane.setViewportView(listHistoria);
        lista = new DefaultListModel();
        listHistoria.setModel(lista);

        
        
        JLabel lblAlto = new JLabel("Alto");
        lblAlto.setFont(new Font("Arial", Font.PLAIN, 12));
        lblAlto.setBounds(10, 43, 74, 14);
        contentPane.add(lblAlto);
        
        JLabel lblAncho = new JLabel("Ancho");
        lblAncho.setFont(new Font("Arial", Font.PLAIN, 12));
        lblAncho.setBounds(10, 68, 74, 14);
        contentPane.add(lblAncho);
        
        textAlto = new JTextField();
        textAlto.setText("5000");
        textAlto.setBounds(88, 38, 86, 20);
        contentPane.add(textAlto);
        textAlto.setColumns(10);
        
        textAncho = new JTextField();
        textAncho.setText("5000");
        textAncho.setBounds(88, 66, 86, 20);
        contentPane.add(textAncho);
        textAncho.setColumns(10);
        
        textSeres = new JTextField();
        textSeres.setText("40");
        textSeres.setBounds(88, 121, 86, 20);
        contentPane.add(textSeres);
        textSeres.setColumns(10);
        
        JLabel lblMinSeres = new JLabel("Min Seres");
        lblMinSeres.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMinSeres.setBounds(10, 123, 74, 14);
        contentPane.add(lblMinSeres);
        
        textEspera = new JTextField();
        textEspera.setColumns(10);
        textEspera.setText("5");
        textEspera.setBounds(338, 66, 86, 20);
        contentPane.add(textEspera);
        
        JLabel lblEspera = new JLabel("Espera");
        lblEspera.setFont(new Font("Arial", Font.PLAIN, 12));
        lblEspera.setBounds(226, 69, 102, 14);
        contentPane.add(lblEspera);
        
        JLabel lblPromedioParaClonar = new JLabel("Promedio para clonar");
        lblPromedioParaClonar.setFont(new Font("Arial", Font.PLAIN, 12));
        lblPromedioParaClonar.setBounds(199, 41, 129, 14);
        contentPane.add(lblPromedioParaClonar);
        
        textPromClon = new JTextField();
        textPromClon.setText("5");
        textPromClon.setBounds(338, 37, 86, 20);
        contentPane.add(textPromClon);
        textPromClon.setColumns(10);
        
        JCheckBox chckbxAutoRepoblar = new JCheckBox("Auto Repoblar");
        chckbxAutoRepoblar.setBounds(10, 93, 129, 23);
        contentPane.add(chckbxAutoRepoblar);
        
        textMaxSeres = new JTextField();
        textMaxSeres.setText("50");
        textMaxSeres.setColumns(10);
        textMaxSeres.setBounds(338, 121, 86, 20);
        contentPane.add(textMaxSeres);
        
        JLabel lblMaxSeresinfinito = new JLabel("Max Seres(0:infinito)");
        lblMaxSeresinfinito.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMaxSeresinfinito.setBounds(211, 123, 123, 14);
        contentPane.add(lblMaxSeresinfinito);

        lista.addElement("Inicio");

        simulacion = null;
        cementerio = new Cementerio();
    }

    private void iniciarSimulacion()
    {
        if (simulacion != null)
            return;
        this.simulacion = new Pantalla();
        this.juego = simulacion.getJuego();
        iniciarRegistrador();
        simulacion.setCementerio(cementerio);
    }

    private void verSimulacion()
    {
        if (simulacion == null)
            return;
        if (simulacion.isMostrando())
        {
            this.simulacion.ocultar();
            //simulacion.setVisible(false);
        } else
        {
            this.simulacion.mostrar();
            //simulacion.setVisible(true);
        }
    }

    private void iniciarRegistrador()
    {
        cementerio.setRegistro(lista);
    }

    private void aplicarParametros()
    {
        if(this.simulacion != null)
            simulacion.setEspera( Integer.valueOf(textEspera.getText()) );
        if (cementerio != null)
            cementerio.setValores(0,0);
        if(juego!=null)
        {
            juego.setDimension(Integer.valueOf(textAlto.getText()), Integer.valueOf(textAncho.getText()), Integer.valueOf(textSeres.getText()), Integer.valueOf(textMaxSeres.getText()) );
            juego.setPromedioClon( Integer.valueOf(textPromClon.getText()) );
        }
    }
    
    private void cargarRedes()
    {
        if (cementerio != null & juego != null)
        {
            juego.crearSeres( cementerio.cargarRedes() );
        }
    }
}
