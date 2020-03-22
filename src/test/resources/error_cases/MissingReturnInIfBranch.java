// should not compile
class MissingReturnInIfBranch {
    public int main() {
        if (1 == 1) {
            // just a random statement
            main();
        } else {
            return 1;
        }
    }
}
