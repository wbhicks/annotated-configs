package tasselparsing

import scala.collection.immutable.SortedMap
import scala.collection.immutable.TreeMap
import vuedata.VueLink
import vuedata.VueResource

object BasicTasselParser {

  object theTasselResources {
    var m: SortedMap[ Int, TasselResource ] = new TreeMap[ Int, TasselResource ]
    var privateCounter: Int = 50000 // hideous kludge
    def nextID(): Int = { privateCounter += 1; privateCounter }
    /** When true, only the first-encountered instance of an resource is stored normally.
     *  Subsequent resources that match it will instead be stored as "aliases".
     *  Warning: When true, an edge (VUE link) with an alias at an endpoint will not yield
     *  meaningful information. TODO write code to handle link to/from alias
     */
    val storeDupeAsAlias: Boolean = false
    
    def toStr: String = { 
      var sb: StringBuilder = new StringBuilder
      sb.append("# This is a Turtle document, EXCEPT that @decl is a non-Turtle kludge. \n" +
          "# Above each triple, its 1st debug line consists of (1) its ID from the triple store, \n" +
          "# (2) the vueid of the vuechild from which it was derived (usually the same as (1), but \n" +
          "# a node asserting 'Foo bar' will have its vueid used for the declaration of bar, NOT \n" +
          "# for the assertion that bar is of class Foo), and (3) the type of that vuechild.\n\n")
      sb.append("@prefix : <http://no.such.domain/example/>  .\n")
      for (i <- m) {
        sb.append(
          if (i._1 == i._2.sourceVueID) {
            "\n# " + i._2.sourceVueID + " " + i._2.sourceVueTypeToStringDEPREC + "\n" // use VR's instead
          } else { // the resource (prob a triple) didn't "deserve" ownership of the orig vueid
            "\n# " + i._1.toString + " (derived from " + i._2.sourceVueID + " " + 
            i._2.sourceVueTypeToStringDEPREC + ")\n"
          }
        )
        if (i._2.isInstanceOf[TasselStatement]) {
          // to declare a triple would be to prematurely reify it, so do nothing
          // sb.append("#@decl  :" + i._2.uriFrag + "\n")
        } else { // it's "just" a resource, not a triple as well, so a declaration might come in handy
          sb.append("#@DECL  :" + i._2.uriFrag + "\n")
        }
        if (i._2.asCaptured.length > 0) {
          sb.append("       :" + i._2.uriFrag + "  :hasAsCaptured  \"" + i._2.asCaptured + "\"  .\n")
          if (i._2.isInstanceOf[TasselStatement]) {
            lua("ERROR 35: TasselStatement with non-empty asCaptured")
          }
        } else { // must be a triple, but this is a kludge! TODO data-level enforcement
          if (i._2.isInstanceOf[TasselStatement]) {
            sb.append("  :" + i._2.asInstanceOf[TasselStatement].subj + 
                      "  :" + i._2.asInstanceOf[TasselStatement].pred + // .formatted("%1$10s") +
                      "  :" + i._2.asInstanceOf[TasselStatement].obj + 
                      "  .\n")
          } else lua("ERROR 35: non-TasselStatement with empty asCaptured")
        }
      }
      sb.toString
    }
/*
    def addIfNew(args: String*) : String = {
      if (m.exists(scalaPredForAdd(args: _*))) { // already declared
        if (!storeDupeAsAlias) {
          val id: Int = nextID()
          val uriFrag: String = asUriFrag(id, args: _*)
          m = m.updated(id, TasselResource(uriFrag, args: _*))
          return uriFrag
        } else { // else no point in adding an alias since we aren't returning an ID
          return "UNKNOWN63_uriFrag"
        }
      } else {
        val id: Int = nextID()
        val uriFrag: String = asUriFrag(id, args: _*)
        m = m.updated(id, TasselResource(uriFrag, args: _*)) // new arrival
        return uriFrag
      }
    }
*/    
    // TODO hideous kludge - vc.id had better be less than initial val of privateCounter
    def addIfNew(vc: vuedata.VueChild, args: String*) : String = {
      val o: Option[ Tuple2[ Int, TasselResource ] ] = m.find(scalaPredForAdd(args: _*))
      var vueType: cleaning.VueTypeEnum.Value = cleaning.VueTypeEnum.Unknown
      if (vc.isInstanceOf[vuedata.VueNode]) {
      	vueType = cleaning.VueTypeEnum.Node
      } else if (vc.isInstanceOf[vuedata.VueLink]) {
      	vueType = cleaning.VueTypeEnum.Link
      }
      if (o.isDefined) { // a "close enough" resource already exists, regardless of its ID
        if (storeDupeAsAlias) {
          // add an alias IFF it can have the desired ID (otherwise, no point in adding an alias)
          val existingID: Int = o.get._1.asInstanceOf[Int]
          if (m.contains(vc.sourceVueID)) {
            lua("ERROR 55: proposed ID " + vc.sourceVueID + 
                " was taken! So ... no point in adding an alias. Doing nothing.")
            return "UNKNOWN82_uriFrag"
          } else {
            val uriFrag: String = asUriFrag(vc.sourceVueID, args: _*)
            m = m.updated(vc.sourceVueID, TasselResource(vueType, vc.sourceVueID, uriFrag, 
                "__alias_to_ENTITY_id_" + existingID))
            return uriFrag
          }
        } else { // we don't care that a "close enough" resource already exists
          if (m.contains(vc.sourceVueID)) {
            val fallbackID: Int = nextID()
            lua("INFO 60: proposed ID " + vc.sourceVueID + " was taken. Using fallbackID " + fallbackID + 
                " instead.")
            val uriFrag: String = asUriFrag(fallbackID, args: _*)
            m = m.updated(fallbackID, TasselResource(vueType, vc.sourceVueID, uriFrag, args: _*))  
            return uriFrag
          } else {
            val uriFrag: String = asUriFrag(vc.sourceVueID, args: _*)
            m = m.updated(vc.sourceVueID, TasselResource(vueType, vc.sourceVueID, uriFrag, args: _*)) 
            return uriFrag
          }
        }
      } else { // i.e., it's a new arrival
        if (m.contains(vc.sourceVueID)) {
          val fallbackID: Int = nextID()
//          lua("INFO 67: proposed ID " + vc.sourceVueID + " was taken. Using fallbackID " + fallbackID + " instead.")
          val uriFrag: String = asUriFrag(fallbackID, args: _*)
          m = m.updated(fallbackID, TasselResource(vueType, vc.sourceVueID, uriFrag, args: _*))   
          return uriFrag
        } else {
          val uriFrag: String = asUriFrag(vc.sourceVueID, args: _*)
          m = m.updated(vc.sourceVueID, TasselResource(vueType, vc.sourceVueID, uriFrag, args: _*))
          return uriFrag
        }
      }
    }
    
