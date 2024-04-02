package controller;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.MouseInputListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;
import java.awt.event.MouseAdapter;
import model.Niveau;
import model.Obstacle;
import model.Pegs;
import model.Quadrilatere;
import view.View;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
public class Edit extends JPanel{
    public ArrayList<Obstacle> niveau;
    public ArrayList<objetMobile> listPanel = new ArrayList<objetMobile>();//Liste pour garder compte des JPanel sur la page
    JPanel principal;
    objetMobile pegsEcran;
    objetMobile objetSelectionner;//Le dernière objet sur lequel on a cliqué
    public ArrayList<objetMobile> listeSelection = new ArrayList<objetMobile>();
    boolean peutBouger = false;
    JPanel partieGauche = new JPanel();
    JButton save = new JButton("Sauvegarder");
    JButton leave = new JButton("Quitter");
    JButton cancel = new JButton("Annuler");
    JButton redo = new JButton("Redo");
    JButton delete = new JButton("Tout supprimer");
    JSlider rayon = new JSlider();
    JPanel espaceCoords = new JPanel(new BorderLayout());
    JPanel espaceVie = new JPanel(new GridLayout(4,1));
    JPanel espaceDim = new JPanel(new GridLayout(1,2));
    JSlider largeur = new JSlider(JSlider.VERTICAL){
        @Override
        public void paint(Graphics g) {
            // TODO Auto-generated method stub
            super.paint(g);
            ((Graphics2D)g).drawString("Larg", 0, getHeight()/2);
        }
    };
    JSlider hauteur = new JSlider(JSlider.VERTICAL){
        @Override
        public void paint(Graphics g) {
            // TODO Auto-generated method stub
            super.paint(g);
            ((Graphics2D)g).drawString("Haut", 0, getHeight()/2);
        }
    };
    int width;
    int height;
    Selection selection = new Selection();
    UndoManager undoManager = new UndoManager();
    JTextField xSaisie = new JTextField();
    JTextField ySaisie = new JTextField();
    JSlider pointDeVie = new JSlider();
    View view;
    Controleur controlleur;

