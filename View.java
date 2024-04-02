package view;

import controller.Controleur;
import controller.Edit;
import controller.Sauvegarde;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import model.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Timer;
import java.util.ArrayList;
import java.awt.Image;

import javax.sound.sampled.*;
public class View extends JFrame {

    public JLabel puit = new JLabel();
    public JLabel scoreLabel = new JLabel();
    public JPanel fond = new JPanel();
    public JPanel munition = new JPanel();
    public JPanel fondGauche = new JPanel();
    public JPanel fondDroite = new JPanel();
    public JPanel partie = new JPanel();
    public JProgressBar jauge;

    public JButton resetBalle;
    public static boolean enJeu = true;
    public boolean balleEnJeu = false;
    public double angle;
    public String chemin = System.getProperty("user.dir") + "/ressources/";
    public Timer timer;
    
    public int directionX = 5;
    public Controleur controleur;
    public int nbMunition;
    public double mouseX;
    public double mouseY;
    public static double xBoutCanon;
    public static double yBoutCanon;
    public static int colorX = 25;
    public static int colorY = 15;
    int seconde = 0;
    public static float ratioX;
    public static float ratioY;
    public int MAX_MUNITION = 15;

    static Clip son;
    public static boolean sonMute = false;

    public static int width;
    public static int height;
    public int numNiveau;
    public BufferedImage fondEcran;
    public boolean versDroite = true;
    public View(Controleur controleur) {
        this.controleur = controleur;
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        width = (int) size.getWidth();
        height = (int) size.getHeight();
        this.setSize(width, height);
        this.setTitle("Hit the Peggles");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setUndecorated(true); // nécessaire sinon this.getHeight et this.getWidth renvoie 0
        this.setVisible(true);
        changerPanel(ecranTitre());
    }

