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
 * Methods follow this pattern with regard to the command line BNF:
 * 
 * retnVal meth( arg1, arg2, ... ) throws Xepn1, Xepn2
 * 
 * retnVal: non-void iff success implies particular info needed to generate output string.
 * 
 * Xepn1: might serve double duty, i.e. 2 of the BNF's error conditions rely on this Xepn1. Xepns
 * carry all info required for error reporting as per BNF.
 * 
 * @author wbhicks
 * 
 * @version
 */
public interface DotWorld {

FrameViz[] animHorizPath( Coord xmin, Coord xmax, Coord ypos );

boolean colorDot( DotName string, DotColor string2 );

int colorSegs( Coord lx, Coord ly, Coord ux, Coord uy, DotColor dc );

Dot createDot( DotName string, Coord coord, Coord coord2, Radius radius, DotColor string2 )
				throws DotNameDuplicateException, CoordDuplicateException,
				// next 2 could have been BadCodeXptns?
				DotNameFormatException, DotColorFormatException;

void createPath( DotName string, DotName string2 );

DotName deleteDot( DotName string ) throws NotFoundException, BadCodeException;

void deletePath( DotName string, DotName string2 );

FrameViz drawFrame( Coord coord, Coord coord2 );

void exit();

void initQT( QTMagnitude qtm ) throws RangeException, QTDuplicateException;

void listDots();

/** 
 * Works with self-loops too.
 * @param string
 * @param string2
 * @return
 * @throws NotFoundException
 * @throws IntersectionException
 * @throws BadCodeException
 */
DotName[] mapSeg( DotName string, DotName string2 ) throws NotFoundException, IntersectionException,
				BadCodeException;

SegWithDistance nearestSegToPoint( Coord coord, Coord coord2 ) throws NotFoundException;

BPTreeViz printBPTree() throws NotFoundException;

QTViz printQT() throws NotFoundException;

DotList rangeDots( DotName dn1, DotName dn2 ) throws DotNameRangeException;

BTreeOrder setBPTreeOrder( BTreeOrder bto ) throws BTreeInitException;

void setDrawMode( String mode, Integer xsize, Integer ysize, Integer step );

void shortestPath( DotName string, DotName string2 );

/**
 * Currently, this is what happens in the DotWorldVeriList implementation of this method: (1) the
 * seg is removed (i.e. from the QT and from segsOrderedBySpec.) (2) Neither Dot <i>per se</i> can be 
 * "removed" from the QT because Dots are not placed in the QT independently of segs. I.e. when the QT
 * has a ref to a Dot, it is only because it has a ref to a Seg incident to that Dot. If you tell the
 * QT to remove a Seg, it will take care of the Dot(s) as necessary. Therefore the QT is complete and
 * correct. (3) If a Dot has just had its incidence decremented to 0 (i.e. it has no more Segs), it
 * is removed from dotsByName and dotsByXY, and thus from the DotWorld.<p>
 * <p>
 * Note that this means that unmapSegment has the power to implicitly delete a Dot, so that deleteDot()
 * isn't the only way to remove a Dot from the DotWorld. In fact, in the DotWorldVeriList implementation,
 * deleteDot merely calls unmapSegment repeatedly!<p>
 * <p>
 * This implies (i) that Dots have no right to exist without Segs; (ii) an asymmetry between a Dot's birth,
 * at which time it may exist (in dotsByName and dotsByXY) indefinitely before a seg has been added to it,
 * and a Dot's death, which occurs as soon as it loses all its Segs; (iii) that deleteDot might as well be
 * named unmapAllSegments. Do these match the spec? TODO
 * @param string
 * @param string2
 * @return @throws NotFoundException
 * @throws BadCodeException
 */
DotName[] unmapSegment( DotName string, DotName string2 ) throws NotFoundException, BadCodeException;

} // end class
