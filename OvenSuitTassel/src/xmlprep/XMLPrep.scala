// OK
package xmlprep

object XMLPrep {
  def lua(args: Any*) = sllogging.BasicLogger.lua(args:_*)
  def lue(elem: scala.xml.Elem) = sllogging.BasicLogger.lue(elem)

  /**
   * To use (Launcher.scala is no longer needed):
   * 
   * cd /Users/sample/Dev/Eclipse_Classic_3.5.2_Cocoa_64_work/slVueTest1/bin
   * 
   * /Users/sample/Dev/Scala/scala-2.8.0.Beta1-prerele
   * ase/bin/scala -classpath . xmlprep.XMLPrep ../content/vue_sample_2010-05-11
   */
  def main(args: Array[String]) {
	  lua("hello 2010-12-20 1248 ...")
    // START of kludgey test for "UNDER CONSTRUCTION" methods in BasicTasselParser
//		    val s: String = "{a}bcd{efghi}jk\"l\"mno{p}qr\"st\"uv"
//		    lua(s + " generates:")
//		    lua(tasselparsing.BasicTasselParser.asSetOfDeparameterizedLinks(s).toString + "\n")
//		    return
    // END of kludgey test for "UNDER CONSTRUCTION" methods in BasicTasselParser
    
    // If the input file is foo.xml, baseName should be "foo". 
    // However, it's typically supplied via 0th cmd-line arg.
    var baseName = "typically supplied via cmd-line, e.g. vue_sample_2010-05-11"
    if (0 < args.length) baseName = args.apply(0)
    val nofwc: String = nameOfFileWithoutCruft(nameOfFileWithoutRootSiblings(baseName), baseName)
    nameOfVueDiagnosticsReport(baseName) // as side effect, write diagnostic report
    nameOfObjectReport(baseName)
    nameOfClassBasedN3File(baseName)
    nameOfN3File(baseName) // as side effect, write the N3 files (the ultimate goal)
  }

  /**
   * Read the original file, and write a file in which all siblings of
   * the root have been removed. (BTW, root elem name is "LW-MAP").
   * This step is needed only because scala.xml.XML.loadFile does not
   * (yet) accept a normal XML file. (Am I missing something?)
   */
  def nameOfFileWithoutRootSiblings(baseNameIn: String): String = {
    lua("nameOfFileWithoutRootSiblings() ...", 1)
    val fileNameIn = baseNameIn + ".vue" // used to be ".xml"
    val fileNameOut = baseNameIn + "_wrs.xml"
    lua("Reading " + fileNameIn)
    val bw = new java.io.BufferedWriter(new java.io.FileWriter(fileNameOut))
    val fis = new java.io.FileInputStream(fileNameIn) // we'll read the original
    val bs = new scala.io.BufferedSource(fis)
    for (val line <- bs.getLines()) {
      // in XML, these are the only possible siblings of the root:
      if (line.startsWith("<!") || line.startsWith("<?")) {
        // lua("Excluded line: " + line)
      } else bw.write(line)
    }
    bw.close()
    lua("Wrote " + fileNameOut)
    lua(-1, "... nameOfFileWithoutRootSiblings()")
    return fileNameOut
  }

  /**
   * Read the file whose name is supplied, and write a clean, minimal file in 
   * which "cruft" has been removed. In the process, as the XML tree is
   * traversed, create and populate the data structures in memory that will
   * later be used to generate reports and RDF documents. Currently these
   * structures are: BasicVueReporter.theVueTotals and 
   * BasicTasselParser.theTasselResources
   *  
   * @precondition The file whose name is supplied has no siblings of the root
   */
  def nameOfFileWithoutCruft(nofwrs: String, baseNameOut: String): String = {
    lua("nameOfFileWithoutCruft() ...", 1)
    // won't work if "root" has siblings:
    val o: Option[scala.xml.Node] = (new cleaning.Cleaner).cleanScNode(scala.xml.XML.loadFile(nofwrs), 1)
    
    lua("Read " + nofwrs + " and cleaned the tree in memory")
    val fileNameOut = baseNameOut + "_min.xml"
    val bw = new java.io.BufferedWriter(new java.io.FileWriter(fileNameOut))
    if (o.isDefined) {
      var sb: StringBuilder = new StringBuilder()
      // works via side effect on sb:
      val out2: Unit = (new scala.xml.PrettyPrinter(300, 5)).format(o.get, sb)
      bw.write(sb.toString())
    } else lua("ERROR 73: is file corrupt?")
    bw.close()
    lua("Wrote " + fileNameOut)
    lua(-1, "... nameOfFileWithoutCruft()")
    return fileNameOut
  }

  def nameOfVueDiagnosticsReport(baseNameOut: String): String = {
    lua("nameOfVueDiagnosticsReport() ...", 1)
    val fileNameOut = baseNameOut + "_vue_diagnostics.txt"
    val bw = new java.io.BufferedWriter(new java.io.FileWriter(fileNameOut))
    lua("Reading vue diagnostics totals from memory")
    bw.write(vuereporting.BasicVueReporter.theVueTotals.toStr)
    bw.close()
    lua("Wrote " + fileNameOut)
    lua(-1, "... nameOfVueDiagnosticsReport()")
    return fileNameOut
  }	

  def nameOfObjectReport(baseNameOut: String): String = {
    lua("nameOfObjectReport() ...", 1)
    val fileNameOut = baseNameOut + "_obj_diagnostics.txt"
    val bw = new java.io.BufferedWriter(new java.io.FileWriter(fileNameOut))
    lua("Reading object graph from memory")
    bw.write(vuedata.VueResource.toStr)
    bw.close()
    lua("Wrote " + fileNameOut)
    lua(-1, "... nameOfObjectReport()")
    return fileNameOut
  }
 
  def nameOfClassBasedN3File(baseNameOut: String): String = {
    lua("nameOfClassBasedN3File() ...", 1)
    val fileNameOut = baseNameOut + "_class-based_n3.txt"
    val bw = new java.io.BufferedWriter(new java.io.FileWriter(fileNameOut))
    lua("Reading TasselObj.allTOs from memory")
    bw.write(tasseldata.TasselObj.toStr)
    bw.close()
    lua("Wrote " + fileNameOut)
    lua(-1, "... nameOfClassBasedN3File()")
    return fileNameOut
  }
  
  def nameOfN3File(baseNameOut: String): String = {
    lua("nameOfN3File() ...", 1)
    var bw = new java.io.BufferedWriter(new java.io.FileWriter(baseNameOut + "_n3.txt"))
    this lua "Reading theTasselResources from memory"
    bw.write("#  This file written BEFORE link endpoints were resolved.\n\n")
    bw.write(tasselparsing.BasicTasselParser.theTasselResources.toStr)
    bw.close()    
    lua(tasselparsing.BasicTasselParser.theTasselResources.makeFinal)
    bw = new java.io.BufferedWriter(new java.io.FileWriter(baseNameOut + "_n3_FINALIZED.txt"))
    bw.write("#  This file written AFTER link endpoints were resolved.\n\n")
    bw.write(tasselparsing.BasicTasselParser.theTasselResources.toStr)
    bw.close()    
    lua(-1, "... nameOfN3File()")
    return "More than 1 file was written, so which filename were you expecting? ;-)"
  }
}
