// no public classes allowed
class SmokeClass {
    // no 'static' keyword available
    // no 'String' type available
    public void main() {
        int a;
        int b;
        int c;
        int d;
        int e;
        int result;
        a = 1;
        b = 2;
        c = 3;
        d = 4;
        e = 5;
        result = a+(b-c)*d/e;
        if (a <= b) {println(a);} else {println(b);}
        if (a <  b) {println(a);} else {println(b);}
        if (a == b) {println(a);} else {println(b);}
        if (a  > b) {println(a);} else {println(b);}
        if (a >= b) {println(a);} else {println(b);}
        println(result);
    }
}
