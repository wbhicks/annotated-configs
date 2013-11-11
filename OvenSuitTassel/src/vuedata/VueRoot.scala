package vuedata

object VueRoot {

	def apply(elem: scala.xml.Elem): Option[VueRoot] = {
		var o: Option[VueRoot] = null // avoiding None here, since that's not what None means	
		if ("LW-MAP".equalsIgnoreCase(elem.label)) {
			val v0: VueRoot = new VueRoot( 100000, "I am the LW-MAP indeed" )
			o = Some(v0)
		} else o = None // wasn't Vue root elem, apparently
		return o
	}    

}

class VueRoot(sourceVueID: Int, vueLabel: String) extends VueResource(sourceVueID, vueLabel) {
	override def toStr: String = { "V0:" + sourceVueID + ":" + vueLabel }
}
