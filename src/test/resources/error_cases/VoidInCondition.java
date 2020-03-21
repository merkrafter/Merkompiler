// a void-typed procedure must not be used as a condition in a while loop
class VoidInCondition {
    public void voidFunc() {
        return;
    }

    public void main() {
        int a;
        while(voidFunc()) {
            a = a+1;
        }
    }
}