    /** note that you don't care what kind of vuechild it came from, so if a vue node
     *  and vue text both say "foo", it still appears only once
     */    
    def scalaPredForAdd(args: String*): Tuple2[ Int, TasselResource ] => Boolean = {
      n: Tuple2[ Int, TasselResource ] => {
        if (1 == args.length) {
          n._2.asCaptured.equalsIgnoreCase(args.apply(0))
        } else { // a statement
          if (n._2.isInstanceOf[TasselStatement]) {
            n._2.asInstanceOf[TasselStatement].subj.equalsIgnoreCase(args.apply(0)) && 
            n._2.asInstanceOf[TasselStatement].pred.equalsIgnoreCase(args.apply(1)) && 
            n._2.asInstanceOf[TasselStatement].obj.equalsIgnoreCase(args.apply(2))
          } else false
        }
      }
    }
    
    def asUriFrag(id: Int, args: String*): String = {
      if (1 == args.length) { // i.e. only asCaptured
        asUriFragThird(args.apply(0)) + "_" + id
      } else { // a statement
        asUriFragThird(args.apply(0)) + asUriFragThird(args.apply(1)) + 
            asUriFragThird(args.apply(2)) + "_" + id
        // i.e. subj, pred, obj
      }
    }

    def asUriFragThird(s: String): String = {
      if (s.startsWith("__vueID_")) { // i.e., it isn't final
        return "qk161_" + "~" + s.substring(8)
      } else {
    	  return cleaning.Cleaner.toUriFrag(s)
      }
    }
 
