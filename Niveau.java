package model;
import java.util.ArrayList;
public class Niveau {

    public ArrayList<Obstacle> list = new ArrayList<Obstacle>();
    public int niveau;
    public static double milieu=-1;
    public int nbVieTotal;
    public int vieActuelTotale = 0;
    public Niveau(int i) {
        
        switch (i) {
            case 1:
            niveau_1();//liste de lignes
            niveau = 1;
            
            break;
            case 2:
                niveau_2();//dé
                niveau = 2;
                break;
            case 3:
                niveau_3();//séries de carrées
                niveau = 3;
                break;
            case 4:
                niveau_4();//sardine
                niveau = 4;
                break;
            case 5:
                niveau_5();//triforce de zelda (le retour)
                niveau = 5;
                break;
        }
    }

    //formes
    protected void diagonal(double x, double y, int pegUsed, boolean direction, boolean sens,int vie) {
        double nvx = x;
        double nvy = y;
        for (int i = 0; i < pegUsed; i++) {
            Pegs p = (1<=vie&&vie<=3)?new Pegs(nvx, nvy, vie): new Pegs(nvx, nvy);
            list.add(p);
            nvx = direction? nvx + p.getDiametre() : nvx - p.getDiametre();//true pour droite, false pour gauche
            nvy = sens? nvy - p.getRayon() : nvy + p.getRayon();//true pour haut, false pour bas
        }
    }


    protected void carres(double x, double y, int pegCote, int vie) {
        lignes(x, y, pegCote + 2, vie);
        colonne(x, y + getDiametre(), pegCote,vie);
        colonne(x + ((pegCote + 1) * getDiametre()), y + getDiametre(), pegCote,vie);
        lignes(x, y + ((pegCote + 1) * getDiametre()), pegCote + 2,vie);
    }

    protected void lignes(double x, double y, int pegUsed, int vie) {
        double nvx = x;
        for (int i = 0; i < pegUsed; i++) {
            Pegs p = (1<=vie&&vie<=3)?new Pegs(nvx, y, vie): new Pegs(nvx, y);
            list.add(p);
            nvx += p.getDiametre();
        }
    }

    protected void colonne(double x, double y, int pegUsed, int vie) {
        double nvy = y;
        for (int i = 0; i < pegUsed; i++) {
            Pegs p = (1<=vie&&vie<=3)?new Pegs(x, nvy, vie): new Pegs(x, nvy);
            list.add(p);
            nvy += p.getDiametre();
        }
    }
    protected void triangle(double x, double y, int lignes, Boolean alenvers,int vie){
        double nvx=x;
        double nvy=y;
        for(int i=1;i<=lignes;i++){
            lignes(nvx, nvy, i,vie);
            nvx-=getRayon();
            nvy=alenvers==false?nvy+getDiametre():nvy-getDiametre();
        }
    }

    //niveaux 
    protected void niveau_1() {//liste de lignes
        double y1=200; double x1=getRayon()/6;
        for(int i=0;i<6;i++){
            if(i%2==0){
                lignes(x1+getRayon(), y1+(getDiametre()*i), 16,1);
            }
            else{
                Quadrilatere obr = new Quadrilatere(x1, y1+(getDiametre()*i),getRayon()*5,getRayon());
                Quadrilatere obr2 = new Quadrilatere(x1+getDiametre()*14, y1+(getDiametre()*i),getRayon()*5,getRayon());
                lignes(x1+getRayon()*6, y1+(getDiametre()*i), 11,0);
                list.add(obr);
                list.add(obr2);
            }
            Quadrilatere barriereGauche= new Quadrilatere(x1+getDiametre()*3, y1+getDiametre()*6+getRayon(),getDiametre()*3,getRayon());
            Quadrilatere barriereDroit= new Quadrilatere(x1+getDiametre()*10+getRayon(), y1+getDiametre()*6+getRayon(),getDiametre()*3,getRayon());
            list.add(barriereGauche);
            list.add(barriereDroit);
        }
        int rep = 0;
        for (int k = 0 ; k < list.size() ; k++) {
            if(list.get(k) instanceof Pegs){
                rep+=list.get(k).getVie();
            }
        }
        nbVieTotal = rep;
    }

