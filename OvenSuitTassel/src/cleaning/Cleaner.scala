// OK
package cleaning

import scala.xml.MetaData
import scala.collection.immutable.SortedMap

object Cleaner {
	
  /**
   * Safest are: - _ . ~
   * Followed by the "sub-delims": ! $ & ' ) ( * + , ; =
   */
  def toUriFrag(s: String): String = {
    val s2: String = s.trim
    val s3: String = s2.replaceAll("_", "__") // repl underscore with doubled underscore
    val s4: String = s3.replaceAll("\\s+", "_") // repl whitespace(s) with a single underscore
    val s99: String = s4.replaceAll("[^-a-zA-Z0-9_\\.\\~]", "!")
    if (s99.length < 36) return s99
    else {
      return s99.substring(0, 16) + "~~~" + s99.substring(s99.length - 16, s99.length)
    }
  }

  def parseVueID(elem: scala.xml.Elem): Int = {
		Integer.parseInt((elem \ "@ID").toString)
	}
  
  def parseVueLabel(elem: scala.xml.Elem): String = {
    var labelOfLink: String = "_" // kludge for the unlabelled links
    var tmp: Option[Seq[scala.xml.Node]] = elem.attribute("label")
    if (tmp.isDefined) labelOfLink = tmp.get.first.toString
    return labelOfLink
  	// or
//		(elem \ "@label").toString
		// or
//    		if (elem.attribute("label").isDefined) {
//    			vueLabel = elem.attribute("label").get.first.toString
		// or
//        var labelOfLink: String = "_" // kludge for the unlabelled links
//        var tmp: Option[Seq[scala.xml.Node]] = elem.attribute("label")
//        if (tmp.isDefined) labelOfLink = tmp.get.first.toString
		// or n.value.toString ?
	}
  
  def parseIntValOfSubElem(elem: scala.xml.Elem, s: String): Int = {
  	var result: Int = -1
    var theKids: scala.xml.NodeSeq = elem \ "_" // "elem.child" would pick up Text as well as Elems
    var found: Boolean = false
//    if (0 < theKids.length) {
      for (n <- theKids) {
        if (n.isInstanceOf[scala.xml.Elem]) {
        	if (s.equalsIgnoreCase(n.label)) {
            found = true // TODO use to short-circuit loop
            result = Integer.parseInt(n.child.first.toString)
        	}
        } // else ERROR: elem \ "_" should pick up Elems only
      }    	
//    } // else ERROR: original link node has no kids
  	result
	}

  def parseSubElem(elem: scala.xml.Elem, s: String): String = {
  	var result: String = "waiting to reassign MEH"
    var theKids: scala.xml.NodeSeq = elem \ "_" // "elem.child" would pick up Text as well as Elems
    var found: Boolean = false
//    if (0 < theKids.length) {
      for (n <- theKids) {
        if (n.isInstanceOf[scala.xml.Elem]) {
        	if (s.equalsIgnoreCase(n.label)) {
            found = true // TODO use to short-circuit loop
            result = n.child.first.toString
        	}
        } // else ERROR: elem \ "_" should pick up Elems only
      }    	
//    } // else ERROR: original link node has no kids
  	result
	}
  
  
} // end of object Cleaner

class Cleaner {

  def lua(args: Any*) = sllogging.BasicLogger.lua(args:_*)
  def lue(elem: scala.xml.Elem) = sllogging.BasicLogger.lue(elem)
  def incr(which: String): Int = vuereporting.BasicVueReporter.theVueTotals.incr(which)
  def incr(kind: String, which: String): Int = vuereporting.BasicVueReporter.incr(kind, which)

  def isKnownCruftNode(n: scala.xml.Node): Boolean = {
    var result: Boolean = false
    if (n.isInstanceOf[scala.xml.Elem]) {
      // TODO should be case-insens:
      result = (vuereporting.BasicVueReporter.knownCruftElementKeysList).contains(n.label)
      if (result) incr("ElemLabelKnownToBeCruft") else incr("ElemLabelNotKnownToBeCruft")
    } else if (n.isInstanceOf[scala.xml.Text]) {
      if ("".equals(n.asInstanceOf[scala.xml.Text].buildString(new StringBuilder).toString().trim)) {
        result = true // whitespace
        incr("PureWhitespace")
      } else incr("TextOtherThanPureWhitespace") // some other Text
    }
  return result
  }

