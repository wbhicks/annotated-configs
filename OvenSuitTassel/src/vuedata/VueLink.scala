package vuedata

import scala.xml.MetaData
import scala.collection.immutable.SortedMap
import scala.collection.immutable.TreeMap

object VueLink {

	/**
	 * Called only from VueChild's apply - which is good
	 * 
	 * @param elem
	 * @return None iff the elem wasn't equiv to a VUE <child> with type="link", or 
	 * else a Some wrapping the VueLink that was constructed from the elem. Side effect: 
	 * That VueLink is added to the map and processed recursively; i.e. all VUE resources 
	 * under it are added to the map.
	 * 
	 * 1. const a VL. 2. add this VL to m. 3. clean the elem. 4. set VL.asChildlessScElem to cleaned elem. 5. obtain all kids as VRs.
	 * 6. set VL.kidsAsVRs to those kids. 7. tasselparsing.BasicTasselParser.parseVueLink
	 */
  def apply(elem: scala.xml.Elem): Option[VueLink] = {
		var o: Option[VueLink] = null // avoiding None here, since that's not what None means	
		if (elem.attribute("label").isDefined) { // TODO but why not allow unlabelled links?
//			var labelOfLink: String = "_" // kludge for the unlabelled links
			require("link".equalsIgnoreCase(elem.attribute("http://www.w3.org/2001/XMLSchema-instance", "type").get.first.toString))
//		require(-1 != cleaning.Cleaner.parseIntValOfSubElem(elem, "ID2") || -1 != cleaning.Cleaner.parseIntValOfSubElem(elem, "ID1")) // up to you, ok if both are missing?
			val vl: VueLink = new VueLink(
					 cleaning.Cleaner.parseVueID(elem), 
					 cleaning.Cleaner.parseVueLabel(elem), 
					 vuedata.VueResource.allVRs.get(cleaning.Cleaner.parseIntValOfSubElem(elem, "ID1")), 
					 vuedata.VueResource.allVRs.get(cleaning.Cleaner.parseIntValOfSubElem(elem, "ID2"))			
			)
			// populate vl's asChildlessScElem field:
			var freshMD: scala.xml.MetaData = elem.attributes.filter(VueChild.belongsToVueChildAndIsAttribToKeep(cleaning.Cleaner.parseVueID(elem))) // ditch the cleaning.Cleaner.parseVueID(elem)) TODO
			val kidsAsScNodes: scala.xml.NodeSeq = elem.child // \ "_" would pick up only Elems, not Text as well
//	    if (0 < kidsAsScNodes.length) {
	      for (k <- kidsAsScNodes) {
		      	if (k.isInstanceOf[scala.xml.Elem] && "ID2".equalsIgnoreCase(k.label)) { // too minor for a VueResource, & extracted earlier
		      		freshMD = new scala.xml.PrefixedAttribute(elem.prefix, "synth_id2", k.child.first.toString, freshMD)
		      	} else if (k.isInstanceOf[scala.xml.Elem] && "ID1".equalsIgnoreCase(k.label)) { // too minor for a VueResource, & extracted earlier
		      		freshMD = new scala.xml.PrefixedAttribute(elem.prefix, "synth_id1", k.child.first.toString, freshMD)	      		
		        } else if (k.isInstanceOf[scala.xml.Elem]) { // ignore? does a link have other interesting elem kids?
		        } else if (k.isInstanceOf[scala.xml.Text] && (new cleaning.Cleaner).cleanScText(k.asInstanceOf[scala.xml.Text], false).isDefined) {
		          lua("ERROR: original link node has a non-whitespace text child (rather than grandchild)")          	
		        } else if (k.isInstanceOf[scala.xml.Text]) { // whitespace, so ignore
		        } else lua("ERROR: original link node has mystery kids")      	      	
	      }
//	    } // else elem had no kids
	    // elem.scope makes the xsi prefix explicit, but no problem
	    vl.asChildlessScElem = new scala.xml.Elem(elem.prefix, "vuechild_link", freshMD, elem.scope, null) // null: no kids
//		if (-1 != cleaning.Cleaner.parseIntValOfSubElem(elem, "ID2") && -1 != cleaning.Cleaner.parseIntValOfSubElem(elem, "ID1")) {
			tasselparsing.BasicTasselParser.parseVueLink(vl.id1, vl, vl.id2)
//					cleaning.Cleaner.parseVueID(elem), 
//					cleaning.Cleaner.parseIntValOfSubElem(elem, "ID1"), 
//					cleaning.Cleaner.parseVueLabel(elem), 
//					cleaning.Cleaner.parseIntValOfSubElem(elem, "ID2"))
//		}
			o = Some(vl)
		} else o = None // TODO error
		return o
  }

  def lua(args: Any*) = sllogging.BasicLogger.lua(args:_*)	

} // end of object VueLink

class VueLink(sourceVueID: Int, vueLabel: String, id1: Option[VueResource], id2: Option[VueResource]) extends VueChild(sourceVueID, vueLabel) {
	def id1(): Option[VueResource] = id1
	def id2(): Option[VueResource] = id2		
	override def toStr: String = { "VL:" + sourceVueID + ":" + vueLabel + ":" +
		( if (id1.isDefined) { id1.get.sourceVueID } else { "*" } ) + ":" +
		( if (id2.isDefined) { id2.get.sourceVueID } else { "*" } ) }
}
