// should not compile
class MissingReturnAfterWhileLoop {
    public int main() {
        while(4==4){
            return 1;
        }
    }
}