  /**
   * called only from BKMK IATK
   * @precondition: theScLabel has already been reformulated as, e.g., vuechild_node, etc.
   */
  def isAttribToKeep(theScLabel: String, vcOpt: Option[vuedata.VueChild]): ((MetaData) => Boolean) = {
    (n: MetaData) => {
      incr("xAttributeKeys", n.key)
      if (theScLabel.startsWith("vuechild_") && "type".equalsIgnoreCase(n.key)) false
      else if (theScLabel.startsWith("vuechild_") && "label".equalsIgnoreCase(n.key)) {
      	if (vcOpt.isDefined) {
      		incr("vueLabels", theScLabel + "::" + vcOpt.get.sourceVueID + "::" + n.value.toString)
      	}
        // kludge
        if ("vuechild_node".equalsIgnoreCase(theScLabel)) {
	      	if (vcOpt.isDefined && !"DO_NOT_PERSIST".equalsIgnoreCase(vuedata.VueResource.head(vcOpt.get.vueLabel, 14))) {
	          tasselparsing.BasicTasselParser.parseVueLabel(theScLabel, vcOpt.get, n.value.toString)
	        }
        }
        /* A vuechild_text has 3 similar things: (1) an attribute with the key "label",
         * (2) a subelement with the name (label) "label", and (3) a subelement with 
         * the name (label) "richText". (3) is the most reliable and so (1) and (2)
         * should be pruned. Here, we'll toss (1):
         */
        !("vuechild_text".equalsIgnoreCase(theScLabel))
      } else !((vuereporting.BasicVueReporter.knownCruftAttributeKeysList).contains(n.key))
    }
  }

  /* if passed an Elem, must return a Some of an Elem, not of a non-Elem Node
   */
  def cleanScNode(n: scala.xml.Node, depth: Int): Option[scala.xml.Node] = {
    var o: Option[scala.xml.Node] = null // avoiding None here, since that's not what None means
    // 2b replaced by ...
  	var o2: Option[vuedata.VueResource] = null // avoiding None here, since that's not what None means
    if (n.isInstanceOf[scala.xml.Elem]) {
	    val elem: scala.xml.Elem = n.asInstanceOf[scala.xml.Elem]
	  	incr("xElementKeys", elem.label)
	  	if ((vuereporting.BasicVueReporter.knownCruftElementKeysList).contains(elem.label)) {
	  		o = None
	  	} else {
	  		if ("child".equalsIgnoreCase(elem.label)) {
	  			o = processVueChild(elem, depth)
	  		} else if ("LW-MAP".equalsIgnoreCase(elem.label)) { // is <LW-MAP>
	  			o2 = vuedata.VueResource(elem) // comment out to ensure empty obj_diag file
	  			o = processLWMAP(elem, 0) // kludging depth 0
	  		} else { // neither <child> nor <LW-MAP>, i.e. must be <shape> (for now)
	  			o = None // TODO
	  		}
	  	}	
    } else if (n.isInstanceOf[scala.xml.Group]) {
      lua("ERROR 177 found a scala.xml.Group!")
      o = Some(n)
    } else if (!n.isInstanceOf[scala.xml.SpecialNode]) {
      lua("ERROR 178 unknown subclass of scala.xml.Node!")
      o = Some(n)
    } else if (!n.isInstanceOf[scala.xml.Text]) {
      lua("ERROR 179 found a scala.xml.SpecialNode other than a scala.xml.Text!")
      o = Some(n)
    } else {
      o = cleanScText(n.asInstanceOf[scala.xml.Text], false)
    }
  	return o
  }

