package tasseldata

class TasselAssertion(asN3Resource: String, sourceVueID: Int, subj: TasselObj, pred: TasselObj, obj: TasselObj) 
					extends TasselObj(asN3Resource, sourceVueID) {
	
  def subj(): TasselObj = subj
  def pred(): TasselObj = pred
  def obj(): TasselObj = obj      
  
  override def toStr: String = { 
  	"       :" + subj.asN3Resource + "  :" + pred.asN3Resource + "  :" + obj.asN3Resource + "  .\n"
  }
}
