# 一些实验性的代码

## 试验在Servlet中，反向代理到另外的服务。

1. `cd proxyfrom && mvn package && java -jar target/servletproxy-0.1.0.jar`
1. `cd proxyto && go run main.go`
1. do some tests by httpie.

Client:

```bash
 ~> http :8080/proxy name=bingoo123 xxx=yyy
HTTP/1.1 200
Content-Length: 13
Date: Fri, 22 Mar 2019 06:47:38 GMT

Hello World!

```

Go proxyto:

```bash
proxyto> go run main.go
start to listen on port 8888
POST /proxy HTTP/1.1
Host: 127.0.0.1:8888
Accept: application/json, */*
Accept-Encoding: gzip, deflate
Cache-Control: no-cache
Connection: keep-alive
Content-Length: 35
Content-Type: application/json
Pragma: no-cache
User-Agent: HTTPie/1.0.2

{"name": "bingoo123", "xxx": "yyy"}

```