package vuedata

object VueShape {
	
  /**
	 * Called only from VueResource's apply - which is good
	 * 
   * @param elem
   * @return None iff the elem wasn't equiv to a VUE <shape>, or else a Some wrapping the 
   * VueShape that was constructed from the elem. Side effect: That VueShape is 
   * added to the map and processed recursively; i.e. all VUE resources under it 
   * (can <shape> have any?) are added to the map.
   */
  def apply(elem: scala.xml.Elem): Option[VueShape] = {
  	apply(elem, 161616, "wasnt-told-what-owners-label-is")
  }    

  /**
	 * Called only from VueNode's apply - which is good
   */
  def apply(elem: scala.xml.Elem, ownerSourceVueID: Int, ownerVueLabel: String): Option[VueShape] = {
		var o: Option[VueShape] = null // avoiding None here, since that's not what None means
		if ("shape".equalsIgnoreCase(elem.label)) {
			o = Some(new vuedata.VueShape( ownerSourceVueID, 
																		ownerVueLabel, 
																		(elem \ "@{http://www.w3.org/2001/XMLSchema-instance}type").first.asInstanceOf[scala.xml.Text].toString()))
		} else o = None // TODO error
		return o
  }    
  
}

class VueShape(ownerSourceVueID: Int, ownerVueLabel: String, xsiType: String) extends VueResource(22000 + ownerSourceVueID, ownerVueLabel) {
	def xsiType(): String = xsiType
	override def toStr: String = { "<VS:" + ownerSourceVueID + ":" + ownerVueLabel + ":" + xsiType + ">" }

}