    def makeFinal: String = {
      var sb: StringBuilder = new StringBuilder
      var numTriplesWithZeroUnresolvedRefs: Int = 0
      var numTriplesWithOneUnresolvedRefs: Int = 0
      var numTriplesWithTwoUnresolvedRefs: Int = 0
      var numTriplesWithThreeUnresolvedRefs: Int = 0 // how can a link label be unresolved???
      var numOfUnresolvedRefsInCurrentTriple: Int = 0
      for (numOfThisPass <- 1 to 3) {
        numTriplesWithZeroUnresolvedRefs = 0 // reset counter
        numTriplesWithOneUnresolvedRefs = 0 // reset counter
        numTriplesWithTwoUnresolvedRefs = 0 // reset counter
        numTriplesWithThreeUnresolvedRefs = 0 // reset counter
        for (i <- m) {
          if (i._2.isInstanceOf[TasselStatement]) {
            numOfUnresolvedRefsInCurrentTriple = 0 // reset counter
            if (i._2.asInstanceOf[TasselStatement].subj.startsWith("__vueID_")) { 
              // i.e., i's subj isn't final
              numOfUnresolvedRefsInCurrentTriple += 1
            }
            if (i._2.asInstanceOf[TasselStatement].pred.startsWith("__vueID_")) { 
              // how can a link label be unresolved???
              numOfUnresolvedRefsInCurrentTriple += 1
              lua("WARNING: how can a link label be unresolved???")
            }
            if (i._2.asInstanceOf[TasselStatement].obj.startsWith("__vueID_")) { 
              // i.e., i's obj isn't final
              numOfUnresolvedRefsInCurrentTriple += 1
            }
            if (0 == numOfUnresolvedRefsInCurrentTriple) numTriplesWithZeroUnresolvedRefs += 1
            else if (1 == numOfUnresolvedRefsInCurrentTriple) numTriplesWithOneUnresolvedRefs += 1
            else if (2 == numOfUnresolvedRefsInCurrentTriple) numTriplesWithTwoUnresolvedRefs += 1
            else if (3 == numOfUnresolvedRefsInCurrentTriple) numTriplesWithThreeUnresolvedRefs += 1
          } // else not a statement, so ignore
        }
        sb.append("numOfThisPass: " + numOfThisPass + "\n")
        sb.append("numTriplesWithZeroUnresolvedRefs: " + numTriplesWithZeroUnresolvedRefs + "\n")
        sb.append("numTriplesWithOneUnresolvedRefs: " + numTriplesWithOneUnresolvedRefs + "\n")
        sb.append("numTriplesWithTwoUnresolvedRefs: " + numTriplesWithTwoUnresolvedRefs + "\n")
        sb.append("numTriplesWithThreeUnresolvedRefs: " + numTriplesWithThreeUnresolvedRefs + "\n")
        sb.append("     Now making pass " + numOfThisPass + " ... \n")
        makeFinalSinglePass
      }
      return sb.toString
    }
    
