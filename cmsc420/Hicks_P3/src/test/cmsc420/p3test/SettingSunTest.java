package cmsc420.p3test;

import junit.framework.TestCase;


/**
 * @author wbhicks
 *
 * @version 
 */
public class SettingSunTest extends TestCase {

    public abstract class Piece {
        int id;
    }

    public class Empty extends Piece {
        public String toString() {
            return "e";
        }
    }

	public class Square extends Piece {
        public String toString() {
        	return "s";
        }
    }

    public class Rectangle extends Piece {
        public String toString() {
            return "r";
    }
    }
       
     Square bigRed;
     Square[] smallBlue;
     Rectangle hYellow;
     Rectangle[] vYellow;
     Piece[][] map;
     char[][] picture;

    public SettingSunTest() {
        map = new Piece[4][5];
        for (int x = 0; x < 4; x++ ) {
            for (int y = 0; y < 5; y++ ) {
            	map[x][y] = new Empty();
            }
        }

        bigRed = new Square();    
        hYellow = new Rectangle();   
        smallBlue = new Square[4];
        vYellow = new Rectangle[4];
        for (int i = 0; i < 4; i++ ) {
            smallBlue[i] = new Square();
        }
        for (int i = 0; i < 4; i++ ) {
            vYellow[i] = new Rectangle();
        }
        map[1][3] = smallBlue[0];
        map[2][3] = smallBlue[1];
        map[1][4] = smallBlue[2];
        map[2][4] = smallBlue[3];
        map[1][0] = bigRed;
        map[1][2] = hYellow;
        map[0][0] = vYellow[0];
        map[3][0] = vYellow[1];
        map[0][3] = vYellow[2];
        map[3][3] = vYellow[3];
        
        picture = new char[12][15];
   
    }
    
    public static void main( String[] args ) {
        SettingSunTest s = new SettingSunTest();
        //System.out.println( s.map[1][3]);
        s.drawPicture();
        System.out.println("hi");
    }
    
    public void drawPicture() {
        for (int x = 0; x < 4; x++ ) {
            for (int y = 0; y < 5; y++ ) {
                picture[x][y] = ' ';
            }
        }
    	for (int x = 0; x < 4; x++ ) {
            for (int y = 0; y < 5; y++ ) {
            	System.out.print( this.map[x][y].toString());
               
            }
            System.out.println();
        }
    }
}
