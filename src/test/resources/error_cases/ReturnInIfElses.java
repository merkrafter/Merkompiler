// In a void-typed procedure, returning an integer value is not allowed.
// This class tests the edge case of a return statement inside a conditional construct.
class ReturnInIfElses {
    public void main() {
        int a;
        a = 4;
        if (a == 4) {
            // should not be allowed because of void return type
            return a;
        } else {
            return;
        }
    }
}