    def makeFinalSinglePass: String = { 
      // process links
      var candidateForTheSubj: String = "" // PLACEHOLDER_candidateForTheSubj
      var candidateForTheObj: String = "" // PLACEHOLDER_candidateForTheObj
      var currentTriplePointedTo: Int = -1 // i.e. none
      for (i <- m) {
        if (i._2.isInstanceOf[TasselStatement]) {
          candidateForTheSubj = "" // reset it
          candidateForTheObj = "" // reset it
          // handle i's theSubj:
          if (i._2.asInstanceOf[TasselStatement].subj.startsWith("__vueID_")) { 
            // i.e., i's theSubj isn't final
  //          lua("i._2.subj == " + i._2.subj)
            currentTriplePointedTo = 
                Integer.parseInt(i._2.asInstanceOf[TasselStatement].subj.substring(8))
            // ... but does it exist? Let's test ...
            if (m.contains(currentTriplePointedTo)) {
              // for now, we'll handle resources only, not "real" triples:
              if (m.apply(currentTriplePointedTo).isInstanceOf[TasselStatement]) {
                // points to a "real" triple, not a rsrc - do nothing
              } else { // it's a rsrc, not a "real" triple
                candidateForTheSubj = m.apply(currentTriplePointedTo).uriFrag // was .asCaptured
              }
            } // else it doesn't exist, so obv. do nothing (xcpt wonder)
          } // else i's theSubj already final
          // handle i's theObj:
          if (i._2.asInstanceOf[TasselStatement].obj.startsWith("__vueID_")) { 
            // i.e., i's theObj isn't final
  //          lua("i._2.obj == " + i._2.obj)
            currentTriplePointedTo = 
                Integer.parseInt(i._2.asInstanceOf[TasselStatement].obj.substring(8))
            // ... but does it exist? Let's test ...
            if (m.contains(currentTriplePointedTo)) {
              // for now, we'll handle resources only, not "real" triples:
              if (m.apply(currentTriplePointedTo).isInstanceOf[TasselStatement]) {
                // points to a "real" triple, not a rsrc - do nothing
              } else { // it's a rsrc, not a "real" triple
                candidateForTheObj = m.apply(currentTriplePointedTo).uriFrag // was .asCaptured
              }
            } // else it doesn't exist, so obv. do nothing (xcpt wonder)
          } // else i's theObj already final
          // now, build a replacement Tuple3 ...
          // may we update theSubj? (length check's a kludge)
          val theNewSubj: String = if (candidateForTheSubj.length > 0) candidateForTheSubj 
                                   else i._2.asInstanceOf[TasselStatement].subj
          // may we update theObj? (length check's a kludge)
          val theNewObj: String = if (candidateForTheObj.length > 0) candidateForTheObj 
                                  else i._2.asInstanceOf[TasselStatement].obj
          // thePred, of course, hasn't changed, since it's always "in" the VUE link itself:
          val t3: TasselStatement = TasselResource(i._2.asInstanceOf[TasselStatement].sourceVueType, 
              i._2.asInstanceOf[TasselStatement].sourceVueID, 
              i._2.asInstanceOf[TasselStatement].uriFrag, theNewSubj, 
              i._2.asInstanceOf[TasselStatement].pred, theNewObj).asInstanceOf[ TasselStatement ]
          val ourKey: Int = i._1
          m = m.-(ourKey)
          m = m.updated(ourKey, t3)
        }
      }
      return "placeholder line 115\n\n"
    }
    
  } // end of object theTasselResources

  def lua(args: Any*) = sllogging.BasicLogger.lua(args:_*)

  def parseVueRichText(vc: vuedata.VueChild, theVRT: String): Unit = {
    var vueType: cleaning.VueTypeEnum.Value = cleaning.VueTypeEnum.Unknown
    if (vc.isInstanceOf[vuedata.VueNode]) {
    	vueType = cleaning.VueTypeEnum.Node
    } else if (vc.isInstanceOf[vuedata.VueLink]) {
    	vueType = cleaning.VueTypeEnum.Link
    }  	
    theTasselResources.addIfNew(vc, "qk290_" + declutteredVueTextHTML(theVRT, vueType, false))
    // TODO should be error if vc.vueType != Text
  }	

