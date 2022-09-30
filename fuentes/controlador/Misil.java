package controlador;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import modelo.Imagen;

public class Misil extends Objeto
{
    /*
     * Si el misisl tiene objetivo, es misil guiado, si el misil no tiene objetivo, es una bala...
     */
    private final int VELOCIDAD = 4;

    //private final int DANO_BALA = 1;
    private float MAX_GIRO = 0.01f;
    private int DANO_MISIL = 340;
    private int MAX_VIDA = 1000;
    //private final int MAX_VIDA_BALA = 80;

    private Objeto objeto;
    private Imagen sprites;
    //private Ser padre;

    private int vida = 0;
    private boolean bala;

    Misil(int ancho, int alto, float x, float y, float angulo, Imagen img)
    {
        /*
         * Contructor para misiles, objetos moviles
         */
        super(ancho, alto, false);
        // TODO Auto-generated constructor stub
        float vex = (float) Math.cos(angulo);
        float vey = (float) Math.sin(angulo);
        this.setX(x);
        this.setY(y);
        this.setVex(vex);
        this.setVey(vey);
        this.setVel(VELOCIDAD);
        this.bala = true;
        this.sprites = img;
        valoresIniciales();
    }

    private void valoresIniciales()
    {
        this.RADIO = 3;
        this.DIAMETRO = RADIO * 2;
        this.objeto = null;
        //this.padre = null;
        this.vida = MAX_VIDA;
        this.VELOCIDAD_MAX = VELOCIDAD;
    }

    public void setObjetivo(Objeto objetivo)
    {
        bala = false;
        if (objetivo == null)
        {
            vida = 80;//MAX_VIDA * vida / MAX_VIDA;
            this.objeto = null;
            return;
        }
        this.objeto = objetivo;
        objeto.agregaMisilSiguiendo(this);
        //vida = MAX_VIDA;
    }

    /*public void setPadre(Ser creador)
    {
        this.padre = creador;
    }*/

    public Objeto getObjetivo()
    {
        return this.objeto;
    }

    public void actualizar()
    {
        if (this.objeto != null)
        {
            if (!objeto.estaVivo())
                setObjetivo(null);
            else
            {
                float disx = (float) (objeto.getX() - getX());
                float disy = (float) (objeto.getY() - getY());
                float dis = (float) Math.sqrt(disx * disx + disy * disy);
                if (dis > RADIO)
                {
                    float vex = disx / dis;
                    float vey = disy / dis;
                    float mivex = this.getVex();
                    float mivey = this.getVey();

                    if (mivex < vex)
                        mivex += MAX_GIRO;
                    else if (mivex > vex)
                        mivex -= MAX_GIRO;

                    if (mivey < vey)
                        mivey += MAX_GIRO;
                    else if (mivey > vey)
                        mivey -= MAX_GIRO;

                    this.setVex(mivex);
                    this.setVey(mivey);
                    this.setVel(VELOCIDAD_MAX);
                    if (Math.abs(mivex - vex) > 0.9f)
                        setObjetivo(null);
                    else if (Math.abs(mivey - vey) > 0.9f)
                        setObjetivo(null);
                }
            }
        }
        /*else
            vida = 0;*/
        vida--;
        super.actualizar();
    }

    public boolean estaVivo()
    {
        return vida > 0;
    }

    public void destruir()
    {
        objeto = null;
        vida = 0;
    }

    public void golpe()
    {
        vida = 0;

        /*padre.sumarPts(1);
        if (objeto != null)
        {
            padre.sumarPts(5);
            padre.curar();
        }*/
    }

    public int getDano()
    {
        //if (objeto != null)
        return DANO_MISIL;
        //else
        //    return DANO_BALA;
    }

    @Override
    public void dibujar(Graphics2D g)
    {
        // super.dibujar(g);
        if (!bala)
        {
            // g.drawOval((int) (getX() - RADIO - 2), (int) (getY() - RADIO - 2), DIAMETRO +
            // 4,DIAMETRO + 4);
            g.setColor(Color.WHITE);
            g.drawLine((int) (getX() - RADIO * getVex()), (int) (getY() - RADIO * getVey()),
                    (int) (getX() + RADIO * getVex()), (int) (getY() + RADIO * getVey()));
            /*double angulo = getVey()/getVex();
            AffineTransform reset = new AffineTransform();
            reset.rotate(0,0,0);
            g.rotate(angulo+Math.PI/2.0, (int)getX(), (int)getY());
            g.drawImage(sprites.getSprite(Imagen.MISIL),(int)(getX()-Imagen.MISIL.width/2.0),(int)(getY()-Imagen.MISIL.height/2.0),null);
            g.setTransform(reset);*/
        } else
            super.dibujar(g);

    }

    public void setParametros(int dano, int alcance, int persecucion)
    {
        // TODO Auto-generated method stub
        this.DANO_MISIL = dano;
        this.vida = alcance;
        this.MAX_GIRO = persecucion*0.01f;
    }
}