  def processVueChild(elem: scala.xml.Elem, depth: Int): Option[scala.xml.Elem] = {
  	require( (0 < depth) && (0 < cleaning.Cleaner.parseVueID(elem)) && "child".equalsIgnoreCase(elem.label), 
  					 depth + "," + cleaning.Cleaner.parseVueID(elem) + "," + elem.label )
			  /* it's a VUE "child" child, so re-label to avoid confusion ...
			   * i.e. will end with "node", "group", "text", or "link"
			   * (this attribute must be read _before_ cruft attribs are trimmed)
			   */
		var theVueTypeAsString: String = "was not reassigned"
		if (elem.attribute("http://www.w3.org/2001/XMLSchema-instance", "type").isDefined) {
					 theVueTypeAsString = elem.attribute("http://www.w3.org/2001/XMLSchema-instance", 
					 "type").get.first.toString
		}
		val theScLabel: String = "vuechild_" + theVueTypeAsString
		incr(theScLabel)
		if ("node".equalsIgnoreCase(theVueTypeAsString) || 
				"group".equalsIgnoreCase(theVueTypeAsString) || 
				"text".equalsIgnoreCase(theVueTypeAsString)) {  			
			val dELMEmatchResult: cleaning.VueTypeEnum.Value = theVueTypeAsString match {
				 case "node" => cleaning.VueTypeEnum.Node 
				 case "group" => cleaning.VueTypeEnum.Group 
				 case "text" => cleaning.VueTypeEnum.Text 
			}
			val vc: vuedata.VueChild = theVueTypeAsString match {
				 case "node" => new vuedata.VueNode(
						 cleaning.Cleaner.parseVueID(elem), 
						 "DO_NOT_PERSISTmystery_VueNode_188_" + cleaning.Cleaner.parseVueID(elem),
						 new vuedata.VueShape(193193, "placeholder-cuz-node-lacks-shape", "shape-for-mystery_VueNode_190_x"))
				 case "group" => new vuedata.VueNode( // TODO VueGroup
						 cleaning.Cleaner.parseVueID(elem), 
						 "DO_NOT_PERSISTmystery_VueNode_192_" + cleaning.Cleaner.parseVueID(elem),
						 new vuedata.VueShape(193193, "placeholder-cuz-node-lacks-shape", "shape-for-mystery_VueNode_190_x"))
				 case "text" => new vuedata.VueText(
						 cleaning.Cleaner.parseVueID(elem), 
						 "DO_NOT_PERSISTmystery_VueText_196_" + cleaning.Cleaner.parseVueID(elem))
			}			
			val theNonCruftKids: scala.xml.NodeBuffer = nonCruftKidsOfVuechild(elem, depth, dELMEmatchResult, false)
			// elem.scope makes the xsi prefix explicit, but no problem
			val tidiedElem: scala.xml.Elem = new scala.xml.Elem(
					 elem.prefix, 
					 theScLabel, 
					 elem.attributes.filter(isAttribToKeep(theScLabel, Some(vc))), // BKMK IATK - Some(vc) was vc
					 elem.scope, // elem.scope makes the xsi prefix explicit, but no problem
					 theNonCruftKids: _*)
			return Some(tidiedElem)									
		} else if ("link".equalsIgnoreCase(theVueTypeAsString)) {
				 return None // Some(vuedata.VueLink(elem).get.asChildlessScElem)
		} else { lua("ERROR 100"); return None }
  }

//  def processVueChildNode(elem: scala.xml.Elem, depth: Int): Option[scala.xml.Elem] = {
//  	
//  }
  
  
  def processLWMAP(elem: scala.xml.Elem, depth: Int): Option[scala.xml.Elem] = {
  	require( (0 == depth) && (0 == cleaning.Cleaner.parseVueID(elem)) && "LW-MAP".equalsIgnoreCase(elem.label) && (0 < elem.child.length), 
  					 depth + "," + cleaning.Cleaner.parseVueID(elem) + "," + elem.label + "," + elem.child.length ) // LW-MAP isn't a leaf
  	var theNonCruftKids: scala.xml.NodeBuffer = new scala.xml.NodeBuffer
	  val theKids: scala.xml.NodeSeq = elem.child // \ "_" would pick up only Elems, not Text as well
	  for (k <- theKids) {
		  val o: Option[scala.xml.Node] = cleanScNode(k, depth + 1)
		  if (o.isDefined) theNonCruftKids = theNonCruftKids &+ o.get
	  }
  	val tidiedElem: scala.xml.Elem = new scala.xml.Elem(
  			elem.prefix, 
  			elem.label, 
  			elem.attributes, // TODO elem.attributes.filter(isAttribToKeep(elem.label, vc)),
  			elem.scope, // elem.scope makes the xsi prefix explicit, but no problem
  			theNonCruftKids: _*)
  	return Some(tidiedElem)								
  }

