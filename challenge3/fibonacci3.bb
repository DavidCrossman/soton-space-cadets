var fib;
var n = 29;
if n > 0 then; {
    var n = n; // Make local copy of 'n'
    var first = 0;
    var second = 1;
    while n > 1 do; {
        decr n;
        fib = first;
        first = 0;
        while first < second do; // Add 'second' to 'fib'
            incr fib;
            incr first;
        end;
        second = fib;
    }; end;
    fib = second;
}; else;
    fib = 0; // 0th fibonacci number is 0
end;
// Only 'fib' and 'n' exist