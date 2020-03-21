// variable 'a' is an integer and must not be returned in an void procedure
class ReturnInWhileLoops {
    public void main() {
        int a;
        a = 4;
        while(a==4){
            // should not be allowed
            return a;
        }
    }
}
