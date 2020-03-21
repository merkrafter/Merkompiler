// a void-typed procedure must not be used in an expression
class VoidInAssignment {
    public void voidFunc() {
        return;
    }

    public void main() {
        int a;
        // should not be allowed
        a = 4 * voidFunc();
    }
}