    protected void niveau_2(){//dé
        double x2=400+(getRayon()/6);double y2=300;
        colonne(x2, y2, 6,2);
        //coté droit du dé
        diagonal(x2+getDiametre(), y2-getRayon(),4, true, true,2);
        colonne(x2+getDiametre()*4, y2-getDiametre(), 4,2);
        diagonal(x2+getDiametre(), y2+getRayon()*9, 4, true, true,2);
        lignes(x2+getDiametre(), y2+getRayon()*7, 1,3);
        lignes(x2+getDiametre()*3, y2-getRayon(), 1,3);
        //coté gauche du dé
        diagonal(x2-getDiametre(), y2-getRayon(), 4, false, true,2);
        colonne(x2-getDiametre()*4, y2-getDiametre(), 4,2);
        diagonal(x2-getDiametre(), y2+getRayon()*9, 4, false, true,2);
        lignes(x2-getDiametre()*2, y2+getRayon()*3, 1,3);
        //face du dé
        diagonal(x2+getDiametre(), y2-getRayon()*7, 3, true, false,2);
        diagonal(x2-getDiametre(), y2-getRayon()*7, 3, false, false,2);
        colonne(x2, y2-getDiametre()*4, 1,2);
        colonne(x2, y2-getDiametre()*3, 3,3);

        int rep = 0;
        for (int k = 0 ; k < list.size() ; k++) {
            if(list.get(k) instanceof Pegs){
                rep+=list.get(k).getVie();
            }
        }
        nbVieTotal = rep;
    }
    protected void niveau_3(){//series de carrés
        double x3=(400/5)-(getRayon()/12); double y3=getRayon()*5;
        int m1=0;int m2=0;int j=0;
        for(int i=3; i>-1;i--){
            carres(x3+getDiametre()*m1, y3, i,1);
            carres(x3+getDiametre()*m2, y3+getDiametre()*(6-j), j,2);
            m1+=i+2;m2+=j+2;j++;
        }
        Quadrilatere colonneGauche = new Quadrilatere(x3-getDiametre(), y3-getRayon()-2, getRayon(), getDiametre()*8+getRayon());
        Quadrilatere colonneDroit = new Quadrilatere(x3+getDiametre()*14, y3-getRayon()-2, getRayon(), getDiametre()*8+getRayon());
        Quadrilatere ligneGaucheHaut = new Quadrilatere(x3-getDiametre(), y3-getDiametre(), getDiametre()*6, getRayon());
        Quadrilatere ligneGaucheBas = new Quadrilatere(x3-getDiametre(), y3+getRayon()*16, getDiametre()*6, getRayon());
        Quadrilatere ligneDroitHaut = new Quadrilatere(x3+getDiametre()*8+getRayon(), y3-getDiametre(), getDiametre()*6, getRayon());
        Quadrilatere ligneDroitBas = new Quadrilatere(x3+getDiametre()*8+getRayon(), y3+getRayon()*16, getDiametre()*6, getRayon());
        list.add(colonneGauche);list.add(colonneDroit);
        list.add(ligneGaucheHaut);list.add(ligneGaucheBas);
        list.add(ligneDroitHaut);list.add(ligneDroitBas);

        int rep = 0;
        for (int k = 0 ; k < list.size() ; k++) {
            if(list.get(k) instanceof Pegs){
                rep+=list.get(k).getVie();
            }
        }
        nbVieTotal = rep;
    }
    protected void niveau_4(){//sardine
        double x4=getRayon(); double y4=getDiametre()*5;
        //bouche et tete
        diagonal(x4, y4, 4, true, true,1);
        diagonal(x4+getDiametre(), y4+getRayon(), 2, true, false,1);
        diagonal(x4+getDiametre(), y4+getRayon()*3, 2, false, false,1);
        diagonal(x4+getDiametre(), y4+getRayon()*5, 3, true, false,1);
        //aile haute
        lignes(x4+getDiametre()*4, y4-getRayon()*3, 3,1);
        lignes(x4+getDiametre()*5, y4-getRayon()*5, 3,2);
        lignes(x4+getDiametre()*6, y4-getRayon()*7, 3,3);
        //aile basse
        lignes(x4+getDiametre()*4, y4+getRayon()*7, 3,1);
        lignes(x4+getDiametre()*5, y4+getRayon()*9, 3,2);
        lignes(x4+getDiametre()*6, y4+getRayon()*11, 3,3);
        //arriere
        diagonal(x4+getDiametre()*7, y4-getRayon()*3, 5, true, false,1);
        diagonal(x4+getDiametre()*7, y4+getRayon()*7, 5, true, true,1);
        //queue
        diagonal(x4+getDiametre()*12, y4, 3, true, true,2);
        diagonal(x4+getDiametre()*12, y4+getDiametre()*2, 3, true, false,2);
        colonne(x4+getDiametre()*15, y4+getDiametre()*2, 2,3);
        colonne(x4+getDiametre()*15, y4-getDiametre(), 2,3);
        colonne(x4+getDiametre()*14, y4+getDiametre(), 1,3);
        //visage
        lignes(x4+getRayon()*7, y4-getRayon(), 1,3);
        Quadrilatere ligne = new Quadrilatere(x4+getRayon()*9, y4-getRayon(), getRayon(), getRayon()*6);
        list.add(ligne);

        int rep = 0;
        for (int k = 0 ; k < list.size() ; k++) {
            if(list.get(k) instanceof Pegs){
                rep+=list.get(k).getVie();
            }
        }
        nbVieTotal = rep;
    }
    protected void niveau_5(){//triforce de zelda
        double x5=getRayon()/2; double y5=150;
        x5=400+(getRayon()/6);
        triangle(x5, y5, 4, false,0);
        triangle(x5-getDiametre()*2, y5+getDiametre()*4, 4, false,0);
        triangle(x5-getDiametre()*5-getRayon(), y5+getDiametre()*5, 3, false,1);
        triangle(x5+getDiametre()*2, y5+getDiametre()*4, 4, false,0);
        triangle(x5+getDiametre()*5+getRayon(), y5+getDiametre()*5, 3, false,1);
        for(int i=1;i<4;i++){
            Quadrilatere ligneMilieu = new Quadrilatere(x5-getRayon()*4+(getRayon()/2)+getRayon()*i, y5+getDiametre()*3+getDiametre()*i, getDiametre()*4-getDiametre()*i, getRayon());
            list.add(ligneMilieu);
        }

        int rep = 0;
        for (int k = 0 ; k < list.size() ; k++) {
            if(list.get(k) instanceof Pegs){
                rep+=list.get(k).getVie();
            }
        }
        nbVieTotal = rep;
    }
    //methodes
    public ArrayList<Obstacle> getList() {return list;}
    public int getNiveau() {return niveau;}
    public double getRayon(){
        Pegs p = new Pegs(0, 0);
        return p.getRayon();
    }

    public double getDiametre() {
        Pegs p = new Pegs(0, 0);
        return p.getDiametre();
    }

    public void setList(ArrayList<Obstacle> charge) {
        this.list = charge;
    }

    public boolean detruit(int i) {
        if (list.get(i).vie - 1 == 0) {
            return true;
        } else {
            list.get(i).vie--;
            return false;
        }
    }
    public static void setMilieu(double m) {
        milieu = m;
    }
    public int getNumNiveau(){
        return niveau;
    }
    public boolean NoMorePeg() {
        for (int i = 0 ; i < list.size() ; i++ ){
            if (list.get(i) instanceof Pegs){
                if(list.get(i).vie > 0 ) {
                    return false;
                }
            }
        }
        return true;
    }
}
