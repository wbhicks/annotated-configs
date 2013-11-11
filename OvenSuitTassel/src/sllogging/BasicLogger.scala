// OK
package sllogging

object BasicLogger {

  var indent = 0
  val filler = "  "
  val leftDelim = " ["
  val rightDelim = "]"
  val logo = "sl> "

  // alias
  def lua(args: Any*) = logUsingArray(args:_*)
  // to
  def logUsingArray(args: Any*) = {
    println
    print(logo) // & something like dateFormat.format(new Date())
    for (j <- 0 until indent) print(filler)
    for (i <- 0 until args.length) {
      if (args(i).isInstanceOf[Int]) {
        val delta = args(i).asInstanceOf[Int]
        indent += delta
        if (0 > delta) print(rightDelim)
        else if (0 < delta) print(" " + leftDelim)
      } else if (args(i).isInstanceOf[String]) {
        print(filler)
        print(args(i).asInstanceOf[String])
      }
    }
  }

  // alias
  def lue(elem: scala.xml.Elem): Unit = logUsingElem(elem)
  // to
  def logUsingElem(elem: scala.xml.Elem): Unit = {	
    lua("e", 1)
    lumd(elem.attributes)
    var sb: StringBuilder = new StringBuilder()
    sb.append("elem is (" 
        + (if (null != elem.prefix) (elem.prefix + ":") else "")
        + (if (null != elem.label) (elem.label) else "ERROR 40 NULL LABEL") + ")"
        + (if (!elem.hasDefiniteSize) ", lacks definite size!" else "")
        + (if (elem.isEmpty) ", is empty!" else "")
        + (if (1 != elem.length) ", length (size) not 1!" else "")
        + (if (null != elem.namespace) (", namespace==" + elem.namespace) else ""))
    // was = elem \ "_", but that picks up only Elems, not Text as well:
    val theKids: scala.xml.NodeSeq = elem.child
    if (0 < theKids.length) {
      sb.append(", " + theKids.length + " kid" + 
          (if (1 < theKids.length) "s" else "") + " in following NodeSeq:")
      lua (sb.toString)
      luns(theKids)
    } else {
      lua (sb.toString)
    }
    lua(-1, "e")
  }

  // alias
  def lumd(md: scala.xml.MetaData): Unit = logUsingMetaData(md)
  // to
  def logUsingMetaData(md: scala.xml.MetaData): Unit = {
    var isEmptyAttempt = "WONT SEE THIS"
    try { isEmptyAttempt = (if (md.isEmpty) "" else "not ") } 
    catch { case e => (isEmptyAttempt = "XCPTN 112! may be ") }
    var lengthAttempt = "WONT SEE THIS"
    try { lengthAttempt = "" + md.length } 
    catch { case e => (lengthAttempt = "XCPTN 118! unknown") }
    var sizeAttempt = "WONT SEE THIS"
    try { sizeAttempt = "" + md.size } 
    catch { case e => (sizeAttempt = "XCPTN 124! unknown") }
    var sb: StringBuilder = new StringBuilder()
    sb.append((if (md.isPrefixed) "" else "not ") + "prefixed"
        + ", " + isEmptyAttempt + "empty"
        + ", length==" + lengthAttempt
        + ", size==" + sizeAttempt
        + (if (!md.hasDefiniteSize) ", lacks definite size!" else ""))
    if ("0".equalsIgnoreCase(lengthAttempt)) {
      lua ("md [ " + sb.toString + " ] md")
    } else {
      lua("md", 1)
      lua(sb.toString)
    }
    var iter: Iterator[scala.xml.MetaData] = md.iterator
    var i2: scala.xml.MetaData = null
    var count = 1
    try {
      while (iter.hasNext) {
        i2 = iter.next
        sb = new StringBuilder()
        sb.append("    " + count + ". " + i2.key + "==(")
        count += 1
        sb.append("" + (if (i2.value != null) i2.value.first.toString else "(i2.value was null)"))
        sb.append(")")
        lua (sb.toString)
      }
    } catch { case e => lua("it's that weird first case") }
    if ("0".equalsIgnoreCase(lengthAttempt)) {
    } else {
      lua(-1, "md")
    }
  }

  // alias
  def lun(node: scala.xml.Node): Unit = logUsingNode(node)
  // to
  def logUsingNode(node: scala.xml.Node): Unit = {
    if (node.isInstanceOf[scala.xml.Text]) {
      lut(node.asInstanceOf[scala.xml.Text])
    } else if (node.isInstanceOf[scala.xml.SpecialNode]) {
      lua("n", 1)
      lua("node isInstanceOf SpecialNode other than Text (wow!)")
      lua(-1, "n")
    } else if (node.isInstanceOf[scala.xml.Group]) {
      lua("n", 1)
      lua("node isInstanceOf Group (wow!)")
      lua(-1, "n")
    } // must be Elem ... 
    else if (node.isInstanceOf[scala.xml.Elem]) {
      lue(node.asInstanceOf[scala.xml.Elem])
    } else lua("ERROR 146 some other type")
  }

  // alias
  def luns(nodeSeq: scala.xml.NodeSeq): Unit = logUsingNodeSeq(nodeSeq)
  // to
  def logUsingNodeSeq(nodeSeq: scala.xml.NodeSeq): Unit = {
    if (nodeSeq.isInstanceOf[scala.xml.Document]) {
      lua("ns", 1)
      lua("wow! nodeSeq isInstanceOf scala.xml.Document")
      lua(-1, "ns")
    } 
    /* should be instance of Node then, right? Oddly, it isn't, even 
    		   if there's just 1 Node in the sequence. 
     */ 
    else if (nodeSeq.isInstanceOf[scala.xml.Node]) {
      lua("ns", 1)
      lua("wow! nodeSeq isInstanceOf scala.xml.Node. Well, logging as a Node ...")
      lun(nodeSeq.asInstanceOf[scala.xml.Node])
      lua(-1, "ns")
    } else if (nodeSeq.isInstanceOf[scala.collection.Seq[scala.xml.Node]]) {
      if (0 < nodeSeq.length) {
        lua("ns", 1)
        // it always is, even though NodeSeq is abstract, so no need to state the obvious:
        //    			lua("nodeSeq isInstanceOf scala.collection.Seq[scala.xml.Node]")
        lua("length==" + nodeSeq.length)
        nodeSeq.foreach(lun)
        /* ... shorthand for ... nodeSeq.foreach((n: scala.xml.Node) => lun(n)) */
        lua(-1, "ns")
      } else { // empty, but luns is no longer called in this case
        lua("ns  []  ns")
      }
    } else println("ERROR 139 some other type")
  }

  // alias
  def lut(text: scala.xml.Text): Unit = logUsingText(text)
  // to
  def logUsingText(text: scala.xml.Text): Unit = {
    print(" |_" + text.buildString(new StringBuilder).toString().trim + "_| ")
  }

}