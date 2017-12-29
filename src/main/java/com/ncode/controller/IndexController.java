package com.ncode.controller;

import com.ncode.model.User;
import com.ncode.service.WendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

//@Controller
public class IndexController {
    @Autowired
    private WendaService wendaService;

    @RequestMapping(path = {"/index"} )
    @ResponseBody
    public String index(@RequestParam(value = "type", defaultValue = "zz") String type,
                        HttpSession httpSession) {
        return String.format("index page of %s, %s, %s", type, httpSession.getAttribute("msg"), wendaService.getMessage(11));
    }

    @RequestMapping(path = "/post", method = RequestMethod.POST)
    @ResponseBody
    public String post() {
        return String.format("post page");
    }

    @RequestMapping(path = "/http")
    @ResponseBody
    public String getHTTPHead(Model model, HttpServletRequest request,
                              HttpServletResponse response,
                              HttpSession session) {
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            sb.append(name + ":" + request.getHeader(name) + "<br>");
        }

        if(request.getCookies() != null) {
            for(Cookie cookie: request.getCookies()) {
                sb.append(cookie.getName() + "|" + cookie.getValue() + "<br>");
            }
        }

        sb.append(request.getMethod() + "<br>");
        sb.append(request.getRequestURL() + "<br>");
        sb.append(request.getProtocol() + "<br>");


        response.addCookie(new Cookie("key", "cococococ"));
        response.setHeader("myhead", "hhhhhhh");
        return sb.toString();
    }

    @RequestMapping(path = "/redirect/{code}")
    public RedirectView redirect(@PathVariable("code") int code,
                           HttpSession httpSession) {
        httpSession.setAttribute("msg", "from redirect url");
        RedirectView red = new RedirectView("/", true);
        if(code == 301) {
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return red;
    }

    @ExceptionHandler
    @ResponseBody
    public String error(Exception e) {
        return "ERROR:" + e.getMessage();
    }

    @RequestMapping(path = "/admin")
    @ResponseBody
    public String admin(@RequestParam("key") String key){
        if("admin".equals(key)) {
            return "Hello admin";
        }
        throw new IllegalArgumentException("error Argument");
    }

    @RequestMapping(path = "/vm")
    public String template(Model model) {
        User user = new User();
        model.addAttribute("user", user);

        List<String> colors = Arrays.asList(new String[] {"red", "blue", "green"});
        model.addAttribute("colors", colors);

        Map<String, String> map = new HashMap<>();
        for(int i = 0; i < 4; ++i) {
            map.put(String.valueOf(i), String.valueOf(i*i));
        }

        model.addAttribute("map", map);
        return "home";
    }

}
