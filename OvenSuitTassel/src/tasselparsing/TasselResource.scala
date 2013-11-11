package tasselparsing

object TasselResource {

  def apply(uriFrag: String, args: String*) = {
    if (1 == args.length) { // i.e. only asLiteral
      new TasselResource(uriFrag, args.apply(0), cleaning.VueTypeEnum.Unknown, -1)
    } else { // a statement
      new TasselStatement(uriFrag, "", cleaning.VueTypeEnum.Unknown, -1, 
          args.apply(0), args.apply(1), args.apply(2)) // i.e. subj, pred, obj
    }
  }

  def apply(sourceVueType: cleaning.VueTypeEnum.Value, sourceVueID: Int, 
      uriFrag: String, args: String*) = {
    if (1 == args.length) { // i.e. only asLiteral
      new TasselResource(uriFrag, args.apply(0), sourceVueType, sourceVueID)
    } else { // a statement
      new TasselStatement(uriFrag, "", sourceVueType, sourceVueID, 
          args.apply(0), args.apply(1), args.apply(2)) // i.e. subj, pred, obj
    }
  }
  
}

class TasselResource(uriFrag: String, asCaptured: String, sourceVueType: cleaning.VueTypeEnum.Value, 
					 sourceVueID: Int) {
  def uriFrag(): String = uriFrag
  def asCaptured(): String = asCaptured
  // What was the vueChild type of the vueChild that provided this resource?
  def sourceVueType(): cleaning.VueTypeEnum.Value = sourceVueType
  // Which vueChild provided this resource?
  def sourceVueID(): Int = sourceVueID
  
  def sourceVueTypeToStringDEPREC: String = {
    val matchResult: String = sourceVueType match {
      case cleaning.VueTypeEnum.Node => "N"
      case cleaning.VueTypeEnum.Group => "G"
      case cleaning.VueTypeEnum.Text => "T"
      case cleaning.VueTypeEnum.Link => "L"
      case cleaning.VueTypeEnum.Unknown => "?"
      case cleaning.VueTypeEnum.NotEvenAVueChild => "-"
      case _ => "!" // TODO ERROR
    }
    return matchResult
  }
  
}

class TasselStatement(uriFrag: String, asCaptured: String, sourceVueType: cleaning.VueTypeEnum.Value,
                      sourceVueID: Int, subj: String, pred: String, obj: String) 
                      extends TasselResource(uriFrag, asCaptured, sourceVueType, sourceVueID) {
  def subj(): String = subj
  def pred(): String = pred
  def obj(): String = obj      
}