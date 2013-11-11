package vuedata

import scala.xml.MetaData

object VueChild {
	
	/**
	 * Called only from VueResource's apply - which is good
	 * 
	 * @param elem
	 * @return None iff the elem wasn't equiv to a VUE <child>, or else a Some wrapping the 
	 * VueChild that was constructed from the elem. Side effect: That VueChild is 
	 * added to the map and processed recursively; i.e. all VUE resources under it 
	 * (whether <child> or not) are added to the map.
	 */
  def apply(elem: scala.xml.Elem): Option[VueChild] = {
		var o: Option[VueChild] = null // avoiding None here, since that's not what None means	
    val osn: Option[Seq[scala.xml.Node]] = elem.attribute("http://www.w3.org/2001/XMLSchema-instance", "type")    	
  	if (osn.isDefined) {
	    if ("node".equalsIgnoreCase(osn.get.first.toString)) { // or osn.value.toString ?
	    	o = vuedata.VueNode(elem)
	    } else if ("group".equalsIgnoreCase(osn.get.first.toString)) {
	    	o = None // vuedata.VueGroup(elem)
	    } else if ("text".equalsIgnoreCase(osn.get.first.toString)) {
	    	o = vuedata.VueText(elem)
	    } else if ("link".equalsIgnoreCase(osn.get.first.toString)) {
	    	o = vuedata.VueLink(elem)
	    } else o = None // TODO error
    } else o = None // wasn't a VUE <child>, apparently
		return o
  }    

  def belongsToVueChildAndIsAttribToKeep(vueIDForDebuggingOnly: Int): ((MetaData) => Boolean) = {
    (n: MetaData) => {
      vuereporting.BasicVueReporter.incr("xAttributeKeys", n.key)
      if ("ID".equalsIgnoreCase(n.key)) {
      	if (Integer.parseInt(n.value.toString) != vueIDForDebuggingOnly) lua("ERROR: 70")
      	true
      } else if ("type".equalsIgnoreCase(n.key)) { // expect n.value.toString.equalsIgnoreCase("link") or else "node" or now "text"
      	false
      } else if ("label".equalsIgnoreCase(n.key)) {
      	// harmless, but pointless (the label is kept in a field nowadays):
//        vuereporting.BasicVueReporter.incr("vueLabels", "vuechild_link::76_" + vueIDForDebuggingOnly + "::" + n.value.toString)
        true
      } else {
      	if (!vuereporting.BasicVueReporter.knownCruftAttributeKeysList.contains(n.key)) lua("ERROR: 79")
      	false
      }
    }
  }
      
  def lua(args: Any*) = sllogging.BasicLogger.lua(args:_*)	
	
}

class VueChild(sourceVueID: Int, vueLabel: String) extends VueResource(sourceVueID, vueLabel) {
	override def toStr: String = { "VC:" + sourceVueID + ":" + vueLabel }
}

