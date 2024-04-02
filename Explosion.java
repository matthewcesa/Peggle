package view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import javax.swing.JPanel;

public class Explosion extends JPanel {

    public double x, y;
    public int radius;
    public double maxRadius = 60.0;
    public boolean active;
    public int point;
    public View view;

    public Explosion(double x, double y,int point,View view) {
        this.x = x;
        this.y = y;
        this.radius = 0;
        this.active = true;
        this.point = point;
        this.view = view;
        int size = (int) (maxRadius * 2);

        setBounds((int) x, (int) y, 40, 40);
        setPreferredSize(new Dimension(size, size));
        setVisible(active);
        setOpaque(false);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (active) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.RED);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            Ellipse2D.Double circle = new Ellipse2D.Double(getWidth() / 2 - radius, getWidth() / 2 - radius, radius,
                    radius);
            g2d.draw(circle);
            g2d.dispose();
            if (getWidth() / 2 - radius <= 0) {
                active = false;
                view.getPartie().remove(this);
            }
            radius += 4;
            g.setFont(new Font("TimesRoman", Font.ROMAN_BASELINE, 12));
            g.drawString("+"+point, 20, 10);
        }
    }
}
