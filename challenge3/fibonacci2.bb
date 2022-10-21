var first = 0;
var second = 1;
var n = 20;
var fib;
while n do;
    fib = first; // test
    decr n;
    first = 0;
    while first not second do;
        incr fib;
        incr first; // while = ;;; // end
    end;
    second = fib;
end;