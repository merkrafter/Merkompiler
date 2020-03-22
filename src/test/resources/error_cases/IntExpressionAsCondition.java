// should not compile
class IntExpressionAsCondition {
    public void main() {
        int a;
        a = 4;
        while (a + 4) {
            a = a + 1;
        }
    }
}
