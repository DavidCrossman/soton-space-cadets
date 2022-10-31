var fib = 0;
var n = 29;
if n > 0 {
    var n = n;
    var next = 1;
    while n > 0 {
        n = n - 1;
        next = next + fib;
        fib = next - fib;
    }
}