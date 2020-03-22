// even though main is a void-typed procedure, it is not allowed to return a void-typed value
class VoidReturnage {
    public void voidFunc() {
        return;
    }

    public void main() {
        return voidFunc();
    }
}