  def parseVueLabel(theScLabel: String, vc: vuedata.VueChild, vueLabel: String): Unit = {
    var vueType: cleaning.VueTypeEnum.Value = cleaning.VueTypeEnum.Unknown
    if (vc.isInstanceOf[vuedata.VueNode]) {
    	vueType = cleaning.VueTypeEnum.Node
    } else if (vc.isInstanceOf[vuedata.VueLink]) {
    	vueType = cleaning.VueTypeEnum.Link
    }  	
    if (vueLabel.startsWith("Class ")) {
      /* "Class Foo" (i.e. Foo, a, Class) is NOT the desired recipient of the vueID. The vueID
       * should be used instead to identify the resource Foo, since that is the meaning of any
       * link to/from that node. To ensure this, add the simple resource before the statement:
       */
      val uriFrag: String = theTasselResources.addIfNew(vc, 
          "qk307_" + declutteredVueNodeTail(vueLabel.substring(6), vueType, false))
      theTasselResources.addIfNew(vc, uriFrag, "a", "Class")
    } else if ("vuechild_node".equalsIgnoreCase(theScLabel) && 
        vueLabel.matches("[A-Z]\\w* \\S[\\s\\S]*")) {
      // starts with a class name
      /* "Foo bar" (i.e. bar, a, Foo) is NOT the desired recipient of the vueID. The vueID
       * should be used instead to identify the resource bar, since that is the meaning of any
       * link to/from that node. To ensure this, add the simple resource before the statement:
       */
      val uriFrag: String = theTasselResources.addIfNew(vc, 
          "qk317_" + declutteredVueNodeTail(vueLabel.substring(vueLabel.indexOf(' ') + 1), vueType, false))
      theTasselResources.addIfNew(vc, uriFrag, "a", vueLabel.substring(0, vueLabel.indexOf(' ')))
    } else { // neither class decl nor obj decl
      theTasselResources.addIfNew(vc, declutteredVueNodeTail(vueLabel, vueType, false))
    }
  }

  /**
   * In Tassel, the interpretation of a link is the opposite of the interpretation of a 
   * node, in this respect: If a node both declares a mere resource and asserts a statement 
   * about it (e.g. "Foo bar"), then the use of that node as a link-end is interpreted to mean 
   * that the mere resource, not the statement, is the thing being used. Suppose the node
   * "Foo bar" is linked to "blah" by the link label "x". That asserts ":bar :x :blah .", not
   * "[:bar a :Foo .] :x :blah ." However, interpreting a link label works in the opposite way:
   * suppose "hr" is linked to the aforementioned link (i.e. to the link label "x") by the 
   * link label "believes". That asserts ":hr :believes [:bar :x :blah .] .", not 
   * ":hr :believes :x ." (Statements like ":hr :believes :x ." or "[:bar a :Foo .] :x :blah ."
   * would likely be nonsensical anyway - try it.) Therefore, the theLinksOwnID is awarded 
   * to the winner: the triple (":bar :x :blah ."), not the mere resource (":x"), so that links
   * to/from the link "x" can be processed more naturally.
   */
  def parseVueLink(theLinksOwnID: Int, theFromID: Int, theLinkLabel: String, theToID: Int): Unit = {
//    var thumbnailForTheFromID: String = ""
//    var thumbnailForTheToID: String = ""
//    thumbnailForTheFromID = theTasselTriples.getThumbnailOf(theFromID)
//    thumbnailForTheToID = theTasselTriples.getThumbnailOf(theToID)
    // TODO __ means "revisit me after traversal completes"
    theTasselResources.addIfNew(new vuedata.VueLink(theLinksOwnID, "fake_link_344_" + theLinksOwnID, None, None)
        , "__vueID_" + theFromID
        //+ "=(" + thumbnailForTheFromID + ")"
        , theLinkLabel, "__vueID_" + theToID //+ "=(" + thumbnailForTheToID + ")"
        )
    // add the link label as a mere resource too. By adding this AFTER the triple, it will 
    // receive a fresh id rather than the theLinksOwnID, which was taken by the triple: 
    theTasselResources.addIfNew(new vuedata.VueLink(theLinksOwnID, "fake_link_351_" + theLinksOwnID, None, None)
        , theLinkLabel)
  }
  
