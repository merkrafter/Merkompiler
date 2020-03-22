// this longer class uses the 'println' procedure which was not defined anywhere in the file
class UndefinedProcedure {
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
        // the following calls to println are not allowed
        if (a <= b) {println(a);} else {println(b);}
        if (a <  b) {println(a);} else {println(b);}
        if (a == b) {println(a);} else {println(b);}
        if (a  > b) {println(a);} else {println(b);}
        if (a >= b) {println(a);} else {println(b);}
        println(result);
    }
}
