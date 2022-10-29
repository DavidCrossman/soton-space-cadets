var x = 0;
var out = 0;
while x < 20 do; {
    incr x;
    if x > 14 then;
        incr out;
    else if x < 5 then;
        incr out;
        incr out;
        incr out;
    end;
}; end;