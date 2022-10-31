var result;
var n = 153;
{
    var fizz = -1111;
    var buzz = -2222;
    var fizzbuzz = -11112222;
    var fizz_num = 3;
    var buzz_num = 5;

    result = if (n / fizz_num) * fizz_num == n && (n / buzz_num) * buzz_num == n {
        fizzbuzz
    } else if (n / fizz_num) * fizz_num == n {
        fizz
    } else if (n / buzz_num) * buzz_num == n {
        buzz
    } else { n };
}