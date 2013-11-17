package cmsc420.p3;

import cmsc420.exceptions.BTreeInitException;
import cmsc420.exceptions.BadCodeException;
import cmsc420.exceptions.CoordDuplicateException;
import cmsc420.exceptions.DotColorFormatException;
import cmsc420.exceptions.DotNameDuplicateException;
import cmsc420.exceptions.DotNameFormatException;
import cmsc420.exceptions.DotNameRangeException;
import cmsc420.exceptions.IntersectionException;
import cmsc420.exceptions.NotFoundException;
import cmsc420.exceptions.QTDuplicateException;
import cmsc420.exceptions.RangeException;


/**
 * @author wbhicks
 *
 * @version 
 */
public class DotWorldPM1QT implements DotWorld {

public DotWorldPM1QT() {
	super();
	// TODO Auto-generated constructor stub
}

public FrameViz[] animHorizPath( Coord xmin, Coord xmax, Coord ypos ) {
	// TODO Auto-generated method stub
	return null;
}

public boolean colorDot( DotName string, DotColor string2 ) {
	// TODO Auto-generated method stub
	return false;
}

public int colorSegs( Coord lx, Coord ly, Coord ux, Coord uy, DotColor dc ) {
	// TODO Auto-generated method stub
	return 0;
}

public Dot createDot( DotName string, Coord coord, Coord coord2, Radius radius, DotColor string2 )
				throws DotNameDuplicateException, CoordDuplicateException, DotNameFormatException,
				DotColorFormatException {
	// TODO Auto-generated method stub
	return null;
}

public void createPath( DotName string, DotName string2 ) {
// TODO Auto-generated method stub

}

public DotName deleteDot( DotName string ) throws NotFoundException, BadCodeException {
	// TODO Auto-generated method stub
	return null;
}

public void deletePath( DotName string, DotName string2 ) {
// TODO Auto-generated method stub

}

public FrameViz drawFrame( Coord coord, Coord coord2 ) {
	// TODO Auto-generated method stub
	return null;
}

public void exit() {
// TODO Auto-generated method stub

}

public void initQT( QTMagnitude qtm ) throws RangeException, QTDuplicateException {
// TODO Auto-generated method stub

}

public void listDots() {
// TODO Auto-generated method stub

}

public DotName[] mapSeg( DotName string, DotName string2 ) throws NotFoundException, IntersectionException,
				BadCodeException {
	// TODO Auto-generated method stub
	return null;
}

public SegWithDistance nearestSegToPoint( Coord coord, Coord coord2 ) throws NotFoundException {
	// TODO Auto-generated method stub
	return null;
}

public BPTreeViz printBPTree() throws NotFoundException {
	// TODO Auto-generated method stub
	return null;
}

public QTViz printQT() throws NotFoundException {
	// TODO Auto-generated method stub
	return null;
}

public DotList rangeDots( DotName dn1, DotName dn2 ) throws DotNameRangeException {
	// TODO Auto-generated method stub
	return null;
}

public BTreeOrder setBPTreeOrder( BTreeOrder bto ) throws BTreeInitException {
	// TODO Auto-generated method stub
	return null;
}

public void setDrawMode( String mode, Integer xsize, Integer ysize, Integer step ) {
// TODO Auto-generated method stub

}

public void shortestPath( DotName string, DotName string2 ) {
// TODO Auto-generated method stub

}

public DotName[] unmapSegment( DotName string, DotName string2 ) throws NotFoundException, BadCodeException {
	// TODO Auto-generated method stub
	return null;
}

}