  def nonCruftKidsOfVuechild(elem: scala.xml.Elem, depth: Int, matchResult: cleaning.VueTypeEnum.Value, 
      debug: scala.Boolean): scala.xml.NodeBuffer = {
    var theKids: scala.xml.NodeSeq = elem.child // \ "_" would pick up only Elems, not Text as well
    var theNonCruftKids: scala.xml.NodeBuffer = new scala.xml.NodeBuffer
//    if (0 < theKids.length) {
      for (k <- theKids) {
        if (cleaning.VueTypeEnum.Node == matchResult || cleaning.VueTypeEnum.Group == matchResult) {
          val o: Option[scala.xml.Node] = cleanScNode(k, depth + 1)
          if (o.isDefined) {
            theNonCruftKids = theNonCruftKids &+ o.get
          }
        } else if (cleaning.VueTypeEnum.Text == matchResult) {
          if (debug) {
            val labelAsAttribute: String = elem.attribute("label").get.first.toString
            val labelAsElemPrepNS1: scala.xml.NodeSeq = elem \ "label"
            val labelAsElemPrepE: scala.xml.Elem = labelAsElemPrepNS1.first.asInstanceOf[scala.xml.Elem]
            val labelAsElemPrepNS2: scala.xml.NodeSeq = labelAsElemPrepE.child
            val labelAsElem: String = labelAsElemPrepNS2.first.toString
            if (labelAsAttribute.length != labelAsElem.length) {
              lua("labelAsAttribute (" + labelAsAttribute.length + "): [__" + labelAsAttribute + 
                  "__], labelAsElem (" + labelAsElem.length + "): [__" + labelAsElem + "__]")	
            }
          }
          if (!k.isInstanceOf[scala.xml.Elem]) {
            if (!k.isInstanceOf[scala.xml.Text]) {
              lua("ERROR 173 - kid of a VuechildText that's neither scala.xml.Elem nor scala.xml.Text!")
            } else { // is Text
              if (!("".equals(k.text.trim))) {
                lua("ERROR 177 - Text isn't just whitespace")
              } // else is whitespace only, omit
            }
          } else if ("richText".equalsIgnoreCase(k.label)) { // k's a scElem and a richText
            val o: Option[scala.xml.Node] = cleanVuerichtext(k.asInstanceOf[scala.xml.Elem], 
                depth + 1, debug)
            if (o.isDefined) {
              theNonCruftKids = theNonCruftKids &+ o.get
              val vText: vuedata.VueText = new vuedata.VueText(
													 cleaning.Cleaner.parseVueID(elem), 
													 "DO_NOT_PERSISTmystery_VueText_279_" + cleaning.Cleaner.parseVueID(elem))              
              tasselparsing.BasicTasselParser.parseVueRichText(vText,
                  o.get.asInstanceOf[scala.xml.Elem].child.first.asInstanceOf[scala.xml.Text].toString)
            }
          } else if (!("label".equalsIgnoreCase(k.label))) { // k's a scElem, but neither richText nor label
            val o: Option[scala.xml.Node] = cleanScNode(k, depth + 1)
            if (o.isDefined) {
              theNonCruftKids = theNonCruftKids &+ o.get
            }
          } // else lua("this kid's label is 'label', so omitting it")
        } else if (cleaning.VueTypeEnum.Link == matchResult) {
//	      	if (k.isInstanceOf[scala.xml.Elem] && "ID2".equalsIgnoreCase(k.label)) { // too minor for a VueResource, & extracted earlier
//	      	} else if (k.isInstanceOf[scala.xml.Elem] && "ID1".equalsIgnoreCase(k.label)) { // too minor for a VueResource, & extracted earlier
//	        } else if (k.isInstanceOf[scala.xml.Elem]) { // ignore?
//	        } else if (k.isInstanceOf[scala.xml.Text] && (new cleaning.Cleaner).cleanScText(k.asInstanceOf[scala.xml.Text], false).isDefined) {
//	          lua("ERROR: original link node has a non-whitespace text child (rather than grandchild)")          	
//	        } else if (k.isInstanceOf[scala.xml.Text]) { // whitespace, so ignore
//	        } else lua("ERROR: original link node has mystery kids")
        } else lua("ERROR: bad code")
      }
//    } // else was leaf node, so theNonCruftKids should remain empty
  return theNonCruftKids
  }

