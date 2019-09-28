# Client_Library_HTTP_1.0


To test the HTTP custom library, following commands need to be passed:

To Test GET Command:
httpc get 'http://httpbin.org/get?course=networking&assignment=1'

To Test Verbose Command with GET:
httpc get -v 'http://httpbin.org/get?course=networking&assignment=1'

To Test Verbose Command and file writing option with GET:
httpc get -v 'http://httpbin.org/get?course=networking&assignment=1' -o hello.txt

To Test POST  with inline data. 
httpc post -h Content-Type:application/json -d '{\"Assignment\":1}' http://httpbin.org/post"

To Test the Redirection:
httpc get https://www.amazon.com/
