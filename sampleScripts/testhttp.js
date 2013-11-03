console.log(http.get("http://google.com?p=pacifista"));
console.log(http.request("http://google.com?p=pacifista", "GET", "{\"test\":\"value\"}","Content-Type: json"));