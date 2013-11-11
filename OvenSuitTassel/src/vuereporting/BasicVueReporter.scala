package vuereporting

import scala.collection.immutable.SortedMap
import scala.collection.immutable.TreeMap

object BasicVueReporter {

  object theVueTotals {
    var m: SortedMap[ Tuple2[ String, String ], Int ] = new TreeMap[ Tuple2[ String, String ], Int ]

    def toStr: String = { 
      val sb: StringBuilder = m.addString(new StringBuilder, "\n")
      
      // rest is kludge to let me see link labels at a glance:
//      var kludge2ndMapOfLinks: SortedMap[ String, Int ] = new TreeMap[ String, Int ]
//      var tmpStr: String = ""
//      for (i <- m) {
//        tmpStr = i._1._2
//        if (tmpStr.startsWith("vuechild_link")) {
//        	lua("ERROR: 20 |_" + i._1._1 + "_|_" + tmpStr + "_|_" + i._2 + "_|")
//          tmpStr = tmpStr.replaceAll("vuechild_link::", "")
//          tmpStr = tmpStr.substring(tmpStr.indexOf(':') + 2)
//          if (kludge2ndMapOfLinks.contains(tmpStr)) {
//            val origCount: Int = kludge2ndMapOfLinks.apply(tmpStr)
//            val newMap: SortedMap[ String, Int ] = kludge2ndMapOfLinks.updated(tmpStr, 1 + origCount)
//            val updatedCount: Int = newMap.apply(tmpStr)
//            if (updatedCount != origCount + 1) throw new Exception(
//                "Aaaargh! origCount==" + origCount + " but updatedCount==" + updatedCount)
//            kludge2ndMapOfLinks = newMap
//          } else {
//            kludge2ndMapOfLinks = kludge2ndMapOfLinks.updated(tmpStr, 1)
//          }
//        }
//      }
//      val sb2: StringBuilder = kludge2ndMapOfLinks.addString(new StringBuilder, "\n")      
//      sb.append("\n\nFollowing is a kludge to let me see link labels at a glance:\n\n" + sb2.toString)
      // end of kludge
      
      sb.toString 
    }

    // this method is only place in proj where m gets changed
    def incr(category: String, item: String) : Int = {
      if (m.exists(scalaPredForAdd(category, item))) { 
        // i.e., the pair was already there, so incr its count
        val origCount: Int = m.apply(new Tuple2(category, item))
        val newMap: SortedMap[ Tuple2[ String, String ], Int ] = m.updated(new Tuple2(category, 
            item), 1 + origCount)
        val updatedCount: Int = newMap.apply(new Tuple2(category, item))
        if (updatedCount != origCount + 1) throw new Exception(
            "Aaaargh! origCount==" + origCount + " but updatedCount==" + updatedCount)
        m = newMap
        updatedCount
      } else { // i.e., it's a new arrival, so add it
        m = m.updated(new Tuple2(category, item), 1)
        1
      }
    }

    def incr(item: String) : Int = { incr("default", item) }

    def scalaPredForAdd(category: String, item: String): Tuple2[ Tuple2[ String, 
            String ], Int ] => Boolean = {
      n: Tuple2[ Tuple2[ String, String ], Int ] => {
        n._1._1.equalsIgnoreCase(category) && n._1._2.equalsIgnoreCase(item)
      }
    }
  }

  def lua(args: Any*) = sllogging.BasicLogger.lua(args:_*)

  var knownCruftElementKeysList: List[ String ] = List(
      "fillColor", "strokeColor", "textColor", "font", "point1", "point2", "metadata-list", 
      "ctrlPoint0", "URIString", "title", "layer", "userOrigin", "userZoom", // "shape"
      "presentationBackground", "PathwayList", "date", "mapFilterModel", "modelVersion", 
      "saveFile", "saveLocation",
      "resource" // VUE's "web resources"
      )

  var knownNonCruftElementKeysList: List[ String ] = List(
      "LW-MAP", "child", "label", "ID1", "ID2", "resource", "property", "richText")

  var knownCruftAttributeKeysList: List[ String ] = List(
      "x", "y", "width", "height", "arrowState", "strokeWidth", "strokeStyle", "autoSized", 
      "controlCount", "created", "layerID")

      // TODO add "synth_id1", "synth_id2"
  var knownNonCruftAttributeKeysList: List[ String ] = List("label", "type", "ID")

  def incr(kind: String, which: String): Int = {
    var matchResult: Int = kind match {
      case "xElementKeys" => {	
        if (knownCruftElementKeysList.contains(which)) {
          theVueTotals.incr("knownCruftElementKeys", which)
        } else if (knownNonCruftElementKeysList.contains(which)) {
          theVueTotals.incr("knownNonCruftElementKeys", which)
        } else {
          theVueTotals.incr("unknownElementKeys", which)
        }
      }
      case "xAttributeKeys" => {	
        if (knownCruftAttributeKeysList.contains(which)) {
          theVueTotals.incr("knownCruftAttributeKeys", which)
        } else if (knownNonCruftAttributeKeysList.contains(which)) {
          theVueTotals.incr("knownNonCruftAttributeKeys", which)
        } else {
          theVueTotals.incr("unknownAttributeKeys", which)
        }
      }
      case "vueLabels" => {
        val whichTruncated: String = which.substring(0, Math.min(60, which.length()))
        theVueTotals.incr("vueLabels", whichTruncated)
      }
  }
  -1 // logic error
  }

}

//object VueChildType extends Enumeration {
//  type VueChildType = Value
//  val Node, Group, Text, Link, Error = Value
//}

