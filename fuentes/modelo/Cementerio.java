package modelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

import controlador.RNA;
import controlador.Ser;

public class Cementerio extends Thread
{
    private final int PERIODO_TRABAJO = 10000 ;// cada 10 segundos verifica si puede hacer recolecta de cuerpos
    private int PROMEDIO_MIN = 100, GENERACION_MIN = 10;
    private final String RUTA = "rnas/";

    private ArrayList<Ser> principal, secundario;
    private int promedio_actual, conteo, generacion, mejor, total;
    private boolean principal_ocupado, trbajar, fin_generacion;
    private Thread proceso;
    private DefaultListModel registro;
    private ArrayList<String> ficheros;

    public Cementerio()
    {
        // TODO Auto-generated constructor stub
        this.principal = new ArrayList<Ser>();
        this.secundario = new ArrayList<Ser>();
        this.promedio_actual = 0;
        this.principal_ocupado = false;
        this.proceso = new Thread(this);
        this.trbajar = true;
        this.generacion = 0;
        this.ficheros = new ArrayList<String>();
        this.fin_generacion = false;
        proceso.start();
    }

    public void listarCerebros()
    {
        if (registro == null)
            return;
        String ruta = RUTA;
        File dir = new File(ruta);
        String[] fichs = dir.list();
        if (fichs == null)
        {
            registro.addElement("No existen ficheros");
        } else
        {
            for (int i = 0; i < fichs.length; i++)
            {
                if( fichs[i].contains(".rna") )
                    ficheros.add(RUTA + fichs[i]);
                else
                    registro.addElement("No es fichero :"+fichs[i]);
            }
        }
    }

    public void addMuerto(Ser nuevo)
    {
        if (principal_ocupado)
        {
            secundario.add(nuevo);
        } else
            principal.add(nuevo);
    }
    public void finGeneracion()
    {
        fin_generacion = true;
    }
    @Override
    public void run()
    {
        // TODO Auto-generated method stub
        while (trbajar)
        {
            while(!fin_generacion)
                espera();
            conteo = 0;
            if (principal_ocupado)
            {
                principal_ocupado = false;
                trabajar(secundario);
                secundario = new ArrayList<Ser>();
            } else
            {
                principal_ocupado = true;
                trabajar(principal);
                principal = new ArrayList<Ser>();
            }
            fin_generacion = false;
        }
    }

    private void trabajar(ArrayList<Ser> lista)
    {
        if (lista.size() == 0)
            return;
        mejor = 0;
        total = lista.size();
        for (Ser ser : lista)
        {
            promedio_actual += ser.getTotalPuntos();
            if (ser.getTotalPuntos() > mejor)
            {
                mejor = ser.getTotalPuntos();
            }
        }
        promedio_actual = promedio_actual / lista.size();
        for (Ser ser : lista)
        {
            if (ser.getTotalPuntos() > promedio_actual)
            {
                guardarCopia(ser);
            }
        }
        informar();
        generacion++;
    }

    private void espera()
    {
        try
        {
            Thread.sleep(PERIODO_TRABAJO);
        } catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void guardarCopia(Ser copia)
    {
        if (generacion < GENERACION_MIN | copia.getTotalPuntos() < PROMEDIO_MIN)
            return;
        RNA guardar = copia.getCerebro();
        String fichero = "Gen" + generacion + "Pt" + copia.getTotalPuntos() + "Id" + conteo
                + ".rna";
        File folder = new File(RUTA);
        if (!folder.exists())
        {
            folder.mkdirs();    
        }
        else if (folder.isFile())
        {
            System.out.println("El sistema era un archivo");
            registro.addElement("El sistema era un archivo");
            folder.mkdirs();
        }
        if (!folder.isDirectory())
        {
            System.out.println("No es un directorio?");
            registro.addElement("No es un directorio?");
            return;
        }

        ObjectOutputStream oos;
        try
        {
            oos = new ObjectOutputStream(new FileOutputStream(RUTA + fichero));
            oos.writeObject(guardar);
            oos.close();
            conteo++;
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setRegistro(DefaultListModel lista)
    {
        this.registro = lista;
    }

    private void informar()
    {
        if (registro == null)
            return;
        String texto = "Generacion :" + generacion;
        texto += ", Total :" + total;
        texto += ", Promedio :" + promedio_actual;
        texto += ", Mejor :" + mejor;
        texto += ", Archivos :" + conteo;
        registro.add(0, texto);
    }

    public void setValores(int promedio, int generacion)
    {
        if (promedio > 0)
            PROMEDIO_MIN = promedio;
        if (generacion > 0)
            GENERACION_MIN = generacion;
    }

    public ArrayList<RNA> cargarRedes()
    {
        // TODO Auto-generated method stub
        listarCerebros();
        if (ficheros.size() == 0)
            return null;
        ArrayList<RNA> cerebros = new ArrayList<RNA>();
        for (String fich : ficheros)
        {
            ObjectInputStream ois;
            try
            {
                ois = new ObjectInputStream(new FileInputStream(fich));
                Object aux = ois.readObject();
                cerebros.add((RNA) aux);
                ois.close();
                File archivo = new File(fich);
                archivo.delete();
            } catch (FileNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        registro.addElement("Seres creados a partir de ficheros leidos");
        ficheros.clear();
        return cerebros;
    }
}
