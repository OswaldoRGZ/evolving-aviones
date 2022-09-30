package controlador;

import java.awt.Color;
import java.awt.Graphics2D;

public class Contramedida extends Objeto
{
    private final float VELOCIDAD = 1;
    private int MAX_VIDA = 100;
    private int vida;
        
    Contramedida(int ancho, int alto, boolean esta_vivo)
    {
        super(ancho, alto, false);
        
        // TODO Auto-generated constructor stub
    }
    Contramedida(int ancho, int alto, float x, float y, float angulo)
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
        valoresIniciales();
    }
    private void valoresIniciales()
    {
        // TODO Auto-generated method stub
        this.vida = MAX_VIDA;
        this.RADIO = 2;
        this.DIAMETRO = RADIO*2;
    }
    public boolean estaVivo()
    {
        return vida > 0;
    }
    
    @Override
    public void actualizar()
    {
        // TODO Auto-generated method stub
        vida--;
        super.actualizar();
    }
    @Override
    public void dibujar(Graphics2D g)
    {
        // TODO Auto-generated method stub
        g.setColor(Color.WHITE);
        g.drawOval((int) (getX() - RADIO),
                (int) (getY() - RADIO), DIAMETRO,
                DIAMETRO);
    }
}
