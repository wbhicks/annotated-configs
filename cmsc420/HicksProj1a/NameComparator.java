// not currently used - ignore

public class NameComparator implements java.util.Comparator {

//   if static, then the iface's version won't be hidden
    public int compare(Object a, Object b) {
        // no way to avoid a misleading comparison when non-DotValue(s) passed
        int result = 99;
        if (( a instanceof DotValue ) && ( b instanceof DotValue )) {
            DotValue dva = (DotValue) a;
            DotValue dvb = (DotValue) b;
            result = ( dva.getName() ).compareTo( dvb.getName() );
        }
        return result;
    }

}