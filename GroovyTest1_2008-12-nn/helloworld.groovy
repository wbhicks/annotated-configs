def a = 2
println "The ewiruehfgdf of variable a is dsvbsdvhjdskfsgef equal to " + a
def b = 3 + 77
println "What's b right now? Let's see ... b is " + b
a = 20
b = a
println "the variable a is " + a + " and b is " + b 
c = 45
println c
c = 46
println c
c = 47
println c

/*
 * 		stMap = [ 't35a': new SimpleTileG( 3, 5 ), 
		          't35b': new SimpleTileG( 3, 5 ), 
		          't37' : new SimpleTileG( 3, 7 ), 
		          't75' : new SimpleTileG( 7, 5 ),
		          't53' : new SimpleTileG( 5, 3 ), 
		          't22' : new SimpleTileG( 2, 2 ), 
		          't04a': new SimpleTileG( 0, 4 ), 
		          't04b': new SimpleTileG( 0, 4 ),
		          't00a': new SimpleTileG( 0, 0 ), 
		          't00b': new SimpleTileG( 0, 0 ) ]

*/

/*
	// A "bare" return statement returns the null Object, regardless of whether method is declared "void", "Object" or "def".
	// 3 dots are merely a short(?!)hand for an array: "..." could have been "[]"
Object sum2(int... someInts) { // could have been "void" or "def"
    p "size: " + someInts.size()
    for (i = 0; i < someInts.size(); i++) {
        p i; p someInts[i]
    }
    return null // "null" is optional
}
p sum2( 77, 55, 33)
*/

/*
println "bad: •, good: " + (8226 as char)
x Character.codePointAt( "a•b", 1 )
(8208..8300).each {print it; x it as char}
*/

/*
f = new File( "/Users/Shared/delme.txt" )
f.eachLine { x it }  

x()
x x.class
justNull = null; x justNull?.class

qq = null; def yy; assert yy == qq
println yy ?: 7 
		
x "banana" ==~ /b[an]+/

println "$yy 1 ${yy}"

def c= { def a= 5, b= 9; a * b } 
assert 45 == c()
*/

/*
def i=0, j=0 
//create or overwrite this file 
def f= new File(
    "/Users/Shared/delme2.txt")
Thread.start{ 
while(true){ 
i++ 
if(i%1000 == 0) f<< 'S' //spawned thread 
} 
} 
while(true){ 
j++ 
if(j%1000 == 0) f<< 'M' //main thread 
}
*/