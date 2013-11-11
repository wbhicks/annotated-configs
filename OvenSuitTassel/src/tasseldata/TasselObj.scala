package tasseldata

import scala.collection.immutable.SortedMap
import scala.collection.immutable.TreeMap

object TasselObj {
	
	def lua(args: Any*) = sllogging.BasicLogger.lua(args:_*)
	var allTOs: SortedMap[ Int, TasselObj ] = new TreeMap[ Int, TasselObj ]
  var privateCounter: Int = 50000 // hideous kludge
  def nextID(): Int = { privateCounter += 1; privateCounter }
	val builtInsInitializer: List[String] = List("a", "Class")
	var builtIns: SortedMap[ String, TasselObj ] = new TreeMap[ String, TasselObj ]
	
  /** When false, only the first-encountered instance of a resource is stored normally.
   *  Subsequent resources that match it will instead be stored as "aliases".
   */
	// woohoo! it works!	
  /** Accept an object even if it's a de facto dupe, or replace it with an alias to the pre-existing object instead?
   */
  val allowDupeObjs: Boolean = false // was !storeDupeAsAlias

  var i: Int = -1
  for (s <- builtInsInitializer) {
  	var nextBuiltIn: TasselObj = new TasselObj(s, i)
  	builtIns = builtIns.updated(s, nextBuiltIn)
  	addIfNew(nextBuiltIn)
  	i -= 1
  }
  
  /** TODO hideous kludge - t.sourceVueID had better be less than initial val of privateCounter
   */
  def addIfNew(t: TasselObj): Unit = {
  	val o: Option[ Tuple2[ Int, TasselObj ] ] = allTOs.find(scalaPredForAdd(t))
  	if (o.isDefined) { // a "close enough" resource already exists, regardless of its ID
  		if (allowDupeObjs) { // we don't mind that a "close enough" resource already exists
	  		if (allTOs.contains(t.sourceVueID)) {
	  			if (!allTOs.contains(10000 + t.sourceVueID)) { // if 10000 + the ID is avail, use that
	  				allTOs = allTOs.updated(10000 + t.sourceVueID, t)
	  			} else { // we'll settle for an unrelated number
		  			val fallbackID: Int = nextID()
		  			lua("INFO TO.39: both proposed ID " + t.sourceVueID + " and " + 10000 + t.sourceVueID + "were taken. Using fallbackID " + fallbackID + " instead.")
			  		allTOs = allTOs.updated(fallbackID, t)
	  			}
	  		} else {
	  			allTOs = allTOs.updated(t.sourceVueID, t)
	  		}
  		} else { // dupes forbidden, so store alias instead
  			// add an alias IFF it can have the desired ID (otherwise, no point in adding an alias)
	  		if (allTOs.contains(t.sourceVueID)) {
	  			if (!(TasselObj.builtIns contains t.asN3Resource)) {
            lua("ERROR TO.49: All 3 conditions hold: (1) dupes forbidden, (2) proposed ID " + t.sourceVueID + " was taken, (3) " + t.asN3Resource + " is not a builtIn. So ... no point in adding an alias. Doing nothing.")
	  			}
	  		} else {
	  			t.replaceWithAliasTo = Some(o.get._1.asInstanceOf[Int]) // the ID of the pre-existing one
	  			allTOs = allTOs.updated(t.sourceVueID, t)
	  		}  			
  		}
  	} else { // i.e., it's a new arrival
  		if (allTOs.contains(t.sourceVueID)) {
  			if (!allTOs.contains(10000 + t.sourceVueID)) { // if 10000 + the ID is avail, use that
  				allTOs = allTOs.updated(10000 + t.sourceVueID, t)
  			} else { // we'll settle for an unrelated number
	  			val fallbackID: Int = nextID()
	  			lua("INFO TO.60: both proposed ID " + t.sourceVueID + " and " + 10000 + t.sourceVueID + "were taken. Using fallbackID " + fallbackID + " instead.")
		  		allTOs = allTOs.updated(fallbackID, t)
  			}	  		
  		} else {
	  		allTOs = allTOs.updated(t.sourceVueID, t)
  		}
  	}
  }
  
  /** note that you don't care what kind of vuechild it came from, so if a vue node
   *  and vue text both say "foo", it still appears only once
   */    
  def scalaPredForAdd(t: TasselObj): Tuple2[ Int, TasselObj ] => Boolean = {
    n: Tuple2[ Int, TasselObj ] => {
      if (!t.isInstanceOf[TasselAssertion]) {
        // n._2 == t // i.e. same Java object - do we want to be that strict?
      	n._2.asN3Resource.equalsIgnoreCase(t.asN3Resource)
      } else { // a triple
        if (n._2.isInstanceOf[TasselAssertion]) {
          n._2.asInstanceOf[TasselAssertion] == t.asInstanceOf[TasselAssertion]
        } else false // TODO error
      }
    }
  }
  
  def toStr: String = { 
    val sb: StringBuilder = new StringBuilder
    sb.append("#  This fi\n\n" +
    		"# This is a Tur\n" +
        "# Above each tri\n" +
        "# (2) the vueid of the vu\n" +
        "# a node asse\n" +
        "# for the asse\n\n")
    sb.append("@prefi\n\n")
    for (i <- allTOs) {
      sb.append(
        if (i._1 == i._2.sourceVueID) {
        	val tempVROpt: Option[vuedata.VueResource] = vuedata.VueResource.allVRs.get(i._2.sourceVueID)
        	"# " + i._1 + " " + ( if (tempVROpt.isDefined) tempVROpt.get.sourceVueTypeAsString else "(none)" ) + "\n"
        } else { // the resource (prob a triple, or a heretofore-unencountered class) didn't "deserve" ownership of the orig vueid
          "# " + i._1 + " (derived from " + i._2.sourceVueID + ")\n"
        }
      )
    	sb.append(i._2.toStr)
    	sb.append("\n")
    }     
    sb.toString 
  }
	
}

class TasselObj(asN3Resource: String, sourceVueID: Int) {
	def asN3Resource(): String = asN3Resource	
  /**
   * Which vueChild provided this resource?
   * kludge: currently, negatives indicate the builtIns
   * TODO refactor to an Option
   */
  def sourceVueID(): Int = sourceVueID // these are NOT unique
  var replaceWithAliasTo: Option[Int] = None
  
  def toStr: String = {
		if (vuedata.VueResource.allVRs.get(sourceVueID).isDefined) {
			"#@DECL :" + asN3Resource + "\n" +
				"       :" + asN3Resource + "  :hasAsCaptured  \"" + 
				vuedata.VueResource.allVRs.get(sourceVueID).get.vueLabel + 
				"\"  .\n"
		} else if (TasselObj.builtIns contains asN3Resource) {
			"# this is the TasselObj that holds the Tassel builtIn " + asN3Resource + "\n" // no need to declare Class, a, etc.
		} else {
			"#@DECL :" + asN3Resource + "\n" +
				"#      (Error - this WAS derived from a VR!)\n"
		}
	}
}
