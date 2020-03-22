// two void-typed procedures must not be compared
class VoidComparison {
    public void voidFunc() {
        return;
    }

    public void main() {
        int a;
        while(voidFunc()==voidFunc()) {
            a = a+1;
        }
    }
}
