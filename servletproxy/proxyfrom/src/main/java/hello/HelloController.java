package hello;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  @RequestMapping("/hello")
  public String index() {
    return "Greetings from Spring Boot!";
  }

  @RequestMapping("/proxy")
  public void proxy(HttpServletRequest q, HttpServletResponse p) {
    try {
      new RequestForwarder(q, p).forward("http://127.0.0.1:8888");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}