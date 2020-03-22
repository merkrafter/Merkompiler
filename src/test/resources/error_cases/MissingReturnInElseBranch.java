// should not compile
class MissingReturnInElseBranch {
    public int main() {
        if (1 == 1) {
            return 1;
        } else {
            // just a random statement
            main();
        }
    }
}
