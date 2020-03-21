// two void-typed procedures must not be added
class VoidAddition {
    public void voidFunc() {
        return;
    }

    public void main() {
        int a;
        a = voidFunc() + voidFunc();
    }
}
