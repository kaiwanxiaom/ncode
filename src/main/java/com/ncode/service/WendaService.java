package com.ncode.service;

import org.springframework.stereotype.Service;

@Service
public class WendaService {
    public String getMessage(int id) {
        return String.valueOf(id) + "wendaService";
    }
}
