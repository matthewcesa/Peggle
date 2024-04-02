package model;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import view.Image;
import view.View;
public class Balle {

  public double x;
  public double vY;
  public double y;
  public double vX;
  public double v0;
  public final double diametre = 16;
  public final double rayon = diametre/2;
  public final double g = 1000;
  public BufferedImage image;

  public Balle(double x0, double y0, double v0, double angle) {
    this.x = x0;
    this.y = y0;
    this.vX = Math.cos(Math.toRadians(angle)) * v0;
    this.vY = Math.sin(Math.toRadians(angle)) * v0;
    this.image=Image.boulet;
  }

  public void update() {
    x = x + 0.01 * vX;
    y = y + 0.01 * vY;
    vY = vY + 0.01 * g;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }
  
  public void rebondMur() {
    vX = vX * -1;
  }
  public void dessine(Graphics g){
    int gx=(int)(this.x*View.ratioX);
    int gy=(int)(this.y*View.ratioY);
    int gw=(int)(this.diametre*View.ratioX);
    int gh=(int)(this.diametre*View.ratioY);
    if(image == null){
        g.fillOval(gx,gy,gw,gh);
    }
    Graphics2D g2d = (Graphics2D)g;
    g2d.drawImage(this.image,gx, gy,gw,gh,null);
  }
}