  def parseVueLink(theFrom: Option[VueResource], theLink: VueLink, theTo: Option[VueResource]): Unit = {
//    var thumbnailForTheFromID: String = ""
//    var thumbnailForTheToID: String = ""
//    thumbnailForTheFromID = theTasselTriples.getThumbnailOf(theFromID)
//    thumbnailForTheToID = theTasselTriples.getThumbnailOf(theToID)
    // TODO __ means "revisit me after traversal completes"
    theTasselResources.addIfNew(new vuedata.VueLink(theLink.sourceVueID, "fake_link_361_" + theLink.sourceVueID, None, None)
        , "__vueID_" + (if (theFrom.isDefined) theFrom.get.sourceVueID else "88344" )
        //+ "=(" + thumbnailForTheFromID + ")"
        , theLink.vueLabel, "__vueID_" + (if (theTo.isDefined) theTo.get.sourceVueID else "88346" ) //+ "=(" + thumbnailForTheToID + ")"
        )
    // add the link label as a mere resource too. By adding this AFTER the triple, it will 
    // receive a fresh id rather than the theLinksOwnID, which was taken by the triple: 
    theTasselResources.addIfNew(new vuedata.VueLink(theLink.sourceVueID, "fake_link_368_" + theLink.sourceVueID, None, None)
        , theLink.vueLabel)
  }

  // UNDER CONSTRUCTION
  def asSetOfDeparameterizedLinks(theLinkLabel: String): List[String] = {
    var l: Tuple2[String, List[String]] = 
        asSetOfDeparameterizedLinksRecurser(new Tuple2(theLinkLabel, Nil))
    if ("".equalsIgnoreCase(l._1)) return l._2
    // kludge:
    else return l._1 :: "<- leftover to the left, result to the right ->" :: l._2
  }
  
  // UNDER CONSTRUCTION
  def asSetOfDeparameterizedLinksRecurser(t: Tuple2[String, 
      List[String]]): Tuple2[String, List[String]] = {
    var s: String = t._1
    var l: List[String] = t._2
    var o: Option[Tuple2[String, List[String]]] = deparameterizedViaBraces(s)
    if (o.isDefined) {
      s = o.get._1
      l = o.get._2 ++ l
    }
    o = deparameterizedViaQuotes(s)
    if (o.isDefined) {
      s = o.get._1
      l = o.get._2 ++ l
    }
    return new Tuple2(s, l)
  }
  
  // UNDER CONSTRUCTION
  def deparameterizedViaBraces(s: String): Option[Tuple2[String, List[String]]] = {
    var s1: String = s
    var s2: List[String] = Nil
    while (s1.contains('{') && s1.contains('}')) {
      val interior: String = s1.substring(s1.indexOf('{') + 1, s1.indexOf('}'))
      val exterior: String = s1.substring(0, s1.indexOf('{')) + s1.substring(s1.indexOf('}') + 1)
      if ("A".equals(interior) || "E".equals(interior)) { // must be in caps
        s2 = "LOGIC[" + interior + "]" :: s2
      } else { // must be time
        s2 = "TIME[" + interior + "]" :: s2
      }
      s1 = exterior
    }
    if (Nil == s2) return None
    else return Some(new Tuple2(s1, s2))
  }
  
