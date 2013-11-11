package vuedata

import scala.xml.MetaData
import tasseldata.TasselObj

object VueNode {
	
	/**
	 * Called only from VueChild's apply - which is good
	 * 
	 * @param elem
	 * @return None iff the elem wasn't equiv to a VUE <child> with type="node", or 
	 * else a Some wrapping the VueNode that was constructed from the elem. Side effect: 
	 * That VueNode is added to the map and processed recursively; i.e. all VUE resources 
	 * under it are added to the map. TODO
	 */
  def apply(elem: scala.xml.Elem): Option[VueNode] = {
		var o: Option[VueNode] = null // avoiding None here, since that's not what None means	
		if (elem.attribute("label").isDefined) { // why disallow unlabelled nodes? Why not regard as wildcards?
			require("node".equalsIgnoreCase(elem.attribute("http://www.w3.org/2001/XMLSchema-instance", "type").get.first.toString))
			val itsID: Int = cleaning.Cleaner.parseVueID(elem)
			val itsVueLabel: String = elem.attribute("label").get.first.toString
			val itsShapeOpt: Option[VueShape] = vuedata.VueShape((elem \ "shape").first.asInstanceOf[scala.xml.Elem], itsID, itsVueLabel)
			val itsShape = itsShapeOpt.getOrElse(new vuedata.VueShape(181818, "placeholder-cuz-node-lacks-shape", "mystery-shape"))
			val vn: VueNode = new vuedata.VueNode(
					itsID, 
					itsVueLabel, 
					itsShape)
			// populate vn's asChildlessScElem field:
			var freshMD: scala.xml.MetaData = elem.attributes.filter(VueChild.belongsToVueChildAndIsAttribToKeep(itsID)) // ditch the cleaning.Cleaner.parseVueID(elem)) TODO
	    // elem.scope makes the xsi prefix explicit, but no problem
	    vn.asChildlessScElem = new scala.xml.Elem(elem.prefix, "vuechild_node", freshMD, elem.scope, null) // null: no kids
			// tasselparsing.BasicTasselParser.parseVueNode( ...
			o = Some(vn)
		} else o = None // TODO error
		return o
  }
  
  def lua(args: Any*) = sllogging.BasicLogger.lua(args:_*)	
		
}

class VueNode(sourceVueID: Int, vueLabel: String, shape: vuedata.VueShape) extends VueChild(sourceVueID, vueLabel) {
	def shape(): vuedata.VueShape = shape	
	var isN3Statement: Boolean = false;

	if (!"DO_NOT_PERSIST".equalsIgnoreCase(VueResource.head(vueLabel, 14))) {
		if (vueLabel.startsWith("Class ")) {
	      /* "Class Foo" (i.e. Foo, a, Class) is NOT the desired recipient of the vueID. The vueID
	       * should be used instead to identify the resource Foo, since that is the meaning of any
	       * link to/from that node. STILL RELEVANT?
	       */
	    val theSubject: TasselObj = new TasselObj(VueResource.toURISafe(vueLabel.substring(6)), sourceVueID)
	  	TasselObj.addIfNew(theSubject)
	  	val thePredicate: TasselObj = tasseldata.TasselObj.builtIns.get("a").get
	    TasselObj.addIfNew(thePredicate)
	  	val theObject: TasselObj = tasseldata.TasselObj.builtIns.get("Class").get
	    TasselObj.addIfNew(theObject)
			TasselObj.addIfNew(new tasseldata.TasselAssertion(vueLabelAsURISafe, sourceVueID, theSubject, thePredicate, theObject))
		} else if (vueLabel.matches("[A-Z]\\w* \\S[\\s\\S]*")) {
			      // starts with a class name
      /* "Foo bar" (i.e. bar, a, Foo) is NOT the desired recipient of the vueID. The vueID
       * should be used instead to identify the resource bar, since that is the meaning of any
       * link to/from that node. To ensure this, add the simple resource before the statement:
       * (TODO make that choice more explicit - maybe 2 fields, sourceVueID and isIntendedOwnerOfSourceVueID)
       */
	    	// TODO may need to use declutteredVueNodeTail(vueLabel.substring(vueLabel.indexOf(' ') + 1), vueType, false)
	    val theSubject: TasselObj = new TasselObj(vueLabel.substring(vueLabel.indexOf(' ') + 1), sourceVueID)
	  	TasselObj.addIfNew(theSubject)
	  	val thePredicate: TasselObj = tasseldata.TasselObj.builtIns.get("a").get
	    TasselObj.addIfNew(thePredicate)
	  	val theObject: TasselObj = new tasseldata.TasselObj(vueLabel.substring(0, vueLabel.indexOf(' ')), sourceVueID)
	    TasselObj.addIfNew(theObject)
	    TasselObj.addIfNew(new tasseldata.TasselAssertion(vueLabelAsURISafe, sourceVueID, theSubject, thePredicate, theObject))
		} else {
	    TasselObj.addIfNew(new tasseldata.TasselObj(vueLabelAsURISafe, sourceVueID))
		}
	}
	
	override def toStr: String = { "VN:" + sourceVueID + ":" + vueLabel + ":" + shape.toStr }

	override def impliedDeclarationsAsString: String = {	
        if (isN3Statement) {
          "" // to declare a triple would be to prematurely reify it, so do nothing
        } else { // it's "just" a resource, not a triple as well, so a declaration might come in handy
          "#@DECL  :" + vueLabel
        }
	}
	
}