  def cleanVuerichtext(elem: scala.xml.Elem, depth: Int, 
      debug: scala.Boolean): Option[scala.xml.Node] = {
    var theKids: scala.xml.NodeSeq = elem.child // \ "_" would pick up only Elems, not Text as well
    if (!(1 == theKids.length)) {
      lua("ERROR 200 in cleanVuerichtext: theKids.length == " + theKids.length)
      return None
    } else if (!(theKids.first.isInstanceOf[scala.xml.Text])) {
      lua("ERROR 202 in cleanVuerichtext")	
      return None
    }
    val t: scala.xml.Text = theKids.first.asInstanceOf[scala.xml.Text]
    if (debug) {
      val s1: String = t.buildString(false)
      val s2: String = t.data
      val s3: String = t.mkString
      val s4: String = t.text
      val s5: String = t.toString
      val s1l: Int = s1.length
      val s2l: Int = s2.length
      val s3l: Int = s3.length
      val s4l: Int = s4.length
      val s5l: Int = s5.length
      if ((s1l == s3l) && (s1l == s5l) && (s2l == s4l) && (s1l > s2l)) {
        lua("in cleanVuerichtext: all lengths as expected: " + s1l + " > " + s2l)
        lua("s1/3/5 == [__" + s1 + "__], s2/4 = [__" + s2 + "__]")
      } else lua("ERROR 217")			
    }
    // can use techniques of s1, s3, or s5 to get the escaped version, but not s2 or s4
    val escapedVuerichtextPrePrune: String = theKids.first.asInstanceOf[scala.xml.Text].toString
    val escapedVuerichtextPostPrune: String = cleanEscapedString(escapedVuerichtextPrePrune, debug)
    val replacementText: scala.xml.Text = new scala.xml.Text(escapedVuerichtextPostPrune)
    // TODO use replacementText
    return Some(elem) // a Vue element named richText is always a leaf and has nothing to prune
  }

  def cleanEscapedString(s: String, debug: scala.Boolean): String = {
    //		val s1: String = s.replaceAll("&lt;", "<")
    //		val s2: String = s1.replaceAll("&gt;", ">")
    //		val s3: String = s2.replaceAll("<[^>]+>", "")
    //		if (debug) lua("cleanEscapedString gives [__" + s3 + "__]")
    return s.trim
  }
    
  def cleanScText(t: scala.xml.Text, debug: scala.Boolean): Option[scala.xml.Text] = {
    if (debug) {
      /* due to whitespace processing, sometimes s1 > s2 (in such cases,
       * s1 == s3 == s5 and s2 == s4). Normally all 5 are equal.
       */
      val s1: String = t.buildString(false)
      val s2: String = t.data
      val s3: String = t.mkString
      val s4: String = t.text
      val s5: String = t.toString
      val s1l: Int = s1.length
      val s2l: Int = s2.length
      val s3l: Int = s3.length
      val s4l: Int = s4.length
      val s5l: Int = s5.length
      if ((s1l == s2l) && (s1l == s3l) && (s1l == s4l) && (s1l == s5l)) {
        lua("all lengths == " + s5l + ", [" + s5 + "]")
      } else lua("NOTE: all lengths not equal: "+s1l+", "+s2l+", "+s3l+", "+s4l+", "+s5l)
      if (null == s4) { // could have been any sibling method
        lua("ERROR 219 weird")
        return None
      }
      if (0 == s4.length) {
        lua("ERROR 224 weird")
        return None
      }		
    }
    val s: String = t.text 
    if ("".equals(s.trim)) {
      incr("PureWhitespace")
      return None
    } else { // so far, these have all been the numeric vals of the ID1 and ID2 tags
      incr("TextOtherThanPureWhitespace")
      val replacementText: scala.xml.Text = new scala.xml.Text(s.trim)
      val o: Option[scala.xml.Text] = Some(replacementText)
      return o
    }
  }

}