  // UNDER CONSTRUCTION
  def deparameterizedViaQuotes(s: String): Option[Tuple2[String, List[String]]] = {
    var s1: String = s
    var s2: List[String] = Nil
    while (s1.contains('"')) {
      val interior: String = s1.substring(s1.indexOf('"') + 1, s1.indexOf('"', s1.indexOf('"') + 1))
      val exterior: String = s1.substring(0, s1.indexOf('"')) + 
          s1.substring(s1.indexOf('"', s1.indexOf('"') + 1) + 1)
      s2 = "QUOTE[" + interior + "]" :: s2
      s1 = exterior
    }
    if (Nil == s2) return None
    else return Some(new Tuple2(s1, s2))
  }
  
  /* can be a bit reckless, since this is only for printing - 
   * most of this is intended only for vue Text nodes
   */    
  def declutteredVueTextHTML(s: String, sourceVueType: cleaning.VueTypeEnum.Value, 
      debug: scala.Boolean): String = {
    // N.B.!!!!
    if (cleaning.VueTypeEnum.Text != sourceVueType) {
      lua("ERROR 308")
      return s
    }
    // delete CSS statements
    val s1: String =  s.replaceAll("body\\s+\\{[^}]+}", "a")
    val s2: String = s1.replaceAll("ol\\s+\\{[^}]+}", "a")
    val s3: String = s2.replaceAll("p\\s+\\{[^}]+}", "a")
    val s4: String = s3.replaceAll("ul\\s+\\{[^}]+}", "a")
    // delete the comment wrapper for CSS
    val s5: String = s4.replaceAll("&lt;!--[a ]+--&gt;", "a")
    // delete its enclosing style HTML element 
    val s6: String = s5.replaceAll(
        "&lt;style type=&quot;text/css&quot;&gt;\\s*a\\s*&lt;/style&gt;", "a")
    // if its enclosing head HTML element has attribs, delete them
    val s7: String = s6.replaceAll(
        "&lt;head style=&quot;color: #000000&quot; color=&quot;#000000&quot;&gt;\\s*a\\s*&lt;/head&gt;", 
        "&lt;head&gt;    a    &lt;/head&gt;")
    // delete its enclosing head HTML element (now guaranteed to lack attribs)
    val s8: String = s7.replaceAll("&lt;head&gt;\\s*a\\s*&lt;/head&gt;", "a")
    // some p HTML elements have this cruft
    val s9: String = s8.replaceAll(" style=&quot;color: #000000&quot; color=&quot;#000000&quot;", "")
    // delete its enclosing body HTML element, and ITS enclosing html HTML element
    val tmpP: java.util.regex.Pattern = java.util.regex.Pattern.compile(
        "&lt;html&gt;\\s*a\\s*&lt;body&gt;(.+)&lt;/body&gt;\\s*&lt;/html&gt;")
    val tmpM: java.util.regex.Matcher = tmpP.matcher(s9)
    val s10: String = tmpM.replaceFirst("$1")
    // very reckless, but we're just printing - delete all HTML p's and b's
    val s11: String = s10.replaceAll("&lt;/?[bp]&gt;", "")
    // repl newlines with spaces
    val s12: String = s11.replaceAll("\\n", " ")      
    // more than 3 whitespaces (internally)? Reduce to 3 spaces
    val s13: String = s12.replaceAll("\\s\\s\\s\\s+", "   ")      
    return s13.trim
  }    
  
  def declutteredVueNodeTail(s: String, sourceVueType: cleaning.VueTypeEnum.Value, 
      debug: scala.Boolean): String = {
    // N.B.!!!!
    if (cleaning.VueTypeEnum.Node != sourceVueType) {
      lua("ERROR 346")
      return s
    }
    val s2: String = s.replaceAll("\\s", " ") // repl whitespace with space
    val s3: String = s2.replaceAll("  +", "  ") // repl 2 or more spaces with 2 spaces
    val s99: String = s3.trim
    return s99
  }
  
}