    public Edit(Niveau n,int widht, int height,int idSauvegarde,View v,Controleur controlleur){
        this.setBackground(Color.gray);
        niveau = Sauvegarde.charge(idSauvegarde);
        this.view = v;
        this.width = widht;
        this.height = height;
        this.controlleur = controlleur;
        JPanel partieBouton = new JPanel();
        partieBouton.setLayout(new GridLayout(3,1));
        JPanel regroupe = new JPanel(new GridLayout(2,1));
        JPanel separateur1 = new JPanel(new GridLayout(2,1));
            separateur1.add(leave);
            separateur1.add(save);
        regroupe.add(separateur1);
            JPanel sepateur2 = new JPanel(new GridLayout(3,1));
            sepateur2.add(delete);
            sepateur2.add(cancel);
            sepateur2.add(redo);
        regroupe.add(sepateur2);
        JPanel partieMilieu = new JPanel(new GridLayout(2,1));
        partieBouton.add(regroupe);
        partieMilieu.add(espaceCoords);
        partieMilieu.add(espaceVie);
        partieGauche.add(partieBouton);
        partieMilieu.setBackground(Color.yellow);
        partieBouton.add(partieMilieu);
        partieBouton.add(espaceDim);
        espaceVie.add(new JLabel("Point de vie"));
        espaceVie.add(pointDeVie);
        espaceVie.add(new JLabel("rayon"));
        espaceVie.add(rayon);

        JPanel espaceX = new JPanel(new BorderLayout());
        JButton xPlus = new JButton("+");
        JButton xMoins = new JButton("-");
        JPanel xCase = new JPanel(new GridLayout(2,1));
        xSaisie.setHorizontalAlignment(JTextField.CENTER);
        espaceX.add(xSaisie,BorderLayout.WEST);
        espaceX.add(xCase,BorderLayout.EAST);
        xCase.add(xPlus);
        xCase.add(xMoins);
        JLabel xtext = new JLabel("Coordonnées X");
        espaceX.setPreferredSize(new Dimension(50,75));
        espaceX.add(xtext,BorderLayout.WEST);
        espaceX.add(xSaisie,BorderLayout.CENTER);
        espaceCoords.add(espaceX,BorderLayout.NORTH);

        
        pointDeVie.setMinimum(1);
        pointDeVie.setMaximum(5);
        pointDeVie.setMinorTickSpacing(1);
        pointDeVie.setMajorTickSpacing(1);
        pointDeVie.setPaintTrack(true); 
        pointDeVie.setPaintTicks(true); 
        pointDeVie.setPaintLabels(true);
        pointDeVie.addChangeListener (( event ) -> { 
            objetSelectionner.obstacle.setHP(pointDeVie.getValue());
            for(int i = 0; i < listPanel.size();i++){
                listPanel.get(i).obstacle.setHP(pointDeVie.getValue());
            }
            principal.requestFocus();
        });

        JPanel espaceY = new JPanel(new BorderLayout());
        JButton yPlus = new JButton("+");
        JButton yMoins = new JButton("-");
        JPanel yCase = new JPanel(new GridLayout(2,1));
        ySaisie.setHorizontalAlignment(JTextField.CENTER);
        espaceY.add(ySaisie,BorderLayout.WEST);
        espaceY.add(yCase,BorderLayout.EAST);
        yCase.add(yPlus);
        yCase.add(yMoins);
        JLabel ytext = new JLabel("Coordonnées Y");
        espaceY.setPreferredSize(new Dimension(50,75));
        espaceY.add(ytext,BorderLayout.WEST);
        espaceY.add(ySaisie,BorderLayout.CENTER);
        espaceCoords.add(espaceY,BorderLayout.SOUTH);

        rayon.setMinimum(20);
        rayon.setMaximum(50);
        rayon.setMinorTickSpacing(1);
        rayon.setMajorTickSpacing(5);
        rayon.setPaintTrack(true); 
        rayon.setPaintTicks(true); 
        rayon.setPaintLabels(true);
        rayon.addChangeListener (( event ) -> { 
            if(objetSelectionner != null && !objetSelectionner.decoration){
                objetSelectionner.obstacle.setHauteur(rayon.getValue()*2);
                objetSelectionner.obstacle.setLargeur(rayon.getValue()*2);
                objetSelectionner.obstacle.setRayon(rayon.getValue());
                objetSelectionner.setSize(new Dimension((int)(rayon.getValue()*View.getRatio()*2),(int)(rayon.getValue()*View.getRatio()*2)));
                for (objetMobile objetMobile : listeSelection) {
                    objetMobile.obstacle.setHauteur(rayon.getValue()*2);
                    objetMobile.obstacle.setLargeur(rayon.getValue()*2);
                    objetMobile.obstacle.setRayon(rayon.getValue());
                    objetMobile.setSize(new Dimension((int)(rayon.getValue()*View.getRatio()*2),(int)(rayon.getValue()*View.getRatio()*2)));
                }
            principal.requestFocus();
            }
        });


        espaceDim.add(hauteur);
        hauteur.setMinimum(20);
        hauteur.setMaximum(100);
        hauteur.setMinorTickSpacing(2);
        hauteur.setMajorTickSpacing(10);
        hauteur.setPaintTrack(true); 
        hauteur.setPaintTicks(true); 
        hauteur.setPaintLabels(true);
        hauteur.addChangeListener (( event ) -> { 
            objetSelectionner.obstacle.setHauteur(hauteur.getValue());
            objetSelectionner.setSize(new Dimension((int)(objetSelectionner.obstacle.getLargeur()*View.getRatioX()),(int)(hauteur.getValue()*View.ratioY)));
            principal.requestFocus();
        });

        espaceDim.add(largeur);
        largeur.setMinimum(20);
        largeur.setMaximum(100);
        largeur.setMinorTickSpacing(2);
        largeur.setMajorTickSpacing(10);
        largeur.setPaintTrack(true); 
        largeur.setPaintTicks(true); 
        largeur.setPaintLabels(true);
        largeur.addChangeListener (( event ) -> { 
            objetSelectionner.obstacle.setLargeur(largeur.getValue());
            objetSelectionner.setSize(new Dimension((int)(largeur.getValue()*View.ratioX),(int)((int)(objetSelectionner.obstacle.getHauteur()*View.ratioY))));
            principal.requestFocus();
        });
        //espaceDimension.add(hauteur);

        xPlus.addActionListener(
            (ActionEvent e) -> {
                objetSelectionner.setLocation(objetSelectionner.getX()+1, objetSelectionner.getY());
                objetSelectionner.obstacle.setX(objetSelectionner.getX()/View.ratioX);
        });
        xMoins.addActionListener(
            (ActionEvent e) -> {
                objetSelectionner.setLocation(objetSelectionner.getX()-1, objetSelectionner.getY());
                objetSelectionner.obstacle.setX(objetSelectionner.getX()/View.ratioX);
        });
        yPlus.addActionListener(
            (ActionEvent e) -> {
                objetSelectionner.setLocation(objetSelectionner.getX(), objetSelectionner.getY()+1);
                objetSelectionner.obstacle.setY(objetSelectionner.getY()/View.ratioY);
        });
        yMoins.addActionListener(
            (ActionEvent e) -> {
                objetSelectionner.setLocation(objetSelectionner.getX(), objetSelectionner.getY()-1);
                objetSelectionner.obstacle.setY(objetSelectionner.getY()/View.ratioY);
        });
        xSaisie.addActionListener(
            (ActionEvent e) -> {
                objetSelectionner.setLocation(Integer.parseInt(xSaisie.getText()), objetSelectionner.getY());
                objetSelectionner.obstacle.setX(objetSelectionner.getX()/View.ratioX);
        });
        ySaisie.addActionListener(
            (ActionEvent e) -> {
                objetSelectionner.setLocation(objetSelectionner.getX(), Integer.parseInt(ySaisie.getText()));
                objetSelectionner.obstacle.setY(objetSelectionner.getY()/View.ratioY);
        });
        save.addActionListener(
            (ActionEvent e) -> {
                Sauvegarde.save(niveau,idSauvegarde);
        });
        leave.addActionListener(
            (ActionEvent e) -> {
                Sauvegarde.save(controlleur.modele.getPlayer());
                Sauvegarde.save(niveau,idSauvegarde);
                view.changerPanel(view.choixEdit());
        });

        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selection != null){    
                    //Liste pour garder les objet a supprimer, car supprimer des éléments d'un liste qu'on est en train de parcourir est pas très opti
                    for(int i = 0; i < listPanel.size();i++){
                        if(appartient(listPanel.get(i))){
                            listeSelection.add(listPanel.get(i));
                        }
                    }
                    saveChange(listeSelection);//Va aussi remove de niveau, listePanel et principal
                    listeSelection = new ArrayList<objetMobile>();
                }
            }
        });
        cancel.addActionListener(
            (ActionEvent e) -> {
                undoManager.undo();
                repaint();
                principal.repaint();
        });
        redo.addActionListener(
            (ActionEvent e) -> {
                undoManager.redo();
                repaint();
                principal.repaint();
        });
        this.setLayout(new BorderLayout());
        this.add(partieBouton,BorderLayout.WEST);

        JPanel partieDroite = new JPanel();
        partieDroite.setLayout(new BorderLayout());
        this.add(partieDroite,BorderLayout.CENTER);
        partieBouton.setPreferredSize(new Dimension(widht/7,height));

        principal = new JPanel(){
            @Override
            //Dessine le rectangle quand on maintient la souris
            protected void paintComponent(Graphics g) {
                // TODO Auto-generated method stub
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int h = height - height/8;
                g2d.draw(selection.getRectangle());
                g2d.setPaint(Color.red);
                g2d.fillRect(widht/7, h, 20, 20);
                g2d.setPaint(Color.cyan);
                g2d.fillRect(widht/7*2, h, 20, 20);
                g2d.setPaint(Color.green);
                g2d.fillRect(widht/7*3, h, 20, 20);
                g2d.setPaint(Color.magenta);
                g2d.fillRect(widht/7*4, h, 20, 20);
                g2d.setPaint(Color.black);
                drawString(g2d, "Objet sélectionner\nAppuyer sur Z-S-Q-D pour se\ndéplacer vers l'objet le plus\nproche dans la direction", widht/7+20, h);
                drawString(g2d, "Objet sélectionner(Peut bouger)\nAppuyer sur Z-S-Q-D pour se\ndéplacer dans la direction.\nAppuyer sur W pour transformer en Pegs.\nAppuyer sur X pour transformer en Obstacle", widht/7*2+20, h);
                drawString(g2d, "Objet dans liste de sélection.\nSera affecter par le boutton \n\"Tout supprimer\" et les sliders", widht/7*3+20, h);
                drawString(g2d, "Objet dans liste de sélection et Objet sélectionner\nPour ne pas s'y perdre", widht/7*4+20, h);
                
            }
        };
        for(int i = 0; i < niveau.size();i++){
            Obstacle o = niveau.get(i);
            pegsEcran = new objetMobile(niveau.get(i)){
                @Override
                public void paint(Graphics g) {
                    // TODO Auto-generated method stub
                    super.paint(g);
                    o.clone(0, 0, 0, o.getLargeur(),o.getHauteur()).dessine(g);
                    if(this == objetSelectionner){
                        if(peutBouger){
                            this.setForeground(Color.cyan);
                        }
                        else if(listeSelection.contains(this)){
                            this.setForeground(Color.magenta);
                        }
                        else{
                            this.setForeground(Color.red);
                        }
                    }
                    else{
                        if(listeSelection.contains(this)){
                            this.setForeground(Color.green);
                        }
                        else{
                            this.setForeground(Color.black);
                        }
                    } 

                }
                //Astuce ultime pour mettre à jour les textes dès qu'une coordonnée bouge
                @Override
                public void setLocation(int x, int y) {
                    // TODO Auto-generated method stub
                    super.setLocation(x, y);
                    xSaisie.setText(this.getX()+"");
                    ySaisie.setText(this.getY()+"");
                }
            };
            if(niveau.size() != 0){
                pegsEcran.setBounds((int)(o.getX()*View.ratioX), (int)(o.getY()*View.ratioY), (int)(o.getLargeur()*View.ratioX), (int)(o.getHauteur()*View.ratioY));
            }
            pegsEcran.setOpaque(false);
            principal.add(pegsEcran);
            listPanel.add(pegsEcran);
            principal.addMouseListener(pegsEcran);
            principal.addMouseMotionListener(pegsEcran);
        }
        principal.setLayout(null);
        principal.setBackground(Color.lightGray);
        partieDroite.add(principal,BorderLayout.CENTER);
        
        //Peg qui servira à créer d'autre peg rond
        Pegs p= new Pegs(0, 0);
        p.setRayon((int)((20+20)/2));
        p.image = null;
        objetMobile pegRond = new objetMobile(p){
            @Override
            public void paint(Graphics g) {
                // TODO Auto-generated method stub
                super.paint(g);
                g.setColor(Color.yellow);
                obstacle.dessine(g);
            }
            @Override
            public void setLocation(int x, int y) {
                // TODO Auto-generated method stub
                super.setLocation(x, y);
                xSaisie.setText(this.getX()+"");
                ySaisie.setText(this.getY()+"");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                specialDecoration(e);
            }
        };
        pegRond.setBounds(50,750,(int)(pegRond.obstacle.getRayon()*View.getRatio()),(int)(pegRond.obstacle.getRayon()*View.getRatio()));
        pegRond.setOpaque(false);
        pegRond.decoration = true;
        principal.add(pegRond);
        principal.addMouseListener(pegRond);
        principal.addMouseMotionListener(pegRond);
        principal.addMouseListener(selection);
        principal.addMouseMotionListener(selection);

        //Peg qui servira à créer d'autre peg carré
        objetMobile pegRect = new objetMobile(new Quadrilatere(0, 0, 20, 20)){
            @Override
            public void paint(Graphics g) {
                // TODO Auto-generated method stub
                super.paint(g);
                g.setColor(Color.yellow);
                obstacle.dessine(g);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                specialDecoration(e);
            }
        };
        pegRect.setBounds(80,750,(int)(pegRect.obstacle.getLargeur()*View.ratioX),(int)(pegRect.obstacle.getHauteur()*View.ratioY));
        pegRect.setOpaque(false);
        pegRect.decoration = true;
        pegRect.obstacle.image = null;
        principal.add(pegRect);
        principal.addMouseListener(pegRect);
        principal.addMouseMotionListener(pegRect);
        principal.setFocusable(true);
        principal.requestFocus();
        principal.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
              int keyCode = e.getKeyCode();
              view.repaint();
              if(objetSelectionner != null){
                if (keyCode == KeyEvent.VK_Q) {
                    if(objetSelectionner != null && (peutBouger || objetSelectionner.decoration)){
                        objetSelectionner.setLocation(objetSelectionner.getX()-10, objetSelectionner.getY());
                        objetSelectionner.obstacle.setX(objetSelectionner.getX()/View.ratioX);
                    }else{
                        objetSelectionner = plusProche(3);//Gauche
                    }
                }
                if (keyCode == KeyEvent.VK_D) {
                    if(objetSelectionner != null && (peutBouger || objetSelectionner.decoration)){
                        objetSelectionner.setLocation(objetSelectionner.getX()+10, objetSelectionner.getY());
                        objetSelectionner.obstacle.setX(objetSelectionner.getX()/View.ratioX);
                    }else{
                        objetSelectionner = plusProche(1);//Droite
                    }
                }
                if (keyCode == KeyEvent.VK_Z) {
                    if(objetSelectionner != null && (peutBouger || objetSelectionner.decoration)){
                        objetSelectionner.setLocation(objetSelectionner.getX(), objetSelectionner.getY()-10);
                        objetSelectionner.obstacle.setY(objetSelectionner.getY()/View.ratioY);
                    }else{
                        objetSelectionner = plusProche(0);//Haut
                    }
                }
                if (keyCode == KeyEvent.VK_S) {
                    if(objetSelectionner != null && (peutBouger || objetSelectionner.decoration)){
                        objetSelectionner.setLocation(objetSelectionner.getX(), objetSelectionner.getY()+10);
                        objetSelectionner.obstacle.setY(objetSelectionner.getY()/View.ratioY);
                    }
                    else{
                        objetSelectionner = plusProche(2);//Bas
                    }
                }
                if (keyCode == KeyEvent.VK_ENTER) {
                    if(objetSelectionner != null){
                        peutBouger = !peutBouger;
                    }
                }
                if (keyCode == KeyEvent.VK_W) {
                    if(peutBouger){
                        niveau.remove(objetSelectionner.obstacle);
                        listPanel.remove(objetSelectionner);
                        principal.remove(objetSelectionner);
                        Obstacle o = pegRond.obstacle.clone(objetSelectionner.getX()/View.getRatioX(), objetSelectionner.getY()/View.getRatioY(), 20, pegRond.obstacle.getLargeur(),pegRond.obstacle.getHauteur());
                        objetSelectionner = creeObstacle(o,(int)(objetSelectionner.obstacle.getX()*View.getRatioX()), (int)(objetSelectionner.obstacle.getY()*View.getRatioY()), 0, 0);
                        objetSelectionner.deplacement = false;
                    }else{
                        Obstacle o = pegRond.obstacle.clone(objetSelectionner.getX()/View.getRatioX(), objetSelectionner.getY()/View.getRatioY(), 20, pegRond.obstacle.getLargeur(),pegRond.obstacle.getHauteur());
                        objetSelectionner = creeObstacle(o, (int)(objetSelectionner.obstacle.getX()*View.getRatioX()), (int)(objetSelectionner.obstacle.getY()*View.getRatioY()), 0, 0);
                        peutBouger = true;
                    }
                }
                if (keyCode == KeyEvent.VK_X) {
                    if(peutBouger){
                        niveau.remove(objetSelectionner.obstacle);
                        listPanel.remove(objetSelectionner);
                        principal.remove(objetSelectionner);
                        Obstacle o = pegRect.obstacle.clone(objetSelectionner.getX()/View.ratioX, objetSelectionner.getY()/View.ratioY, 0,(int)(pegRect.obstacle.getLargeur()), (int)(pegRect.obstacle.getHauteur()));
                        objetSelectionner = creeObstacle(o,(int)(objetSelectionner.obstacle.getX()*View.getRatioX()), (int)(objetSelectionner.obstacle.getY()*View.getRatioY()), 0, 0);
                        objetSelectionner.deplacement = false;
                    }else{
                        Obstacle o = pegRect.obstacle.clone(objetSelectionner.getX()/View.ratioX, objetSelectionner.getY()/View.ratioY, 0,(int)(pegRect.obstacle.getLargeur()), (int)(pegRect.obstacle.getHauteur()));
                        objetSelectionner = creeObstacle(o, (int)(objetSelectionner.obstacle.getX()*View.getRatioX()), (int)(objetSelectionner.obstacle.getY()*View.getRatioY()), 0, 0);
                        peutBouger = true;
                    }
                }
                if (keyCode == KeyEvent.VK_N) {
                    if(!listeSelection.contains(objetSelectionner)){
                        listeSelection.add(objetSelectionner);
                    }else{
                        listeSelection.remove(objetSelectionner);   
                    }
                }
            }
        }
        });
    }

    public boolean appartient(Component p){
        Rectangle r = selection.getRectangle();
        return p.getX() > r.getX() && p.getX() < r.getX()+r.getWidth() && p.getY() > r.getY() && p.getY() < r.getY()+r.getHeight();
    }

    public objetMobile plusProche(int direction){
        objetMobile voisin = objetSelectionner;
        for(int i = 0; i < listPanel.size();i++){
            if((distance(listPanel.get(i)) <= distance(voisin)  || distance(voisin) == 0 )&& objetSelectionner != listPanel.get(i)){
                switch(direction){
                    case 0://Haut
                        if(listPanel.get(i).getY() < objetSelectionner.getY()){
                            voisin = listPanel.get(i);
                        }
                        break;
                    case 1://Droite
                        if(listPanel.get(i).getX() > objetSelectionner.getX()){
                            voisin = listPanel.get(i);
                        }
                        break;

                    case 2://Bas
                        if(listPanel.get(i).getY() > objetSelectionner.getY()){
                            voisin = listPanel.get(i);
                        }
                        break;

                    case 3://Gauche
                        if(listPanel.get(i).getX() < objetSelectionner.getX()){
                            voisin = listPanel.get(i);
                        }
                        break;
                }
            }
        }
        actualiseSlider();
        return voisin;
    }

    public double distance(objetMobile om){
        return Math.hypot(objetSelectionner.getX()-om.getX(), objetSelectionner.getY()-om.getY());
    }

    public objetMobile creeObstacle(Obstacle o, int eX, int eY, int xClick, int yClick){
        niveau.add(o);
        objetMobile om = new objetMobile(o){
            @Override
            public void paint(Graphics g) {
                // TODO Auto-generated method stub
                super.paint(g);
                o.clone(0, 0, 0, o.getLargeur(),o.getHauteur()).dessine(g);
                if(this == objetSelectionner){
                    if(peutBouger){
                        this.setForeground(Color.cyan);
                    }
                    else if(listeSelection.contains(this)){
                        this.setForeground(Color.magenta);
                    }
                    else{
                        this.setForeground(Color.red);
                    }
                }
                else{
                    if(listeSelection.contains(this)){
                        this.setForeground(Color.green);
                    }
                    else{
                        this.setForeground(Color.black);
                    }
                } 
            }
            @Override
            public void setLocation(int x, int y) {
                // TODO Auto-generated method stub
                super.setLocation(x, y);
                xSaisie.setText(this.getX()+"");
                ySaisie.setText(this.getY()+"");
            }
        };
        om.setOpaque(false);
        om.setBounds(((eX-xClick)), (eY-yClick), (int)(om.obstacle.getLargeur()*View.ratioX), (int)(om.obstacle.getHauteur()*View.ratioY));
        principal.add(om);
        listPanel.add(om);
        principal.addMouseListener(om);
        principal.addMouseMotionListener(om);;
        om.xClick = xClick;
        om.yClick = yClick;
        return om;

    }

    public static void drawString(Graphics g, String text, int x, int y) {
        int lineHeight = g.getFontMetrics().getHeight();
        for (String line : text.split("\n"))
            g.drawString(line, x, y += lineHeight);
    }
    
    

    public class objetMobile extends JPanel implements MouseInputListener{
        Obstacle obstacle;        
        boolean deplacement = false;
        boolean decoration = false;//Pour différence les pegs pour éditer et les pegs des niveaux
        int xClick;
        int yClick;

        public objetMobile(Obstacle o){
            this.obstacle = o;
        }

        public objetMobile(Obstacle o, int x, int y){
            this.obstacle = o;

        }

        public boolean inX(int x){
            return (x >= this.getX() && x <= this.getX()+this.getWidth());
        }
        public boolean inY(int y){
           return (y >= this.getY() && y <= this.getY()+this.getHeight()); 
        }

        public void specialDecoration(MouseEvent e){
            if(inX(e.getX()) && inY(e.getY())){
                xClick = e.getX()-this.getX();
                yClick = e.getY()-this.getY();
                if (peutBouger){
                    niveau.remove(objetSelectionner.obstacle);
                    listPanel.remove(objetSelectionner);
                    principal.remove(objetSelectionner);
                    Obstacle o = obstacle.clone(objetSelectionner.getX()/View.getRatioX(), objetSelectionner.getY()/View.getRatioY(), 20, obstacle.getLargeur(),obstacle.getHauteur());
                    objetSelectionner = creeObstacle(o,(int)(objetSelectionner.obstacle.getX()*View.getRatioX()), (int)(objetSelectionner.obstacle.getY()*View.getRatioY()), 0, 0);
                    objetSelectionner.deplacement = false;
                }else{
                    Obstacle o = obstacle.clone((e.getX()-xClick)/View.ratioX, (e.getY()-yClick)/View.ratioY, 20, obstacle.getLargeur(),obstacle.getHauteur());
                    objetSelectionner = creeObstacle(o, e.getX(), e.getY(), xClick, yClick);
                    objetSelectionner.deplacement = true;
                }
            }
            actualiseSlider();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(inX(e.getX()) && inY(e.getY())){
                xClick = e.getX()-this.getX();
                yClick = e.getY()-this.getY();
                if(e.getButton() == 1){
                    if(!deplacement){
                        deplacement = true;
                        objetSelectionner = this;
                        actualiseSlider();
                    }else{
                        deplacement = false;
                        obstacle.setX((e.getX()-xClick)/View.ratioX);
                        obstacle.setY((e.getY()-yClick)/View.ratioY);
                    }  
                }else if(e.getButton() == 3){
                    if(!listeSelection.contains(this)){
                        listeSelection.add(this);
                    }else{
                        listeSelection.remove(this);
                    }
                }
            }
            principal.requestFocus();
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {}

        @Override
        public void mouseMoved(MouseEvent e) {
            if(deplacement){
                this.setLocation(e.getXOnScreen()-Edit.this.getInsets().left-Edit.this.getX()-xClick-width/7,e.getYOnScreen()- Edit.this.getInsets().top-Edit.this.getY()-yClick);
            }
            principal.repaint();
        }
    }

    public void actualiseSlider(){
        if(objetSelectionner != null){
            if(!objetSelectionner.obstacle.utiliseRayon()){
                hauteur.setEnabled(!objetSelectionner.obstacle.utiliseRayon());
                hauteur.setValue((int)objetSelectionner.obstacle.getHauteur());
        
                largeur.setEnabled(!objetSelectionner.obstacle.utiliseRayon());
                largeur.setValue((int)objetSelectionner.obstacle.getLargeur());
            }else{
            rayon.setEnabled(objetSelectionner.obstacle.utiliseRayon());
            rayon.setValue((int)(objetSelectionner.obstacle.getRayon()));            
            }       
        }
    }

    public class Selection extends MouseAdapter{
        int x1,y1,x2,y2;

        public Rectangle getRectangle(){
            return new Rectangle(Math.min(x1,x2),Math.min(y1,y2),Math.abs(x1-x2),Math.abs(y1-y2));
        }

        public void mousePressed(MouseEvent event){
            this.x1 = this.x2 = event.getX();
            this.y1 = this.y2 = event.getY();
            //cutButton.setEnabled(false);
            repaint();
        }

        public void mouseDragged(MouseEvent event){
            this.x2 = event.getX();
            this.y2 = event.getY();
            //cutButton.setEnabled(true);
            repaint();
        }

        public void mouseMouved(MouseEvent event){
        }
    }

    public void saveChange(ArrayList<objetMobile> l){
        Coupe c = new Coupe(l);
        c.doit();
        CutEdit cE = new CutEdit(c);
        undoManager.addEdit(cE);
    }

    public class Coupe{
        ArrayList<objetMobile> liste;

        public Coupe(ArrayList<objetMobile> l){
            this.liste = l;
        }

        public void undo(){
            for(int i = 0; i < liste.size();i++){
                //liste.get(i).setForeground(Color.black);
                listPanel.add(liste.get(i));
                principal.add(liste.get(i));
                niveau.add(liste.get(i).obstacle);
            }
        }

        public void doit(){
            for(int i = 0; i < liste.size();i++){
                principal.remove(liste.get(i));
                listPanel.remove(liste.get(i));
                niveau.remove(liste.get(i).obstacle);
            }

        }
    }
    
    public class CutEdit extends AbstractUndoableEdit{
        Coupe c;

        public CutEdit(Coupe coupe){
            this.c = coupe;
        }
        public void undo(){c.undo();}

        public void redo(){c.doit();}
        }
}



/*Emploie*
 * Maintenir souris -> espace rectangulaire. Bouton "tout supprimer" supprime tous les pegs qui sont dans le rectangle.
 * Bouton annuler -> annule la suppression des pegs. Bouton redo -> resupprime les pegs
 * Pegs selectionner couleur rouge. Appuyé sur entré pour changer la couleur du peg en rouge ou en bleu
 * Deplacement (Z :Haut, D :Droite, S:Bas, Q: Gauche): -Quand peg rouge, déplace vers le pegs le plus proche dans la direction
 *                                                     -Quand cyan, déplace le pegs dans la direction choisie
 * Pegs jaune: (W :sélectionne le peg rond, X:sélectionne le peg carré): appuyé sur entré pour crée un peg à l'emplacement du peg jaune
 */ 
