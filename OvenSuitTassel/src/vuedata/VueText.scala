package vuedata

object VueText {

  def apply(elem: scala.xml.Elem): Option[VueText] = {
		var o: Option[VueText] = null // avoiding None here, since that's not what None means	
		if (elem.attribute("label").isDefined) { // disallow unlabelled VUE text (if such a thing even exists)
			require("text".equalsIgnoreCase(elem.attribute("http://www.w3.org/2001/XMLSchema-instance", "type").get.first.toString))
			val itsID: Int = cleaning.Cleaner.parseVueID(elem)
			val itsVueLabel: String = elem.attribute("label").get.first.toString
			val vt: VueText = new vuedata.VueText(
					itsID, 
					itsVueLabel)
			// populate vt's asChildlessScElem field:
			var freshMD: scala.xml.MetaData = elem.attributes.filter(VueChild.belongsToVueChildAndIsAttribToKeep(itsID)) // ditch the cleaning.Cleaner.parseVueID(elem)) TODO
	    // elem.scope makes the xsi prefix explicit, but no problem
	    vt.asChildlessScElem = new scala.xml.Elem(elem.prefix, "vuechild_text", freshMD, elem.scope, null) // null: no kids
			// tasselparsing.BasicTasselParser.parseVueText( ...
			o = Some(vt)
		} else o = None // TODO error
		return o
  }
  
//  def lua(args: Any*) = sllogging.BasicLogger.lua(args:_*)	
	
}

class VueText(sourceVueID: Int, vueLabel: String) extends VueChild(sourceVueID, vueLabel) {
	
	override def toStr: String = { "VT:" + sourceVueID + ":" + vueLabel }
	
}
