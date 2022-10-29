var n = 7;
var k = 2;
var catalan = 1;
while k <= n {
    catalan = catalan * (n + k);
    k = k + 1;
}
k = k - 1;
while k > 1 {
    catalan = catalan / k;
    k = k - 1;
}