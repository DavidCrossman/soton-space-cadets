var fib;
var n = 20;
{ // Variables defined in this scope will not exist at the end of the program
    var n = n; // Make local copy of 'n'
    var first = 0;
    var second = 1;
    while n > 0 do; {
        decr n;
        fib = first;
        first = 0;
        while first < second do; // Add 'second' to 'fib'
            incr fib;
            incr first;
        end;
        second = fib;
    }; end;
    fib = first;
}; // Only 'fib' and 'n' exist