    public JPanel ecranTitre(){
        LancerMusic("ressources/SonsWav/Accueil.wav");
        JPanel pane = new JPanel(){
            @Override
            public void paintComponent(Graphics g) {
                try {
                    BufferedImage image = ImageIO.read(new File("ressources/image_acceuil1.png"));
                    super.paintComponent(g);
                    g.drawImage(image,0, 0,width,height, null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        pane.setSize(width, height);
        pane.setLayout(null);
        pane.setBorder(BorderFactory.createTitledBorder("Bienvenue dans notre jeu"));
        pane.setBackground(Color.lightGray);
        mute(pane, "ressources/SonsWav/Accueil.wav");
        add(pane);

        JButton jouer = new JButton("Jouer");
        jouer.setBackground(new Color(255, 51, 51));
        jouer.setBounds(width/2 - width/10/2, height - height/5, width/10, height/10);
        pane.add(jouer);
        jouer.addActionListener(e -> {
            son.stop();
            changerPanel(choixJoueur());
        });
        pane.setBackground(Color.lightGray);
        return pane;
    }


    public JPanel menuPrincipal() {
        String urlDuSon = "ressources/SonsWav/page3.wav";
        LancerMusic(urlDuSon);

        
        JPanel pane = new JPanel(null);
        JLabel nameLabel = new JLabel("Pseudo : ");
        nameLabel.setBounds(width / 2 - 60, height - height / 3 * 2, width / 10, 30);
        pane.add(nameLabel);
        JTextField nameField = new JTextField(controleur.modele.getPlayer().getPseudo());
        nameField.setBounds(width / 2, height - height / 3 * 2, width / 10, 30);
        pane.add(nameField);

        JLabel titrePane = new JLabel("HIT THE PEGGLES");
        titrePane.setBounds(width / 2 - 65, height / 5, 400, 100);
        pane.add(titrePane);

        JButton start = new JButton("START");
        start.setFocusPainted(false);
        start.setBackground(new Color(59, 89, 182));
        start.setBounds(width / 3, height*3/7, width / 3, height / 8);
        pane.add(start);

        JButton edit = new JButton("Création niveaux");
        edit.setBackground(new Color(59, 89, 182));
        edit.setBounds(width / 3, start.getY() + start.getHeight() + 20, width / 3, height / 8);
        pane.add(edit);
        
        JButton choixJoueur = new JButton("Retour sélection");
        choixJoueur.setBackground(new Color(59, 89, 182));
        choixJoueur.setBounds(width / 3, edit.getY()+edit.getHeight()+20, width/3, height/8);
        pane.add(choixJoueur);

        JButton femer = new JButton("Quitter jeu");
        femer.setBackground(Color.red);
        femer.setBounds(width / 3, choixJoueur.getY()+choixJoueur.getHeight()+20, width/3, height/8);
        pane.add(femer);


        start.addActionListener(e -> {
            son.stop();
            controleur.modele.getPlayer().setPseudo(nameField.getText());
            Sauvegarde.save(controleur.modele.getPlayer());
            changerPanel(choixNiveauPane(controleur));
        });

        edit.addActionListener(e -> {
            son.stop();
            controleur.modele.getPlayer().setPseudo(nameField.getText());
            changerPanel(choixEdit());
        });
        choixJoueur.addActionListener(e -> {
            son.stop();
            controleur.modele.getPlayer().setPseudo(nameField.getText());
            Sauvegarde.save(controleur.modele.getPlayer());
            changerPanel(choixJoueur());
        });

        femer.addActionListener(e -> {
            controleur.modele.getPlayer().setPseudo(nameField.getText());
            Sauvegarde.save(controleur.modele.getPlayer());
            System.exit(0);
        });
        resetRatio();
        mute(pane, urlDuSon);
        return pane;
    }

    public JPanel JeuPanel(Controleur controleur) {
        resetRatio();
        nbMunition = MAX_MUNITION - 1;
        controleur.timer.restart();
        fond = new JPanel();
        fond.setLayout(new BorderLayout());

        // --------------CENTRE---------------------
        partie = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                dessineCanon(g);
                drawBall(g);
                for (int i = 0; i < controleur.modele.niveau.list.size(); i++) {
                    controleur.modele.niveau.list.get(i).dessine(g);
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                // TODO Auto-generated method stub
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g;
                if(fondEcran == null){
                    setBackground(Color.gray);
                }
                g2d.drawImage(fondEcran,0, 0,getWidth(),getHeight(),null);
            }   
        };
        partie.setSize(new Dimension(800, 600));
        partie.setLayout(null);

        ImageIcon icon = new ImageIcon(chemin + "puit.png");
        Image image = icon.getImage();
        Image nouvelleImage = image.getScaledInstance(icon.getIconWidth() * 2, icon.getIconHeight() * 2,
                Image.SCALE_SMOOTH);
        ImageIcon nouvelleIcone = new ImageIcon(nouvelleImage);
        puit = new JLabel(nouvelleIcone);
        puit.setSize(new Dimension(partie.getWidth() / 8, nouvelleIcone.getIconHeight()));
        puit.setLocation(0, (int) (partie.getHeight() * ratioY) - partie.getHeight() / 8);

        partie.add(puit);
        fond.add(partie, BorderLayout.CENTER);
        // --------------CENTRE---------------------

        // --------------GAUCHE---------------------
        fondGauche = new JPanel();
        fondGauche.setLayout(new BorderLayout());
        fondGauche.setBackground(Color.gray);
        fondGauche.setPreferredSize(new Dimension(getWidth() / 9, getHeight()));

        munition = new JPanel();
        munition.setLayout(new GridLayout(MAX_MUNITION, 1));
        afficheMunition();

        JButton pause = new JButton("Pause/resume");
        pause.setBounds(0,0,50,50);
        pause.addActionListener(e->{
            if(controleur.timer.isRunning()){
                controleur.timer.stop();
            }else{
                controleur.timer.restart();
            }
        });

        resetBalle = new JButton("RESET");
        resetBalle.setBackground(new Color(47, 250, 30));
        resetBalle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                controleur.modele.balle = null;
                controleur.balleHorsJeu();
            }
        });

        JButton retour = new JButton("Revenir menu");
        retour.setBackground(new Color(59, 89, 182));
        retour.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(Sauvegarde.numNiveau == -1){
                    controleur.modele.getPlayer().setScore(numNiveau-1);
                    changerPanel(choixNiveauPane(controleur));
                }
                else{
                    controleur.modele.getPlayer().setScore(Sauvegarde.numNiveau);
                    changerPanel(choixEdit());
                }
                controleur.balleHorsJeu();
                if(controleur.modele.balle!= null){
                    controleur.modele.balle = null;
                }
                controleur.modele.getPlayer().score = 0;
                controleur.modele.getPlayer().pointGagneParBalleEnJeu = 0;
                Sauvegarde.save(controleur.modele.getPlayer());
            }
        });

        JPanel partieBas = new JPanel(new BorderLayout());
        partieBas.add(resetBalle, BorderLayout.WEST);
        partieBas.add(retour, BorderLayout.EAST);
        fondGauche.add(munition, BorderLayout.CENTER);
        fondGauche.add(pause, BorderLayout.NORTH);
        fondGauche.add(partieBas, BorderLayout.SOUTH);

        fond.add(fondGauche, BorderLayout.WEST);
        // --------------GAUCHE---------------------

        // --------------DROITE---------------------
        fondDroite = new JPanel();
        fondDroite.setLayout(new BorderLayout());
        fondDroite.setBackground(Color.white);
        fondDroite.setPreferredSize(new Dimension(getWidth() / 11, getHeight()));

        /*JPanel info = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paintComponent(g);
                if ((getHeight() - 20) - controleur.modele.player.score < 0) {
                    
                } else {
                    Graphics2D g2d = (Graphics2D) g;
                    setColorX();
                    setColorY();

                    g2d.setColor(Color.black);
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    GradientPaint gp = new GradientPaint(colorX, colorX, Color.yellow, colorY, colorX, Color.cyan,
                            true);
                    g2d.setPaint(gp);
                    g2d.fillRect(10, (getHeight() - 20) - controleur.modele.player.score, getWidth() - 20,
                            getHeight()-10);
                }
            }
        };

        fondDroite.add(info);*/

        
        jauge = new JProgressBar(SwingConstants.VERTICAL,0,200){
            @Override
            protected void paintComponent(Graphics g) {
                // TODO Auto-generated method stub
                super.paintComponent(g);
                super.setValue(controleur.modele.getNiveau().vieActuelTotale*100/controleur.modele.getNiveau().nbVieTotal*2);
            }
        };
        jauge.setPreferredSize(new Dimension(fondDroite.getWidth(),fondDroite.getHeight()));
        jauge.setStringPainted(true); 
        JPanel info=new JPanel();
        info.setBackground(Color.gray);
        info.setPreferredSize(new Dimension(info.getWidth(), info.getHeight()+100));

        scoreLabel.setText("Score : "+controleur.modele.getPlayer().score);
        scoreLabel.setFont(new Font("Serif",Font.PLAIN,20));

        JLabel pseudoLabel=new JLabel("Joueur : "+controleur.modele.player.pseudo);
        pseudoLabel.setFont(new Font("Serif",Font.PLAIN,20));


        info.add(pseudoLabel,BorderLayout.NORTH);
        info.add(scoreLabel,BorderLayout.SOUTH);
        fondDroite.add(info,BorderLayout.NORTH);
        fondDroite.add(jauge);

        fond.add(fondDroite, BorderLayout.EAST);
        // --------------DROITE---------------------

        add(fond);
        setVisible(true);
        resetRatio();

        fond.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                bruitage("ressources/SonsWav/tir.wav");
                controleur.tirer();
            }
        });
        return fond;
    }


    public JPanel choixNiveauPane(Controleur controleur) {
        controleur.modele.player.score = 0;
        String url = "ressources/SonsWav/page4.wav";
        LancerMusic(url);
        JPanel choixNiv = new JPanel(){
            @Override
            public void paintComponent(Graphics g) {
                try {
                    BufferedImage image = ImageIO.read(new File("ressources/image_niveaux.png"));
                    super.paintComponent(g);
                    g.drawImage(image,0, 0,width,height, null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        choixNiv.setBackground(Color.lightGray);
        choixNiv.setLayout(null);
        choixNiv.setSize(width, height);

        JButton precedent = new JButton("Menu Principal");
        precedent.setBackground(new Color(59, 89, 182));
        precedent.setBounds(0, 0, width/6, 100);
        choixNiv.add(precedent);
        ratioX = ratioX / 1;
        ratioY = ratioY / 1;

        JWindow window = new JWindow();
        JPanel panel = new JPanel();
        JTextArea textArea = new JTextArea(5, 20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setText(
                " Le but de PEGS est de détruire tous les pegs.\n\n Pour cela, vous devez tirer des balles sur les pegs. \n Vous disposez d'un nombre limité de balles. \n Vous pourrez passer au niveau suivant si vous détruisez tous les pegs. \n\n Bonne chance !");
        JButton fermer = new JButton("Fermer");
        fermer.setBackground(new Color(59, 89, 182));
        fermer.addActionListener(e -> window.dispose());
        panel.add(fermer);
        JButton suivant = new JButton("Suivant");
        suivant.setBackground(new Color(59, 89, 182));
        suivant.addActionListener(e -> {
            textArea.setText(
                    " Pour tirer, il vous suffit d'appuyer sur la souris.\n\n Important, si vous tirez dans le puit, on vous rajoute une munition.\n Quant aux points, on les calcule ainsi, si vous touchez 3 pegs en un seul tir, vous avez 1+2+3 = 6 points, auquel on ajoute 5 points par peg detruit.\n\n Bonne chance !");
            JButton retour = new JButton("Précédent");
            retour.setBackground(new Color(59, 89, 182));
            panel.add(retour);
            panel.remove(suivant);
            panel.revalidate();
            panel.repaint();

            retour.addActionListener(e2 -> {
                textArea.setText(
                        " Le but de PEGS est de détruire tous les pegs.\n\n Pour cela, vous devez tirer des balles sur les pegs. \n Vous disposez d'un nombre limité de balles. \n Vous pourrez passer au niveau suivant si vous détruisez tous les pegs. \n\n Bonne chance !");
                panel.remove(retour);
                panel.add(fermer);
                panel.add(suivant);
                panel.revalidate();
                panel.repaint();
            });
        });
        panel.add(fermer);
        panel.add(suivant);

        window.getContentPane().add(new JLabel("", new ImageIcon(chemin + "loading.gif"), JLabel.CENTER));
        window.setBounds(0, 0, getWidth() / 3, getHeight() / 5);
        window.setLocationRelativeTo(null);
        window.add(textArea, BorderLayout.CENTER);
        window.add(panel, BorderLayout.SOUTH);
        window.setVisible(true);

        precedent.addActionListener(e -> {
            this.invalidate();
            son.stop();
            changerPanel(menuPrincipal());
        });
        afficheMiniature(1, choixNiv);
        mute(choixNiv,url);
        return choixNiv;
    }

    public void changerPanel(Container pane) {
        invalidate();
        enJeu = pane.equals(fond);
        setContentPane(pane);
        repaint();
        revalidate();
    }

    public JPanel choixEdit() {
        JPanel choix = new JPanel(null);
        JButton acceuil = new JButton("Menu Principal");
        acceuil.setBackground(new Color(59, 89, 182));
        acceuil.addActionListener(
                (ActionEvent e) -> {
                    this.invalidate();
                    controleur.modele.setPlayer(Sauvegarde.listeJoueurs.get(Sauvegarde.joueur));
                    changerPanel(menuPrincipal());
                });
        acceuil.setBounds(0, 0, width/6, 50);
        choix.add(acceuil);
        ratioX = ratioX / 1;
        ratioY = ratioY / 1;
        afficheMiniature(2, choix);
        return choix;
    }

    public void nextLevel() {
        JPanel nextLvl = new JPanel();
        
        JButton acceuil = new JButton("Acceuil");
        JLabel winOrLose = new JLabel();
        JButton niveauSuiv_retry ;
        nextLvl.setSize(width,height);
        controleur.timer.stop();
        nextLvl.setLayout(null);
        acceuil.setBounds(width/3-50, height/2-75, 150,150);
        
        acceuil.addActionListener(e->{
            changerPanel(menuPrincipal());
        });
        if(controleur.modele.getNiveau().NoMorePeg() && nbMunition >= 0) { 
            winOrLose.setText("Vous avez gagné");
            bruitage("ressources/SonsWav/win.wav");
            controleur.modele.getPlayer().progression++;
            winOrLose.setFont(new Font("TimesRoman", Font.BOLD, 100));
            niveauSuiv_retry = new JButton("Niveau Suivant");
            niveauSuiv_retry.setBackground(new Color(59, 89, 182));
            niveauSuiv_retry.addActionListener(e->{
                int niv = controleur.modele.getNiveau().getNumNiveau() + 1;
                fondEcran = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
                try {
                    fondEcran = ImageIO.read(new File("ressources/Niveau"+niv+"Fond.png"));
                } catch (IOException excep) {
                    excep.printStackTrace();
                }
                controleur.modele.getPlayer().score = 0;
                controleur.modele.getPlayer().pointGagneParBalleEnJeu = 0;
                controleur.modele.setNiveau(new Niveau(niv));
                changerPanel(JeuPanel(this.controleur));
                controleur.timer.restart();
            });
        }else {
            winOrLose.setText("Vous avez perdu");
            bruitage("ressources/SonsWav/lose.wav");
            winOrLose.setFont(new Font("TimesRoman", Font.BOLD, 100));
            niveauSuiv_retry = new JButton("Réessayer");
            niveauSuiv_retry.addActionListener(e->{
                fondEcran = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
                try {
                    fondEcran = ImageIO.read(new File("ressources/Niveau"+controleur.modele.getNiveau().getNumNiveau()+"Fond.png"));
                } catch (IOException excep) {
                    excep.printStackTrace();
                }
                controleur.modele.getPlayer().score = 0;
                controleur.modele.getPlayer().pointGagneParBalleEnJeu = 0;
                controleur.modele.setNiveau(new Niveau(controleur.modele.getNiveau().getNumNiveau()));
                changerPanel(JeuPanel(this.controleur));
                controleur.timer.restart();
            });
        }
        winOrLose.setBounds(width/4,height/4, width,150);
        winOrLose.setBackground(new Color(59, 89, 182));
        nextLvl.add(niveauSuiv_retry);
        niveauSuiv_retry.setBounds(acceuil.getX()+300, acceuil.getY(),150,150);
        nextLvl.add(winOrLose);
        nextLvl.add(acceuil);
        Sauvegarde.save(controleur.modele.getPlayer());
        changerPanel(nextLvl);
    }

    public void afficheMiniature(int mode, JPanel pane) {
        // 1 = Niveau imposé
        // 2 = Niveau créer soit même
        if (mode == 1) {
            Sauvegarde.numNiveau = -1;
        } else {
            Sauvegarde.numNiveau = 0;
        }
        JPanel bis = new JPanel(null);
        int borne = mode == 1 ? controleur.modele.getPlayer().progression : Math.max(Sauvegarde.listeJoueurs.get(Sauvegarde.joueur).liste.size(), 1);
        bis.setBounds(width / 30, height/8, width, height / 6);
        for (int i = 0; i < borne; i++) {
            int k = i;
            JPanel panelPrincipal = new JPanel(new BorderLayout());
            panelPrincipal.setBounds(width / 20 , i * height, width - width/5, height );
            JPanel miniature = new JPanel(null);
            if (mode == 2) {
                JButton supprimer = new JButton("X");
                supprimer.setBackground(Color.red);
                supprimer.addActionListener(
                    (ActionEvent e) -> {
                        if(Sauvegarde.listeJoueurs.get(Sauvegarde.joueur).liste.size() > 0){
                            Sauvegarde.listeJoueurs.get(Sauvegarde.joueur).liste.remove(k);
                            Sauvegarde.listeJoueurs.get(Sauvegarde.joueur).listeScoreEdit.remove(k);
                            Sauvegarde.save(Sauvegarde.listeJoueurs.get(Sauvegarde.joueur));
                            changerPanel(choixEdit());
                        }
                    });
                supprimer.setBounds(panelPrincipal.getWidth()-50, 0, 50, 50);
                supprimer.requestFocus();
                miniature.add(supprimer);

                JButton edit = new JButton("E");
                edit.setBackground(Color.cyan);
                edit.addActionListener(
                        (ActionEvent e) -> {
                            changerPanel(new Edit(null, width, height, k, this,controleur));
                        });
                edit.setBounds(panelPrincipal.getWidth()-50, 50, 50, 50);
                edit.requestFocus();
                miniature.add(edit);
            }
            JButton bouton = new JButton(){
                @Override
                protected void paintComponent(Graphics g) {
                    // TODO Auto-generated method stub
                    super.paintComponent(g);
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 30)); 
                    Graphics2D g2d = (Graphics2D)g;
                    if(fondEcran == null){
                        setBackground(Color.gray);
                    }
                    g2d.drawImage(view.Image.fondEcrans[Math.min(k,4)],0, 0,getWidth(),getHeight(),null);
                    
                    if (mode != 1) {
                        dessineNiveau(g, Sauvegarde.charge(k));
                    }
                    if (mode == 1) {
                        if(controleur.modele.getPlayer().progression >= k+1){
                            controleur.modele.setNiveau(new Niveau(k + 1));
                            dessineNiveau(g, controleur.modele.getNiveau().getList());
                        }
                    }
                    if(Sauvegarde.numNiveau == -1){
                        if(controleur.modele.getPlayer().progression >= k){
                            ((Graphics2D)g).drawString("Meilleur score : "+Sauvegarde.listeJoueurs.get(Sauvegarde.joueur).listeScore[k], 0, 30);
                        }
                    }
                    else{
                        ((Graphics2D)g).drawString("Meilleur score : "+Sauvegarde.listeJoueurs.get(Sauvegarde.joueur).listeScoreEdit.get(k), 0, 30);
                    }
                }

                @Override
                public void repaint() {}
            };
            panelPrincipal.add(miniature);
            miniature.add(bouton);
            bouton.setSize(panelPrincipal.getWidth(), panelPrincipal.getHeight());
            bouton.addActionListener(
                    (ActionEvent e) -> {
                        resetRatio();
                        if (mode == 1) {
                            numNiveau = k + 1;
                            fondEcran = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
                            try {
                                fondEcran = ImageIO.read(new File("ressources/Niveau"+numNiveau+"Fond.png"));
                            } catch (IOException excep) {
                                // TODO Auto-generated catch block
                                excep.printStackTrace();
                            }
                            Sauvegarde.numNiveau = -1;
                            controleur.modele.setNiveau(new Niveau(k + 1));
                            changerPanel(JeuPanel(this.controleur));
                        }
                        if (mode == 2) {
                            controleur.modele.setNiveau(new Niveau(1));// Sinon le niveau est pas initialisé
                            controleur.modele.getNiveau().setList(Sauvegarde.charge(k));
                            changerPanel(JeuPanel(this.controleur));
                        }
                        son.stop();
                    });
            bis.add(panelPrincipal);
        }
        if (mode == 2) {
            JButton ajoute = new JButton("Nouveau");
            ajoute.setBackground(new Color(59, 89, 182));
            ajoute.addActionListener(
                    (ActionEvent e) -> {
                        ArrayList<Obstacle> a = new ArrayList<>();
                        Sauvegarde.listeJoueurs.get(Sauvegarde.joueur).liste.add(a);
                        Sauvegarde.listeJoueurs.get(Sauvegarde.joueur).listeScoreEdit.add(0);
                        Sauvegarde.save(a, borne);
                        changerPanel(choixEdit());
            });
            ajoute.setBounds(width / 20 , borne * height, width - width/5, height);
            bis.add(ajoute);
        }
        JScrollPane defile = new JScrollPane(bis, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        defile.getVerticalScrollBar().setUnitIncrement(30);
        bis.setPreferredSize(new Dimension(width / 30 + width / 6, height * (borne+2)));
        defile.setBounds(width / 30, height/8, width - width/15, height);
        bis.setBackground(Color.lightGray);
        pane.add(defile);

    }

    public Container choixJoueur(){
        LancerMusic("ressources/SonsWav/ChoixNiveau.wav");
        JPanel auxiliaire = new JPanel(null);
        JPanel bis = new JPanel(new GridLayout(Sauvegarde.listeJoueurs.size()+1,1));
        JScrollPane principal = new JScrollPane(bis, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        principal.setBounds(200,100, width - width / 4, height - height / 4);
        bis.setPreferredSize(new Dimension(principal.getWidth(),principal.getHeight()/2 * Sauvegarde.listeJoueurs.size()));

        for(int i = 0; i < Sauvegarde.listeJoueurs.size(); i++){
            JPanel pane = new JPanel(new BorderLayout());
            int k = i;
            JPanel info = new JPanel(null){
                @Override
                public void paintComponent(Graphics g) {
                    // TODO Auto-generated method stub
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gp = new GradientPaint(0, 0, new Color(0,0,153), getWidth(), getHeight(),Color.black , false);
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setPaint(Color.white);
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
                    ((Graphics2D)g).drawString("Pseudo : "+Sauvegarde.listeJoueurs.get(k).pseudo, getWidth()/4, getHeight()/4);
                    ((Graphics2D)g).drawString("Nombre de niveau: "+Sauvegarde.listeJoueurs.get(k).liste.size(), getWidth()/4, getHeight()/4*2);
                    ((Graphics2D)g).drawString("Progressions: Niveau "+Sauvegarde.listeJoueurs.get(k).progression, getWidth()/4, getHeight()/4*3);
                }     
            };

            JButton choix = new JButton("Joueur "+(k+1));
            choix.setBackground(new Color(59, 89, 182));
            choix.addActionListener(
                (ActionEvent e) -> {
                    Sauvegarde.joueur = k;
                    controleur.modele.setPlayer(Sauvegarde.listeJoueurs.get(Sauvegarde.joueur));
                    son.stop();
                    changerPanel(menuPrincipal());
            });

            JButton supprimer = new JButton("X"){
                @Override
                public void paintComponent(Graphics g) {
                    // TODO Auto-generated method stub
                    super.paintComponent(g);
                    setBackground(Color.red);
                }
            };
            supprimer.addActionListener(
                (ActionEvent e) -> {
                    Sauvegarde.listeJoueurs.remove(k);
                    Sauvegarde.save(null);
                    changerPanel(choixJoueur());
            });

            pane.add(choix,BorderLayout.WEST);
            pane.add(info,BorderLayout.CENTER);
            info.add(supprimer);
// <<<<<<< HEAD
            info.setBackground(Color.lightGray);
            
// =======

// >>>>>>> develop
            bis.add(pane);
            supprimer.setBounds(principal.getWidth() - 150, 0, 50, 50);
        }

        principal.setBackground(Color.lightGray);

        JButton nouveau = new JButton("Nouvelle sauvegarde");
        nouveau.setBackground(new Color(151, 223, 198));
        nouveau.addActionListener(
                (ActionEvent e) -> {
                    Sauvegarde.joueur = Sauvegarde.listeJoueurs.size();
                    Player p = new Player("Nouveau");
                    Sauvegarde.listeJoueurs.add(p);
                    controleur.modele.setPlayer(p);
                    controleur.modele.getPlayer().listeScoreEdit.add(0);
                    Sauvegarde.save(controleur.modele.getPlayer());
                    changerPanel(menuPrincipal());
                });
        bis.add(nouveau);


        principal.getVerticalScrollBar().setUnitIncrement(30);
        auxiliaire.add(principal);
        auxiliaire.setBackground(Color.lightGray);
        mute(auxiliaire, "ressources/SonsWav/ChoixNiveau.wav");
        return auxiliaire;
    }

    public void placePuit() {
        if (versDroite) {
            ;
            if (puit.getX() + puit.getWidth() >= partie.getWidth()) {
                puit.setLocation(puit.getX() - 5, puit.getY());
                versDroite = false;
            } else {
                puit.setLocation(puit.getX() + 5, puit.getY());
            }
        } else {
            if (puit.getX() <= 0) {
                puit.setLocation(puit.getX() + 5, puit.getY());
                versDroite = true;
            } else {
                puit.setLocation(puit.getX() - 5, puit.getY());
            }
        }
    }

    public void dessineCanon(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int widthBase = 150;
        int heightBase = 150;

        Arc2D.Double arc2 = new Arc2D.Double(partie.getWidth() / 2 - widthBase / 2, -heightBase / 2, widthBase,
                heightBase, 180, 180, Arc2D.OPEN);

        BufferedImage img = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);
        try {
            img = ImageIO.read(new File("ressources/roue.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        double theta = Math.toRadians(90-angle);
        xBoutCanon = (partie.getWidth() / 2) - (5 * heightBase / 8) * Math.sin(theta)- 8 /* Width balle */;
        yBoutCanon = (5 * heightBase / 8) * Math.cos(theta) -8 /* Height balle */;
        // Pour calculer nouvelles coordonnées de la balle après rotaion
        Balle fantome = new Balle(xBoutCanon/View.ratioX, yBoutCanon/View.ratioY, 300d, 180 - this.angle);
        GeneralPath genPath = new GeneralPath();
        boolean premierRebond = false;
        while (!premierRebond) {
            // LancerMusic("ressources/SonsWav/tir_canon.wav");
            fantome.update();
            double a = fantome.getX();
            double b = fantome.getY();
            for (Obstacle o : controleur.modele.getNiveau().list) {
                if (o.collision(fantome)) {
                    o.rebond(fantome);
                    premierRebond = true;
                }
            }
            if (fantome.getY() > height) {
                premierRebond = true;
            }
            genPath.moveTo((a+8)*ratioX, (b+8)*ratioY);
            genPath.lineTo((a+8)*ratioX, (b+8)*ratioY);
        }
        for (int i = 0; i < 10; i++) {
            fantome.update();
            double a = fantome.getX();
            double b = fantome.getY();
            for (Obstacle o : controleur.modele.getNiveau().list) {
                o.rebond(fantome);
            }
            genPath.moveTo((a+8)*ratioX, (b+8)*ratioY);
            genPath.lineTo((a+8)*ratioX, (b+8)*ratioY);
        }

        g2d.setStroke(new BasicStroke(5));
        GradientPaint gp = new GradientPaint(colorX, colorX, Color.yellow, colorY, colorY, Color.cyan, true);
        g2d.setPaint(gp);
        g2d.draw(genPath);
        g2d.setStroke(new BasicStroke(1));
        g2d.setPaint(Color.BLACK);

        g2d.setClip(arc2);
        g2d.drawImage(img, partie.getWidth() / 2 - 85, -85, 170, 170, partie);
        // g2d.draw(arc2);

        try {
            img = ImageIO.read(new File("ressources/canon.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        g2d.rotate(Math.toRadians(90 - angle), partie.getWidth() / 2, 0);
        g2d.setClip(null);
        g2d.drawImage(img, partie.getWidth() / 2 - 85, -55, 170, 170, partie);
        g2d.rotate(Math.toRadians(angle - 90), partie.getWidth() / 2, 0);
        // On annule la rotation après avoir dessiner le rectangle pour que seule le
        // bout du partie rotate

        dessineNiveau(g, controleur.modele.getNiveau().list);
    }

    public void dessineNiveau(Graphics g, ArrayList<Obstacle> l) {
        for (int i = 0; i < l.size(); i++) {
            l.get(i).dessine(g);
        } 
    }

    public void calculeAngle() {
        mouseX = MouseInfo.getPointerInfo().getLocation().getX();
        mouseY = MouseInfo.getPointerInfo().getLocation().getY();
        int pointX = munition.getWidth() + partie.getWidth() / 2;
        double angle1 = Math.atan2(mouseY - 0, mouseX - pointX);
        double angle2 = Math.atan2(0, -pointX);
        angle = Math.toDegrees(angle2 - angle1);
    }

    public void afficheMunition() {
        int xMun = (2 * munition.getWidth()) / 5;
        int yMun = (munition.getHeight() / MAX_MUNITION) / 4;
        for (int i = 1; i <= MAX_MUNITION; i++) {
            JPanel panel = new JPanel();
            if (MAX_MUNITION - i <= nbMunition) {
                panel = new JPanel() {
                    @Override
                    public void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.drawImage(view.Image.boulet, xMun, yMun, 2 * yMun, 2 * yMun, null);
                    }
                };
            }
            panel.setBackground(Color.white);
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createLineBorder(Color.black));
            panel.setVisible(true);
            munition.add(panel);
        }
    }


    public void drawBall(Graphics g) {
        Graphics g2d = g;
        if (this.controleur.modele.balle != null) {
            this.controleur.modele.balle.dessine(g2d);
        }
    }

    public JPanel getPartie() {
        return this.partie;
    }

    public double getAngle() {
        return angle;
    }

    public void setColorX() {
        colorX -= 1;
    }

    public void setColorY() {
        colorY -= 1 ;
    }

    public static void LancerMusic(String url) {
        if(!sonMute) {
            try {

                File ficSon = new File(url);

                if (ficSon.exists()) {
                    AudioInputStream audio = AudioSystem.getAudioInputStream(ficSon);
                    son = AudioSystem.getClip();
                    son.open(audio);
                    son.start();
                    son.loop(Clip.LOOP_CONTINUOUSLY);
                } else {
                    System.out.println("fichier introuvable");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void bruitage(String url) {
        if(!sonMute) {
            try {

                File ficSon = new File(url);

                if (ficSon.exists()) {
                    AudioInputStream audio = AudioSystem.getAudioInputStream(ficSon);
                    son = AudioSystem.getClip();
                    son.open(audio);
                    son.start();

                } else {
                    System.out.println("fichier introuvable");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addExplosion(double x, double y,int point) {
        partie.add(new Explosion(x * ratioX, y * ratioY,point,this));
    }

    public void setScore() {
        scoreLabel.setText("Score : " + controleur.modele.getPlayer().score);
    }

    public int getNumNiveau() {
        return numNiveau;
    }

    public static void resetRatio() {
        ratioX = (float) (width - width / 7 - width / 11) / 800;
        ratioY = (float) height / 600;
    }

    public static double getRatioX() {
        return ratioX;
    }

    public static double getRatioY() {
        return ratioY;
    }

    public static double getRatio() {
        return (ratioX + ratioX) / 2;
    }

    public void mute( JPanel panel, String url) {
        JButton btnMute = new JButton("Mute");
        btnMute.setBounds(width-75,0,75,75);
        panel.add(btnMute);
        btnMute.addActionListener(e->{
            if(son.isActive()) {
                son.stop();
                sonMute = true;
            }else {
                LancerMusic(url);
                sonMute = false;
            }
        });
    }

}
