package vuedata

import scala.collection.immutable.SortedMap
import scala.collection.immutable.TreeMap

object VueResource {
	def lua(args: Any*) = sllogging.BasicLogger.lua(args:_*)
	var allVRs: SortedMap[ Int, VueResource ] = new TreeMap[ Int, VueResource ]
//  var privateCounterTMPFORSHAPEONLY: Int = 70000 // hideous kludge
//  def nextPseudoIDTMPFORSHAPEONLY(): Int = { privateCounterTMPFORSHAPEONLY += 1; privateCounterTMPFORSHAPEONLY }

  /**
   * The entry point for creating instances of VueResource and its subclasses. The VueResource is 
   * added to allVRs in the VueResource constructor, not here, 
   * 
   * @param elem
   * @return None iff the elem wasn't equiv to a VUE resource, or else a Some wrapping the 
   * VueResource that was constructed from the elem. Side effect: That 
   * added to the map and processed recursively; i.e. all VUE resources under it are added 
   * to the map.
   */
  def apply(elem: scala.xml.Elem): Option[VueResource] = {
		var o: Option[VueResource] = null // avoiding None here, since that's not what None means
		if ("LW-MAP".equalsIgnoreCase(elem.label)) { // The Vue root elem
  		o = VueRoot(elem)
//  	} else if ("shape".equalsIgnoreCase(elem.label)) { // a vue <shape>
//  		o = VueShape(elem) //, nextPseudoIDTMPFORSHAPEONLY(), "AHAAAAA")
  	} else if ("child".equalsIgnoreCase(elem.label)) { // a vue <child>
  		o = VueChild(elem)
//  	} else if ("shape".equalsIgnoreCase(elem.label)) { // a vue <shape>
//  		o = VueShape(elem) //, nextPseudoIDTMPFORSHAPEONLY(), "AHAAAAA")
  	} else { // error
  		o = None
  	}
		if (o.isDefined) {
			val theVR: VueResource = o.get
			// recurse, and add kids to the VR instance's subVRs field
			val kidsAsScNodes: scala.xml.NodeSeq = elem.child // \ "_" would pick up only Elems, not Text as well
	      for (k <- kidsAsScNodes) {
	      	if (k.isInstanceOf[scala.xml.Elem]) {
	        	val o2: Option[VueResource] = VueResource(k.asInstanceOf[scala.xml.Elem]) // recurse
	        	if (o2.isDefined) {
	        		theVR.subVRs = ::(o2.get, theVR.subVRs) // add to the VR instance's subVRs field
	        	}
	        } else if (k.isInstanceOf[scala.xml.Text] && (new cleaning.Cleaner).cleanScText(k.asInstanceOf[scala.xml.Text], false).isDefined) {
	          // TODO make a VR out of it          	
	        }  	      	
	      }
		}
		return o
  }    

  def toStr: String = { 
//      val sb: StringBuilder = m.addString(new StringBuilder, "\n")
    val sb: StringBuilder = new StringBuilder
    
      sb.append("#  This fi\n\n" +
      		"# This is a Tur\n" +
          "# Above each tri\n" +
          "# (2) the vueid of the vu\n" +
          "# a node asse\n" +
          "# for the asse\n\n")
      sb.append("@prefi\n")
    
    
    var tmpStr: String = ""
    var tmpRsrcKind: String = ""
    for (i <- allVRs) {
  	  tmpStr = "# " + i._1 + " " + i._2.sourceVueTypeAsString + "\n" +
  	  	i._2.impliedDeclarationsAsString + "   #   " + i._2.toStr + "\n" +
  	  	"       :" + i._2.vueLabelAsURISafe + "  :hasAsCaptured  \"" + i._2.vueLabel + "\"  ."
  	  sb.append("\n" + tmpStr)
    }     
    sb.toString 
  }
  
  /**
   * Safest are: - _ . ~
   * Followed by the "sub-delims": ! $ & ' ) ( * + , ; =
   */
  def toURISafe(s: String): String = {
    val s2: String = s.trim
    val s3: String = s2.replaceAll("_", "__") // repl underscore with doubled underscore
    val s4: String = s3.replaceAll("\\s+", "_") // repl whitespace(s) with a single underscore
    val s99: String = s4.replaceAll("[^-a-zA-Z0-9_\\.\\~]", "!")
    if (s99.length < 36) return s99
    else {
      return s99.substring(0, 16) + "~~~" + s99.substring(s99.length - 16, s99.length)
    }
  }
  
    // here's an often-helpful little utility:
  def head(s: String, n: Int): String = {
    if (s.length < n) return s
    else return s.substring(0, n)
  }

}

class VueResource(sourceVueID: Int, vueLabel: String) {
	var subVRs: scala.collection.immutable.List[VueResource] = scala.collection.immutable.Nil
	var asScElem: scala.xml.Elem = new scala.xml.Elem("prefix165", "label165", scala.xml.Null, scala.xml.TopScope, null) // BAD
	var asChildlessScElem: scala.xml.Elem = new scala.xml.Elem("prefix165", "label165", scala.xml.Null, scala.xml.TopScope, null) // GOOD
	def sourceVueID(): Int = sourceVueID
	def vueLabel(): String = vueLabel
	val vueLabelAsURISafe: String = VueResource.toURISafe(vueLabel)

	// Any code here is run when constructor is called. (Note: "extends Foo(blah)" 
	// is what guarantees that a super constructor is called, and called first)
			// add to the static allVRs -- this is only line in proj that writes to allVRs
	if (!"DO_NOT_PERSIST".equalsIgnoreCase(VueResource.head(vueLabel, 14))) {
		VueResource.allVRs = VueResource.allVRs.updated(sourceVueID, this)
	}

  def sourceVueTypeAsString: String = {
    val matchResult: String = this.getClass.getName() match {
			case "vuedata.VueNode" => "N"
			case "vuedata.VueLink" => "L"
			case "vuedata.VueText" => "T"
      case _ => "?" // TODO ERROR
    }
    return matchResult
  }

	def impliedDeclarationsAsString: String = { return "" } // override in any subclass that permits declarations (i.e. only VueNode)
	
  def toStr: String = { "VR:" + sourceVueID + ":" + vueLabel }
  override def toString(): String = toStr
}

